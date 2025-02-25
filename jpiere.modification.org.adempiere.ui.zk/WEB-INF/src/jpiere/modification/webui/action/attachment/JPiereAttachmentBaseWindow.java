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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import org.adempiere.util.Callback;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.DialogEvents;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.adempiere.webui.window.MultiFileDownloadDialog;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Zip;
import org.compiere.model.MOrg;
import org.compiere.model.MQuery;
import org.compiere.model.MRole;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.tools.FileUtil;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentBaseWindow extends Window implements EventListener<Event>{

	private static final long serialVersionUID = -5237020323915426689L;
	protected ADWindow adWindow;
	protected ADWindowContent  adWindowContent;

	protected IADTabbox          	 adTabbox;
	protected int AD_Table_ID = 0;
	protected int Record_ID = 0;
	protected Boolean isAccessEditRecord = true;

	protected Button btnAttachment = new Button();
	protected Button btnExport = new Button();
	protected Button btnZoomAcross = new Button();


	public JPiereAttachmentBaseWindow(ADWindow adWindow, EventListener<Event> eventListener)
	{
		super();

		this.adWindow = adWindow;
		this.adWindowContent = adWindow.getADWindowContent();
		this.adTabbox = adWindowContent.getADTab();
		this.AD_Table_ID =adTabbox.getSelectedGridTab().getAD_Table_ID();
		this.Record_ID =  adTabbox.getSelectedGridTab().getRecord_ID();
		this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, this);
		if (eventListener != null)
		{
			this.addEventListener(DialogEvents.ON_WINDOW_CLOSE, eventListener);
		}


		//MRole role = MRole.getDefault(Env.getCtx(), false);
		MTable m_Table =MTable.get(Env.getCtx(), MAttachmentFileRecord.Table_Name);
		int AD_Window_ID = Env.getZoomWindowID(m_Table.getAD_Table_ID(),  0, 0);
		MRole role = MRole.getDefault(Env.getCtx(), false);
		final Boolean windowAccess = role.getWindowAccess(AD_Window_ID);
		if(windowAccess == null)
		{
			isAccessEditRecord = false;
		}

		setSclass("popup-dialog");

    	this.setTitle(Msg.getMsg(Env.getCtx(), "Attachment").replaceAll("&", "") + ": " + adWindowContent.getTitle());
    	this.setClosable(true);
    	this.setSizable(true);
    	this.setMaximizable(false);

    	Div div = new Div();
    	ZKUpdateUtil.setVflex(div, "0");
    	div.setStyle("padding:6px;");
    	this.appendChild(div);

    	//Attchment Button
        btnAttachment.setAttribute("name","btnAttachment");
        btnAttachment.setSclass("img-btn");
        if (ThemeManager.isUseFontIconForImage())
        	btnAttachment.setIconSclass("z-icon-Attachment");
        else
        	btnAttachment.setImage(ThemeManager.getThemeResource("images/Attachment24.png"));
        btnAttachment.addEventListener(Events.ON_CLICK, this);
        btnAttachment.setId("btnAttachment");
        btnAttachment.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("large-toolbarbutton", btnAttachment);

        div.appendChild(btnAttachment);


    	//DownLoad Button
    	btnExport.setAttribute("name","btnExport");
    	btnExport.setSclass("img-btn");
        if (ThemeManager.isUseFontIconForImage())
        	btnExport.setIconSclass("z-icon-Export");
        else
        	btnExport.setImage(ThemeManager.getThemeResource("images/Export24.png"));
        btnExport.addEventListener(Events.ON_CLICK, this);
        btnExport.setId("btnExport");
        btnExport.setStyle("vertical-align: middle;");
        if (ThemeManager.isUseFontIconForImage())
        	LayoutUtils.addSclass("large-toolbarbutton", btnExport);

        div.appendChild(btnExport);

    	//Zoom Across Button
        if(isAccessEditRecord)
        {
	        btnZoomAcross.setSclass("img-btn");
	    	btnZoomAcross.setAttribute("name","btnZoomAcross");
	        if (ThemeManager.isUseFontIconForImage() && isAccessEditRecord)
	        {
	        	btnZoomAcross.setIconSclass("z-icon-Edit");
	        }else {
	        	btnZoomAcross.setImage(ThemeManager.getThemeResource("images/Editor24.png"));//Editor24.png or ZoomAcross24.png
	        }

	        btnZoomAcross.addEventListener(Events.ON_CLICK, this);
	        btnZoomAcross.addEventListener(Events.ON_CLICK, this);
	        btnZoomAcross.setId("btnZoomAcross");
	        btnZoomAcross.setStyle("vertical-align: middle;");
	        if (ThemeManager.isUseFontIconForImage())
	        	LayoutUtils.addSclass("large-toolbarbutton", btnZoomAcross);

	        div.appendChild(btnZoomAcross);
        }


        createAttachemntBaseWindowGridView(div);



        ZKUpdateUtil.setWidth(this, "560px");
//        ZKUpdateUtil.setHeight(this, "250px");


        this.setClientAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "findWindow");
        this.setId("findWindow_"+adWindowContent.getWindowNo());
        LayoutUtils.addSclass("find-window", this);
        this.setZindex(100);

        adWindowContent.getComponent().getParent().appendChild(this);
        adWindowContent.showBusyMask(this);


        ToolBarButton toolbarButton =  adWindowContent.getToolbar().getButton("JPiereAttachment");
        LayoutUtils.openOverlappedWindow(toolbarButton, this, "after_start");

	}

	Grid grid =  null;
	private static final int DEFAULT_PAGE_SIZE = 5;

	private void createAttachemntBaseWindowGridView(Div div)
	{
		ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
		JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList,adWindow);
		JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);

