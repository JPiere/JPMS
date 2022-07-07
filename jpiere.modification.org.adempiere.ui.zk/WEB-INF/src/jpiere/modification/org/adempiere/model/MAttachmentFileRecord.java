/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.modification.org.adempiere.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import org.compiere.model.MClientInfo;
import org.compiere.model.MOrg;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.modification.webui.action.attachment.IJPiereAttachmentStore;
import jpiere.modification.webui.action.attachment.MJPiereStorageProvider;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class MAttachmentFileRecord extends X_JP_AttachmentFileRecord {

	public MAttachmentFileRecord(Properties ctx, int JP_AttachmentFileRecord_ID, String trxName)
	{
		super(ctx, JP_AttachmentFileRecord_ID, trxName);
		initAttachmentStoreDetails(ctx, trxName);
	}

	public MAttachmentFileRecord(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
		initAttachmentStoreDetails(ctx, trxName);
	}

	static public HashSet<String> getAttachmentDirectory(Properties ctx, int AD_Table_ID, int Record_ID, int AD_Org_ID , MJPiereStorageProvider attachmentStorageProvider ,String trxName)
	{
		StringBuilder sql = null;
		HashSet<String> pathSet = new HashSet<String>();
		
		if(attachmentStorageProvider == null)
		{
			sql = new StringBuilder("SELECT * FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=? AND AD_Org_ID=? AND IsActive='Y' AND AD_StorageProvider_ID IS NULL ");
		}else {
			sql = new StringBuilder("SELECT * FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=? AND AD_Org_ID=? AND IsActive='Y' AND AD_StorageProvider_ID =? ");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MAttachmentFileRecord attachmentFileRecord = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			pstmt.setInt(3, AD_Org_ID);
			if(attachmentStorageProvider != null)
			{
				pstmt.setInt(4, attachmentStorageProvider.getAD_StorageProvider_ID());
			}
			rs = pstmt.executeQuery();

			while (rs.next())
			{
				attachmentFileRecord = new MAttachmentFileRecord (ctx, rs, trxName);
				pathSet.add(attachmentFileRecord.getDirectoryAbsolutePath());				
			}
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return pathSet;
	}

	static public ArrayList<MAttachmentFileRecord> getAttachmentFileRecordPO(Properties ctx, int AD_Table_ID, int Record_ID, boolean isCheckRole, String trxName)
	{
		MRole role = MRole.getDefault(ctx, false);
		String orgWhere = null;
		if(isCheckRole)
			orgWhere =role.getOrgWhere(false);

		ArrayList<MAttachmentFileRecord> list = new ArrayList<MAttachmentFileRecord>();
		StringBuilder sql = new StringBuilder("SELECT * FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=? AND IsActive='Y'");
		if(!Util.isEmpty(orgWhere))
		{
			sql = sql.append(" AND ").append(orgWhere);

		}

		sql = sql.append(" ORDER BY JP_AttachmentFileRecord_ID");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MAttachmentFileRecord (ctx, rs, trxName));
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return list;

	}

	static public ArrayList<MOrg> getAttachmentFileOrgList(Properties ctx, int AD_Table_ID, int Record_ID, boolean isCheckRole, String trxName)
	{
		MRole role = MRole.getDefault(ctx, false);
		String orgWhere = null;
		if(isCheckRole)
			orgWhere =role.getOrgWhere(false);

		ArrayList<MOrg> list = new ArrayList<MOrg>();
		StringBuilder sql = new StringBuilder("SELECT DISTINCT org.* FROM JP_AttachmentFileRecord af INNER JOIN AD_Org org ON (af.AD_Org_ID = org.AD_Org_ID) WHERE af.AD_Table_ID=? AND af.Record_ID=? AND af.IsActive='Y'");
		if(!Util.isEmpty(orgWhere))
		{
			sql = sql.append(" AND ").append(orgWhere);

		}

		sql = sql.append(" ORDER BY org.Value");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MOrg (ctx, rs, trxName));
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}


		return list;

	}
	
	static public ArrayList<MJPiereStorageProvider> getAttachmentStorageProviderList(Properties ctx, int AD_Table_ID, int Record_ID, String trxName)
	{
		ArrayList<MJPiereStorageProvider> list = new ArrayList<MJPiereStorageProvider>();
		StringBuilder sql = new StringBuilder("SELECT DISTINCT sp.* FROM JP_AttachmentFileRecord af INNER JOIN AD_StorageProvider sp ON (af.AD_StorageProvider_ID = sp.AD_StorageProvider_ID) WHERE af.AD_Table_ID=? AND af.Record_ID=? AND af.IsActive='Y'");

		sql = sql.append(" ORDER BY sp.Name");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, AD_Table_ID);
			pstmt.setInt(2, Record_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MJPiereStorageProvider (ctx, rs, trxName));
		}
		catch (Exception e)
		{
//			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		list.add(null);//For Backward compatibility.

		return list;

	}

	private MJPiereStorageProvider storageProvider;
	private IJPiereAttachmentStore attachmentStore;

	private void initAttachmentStoreDetails(Properties ctx, String trxName)
	{
		MClientInfo clientInfo = MClientInfo.get(ctx, getAD_Client_ID());
		
		int AD_StorageProvider_ID = 0;
		int record_ID = getJP_AttachmentFileRecord_ID();
		if(record_ID == 0)
		{
			AD_StorageProvider_ID = clientInfo.get_ValueAsInt("JP_StorageAttachment_ID");
		}else {
			AD_StorageProvider_ID = getAD_StorageProvider_ID();
			if(AD_StorageProvider_ID == 0)
			{
				AD_StorageProvider_ID = clientInfo.get_ValueAsInt("JP_StorageAttachment_ID");
			}
		}
		
		setAD_StorageProvider_ID(AD_StorageProvider_ID);		
		storageProvider= new MJPiereStorageProvider(ctx, AD_StorageProvider_ID, trxName);
	}

	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		PO po = null;
		
		if(is_ValueChanged(COLUMNNAME_IsDeleteable))
		{
			if(!isDeleteable())
			{	
				if(po == null)
					po = createPO();
				setReferenceInfo(po);
			}
		}
		
		String columnName = MSysConfig.getValue("JP_ATTACHMENT_MODELCHANGE_UNDELETABLE_COLUMN", "NULL");
		Object columnValue = MSysConfig.getValue("JP_ATTACHMENT_MODELCHANGE_UNDELETABLE_VALUE", "NULL");
		if(!columnName.equals("NULL") && !columnValue.equals("NULL"))
		{
			if(newRecord || isDeleteable())
			{	
				if(po == null)
					po = createPO();
				
				int columnIndex = po.get_ColumnIndex(columnName);
				if(columnIndex > -1)
				{
					Object value = po.get_Value(columnIndex);
					if(value instanceof String)
					{
						if(columnValue.toString().equals(value.toString()))
						{
							setIsDeleteable(false);
						}
	
					}else if (value instanceof Boolean) {
	
						Boolean valuBoolean = (Boolean)value;
						boolean columnValueBoolean = columnValue.equals("Y");
						if(valuBoolean.booleanValue() == columnValueBoolean)
						{
							setIsDeleteable(false);
						}
	
					}else if (value instanceof Integer) {
	
						Integer valuInteger = (Integer)value;
						Integer columnValueInteger = Integer.valueOf(columnValue.toString());
						if(valuInteger.intValue() == columnValueInteger.intValue())
						{
							setIsDeleteable(false);
						}
	
					}
					
					if(!newRecord)
						setReferenceInfo(po);
				}
			}
		}
		
		if(po == null)
			po = createPO();
		
		if(newRecord)
			setReferenceInfo(po);
		
		return true;
	}
	
	private PO createPO()
	{
		MTable m_Table = MTable.get(getAD_Table_ID());
		return m_Table.getPO(getRecord_ID(), get_TrxName());
	}
	
	private void setReferenceInfo(PO from)
	{
		int columnCount = get_ColumnCount();
		for (int i1 = 0; i1 < columnCount; i1++)
		{
			String colName = get_ColumnName(i1);
			if (   colName.equals("AD_Client_ID")
				|| colName.equals("AD_Org_ID")
				|| colName.equals("AD_Table_ID")
				|| colName.equals("Record_ID")
				|| colName.equals("JP_AttachmentFileName")
				|| colName.equals("JP_AttachmentFilePath")
				|| colName.equals("AD_StorageProvider_ID")
				|| colName.equals("JP_AttachmentFileDescription")
				|| colName.equals("JP_MediaContentType")
				|| colName.equals("JP_MediaFormat")
				|| colName.equals("JP_Processing1")
				|| colName.equals("IsActive")
				|| colName.equals("IsDeleteable")
				|| colName.equals("JP_Processing2")
				|| colName.equals("Created")
				|| colName.equals("CreatedBy")
				|| colName.equals("Updated")
				|| colName.equals("UpdatedBy")
				|| colName.equals("JP_AttachmentFile_ID")
				|| colName.equals("JP_AttachmentFile_UU")
				)
			{
				continue;
			}
			
			 if(colName.equals("DateDoc"))
			 {
				 Object dateDoc = null;
				if(from.columnExists("DateOrdered"))
				{
					dateDoc = from.get_Value("DateOrdered");
				}else if(from.columnExists("MovementDate")) {
					dateDoc = from.get_Value("MovementDate");
				}else if(from.columnExists("DateInvoiced")) {
					dateDoc = from.get_Value("DateInvoiced");		
				}else if(from.columnExists("DateTrx")) {
					dateDoc = from.get_Value("DateTrx");	
				}else if(from.columnExists("StatementDate")) {
					dateDoc = from.get_Value("StatementDate");
				}		
				set_ValueNoCheck(colName, dateDoc);
				
			 }else if(from.columnExists(colName)){
				set_ValueNoCheck(colName, from.get_Value(colName));
			}
		}//for
	}

	@Override
	protected boolean beforeDelete()
	{
		if(!isDeleteable())
		{
			//Could not delte the file;
			log.saveError("Error", Msg.getMsg(getCtx(), "JP_CouldNotDeleteFile"));
			return false;
		}


		return true;
	}

	@Override
	protected boolean afterDelete(boolean success)
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			boolean isDelete =attachmentStore.deleteFile(this, storageProvider);

			if(!isDelete)
			{
				//Could not delte the file;
				log.saveError("Error", Msg.getMsg(getCtx(), "JP_CouldNotDeleteFile"));
				return false;
			}
		}

		return true;
	}


	public boolean upLoadLFile (byte[] data)
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.upLoadFile(this, data, storageProvider);
		}

		return false;
	}


	public String getFileAbsolutePath()
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.getFileAbsolutePath(this, storageProvider).toString();
		}

		return null;
	}


	public String getDirectoryAbsolutePath()
	{
		if (attachmentStore == null)
			attachmentStore = storageProvider.getAttachmentStore();

		if (attachmentStore != null)
		{
			return attachmentStore.getDirectoryAbsolutePath(this, storageProvider).toString();
		}

		return null;
	}


	public static int getID(int Table_ID, int Record_ID)
	{
		String sql="SELECT JP_AttachmentFileRecord_ID FROM JP_AttachmentFileRecord WHERE AD_Table_ID=? AND Record_ID=?";
		int attachid = DB.getSQLValue(null, sql, Table_ID, Record_ID);
		return attachid;
	}


}
