package jpiere.modification.webui.apps.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.Extensions;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.WTextEditorDialog;
import org.compiere.model.MSysConfig;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Ini;
import org.compiere.util.Msg;
import org.idempiere.ui.zk.media.IMediaView;
import org.idempiere.ui.zk.media.Medias;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;



public class AttachmentFileViewer extends CustomForm implements EventListener<Event>
{
	private static CLogger log = CLogger.getCLogger(AttachmentFileViewer.class);

	private Iframe preview = new Iframe();

	private Panel previewPanel = new Panel();

	private Borderlayout mainPanel = new Borderlayout();

	private String orientation;

	private int windowNo = 0;
	private int Record_ID = 0;
	private MAttachmentFileRecord attachmentFileRecord;

	private boolean isFileLoad = false;

	private Listbox fCharset = new Listbox();

	private static List<String> autoPreviewList;
	static {
		autoPreviewList = new ArrayList<String>();
		autoPreviewList.add("image/jpeg");
		autoPreviewList.add("image/png");
		autoPreviewList.add("image/gif");
		autoPreviewList.add("text/plain");
		autoPreviewList.add("application/pdf");
		autoPreviewList.add("text/html");
		autoPreviewList.add(Medias.CSV_MIME_TYPE);
		autoPreviewList.add(Medias.EXCEL_MIME_TYPE);
		autoPreviewList.add(Medias.EXCEL_XML_MIME_TYPE);
	}

	
	private int maxPreviewSize;

	private Component customPreviewComponent;
	
	/**
	 *
	 */
	private static final long serialVersionUID = -8498084996736578534L;

