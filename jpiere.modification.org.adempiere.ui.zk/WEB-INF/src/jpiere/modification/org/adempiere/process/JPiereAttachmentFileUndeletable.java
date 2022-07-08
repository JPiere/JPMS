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
package jpiere.modification.org.adempiere.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;

/**
*
* JPIERE-0555: JPiere Attachment File
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentFileUndeletable extends SvrProcess {
	
	
	private Timestamp p_Created_From = null;
	private Timestamp p_Created_To = null;
	private int p_AD_Table_ID = 0; 
	private int p_AD_Client_ID= 0;
	private String p_JP_MediaFormat = null;

	@Override
	protected void prepare() 
	{
		p_AD_Client_ID =getProcessInfo().getAD_Client_ID();
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null){
				;
			}else if (name.equals("Created")){
				p_Created_From = (Timestamp)para[i].getParameter();
				p_Created_To = (Timestamp)para[i].getParameter_To();
			}else if (name.equals("AD_Table_ID")){
				p_AD_Table_ID = para[i].getParameterAsInt();
			}else if (name.equals("Created")){
				p_Created_From = (Timestamp)para[i].getParameter();
				p_Created_To = (Timestamp)para[i].getParameter_To();
			}else if (name.equals("JP_MediaFormat")) {
				p_JP_MediaFormat = para[i].getParameterAsString();
			}else{
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}//if
		}//for

	}

	@Override
	protected String doIt() throws Exception 
	{
		ArrayList<Integer> list_ID = new ArrayList<Integer>();
		StringBuilder sqlInfo = new StringBuilder(" SELECT T_Selection_ID FROM T_Selection WHERE AD_PInstance_ID= ? ") ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sqlInfo.toString(), get_TrxName());
			pstmt.setInt(1, getAD_PInstance_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list_ID.add(rs.getInt(1));
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sqlInfo.toString(), e);
			throw e;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		int count = list_ID.size();
		
		if(count == 0)//process
		{
			if(p_Created_From == null || p_Created_To == null)
			{
				throw new Exception(Msg.getMsg(getCtx(), "JP_UnexpectedError"));
			}
			
			StringBuilder sqlProcess = new StringBuilder( "SELECT * FROM JP_AttachmentFileRecord ")
										.append(" WHERE AD_Client_ID=? AND Created >=? AND Created <= ? AND IsDeleteable ='Y'");
					
			if(p_AD_Table_ID > 0)
			{
				sqlProcess.append(" AND AD_Table_ID = ? ");
			}
			
			if(!Util.isEmpty(p_JP_MediaFormat))
			{
				sqlProcess.append(" AND p_JP_MediaFormat = ? ");
			}
			
			ArrayList<MAttachmentFileRecord> list_AttachmentFileRecord = new ArrayList<MAttachmentFileRecord>();
			pstmt = null;
			rs = null;
			try
			{
				pstmt = DB.prepareStatement(sqlProcess.toString(), get_TrxName());
				int index = 1;
				pstmt.setInt(index, p_AD_Client_ID);
				
				index++;
				pstmt.setTimestamp(index, p_Created_From);
				
				index++;
				pstmt.setTimestamp(index, p_Created_To);
				
				if(p_AD_Table_ID > 0)
				{
					index++;
					pstmt.setInt(index, p_AD_Table_ID);
				}
				
				if(!Util.isEmpty(p_JP_MediaFormat))
				{
					index++;
					pstmt.setString(index, p_JP_MediaFormat);
				}
				
				rs = pstmt.executeQuery();
				while (rs.next())
					list_AttachmentFileRecord.add(new MAttachmentFileRecord (getCtx(), rs, get_TrxName()));
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sqlProcess.toString(), e);
				throw e;
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
			
			for(MAttachmentFileRecord afr : list_AttachmentFileRecord)
			{
				afr.setIsDeleteable(false);
				afr.saveEx(get_TrxName());
				count++;
			}			
			
		}else {// from Info Window
			
			count = 0;
			for(Integer id : list_ID )
			{
				MAttachmentFileRecord afr = new MAttachmentFileRecord(getCtx(), id.intValue(), get_TrxName());
				if(afr.isDeleteable())
				{
					afr.setIsDeleteable(false);
					afr.saveEx(get_TrxName());
					addBufferLog(0, null, null, afr.getJP_AttachmentFileName(), MAttachmentFileRecord.Table_ID, afr.getJP_AttachmentFileRecord_ID());	
					count++;
				}
			}
		}
		
		return Msg.getMsg(getCtx(), "Update") + " : " + count;
	}

}
