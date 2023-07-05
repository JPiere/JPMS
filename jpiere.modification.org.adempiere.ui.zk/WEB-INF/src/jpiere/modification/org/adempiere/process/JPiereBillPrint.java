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
/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.modification.org.adempiere.process;

import java.io.File;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.util.IProcessUI;
import org.compiere.model.MClient;
import org.compiere.model.MClientInfo;
import org.compiere.model.MMailText;
import org.compiere.model.MProcessPara;
import org.compiere.model.MQuery;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MUser;
import org.compiere.model.MUserMail;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.print.MPrintFormat;
import org.compiere.print.ReportEngine;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.EMail;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Msg;
import org.compiere.util.Util;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;

/**
 *	JPIERE-0604
 *  Print Bill
 *
 *  Ref: InvoicePrit.java - Print Invoices on Paper or send PDFs
 *
 * 	@author Jorg Janke
 *  @author h.hagiwara
 */
@org.adempiere.base.annotation.Process
public class JPiereBillPrint extends SvrProcess
{
	/**	Mail PDF			*/
	protected boolean		p_EMailPDF = false;
	/** Mail Template		*/
	protected int			p_R_MailText_ID = 0;
	
	protected int			p_AD_Org_ID = -1;
	protected Timestamp	p_JPDateBilled_From = null;
	protected Timestamp	p_JPDateBilled_To = null;
	protected int			p_C_BPartner_ID = 0;
	protected int			p_JP_Bill_ID = 0;
	protected String		p_DocumentNo_From = null;
	protected String		p_DocumentNo_To = null;
	private String			p_IsPaid = null;
	private int			p_C_DocType_ID = 0;
	private String			p_IsPrinted = null;
	private String			p_PaymentRule = null;
	private int	  	    p_C_PaymentTerm_ID = 0;
	private String			p_DocStatus = null;
	private boolean 		p_IsAttachmentFileJP = false;

	protected volatile StringBuffer sql = new StringBuffer();
	protected volatile List<Object> params = new ArrayList<Object>();

