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
