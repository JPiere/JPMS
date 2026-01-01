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
import org.adempiere.webui.window.ZkJRViewer;
import org.compiere.model.MClientInfo;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.PO;
import org.compiere.model.PrintInfo;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.North;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;
import net.sf.jasperreports.engine.JasperPrint;


/**
*
* JPIERE-0599: JPiere JasperReport Viewer
* *JPiere Attachment File at Report
* *Hide Non Active ToolBar iCon
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereZkJRViewer extends ZkJRViewer {

	private static final long serialVersionUID = 6294105846429782339L;
	protected ToolBarButton		btn_JPiereAttachment = new ToolBarButton();
	private PrintInfo			m_printInfo;
	
	public JPiereZkJRViewer(JasperPrint jasperPrint, String title, PrintInfo printInfo) 
	{
		super(jasperPrint, title, printInfo);
		m_printInfo = printInfo;
		
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
		
		int tableId = printInfo.getAD_Table_ID();
		int recordId = printInfo.getRecord_ID();
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
			btn_JPiereAttachment.setPressed(false);
		}
		
		//JPIERE-Hide Non Active ToolBar iCon-START
		getNonActiveToolbarButtons();
		for(MToolBarButton nonActiveButton : nonActivebuttons)
		{
			String cName =nonActiveButton.getComponentName();
			for (Component p = toolbar.getFirstChild(); p != null; p = p.getNextSibling()) 
			{
				if (p instanceof Toolbarbutton) 
				{
					String pName = ((ToolBarButton)p).getName();
					if (cName.equals(pName) ) 
					{
						toolbar.removeChild(p);
						break;
					}
				}
			}
		}//JPIERE-Hide Non Active ToolBar iCon-END
	}

	public JPiereZkJRViewer(List<JasperPrint> jasperPrintList, String title, PrintInfo printInfo) 
	{
		super(jasperPrintList, title, printInfo);
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
		if(btn_JPiereAttachment.isPressed())
			return;
		
		int tableId = m_printInfo.getAD_Table_ID();
		int recordId = m_printInfo.getRecord_ID();
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
		AMedia media = getMedia(getContentType(), getFileExtension());
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
			Dialog.info(0, "DocumentAttached", m_attachmentFileRecord.getJP_AttachmentFileName());
			
		}else {

			Dialog.error(0, "Error", Msg.getMsg(Env.getCtx(), "JP_UnexpectedError"));
		}
		
	}
	
	/**
	 * JPIERE-Non Active ToolBar iCon
	 */
	private static MToolBarButton[] nonActivebuttons = null;
	private static MToolBarButton[] getNonActiveToolbarButtons()
	{
		if(nonActivebuttons != null)
			return nonActivebuttons;
		
		Query query = new Query(Env.getCtx(), MTable.get(Env.getCtx(), MToolBarButton.Table_ID),
				"Action='R' AND (AD_ToolbarButton_ID<=? OR ActionClassName IS NOT NULL) AND AD_Tab_ID IS NULL AND IsActive='N' ", null);
		
		List<MToolBarButton> list = query.setParameters(MTable.MAX_OFFICIAL_ID)
				.setOrderBy("CASE WHEN COALESCE(SeqNo,0)=0 THEN AD_ToolBarButton_ID ELSE SeqNo END")
				.list();
		
		nonActivebuttons = new MToolBarButton[list.size()];
		list.toArray(nonActivebuttons);
		
		return nonActivebuttons;
	}
	
}
