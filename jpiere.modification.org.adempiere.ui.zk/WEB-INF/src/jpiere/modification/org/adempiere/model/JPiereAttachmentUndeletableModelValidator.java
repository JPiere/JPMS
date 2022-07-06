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
import java.util.List;
import java.util.logging.Level;

import org.compiere.model.MClient;
import org.compiere.model.MSysConfig;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

/**
 *  JPiere Attachment Undeletable Model Validator
 *
 *  @author  Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPiereAttachmentUndeletableModelValidator implements ModelValidator {

	private static CLogger log = CLogger.getCLogger(JPiereAttachmentUndeletableModelValidator.class);
	private int AD_Client_ID = -1;

	private String columnName = null;
	private Object columnValue = null;

	private int configTiming = 0;

	@Override
	public void initialize(ModelValidationEngine engine, MClient client)
	{
		if(client != null)
			this.AD_Client_ID = client.getAD_Client_ID();

		columnName = MSysConfig.getValue("JP_ATTACHMENT_MODELCHANGE_UNDELETABLE_COLUMN", "NULL");
		columnValue = MSysConfig.getValue("JP_ATTACHMENT_MODELCHANGE_UNDELETABLE_VALUE", "NULL");
		if(!columnName.equals("NULL") && !columnValue.equals("NULL"))
		{
			String sql = "SELECT t.TableName FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID = c.AD_Table_ID) WHERE c.ColumnName=? ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				pstmt.setString(1, columnName);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					engine.addModelChange(rs.getString(1), this);
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}

		configTiming = MSysConfig.getIntValue("JP_ATTACHMENT_DOCVALIDATE_UNDELETABLE", 0);
		if(configTiming != 0)
		{
			String sql = "SELECT t.TableName FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID = c.AD_Table_ID) WHERE c.ColumnName='DocAction' ";
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try
			{
				pstmt = DB.prepareStatement(sql, null);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					engine.addDocValidate(rs.getString(1), this);
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sql, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		}

	}

	@Override
	public int getAD_Client_ID()
	{
		return AD_Client_ID;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID)
	{
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception
	{

		if(type == ModelValidator.TYPE_AFTER_CHANGE)
		{
			int columnIndex = po.get_ColumnIndex(columnName);
			if(columnIndex == -1)
			{
				return null;

			}else if(po.is_ValueChanged(columnIndex)) {

				Object value = po.get_Value(columnIndex);
				if(value instanceof String)
				{
					if(columnValue.toString().equals(value.toString()))
					{
						setIsDeleteableN(po);
					}

				}else if (value instanceof Boolean) {

					Boolean valuBoolean = (Boolean)value;
					boolean columnValueBoolean = columnValue.equals("Y");
					if(valuBoolean.booleanValue() ==columnValueBoolean)
					{
						setIsDeleteableN(po);
					}

				}else if (value instanceof Integer) {

					Integer valuInteger = (Integer)value;
					Integer columnValueInteger = Integer.valueOf(columnValue.toString());
					if(valuInteger.intValue() == columnValueInteger.intValue())
					{
						setIsDeleteableN(po);
					}

				}
			}

		}

		return null;
	}

	@Override
	public String docValidate(PO po, int timing)
	{

		if(configTiming == 0)
		{
			return null;

		}else if(timing == configTiming) {

			setIsDeleteableN(po);

		}

		return null;
	}

	private int setIsDeleteableN(PO po)
	{
		StringBuilder whereClause = new StringBuilder("AD_Client_ID = ? AND AD_Table_ID = ? AND Record_ID= ? AND IsDeleteable= 'Y'");

		//
		List<MAttachmentFileRecord> list = new Query(po.getCtx(), MAttachmentFileRecord.Table_Name, whereClause.toString(), po.get_TrxName())
										.setParameters(po.getAD_Client_ID(), po.get_Table_ID(), po.get_ID())
										.list();
		
		
		for(MAttachmentFileRecord afr : list)
		{
			afr.setIsDeleteable(false);
			afr.saveEx(po.get_TrxName());
		}

		return list.size();
	}

}