//		listModel.addTableModelListener(this);

		if(attachmentFileRecordList != null)
		{
	        //Grid
			Div divGrid = new Div();
	    	ZKUpdateUtil.setVflex(divGrid, "0");
//	    	divGrid.setStyle("padding: 5px");
	    	divGrid.setStyle("margin: 6px");
	    	this.appendChild(divGrid);

	        grid = new Grid();
	        divGrid.appendChild(grid);

	        grid.setModel(listModel);

	        JPiereAttachmentFileRecordRenderer renderer = new JPiereAttachmentFileRecordRenderer(listModel,isAccessEditRecord, this);

	        grid.setRowRenderer(renderer);
	        grid.setMold("paging");
	        int pageSize = MSysConfig.getIntValue("JPIERE_ATTACHMENT_FILE_RECORD_PAGING_SIZE", DEFAULT_PAGE_SIZE, Env.getAD_Client_ID(Env.getCtx()));
	        grid.setPageSize(pageSize);

	        if(attachmentFileRecordList.size() == 0)
	        {
	        	btnExport.setDisabled(true);
				if(isAccessEditRecord)
		        {
					btnZoomAcross.setDisabled(true);
		        }
	        }
		}

	}


	@Override
	public void onClose()
	{
		grid = null;
		if(attachmentWindow != null)
		{
			attachmentWindow.onClose();
		}

		adWindowContent.hideBusyMask();
		super.onClose();
	}


	JPiereAttachmentWindow attachmentWindow = null;

	@Override
	public void onEvent(Event event) throws Exception
	{
		Object target = event.getTarget();

		if(target instanceof Button)
		{
			Button btn = (Button)target;
			if(btn.getId().equals("btnAttachment"))
			{
				EventListener<Event> listener = new EventListener<Event>()
				{
					@Override
					public void onEvent(Event event) throws Exception {
//						toolbar.getButton("Attachment").setPressed(adTabbox.getSelectedGridTab().hasAttachment());
//						focusToActivePanel();
					}
				};

				if(attachmentWindow == null)
				{
					attachmentWindow = new JPiereAttachmentWindow (adWindow, null, listener);
					attachmentWindow.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception
						{
							ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
							JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList, adWindow);
							JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);
							if(grid != null)
							{
								grid.setModel(listModel);


						        if(attachmentFileRecordList.size() == 0)
						        {
						        	btnExport.setDisabled(true);
									if(isAccessEditRecord)
							        {
										btnZoomAcross.setDisabled(true);
							        }

						        }else {

						        	btnExport.setDisabled(false);
									if(isAccessEditRecord)
							        {
										btnZoomAcross.setDisabled(false);
							        }
						        }

							}

						}
					});

					this.getParent().appendChild(attachmentWindow);
				}

				LayoutUtils.openOverlappedWindow(btn, attachmentWindow, "after_pointer");
				attachmentWindow.focus();

				return;

			}else if(btn.getId().equals("btnExport")) {

				//Do you compress attachment files by ZIP?
				Dialog.ask(adWindowContent.getWindowNo(), "JP_AttachemntDownloadZIP", Msg.getMsg(Env.getCtx(), "JP_AttachemntDownloadZIP_Description"), new Callback<Boolean>() {

					@Override
					public void onCallback(Boolean result)
					{
						if (result)
						{
							createDownloadZipFiles();
						}
						else
						{
							createDownloadFileList();
						}
					}
				});

			}else if(btn.getId().equals("btnZoomAcross")) {

				MQuery query = new MQuery(MAttachmentFileRecord.Table_Name);
				query.addRestriction("AD_Table_ID", MQuery.EQUAL, AD_Table_ID);
				query.addRestriction("Record_ID", MQuery.EQUAL, Record_ID);
				AEnv.zoom(query);

			}
		}


	}

	private void createDownloadZipFiles()
	{
		ArrayList<File> downloadFiles = new ArrayList<File>();
		ArrayList<MJPiereStorageProvider>  attachmentStorageProviderList = MAttachmentFileRecord.getAttachmentStorageProviderList(Env.getCtx(), AD_Table_ID, Record_ID, null);
		ArrayList<MOrg>  attachmentFileOrgList = MAttachmentFileRecord.getAttachmentFileOrgList(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
		
		int i = 1;
		for(MJPiereStorageProvider attachmentStorageProvider: attachmentStorageProviderList)
		{			
			for(MOrg org :attachmentFileOrgList)
			{
				HashSet<String> directoryList = MAttachmentFileRecord.getAttachmentDirectory(Env.getCtx(),AD_Table_ID, Record_ID, org.getAD_Org_ID(), attachmentStorageProvider, null);
				if(directoryList.size() == 0)
					continue;
				
				for(String directory : directoryList)
				{
					File srcFolder = new File(directory);
					File destZipFile = null;
					destZipFile = new File(srcFolder + File.separator + String.valueOf(i) + "_" + org.getValue()+".zip");
									
					if(destZipFile.exists())
					{
						try {
							FileUtil.deleteFolderRecursive(destZipFile);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
		
					//create the compressed packages
					Zip zipper = new Zip();
				    zipper.setDestFile(destZipFile);
				    zipper.setBasedir(srcFolder);
				    //zipper.setIncludes(includesdir.replace(" ", "*"));
				    zipper.setUpdate(true);
				    zipper.setCompress(true);
				    zipper.setCaseSensitive(false);
				    zipper.setFilesonly(false);
				    zipper.setTaskName("zip");
				    zipper.setTaskType("zip");
				    zipper.setProject(new Project());
				    zipper.setOwningTarget(new Target());
				    zipper.execute();
					downloadFiles.add(destZipFile);
					i++;
				}
			}
		}

		MultiFileDownloadDialog downloadDialog = new MultiFileDownloadDialog(downloadFiles.toArray(new File[downloadFiles.size()]));
		downloadDialog.setPage(adWindow.getComponent().getPage());
		downloadDialog.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
		downloadDialog.addEventListener(DialogEvents.ON_WINDOW_CLOSE, new EventListener<Event>()
		{
			@Override
			public void onEvent(Event event) throws Exception
			{
				for(File file : downloadFiles)
				{
					try {
						FileUtil.deleteFolderRecursive(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

			}
		});

		Events.postEvent(downloadDialog, new Event(MultiFileDownloadDialog.ON_SHOW));

		return ;
	}

	private void createDownloadFileList()
	{
		ArrayList<File> downloadFiles = new ArrayList<File>();
		ListModel<Object> model = grid.getModel();
		for(int i = 0; i < model.getSize(); i++)
		{
			Object[] row = (Object[] )model.getElementAt(i);
			Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
			MAttachmentFileRecord  attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(),JP_AttachmentFileRecord_ID.intValue(), null);
			File downloadFile = new File(attachmentFileRecord.getFileAbsolutePath());
			if(!downloadFile.exists())
				continue;

			downloadFiles.add(downloadFile);
		}

		MultiFileDownloadDialog downloadDialog = new MultiFileDownloadDialog(downloadFiles.toArray(new File[downloadFiles.size()]));
		downloadDialog.setPage(adWindow.getComponent().getPage());
		downloadDialog.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
		Events.postEvent(downloadDialog, new Event(MultiFileDownloadDialog.ON_SHOW));
	}

}
