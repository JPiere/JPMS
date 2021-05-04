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

package jpiere.modification.webui.action.attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Vlayout;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentWindow extends Window implements EventListener<Event>,ValueChangeListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4311076973993361653L;

	private static CLogger log = CLogger.getCLogger(JPiereAttachmentWindow.class);

	/** Attachment File Record	*/
	private MAttachmentFileRecord m_attachmentFileRecord = null;


	private Iframe preview = new Iframe();

	private Textbox JP_AttachmentFileDescription = new Textbox();

	private Button bLoad = new Button();

	private Borderlayout mainPanel = new Borderlayout();

	private Hbox toolBar = new Hbox();

	private Hlayout confirmPanel = new Hlayout();

	private String orientation;

	private static List<String> autoPreviewList;

	static {
		autoPreviewList = new ArrayList<String>();
		autoPreviewList.add("image/jpeg");
		autoPreviewList.add("image/png");
		autoPreviewList.add("image/gif");
		autoPreviewList.add("text/plain");
		autoPreviewList.add("application/pdf");
		autoPreviewList.add("text/html");
	}


	protected ADWindow adWindow;
	protected ADWindowContent  adWindowContent;
	protected IADTabbox adTabbox;
	protected int AD_Table_ID = 0;
	protected int Record_ID = 0;
	protected int windowNo = 0;

	public JPiereAttachmentWindow(ADWindow adWindow, String trxName, EventListener<Event> eventListener)
	{
		super();

		this.adWindow = adWindow;
		this.adWindowContent = adWindow.getADWindowContent();
		this.adTabbox = adWindowContent.getADTab();
		this.AD_Table_ID =adTabbox.getSelectedGridTab().getAD_Table_ID();
		this.Record_ID =  adTabbox.getSelectedGridTab().getRecord_ID();
		this.windowNo = adWindowContent.getWindowNo();

		this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, this);
		if (eventListener != null)
		{
			this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, eventListener);
		}

		try
		{
			staticInit();
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, "", ex);
		}


		try
		{
			setAttribute(Window.MODE_KEY, Window.MODE_HIGHLIGHTED);

		}
		catch (Exception e)
		{
		}

	} // JPiereAttachmentWindow


	Label orgLabel = new Label();
	WSearchEditor orgEditor;

	void staticInit() throws Exception
	{

		this.setAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "attachment");
//		this.setMaximizable(true);
		if (!ThemeManager.isUseCSSForWindowSize())
		{
			ZKUpdateUtil.setWindowWidthX(this, 600);
			ZKUpdateUtil.setHeight(this, "40%");
		}
		else
		{
			addCallback(AFTER_PAGE_ATTACHED, t -> {
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
			});
		}
		this.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
		this.setClosable(true);
		this.setSizable(true);
		this.setBorder("normal");
//		this.setSclass("popup-dialog attachment-dialog");
		this.setSclass("popup-dialog");
		this.setShadow(true);
		this.appendChild(mainPanel);
		ZKUpdateUtil.setHeight(mainPanel, "100%");
		ZKUpdateUtil.setWidth(mainPanel, "100%");

		ZKUpdateUtil.setHeight(this, "250px");
		ZKUpdateUtil.setWidth(this, "560px");


		North northPanel = new North();
		northPanel.setStyle("padding: 4px");
		northPanel.setCollapsible(false);
		northPanel.setSplittable(false);


		int AD_Column_ID = 0;

		Integer AD_Org_ID = (Integer)adTabbox.getSelectedGridTab().getValue("AD_Org_ID");
		// Initialization of Org
		AD_Column_ID = MColumn.getColumn_ID("C_Invoice", "AD_Org_ID");
		MLookup lookupOrg = MLookupFactory.get(Env.getCtx(), windowNo, 0, AD_Column_ID,  DisplayType.Search);
		orgEditor = new WSearchEditor("Invoice_Org_ID", true, false, true, lookupOrg);
		orgEditor.setValue(AD_Org_ID.intValue());
		orgEditor.addValueChangeListener(this);

		orgLabel.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		toolBar.appendChild(orgLabel.rightAlign());
		ZKUpdateUtil.setHflex(orgEditor.getComponent(), "true");
		toolBar.appendChild(orgEditor.getComponent());

		mainPanel.appendChild(northPanel);
		Vlayout div = new Vlayout();
		div.appendChild(toolBar);
		JP_AttachmentFileDescription.setRows(3);
		ZKUpdateUtil.setHflex(JP_AttachmentFileDescription, "2");
		northPanel.appendChild(div);


		div.appendChild(new Label(Msg.getElement(Env.getCtx(), "JP_AttachmentFileDescription")));
		div.appendChild(JP_AttachmentFileDescription);



		Center centerPane = new Center();
		centerPane.setSclass("dialog-content");


		South southPane = new South();
		southPane.setSclass("dialog-footer");
		mainPanel.appendChild(southPane);
		southPane.appendChild(confirmPanel);
		ZKUpdateUtil.setVflex(southPane, "min");


		ZKUpdateUtil.setHflex(confirmPanel, "1");
		Hbox hbox = new Hbox();
		hbox.setPack("end");
		ZKUpdateUtil.setHflex(hbox, "1");
		confirmPanel.appendChild(hbox);
		hbox.appendChild(bLoad);
		if (ThemeManager.isUseFontIconForImage())
			bLoad.setIconSclass("z-icon-Import");
		else
			bLoad.setImage(ThemeManager.getThemeResource("images/Import24.png"));
		bLoad.setSclass("img-btn");
		bLoad.setId("bLoad");