	public AttachmentFileViewer(EventListener<Event> eventListener)
	{
		super();
		maxPreviewSize = MSysConfig.getIntValue(MSysConfig.ZK_MAX_ATTACHMENT_PREVIEW_SIZE, 1048576, Env.getAD_Client_ID(Env.getCtx()));

		this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, this);
		if (eventListener != null)
		{
			this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, eventListener);
		}

	}

	public AttachmentFileViewer()
	{
		;
	}

	@Override
	protected void initForm()
	{
		;
	}

	@Override
	public Mode getWindowMode()
	{
		return Mode.HIGHLIGHTED;
	}

	@Override
	public void setProcessInfo(ProcessInfo pi)
	{
		if(pi == null)
			return ;
		
		super.setProcessInfo(pi);
		Record_ID = getProcessInfo().getRecord_ID();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setRecord_ID(int Record_ID)
	{
		this.Record_ID = Record_ID;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void init() throws Exception
	{
		attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(), Record_ID, null);

		Charset charset = null;
		if(attachmentFileRecord.getJP_MediaContentType().equals("text/plain"))
		{
			Charset[] charsets = Ini.getAvailableCharsets();

			for (int i = 0; i < charsets.length; i++)
				fCharset.appendItem(charsets[i].displayName(), charsets[i]);

			fCharset.setMold("select");
			fCharset.setRows(0);
			fCharset.setTooltiptext(Msg.getMsg(Env.getCtx(), "Charset", false));

			charset = Ini.getCharset();
			for (int i = 0; i < fCharset.getItemCount(); i++)
			{
				ListItem listitem = fCharset.getItemAtIndex(i);
				Charset compare = (Charset)listitem.getValue();

				if (charset == compare)
				{
					fCharset.setSelectedIndex(i);
					break;
				}
			}

			fCharset.addEventListener(Events.ON_SELECT, this);
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.setStyle("padding:4px;");
			hbox.appendChild(new Label(Msg.getElement(Env.getCtx(), "CharacterSet")));
			hbox.appendChild(fCharset);
			this.appendChild(hbox);
		}

		this.setAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "attachment");
		this.setMaximizable(true);

		ZKUpdateUtil.setWidth(this, "50%");
		ZKUpdateUtil.setHeight(this, "80%");


		this.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
		this.setClosable(true);
		this.setSizable(true);
		this.setBorder("normal");
		this.setSclass("popup-dialog attachment-dialog");
		this.setShadow(true);
		this.appendChild(mainPanel);
		ZKUpdateUtil.setHeight(mainPanel, "100%");
		ZKUpdateUtil.setWidth(mainPanel, "100%");

		previewPanel.appendChild(preview);
		ZKUpdateUtil.setVflex(preview, "1");
		ZKUpdateUtil.setHflex(preview, "1");


		File file = new File(attachmentFileRecord.getFileAbsolutePath());
		AMedia media = null;
		try {

			if(attachmentFileRecord.getJP_MediaContentType().equals("text/plain"))
			{
				media = new AMedia(attachmentFileRecord.getJP_AttachmentFileName(),attachmentFileRecord.getJP_MediaFormat()
						,attachmentFileRecord.getJP_MediaContentType(),file,charset.name());//shift-jis or UTF-8

			}else {

				media = new AMedia(attachmentFileRecord.getJP_AttachmentFileName(),attachmentFileRecord.getJP_MediaFormat()
					,attachmentFileRecord.getJP_MediaContentType(),file,true);

			}

		}catch (FileNotFoundException e) {

			FDialog.error(windowNo, this, "AttachmentNotFound", e.toString());
			this.dispose();
			log.saveError("Error", e);

		}catch (Exception e) {

			FDialog.error(windowNo, this, "Error", e.toString());
			this.dispose();
			log.saveError("Error", e);
		}

		if(media == null)
			return ;

		IMediaView view = Extensions.getMediaView(attachmentFileRecord.getJP_MediaContentType()
									, getExtension(attachmentFileRecord.getJP_AttachmentFileName()), ClientInfo.isMobile());
		
		if (view != null) 
		{
			
			if (media.getByteData().length <= maxPreviewSize)
			{
		
				try {
					customPreviewComponent = view.renderMediaView(previewPanel, media, true);
					previewPanel.appendChild(customPreviewComponent);
				} catch (Exception e) {
					log.warning("Error previewing file in attachment entry " + attachmentFileRecord.getJP_AttachmentFileName() + " -> " + e.getLocalizedMessage());
					e.printStackTrace();
					String msg = WTextEditorDialog.sanitize(Msg.getMsg(Env.getCtx(), "ErrorPreviewingFile"));
					Media mediaErr = new AMedia(null, null, "text/html", msg.getBytes());
					preview.setContent(mediaErr);
					preview.setVisible(true);
					preview.invalidate();	
				}
				
			}else {
				
				String msg = WTextEditorDialog.sanitize(Msg.getMsg(Env.getCtx(), "ErrorPreviewingFile") + " Please Check MAX_ATTACHMENT_PREVIEW_SIZE");
				Media mediaErr = new AMedia(null, null, "text/html", msg.getBytes());
				preview.setContent(mediaErr);
				preview.setVisible(true);
				preview.invalidate();	
				
			}
			
		}else {
		
			preview.setContent(media);
			preview.setVisible(true);
			preview.invalidate();		
			
		}
				
		isFileLoad = true;

		Center centerPane = new Center();
		centerPane.setSclass("dialog-content");
		//centerPane.setAutoscroll(true); // not required the preview has its own scroll bar
		mainPanel.appendChild(centerPane);
		centerPane.appendChild(previewPanel);
		ZKUpdateUtil.setVflex(previewPanel, "1");
		ZKUpdateUtil.setHflex(previewPanel, "1");

		if (ClientInfo.isMobile())
		{
			orientation = ClientInfo.get().orientation;
			ClientInfo.onClientInfo(this, this::onClientInfo);
		}

	}
	
	private String getExtension(String name) {
		int index = name.lastIndexOf(".");
		if (index > 0) {
			return name.substring(index+1);
		}
		return "";
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


	public void dispose ()
	{
		preview = null;
		this.detach();
	} // dispose


	public boolean isFileLoad()
	{
		return isFileLoad;
	}


	public void onEvent(Event e)
	{
		//	Save and Close
		if (DialogEvents.ON_WINDOW_CLOSE.equals(e.getName()))
		{
			dispose();

		}else if (e.getTarget() == fCharset) {

			ListItem listitem = fCharset.getSelectedItem();
			if (listitem == null)
				return;

			Charset charset = (Charset)listitem.getValue();

			File file = new File(attachmentFileRecord.getFileAbsolutePath());
			AMedia media = null;

			Component mimeType = e.getTarget();
//			if (autoPreviewList.contains(mimeType))
//			{
//				
//			}
			try {
				media = new AMedia(attachmentFileRecord.getJP_AttachmentFileName(),attachmentFileRecord.getJP_MediaFormat()
						,attachmentFileRecord.getJP_MediaContentType(),file,charset.name());
			} catch (FileNotFoundException e1) {
				;
			}

			preview.setContent(media);

		}

	}	//	onEvent

}
