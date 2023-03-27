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
package jpiere.modification.webui.window;

import java.util.List;

import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.window.Dialog;
import org.adempiere.webui.window.ZkReportViewer;
import org.compiere.model.MClientInfo;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.print.ReportEngine;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbar;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0598: JPiere Attachment File at Report
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereZkReportViewer extends ZkReportViewer {

	protected ToolBarButton		btn_JPiereAttachment			= new ToolBarButton();
	protected ReportEngine 		m_reportEngine;
	
	public JPiereZkReportViewer(ReportEngine re, String title) {
		super(re, title);
		m_reportEngine = re;
	}
	
	private boolean init;

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		if (newpage != null && !init) {
			try
			{
				init();
			}
			catch(Exception e)
			{
				//log.log(Level.SEVERE, "", e);
				Dialog.error(m_WindowNo, "LoadError", e.getLocalizedMessage());
				this.onClose();
			}
		}
	}
	
	private void init()
	{
		init = true;
		
		Toolbar toolbar = null;
		List<Component> children = getChildren();
		for(Component comp : children)
		{
			if(comp instanceof Borderlayout)
			{
				Borderlayout borderlyaout = (Borderlayout)comp;
				North north = borderlyaout.getNorth();
				List<Component> north_Children = north.getChildren();
				for(Component child : north_Children)
				{
					if( child instanceof Toolbar)
					{
						toolbar = (Toolbar)child;
						break;
					}
				}
				
				break;
			}
		}
		
		if(toolbar == null)
		{
			return ;
		}
		
		int tableId = m_reportEngine.getPrintInfo().getAD_Table_ID();
		int recordId = m_reportEngine.getPrintInfo().getRecord_ID();
		if (tableId > 0 && recordId > 0) {
			toolbar.appendChild(new Separator("vertical"));
			btn_JPiereAttachment.setName("JPiereAttachment");
			if (ThemeManager.isUseFontIconForImage())
				btn_JPiereAttachment.setIconSclass("z-icon-Attachment");
			else
				btn_JPiereAttachment.setImage(ThemeManager.getThemeResource("images/JPiereAttachment24.png"));
			btn_JPiereAttachment.setTooltiptext(Util.cleanAmp(Msg.getMsg(Env.getCtx(), "Attachment")));
			toolbar.appendChild(btn_JPiereAttachment);
			btn_JPiereAttachment.addEventListener(Events.ON_CLICK, this);
		}
	}

	@Override
	public void onEvent(Event event) throws Exception 
	{
		super.onEvent(event);
		if (event.getName().equals(Events.ON_CLICK))
		{
			if (event.getTarget() == btn_JPiereAttachment)
				cmd_JPiereAttachment();
		}
	}

	protected void cmd_JPiereAttachment()
	{		
		int tableId = m_reportEngine.getPrintInfo().getAD_Table_ID();
		int recordId = m_reportEngine.getPrintInfo().getRecord_ID();
		if (tableId == 0 || recordId == 0)
			return;
		
		
		 MClientInfo clientInfo = MClientInfo.get(Env.getCtx());
		 
		 if(clientInfo.get_ValueAsInt("JP_StorageAttachment_ID")  == 0)
		 {
			 Dialog.error(0, "Error", Msg.getMsg(Env.getCtx(), "NotFound")
					 + System.lineSeparator() + Msg.getElement(Env.getCtx(), "JP_StorageAttachment_ID"));
			 return ;
		 }
		
		
		MTable table = MTable.get(tableId);
		PO po = table.getPO(recordId, null);
		
		MAttachmentFileRecord m_attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(), 0, null);
		m_attachmentFileRecord.setAD_Table_ID(tableId);
		m_attachmentFileRecord.setRecord_ID(recordId);
		m_attachmentFileRecord.setJP_AttachmentFileName(media.getName());
		//m_attachmentFileRecord.setJP_AttachmentFileDescription(JP_AttachmentFileDescription.getValue());
		m_attachmentFileRecord.setJP_MediaContentType(media.getContentType());
		m_attachmentFileRecord.setJP_MediaFormat(media.getFormat());
		m_attachmentFileRecord.set_ValueNoCheck("AD_Org_ID", po.getAD_Org_ID());
		
		btn_JPiereAttachment.setDisabled(true);
		btn_JPiereAttachment.setPressed(true);
		
		byte[] data = media.isBinary() ? media.getByteData() : media.getStringData().getBytes();
		if(m_attachmentFileRecord.upLoadLFile(data))
		{
			Dialog.info(m_WindowNo, "DocumentAttached", m_attachmentFileRecord.getJP_AttachmentFileName());
			
		}else {

			Dialog.error(m_WindowNo, "Error", Msg.getMsg(Env.getCtx(), "JP_UnexpectedError"));
		}
		
		return ;
	}
}