//		bLoad.setAttribute("org.zkoss.zul.image.preload", Boolean.TRUE);
		bLoad.setTooltiptext(Msg.getMsg(Env.getCtx(), "Load"));
		bLoad.setUpload("multiple=true," + AdempiereWebUI.getUploadSetting());
		bLoad.setLabel(Msg.getMsg(Env.getCtx(), "JP_Upload"));
		bLoad.addEventListener(Events.ON_UPLOAD, this);

		JP_AttachmentFileDescription.setTooltiptext(Msg.getElement(Env.getCtx(), "TextMsg"));

		if (ClientInfo.isMobile())
		{
			orientation = ClientInfo.get().orientation;
			ClientInfo.onClientInfo(this, this::onClientInfo);
		}
	}

	protected void onClientInfo()
	{
		if (getPage() != null)
		{
			String newOrienation = ClientInfo.get().orientation;
			if (!newOrienation.equals(orientation))
			{
				orientation = newOrienation;
				ZKUpdateUtil.setCSSHeight(this);
				ZKUpdateUtil.setCSSWidth(this);
				invalidate();
			}
		}
	}

	/**
	 * 	Dispose
	 */

	public void dispose ()
	{
		this.detach();
	} // dispose


	public void onEvent(Event e)
	{
		//	Save and Close
		if (e instanceof UploadEvent)
		{
			preview.setVisible(false);
			UploadEvent ue = (UploadEvent) e;
			for (Media media : ue.getMedias()) {
				processUploadMedia(media);
			}

			dispose();

		} else if (DialogEvents.ON_WINDOW_CLOSE.equals(e.getName())) {
			if (m_attachmentFileRecord != null)
			{
				dispose();
			}
		}

	}	//	onEvent

	private void processUploadMedia(Media media)
	{
		if (media != null && media.getByteData().length>0)
		{
			;//Noting to do;
		}
		else
		{
			return;
		}

		String blackFiles = MSysConfig.getValue("JP_ATTACHMENT_BLACK_FILE_LIST", "NONE");
		if(!blackFiles.equalsIgnoreCase("NONE"))
		{
			String[] extensions =  blackFiles.split(",");
			for(int i = 0; i < extensions.length; i++)
			{
				if(extensions[i].equalsIgnoreCase(media.getFormat()))
				{
					//This extension of file can not upload.
					FDialog.error(0, "Error", media.getFormat() + " - " +"JP_CanNotUploadFileExtension");
					return;
				}
			}
		}

		String whiteFiles = MSysConfig.getValue("JP_ATTACHMENT_WHITE_FILE_LIST", "NONE");
		if(!whiteFiles.equalsIgnoreCase("NONE"))
		{
			String[] extensions =  whiteFiles.split(",");
			boolean isOK = false;
			for(int i = 0; i < extensions.length; i++)
			{
				if(extensions[i].equalsIgnoreCase(media.getFormat()))
				{
					isOK = true;
					break;
				}
			}

			if(!isOK)
			{
				//This extension of file can not upload.
				FDialog.error(0, "Error", media.getFormat() + " - " +"JP_CanNotUploadFileExtension");
				return;
			}
		}

		m_attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(), 0, null);
		m_attachmentFileRecord.setAD_Table_ID(AD_Table_ID);
		m_attachmentFileRecord.setRecord_ID(Record_ID);
		m_attachmentFileRecord.setJP_AttachmentFileName(media.getName());
		m_attachmentFileRecord.setJP_AttachmentFileDescription(JP_AttachmentFileDescription.getValue());
		m_attachmentFileRecord.setJP_MediaContentType(media.getContentType());
		m_attachmentFileRecord.setJP_MediaFormat(media.getFormat());

		GridTab mTab = adTabbox.getSelectedGridTab();
		GridField[] fields = mTab.getFields();
		String columnName = null;
		int columnIndex = -1;
		Object objectValue = null;
		for(int i = 0 ; i < fields.length; i++)
		{
			columnName = fields[i].getColumnName();
			columnIndex = -1;
			objectValue = null;
			if(columnName.equals("JP_AttachmentFileRecord_ID")
					|| columnName.equals("AD_Client_ID")
//					|| columnName.equals("AD_Org_ID")
					|| columnName.equals("Created")
					|| columnName.equals("CreatedBy")
					|| columnName.equals("Updated")
					|| columnName.equals("UpdatedBy")
				)
			{
				continue;
			}

			columnIndex = m_attachmentFileRecord.get_ColumnIndex(columnName);
			if(columnIndex > -1)
			{
				if(columnName.equals("AD_Org_ID"))
				{
					m_attachmentFileRecord.set_ValueNoCheck("AD_Org_ID", orgEditor.getValue());


				}else {

					objectValue = mTab.getValue(columnName);
					if(objectValue != null)
					{
						m_attachmentFileRecord.set_ValueNoCheck(columnName, objectValue);
					}

				}

			}else if(columnIndex == -1) {

				 if(columnName.equals("DateOrdered")
						|| columnName.equals("MovementDate")
						|| columnName.equals("DateInvoiced")
						|| columnName.equals("DateTrx")
						|| columnName.equals("StatementDate"))
				 {
					if(mTab.getTableModel().getPO(0).get_ColumnIndex("DateDoc") == -1)
					{
						objectValue = mTab.getValue(columnName);
						if(objectValue != null)
						{
							m_attachmentFileRecord.set_ValueNoCheck("DateDoc", objectValue);
						}
					}
				 }
			}

		}//for

		m_attachmentFileRecord.upLoadLFile(media.getByteData());
		String fileName = media.getName();

		log.config(fileName);

	}

	@Override
	public void onClose()
	{
		super.onClose();
	}


	@Override
	public void valueChange(ValueChangeEvent evt)
	{


	}
}