	IProcessUI processMonitor = null;

	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null && para[i].getParameter_To() == null)
				;
			else if (name.equals("JPDateBilled"))
			{
				p_JPDateBilled_From = ((Timestamp)para[i].getParameter());
				p_JPDateBilled_To = ((Timestamp)para[i].getParameter_To());
			}
			else if (name.equals("EMailPDF"))
				p_EMailPDF = "Y".equals(para[i].getParameter());
			else if (name.equals("R_MailText_ID"))
				p_R_MailText_ID = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para[i].getParameterAsInt();
			else if (name.equals("C_BPartner_ID"))
				p_C_BPartner_ID = para[i].getParameterAsInt();
			else if (name.equals("JP_Bill_ID"))
				p_JP_Bill_ID = para[i].getParameterAsInt();
			else if (name.equals("DocumentNo"))
			{
				p_DocumentNo_From = (String)para[i].getParameter();
				p_DocumentNo_To = (String)para[i].getParameter_To();
			}
			else if (name.equals("IsPaid"))
				p_IsPaid = (String)para[i].getParameter();
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = para[i].getParameterAsInt();
			else if (name.equals("IsPrinted"))
				p_IsPrinted = (String)para[i].getParameter();
			else if (name.equals("PaymentRule"))
				p_PaymentRule = (String)para[i].getParameter();
			else if (name.equals("C_PaymentTerm_ID"))
				p_C_PaymentTerm_ID = para[i].getParameterAsInt();
			else if (name.equals("DocStatus"))
				p_DocStatus = (String)para[i].getParameter();
			else if (name.equals("IsAttachmentFileJP"))
				p_IsAttachmentFileJP = para[i].getParameterAsBoolean();
			else
				MProcessPara.validateUnknownParameter(getProcessInfo().getAD_Process_ID(), para[i]);
		}
		
		if (p_DocumentNo_From != null && p_DocumentNo_From.length() == 0)
			p_DocumentNo_From = null;
		if (p_DocumentNo_To != null && p_DocumentNo_To.length() == 0)
			p_DocumentNo_To = null;
		
		processMonitor = Env.getProcessUI(getCtx());
		
	}	//	prepare

	/**
	 *  Perform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		//	Need to have Template
		if (p_EMailPDF && p_R_MailText_ID == 0)
			throw new AdempiereUserError ("@NotFound@: @R_MailText_ID@");
		if (log.isLoggable(Level.INFO)) log.info ("C_BPartner_ID=" + p_C_BPartner_ID
			+ ", JP_Bill_ID=" + p_JP_Bill_ID
			+ ", EmailPDF=" + p_EMailPDF + ",R_MailText_ID=" + p_R_MailText_ID
			+ ", DateInvoiced=" + p_JPDateBilled_From + "-" + p_JPDateBilled_To
			+ ", DocumentNo=" + p_DocumentNo_From + "-" + p_DocumentNo_To
			+ ", IsPaid=" + p_IsPaid
			+ ", C_DocType_ID=" + p_C_DocType_ID
			+ ", IsPrinted=" + p_IsPrinted
			+ ", PaymentRule=" + p_PaymentRule
			+ ", C_PaymentTerm_ID=" + p_C_PaymentTerm_ID
			+ ", DocStatus=" + p_DocStatus);
		
		MMailText mText = null;
		if (p_R_MailText_ID != 0)
		{
			mText = new MMailText(getCtx(), p_R_MailText_ID, get_TrxName());
			if (mText.get_ID() != p_R_MailText_ID)
				throw new AdempiereUserError ("@NotFound@: @R_MailText_ID@ - " + p_R_MailText_ID);
		}

		//	Too broad selection
		if (p_C_BPartner_ID == 0 && p_JP_Bill_ID == 0 && p_JPDateBilled_From == null && p_JPDateBilled_To == null
			&& p_DocumentNo_From == null && p_DocumentNo_To == null && p_PaymentRule == null && p_C_PaymentTerm_ID == 0
			&& p_DocStatus == null)
			throw new AdempiereUserError ("@RestrictSelection@");

		MClient client = MClient.get(getCtx());
		if(p_EMailPDF)
		{
			if(Util.isEmpty(client.getSMTPHost()) || client.getSMTPHost().equals("localhost"))
			{
				throw new AdempiereUserError (Msg.getMsg(getCtx(), "RequestActionEMailNoSMTP"));
			}
		}
		
		if(p_IsAttachmentFileJP)
		{
			MClientInfo cInfo = MClientInfo.get(client.getAD_Client_ID());
			int JP_StorageAttachment_ID =  cInfo.get_ValueAsInt("JP_StorageAttachment_ID");
			if(JP_StorageAttachment_ID == 0)
			{
				throw new AdempiereUserError (Msg.getMsg(Env.getCtx(), "NotFound") + " "+ Msg.getElement(Env.getCtx(), "JP_StorageAttachment_ID"));
			}
		}
		
		setSQLAndParams();
		if (log.isLoggable(Level.FINE)) log.fine(sql.toString());

		MPrintFormat format = null;
		String tableName = null;

		int old_AD_PrintFormat_ID = -1;
		int C_BPartner_ID = 0;
		String docStatus = null;
		int count = 0;
		int errors = 0;
		int JP_Bill_Table_ID = MTable.getTable_ID("JP_Bill");
		MTable m_Table = MTable.get(JP_Bill_Table_ID);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final List<File> pdfList = new ArrayList<File>();
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int idx = 1;
			for (Object param : params) {
				if (param instanceof Integer)
					pstmt.setInt(idx, (Integer) param);
				else if (param instanceof Timestamp)
					pstmt.setTimestamp(idx, (Timestamp) param);
				else
					pstmt.setString(idx, param.toString());
				idx++;
			}
			rs = pstmt.executeQuery();
						
			while (rs.next())
			{
				int JP_Bill_ID = rs.getInt(1);
				
				//	Set Language when enabled
				Language language = Language.getLoginLanguage();		//	Base Language
				String AD_Language = rs.getString(2);
				if (AD_Language != null && "Y".equals(rs.getString(3)))
					language = Language.getLanguage(AD_Language);
				//
				int AD_PrintFormat_ID = rs.getInt(4);
				int copies = rs.getInt(5);
				if (copies == 0)
					copies = 1;
				int AD_User_ID = rs.getInt(6);
				MUser to = new MUser (getCtx(), AD_User_ID, get_TrxName());
				String DocumentNo = rs.getString(7);
				C_BPartner_ID = rs.getInt(8);
				docStatus = rs.getString(9);
				
				
				if(!DocAction.STATUS_Completed.equals(docStatus)
						&& !DocAction.STATUS_Closed.equals(docStatus))
				{
					StringBuilder whereClause = new StringBuilder("JP_Bill_ID=?");
					List<PO> list = new Query(getCtx(), "JP_BillLine", whereClause.toString(), get_TrxName())
													.setParameters(JP_Bill_ID)
													.list();
					
					if(list == null || list.size() == 0)
					{
						addBufferLog(0, null, null, DocumentNo + " - " + Msg.getMsg(getCtx(), "NoLines"), JP_Bill_Table_ID, JP_Bill_ID);
						continue;
					}
				}
				
				
				if (p_EMailPDF && (to.get_ID() == 0 || to.getEMail() == null || to.getEMail().length() == 0))
				{
					addBufferLog(0, null, null, DocumentNo + " - " + Msg.getMsg(getCtx(), "RequestActionEMailNoTo") + " - " + to.getName()
										, JP_Bill_Table_ID, JP_Bill_ID);
					errors++;
					continue;
				}
				
				if (AD_PrintFormat_ID == 0)
				{
					addBufferLog(0, null, null, DocumentNo + " - " +Msg.getMsg(Env.getCtx(), "NotFound") + " "+ Msg.getElement(Env.getCtx(), "AD_PrintFormat_ID")
											, JP_Bill_Table_ID, JP_Bill_ID);
					errors++;
					continue;
				}
				
				//	Get Format & Data
				if (AD_PrintFormat_ID != old_AD_PrintFormat_ID)
				{
					format = MPrintFormat.get (getCtx(), AD_PrintFormat_ID, false);
					old_AD_PrintFormat_ID = AD_PrintFormat_ID;
					tableName = MTable.get(format.getAD_Table_ID()).getTableName();
				}
				format.setLanguage(language);
				format.setTranslationLanguage(language);
				
				if(processMonitor != null)
					processMonitor.statusUpdate(DocumentNo);
				
				//	query
				MQuery query = new MQuery(tableName);
				query.addRestriction("JP_Bill_ID", MQuery.EQUAL, Integer.valueOf(JP_Bill_ID));

				//	Engine
				PrintInfo info = new PrintInfo(
					DocumentNo,
					JP_Bill_Table_ID,
					JP_Bill_ID,
					C_BPartner_ID);
				info.setCopies(copies);
				ReportEngine re = new ReportEngine(getCtx(), format, query, info);
				String fileName = re.getName()+ "_" + getAD_PInstance_ID() + ".pdf";
				boolean printed = false;
				if (p_EMailPDF)
				{
					//Context
					mText.setUser(to);					
					mText.setPO(m_Table.getPO(JP_Bill_ID, get_TrxName()));
					mText.setBPartner(C_BPartner_ID);
					
					String subject = mText.getMailHeader() + " - " + DocumentNo;
					String message = mText.getMailText(true);
					EMail email = client.createEMail(to.getEMail(), subject, message, mText.isHtml());
					if (!email.isValid())
					{
						addBufferLog(0, null, null, DocumentNo + " - "+ Msg.getMsg(getCtx(), "RequestActionEMailError") + " Invalid EMail: " +  to.getName(), JP_Bill_Table_ID, JP_Bill_ID);
						errors++;
						continue;
					}
					
					File attachment = re.getPDF();
					email.addAttachment(attachment);
					
					//
					String msg = email.send();
					if (msg.equals(EMail.SENT_OK))
					{
						MUserMail um = new MUserMail(mText, getAD_User_ID(), email);
						um.setR_MailText_ID(0);
						um.setSubject(subject);
						um.setMailText(message);
						um.setIsDelivered("Y");
						um.saveEx();
						
						commitEx();
						
						setSendEMail(JP_Bill_ID);
						pdfList.add(attachment);
						
						addLog (0, null, null,
						  DocumentNo + " - " + Msg.getMsg(getCtx(),"RequestActionEMailOK") + " - " + to.getEMail(), JP_Bill_Table_ID, JP_Bill_ID);
						count++;
						printed = true;
						
						if(printed)
							setPrinted(JP_Bill_ID);
						
						if(p_IsAttachmentFileJP)
							attachmentFile(re, fileName, JP_Bill_Table_ID, JP_Bill_ID);
						
						commitEx();
					}
					else
					{
						addBufferLog(0, null, null, DocumentNo + " - " + Msg.getMsg(getCtx(),"RequestActionEMailError") + " - " + to.getEMail(), JP_Bill_Table_ID, JP_Bill_ID);
						errors++;
					}
					
				}
				else
				{
					pdfList.add(re.getPDF());
					count++;
					printed = true;
					
					if (printed)
						setPrinted(JP_Bill_ID);
					
					if(p_IsAttachmentFileJP)
						attachmentFile(re, fileName, JP_Bill_Table_ID, JP_Bill_ID);

				}
				
			}	//	for all entries
			
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "doIt - " + sql, e);
			throw new Exception (e);
		}
		finally {
			DB.close(rs, pstmt);
		}
		
		if (processUI != null && pdfList.size() > 0)
		{
			processUI.showReports(pdfList);
		}

		//
		if (p_EMailPDF)
			return "@Sent@=" + count + " - @Errors@=" + errors;
		return "@Printed@=" + count;
	}	//	doIt

	protected void setSQLAndParams() {
		//	Get Info
		sql.append(
			"SELECT i.JP_Bill_ID,bp.AD_Language,c.IsMultiLingualDocument,"		//	1..3
			//	Prio: 1. BPartner 2. DocType, 3. PrintFormat (Org)	//	see ReportCtl+MInvoice
			+ " COALESCE(bp.JP_Bill_PrintFormat_ID, dt.AD_PrintFormat_ID, pf.JP_Bill_PrintFormat_ID),"	//	4 
			+ " dt.DocumentCopies+bp.DocumentCopies,"								//	5
			+ " bpc.AD_User_ID, i.DocumentNo,"										//	6..7
			+ " bp.C_BPartner_ID,i.DocStatus "										//	8..9
			+ "FROM JP_Bill i"
			+ " INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID)"
			+ " LEFT OUTER JOIN AD_User bpc ON (i.AD_User_ID=bpc.AD_User_ID)"
			+ " INNER JOIN AD_Client c ON (i.AD_Client_ID=c.AD_Client_ID)"
			+ " INNER JOIN AD_PrintForm pf ON (i.AD_Client_ID=pf.AD_Client_ID)"
			+ " INNER JOIN C_DocType dt ON (i.C_DocType_ID=dt.C_DocType_ID)"
		    + " WHERE i.AD_Client_ID=? AND i.isSOTrx='Y' AND "
		    + "       pf.AD_Org_ID IN (0,i.AD_Org_ID) " );	//	more them 1 PF
		params.add(Env.getAD_Client_ID(Env.getCtx()));
		
		if (p_JP_Bill_ID != 0) 
		{
			sql.append(" AND i.JP_Bill_ID=?");
			params.add(p_JP_Bill_ID);
			
		} else {
			
			if (p_AD_Org_ID > 0)
			{
				sql.append (" AND i.AD_Org_ID=?");
				params.add(p_AD_Org_ID);
			}
			
			if (p_C_BPartner_ID != 0)
			{
				sql.append (" AND i.C_BPartner_ID=?");
				params.add(p_C_BPartner_ID);
			}
			
			if (p_JPDateBilled_From != null && p_JPDateBilled_To != null)
			{
				sql.append(" AND TRUNC(i.JPDateBilled) BETWEEN ? AND ?");
				params.add(p_JPDateBilled_From);
				params.add(p_JPDateBilled_To);
			}
			else if (p_JPDateBilled_From != null)
			{
				sql.append(" AND TRUNC(i.JPDateBilled) >= ?");
				params.add(p_JPDateBilled_From);
			}
			else if (p_JPDateBilled_To != null)
			{
				sql.append(" AND TRUNC(i.JPDateBilled) <= ?");
				params.add(p_JPDateBilled_To);
			}
			
			if (p_DocumentNo_From != null && p_DocumentNo_To != null)
			{
				sql.append(" AND i.DocumentNo BETWEEN ? AND ?");
				params.add(p_DocumentNo_From);
				params.add(p_DocumentNo_To);
			}
			else if (p_DocumentNo_From != null)
			{
				if (p_DocumentNo_From.indexOf('%') == -1) {
					sql.append(" AND i.DocumentNo >= ?");
				} else {
					sql.append(" AND i.DocumentNo LIKE ?");
				}
				params.add(p_DocumentNo_From);
			}
			else if (p_DocumentNo_To != null)
			{
				if (p_DocumentNo_To.indexOf('%') == -1) {
					sql.append(" AND i.DocumentNo <= ?");
				} else {
					sql.append(" AND i.DocumentNo LIKE ?");
				}
				params.add(p_DocumentNo_To);
			}
			
			if (p_EMailPDF)
			{
				/* if emailed to customer only select COmpleted & CLosed invoices */ 
				sql.append(" AND i.DocStatus IN ('CO','CL') "); 
			}else {
				
				if (p_DocStatus != null)
				{
					sql.append (" AND i.DocStatus=?");
					params.add(p_DocStatus);
				}else {
					sql.append(" AND i.DocStatus IN ('CO','CL') "); 
				}
			}
			
			if (p_C_DocType_ID != 0)
			{
				sql.append (" AND i.C_DocTypeTarget_ID=?");
				params.add(p_C_DocType_ID);
			}
			
			if (p_IsPrinted != null && p_IsPrinted.length() == 1)
			{
				sql.append (" AND i.IsPrinted=?");
				params.add(p_IsPrinted);
			}
			
			if (p_PaymentRule != null)
			{
				sql.append (" AND i.PaymentRule=?");
				params.add(p_PaymentRule);
			}
			
			if (p_C_PaymentTerm_ID != 0)
			{
				sql.append (" AND i.C_PaymentTerm_ID=?");
				params.add(p_C_PaymentTerm_ID);
			}

		}
		
		if(p_AD_Org_ID <= 0)
		{
			String orgWhere = MRole.getDefault(getCtx(), false).getOrgWhere(MRole.SQL_RO);
			if (!Util.isEmpty(orgWhere, true)) {
				orgWhere = orgWhere.replaceAll("AD_Org_ID", "i.AD_Org_ID");
				sql.append(" AND ");
				sql.append(orgWhere);
			}
		}
		sql.append(" ORDER BY i.JP_Bill_ID, pf.AD_Org_ID DESC");	//	more than 1 PF record
	}

	private void attachmentFile(ReportEngine re, String fileName, int AD_Table_ID, int Record_ID) throws Exception
	{
		byte[] fileContent = Files.readAllBytes(re.getPDF().toPath());
		MAttachmentFileRecord afr = new MAttachmentFileRecord(getCtx(),0,get_TrxName());
		afr.setAD_Table_ID(AD_Table_ID);
		afr.setRecord_ID(Record_ID);
		afr.setJP_AttachmentFileName(fileName);
		afr.setJP_MediaContentType("application/pdf");//application/pdf"
		afr.setJP_MediaFormat("pdf");
		afr.upLoadLFile(fileContent);
		afr.saveEx(get_TrxName());
	}
	
	private void setPrinted(int JP_Bill_ID)
	{
		StringBuffer sb = new StringBuffer ("UPDATE JP_Bill "
				+ "SET DatePrinted=getDate(), IsPrinted='Y' , JP_Print_User_ID = ? WHERE JP_Bill_ID=?");
		DB.executeUpdateEx(sb.toString(),new Object[] {getAD_User_ID(),JP_Bill_ID}, get_TrxName());
	}
	
	private void setSendEMail(int JP_Bill_ID)
	{
		StringBuffer sb = new StringBuffer ("UPDATE JP_Bill "
				+ "SET JPDateSent=getDate(),  JP_User_ID = ? WHERE JP_Bill_ID=?");
		DB.executeUpdateEx(sb.toString(),new Object[] {getAD_User_ID(),JP_Bill_ID}, get_TrxName());
	}
	
}	//	Bill Print
