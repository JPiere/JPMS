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
import java.util.ArrayList;
import java.util.List;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ToolBarButton;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.MultiFileDownloadDialog;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.idempiere.ui.zk.media.Medias;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;
import jpiere.modification.webui.apps.form.AttachmentFileViewer;



/**
 *
 * JPIERE-0436: JPiere Attachment File
 *
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereAttachmntFileRecordRenderer implements RowRenderer<Object[]> ,RowRendererExt, RendererCtrl,EventListener<Event>
{

	private JPiereAttachmentFileRecordListModel listModel;
	private RowListener rowListener;
	private ADWindow adWindow;

	private static List<String> autoPreviewList;
	private Boolean isAccessEditRecord = false;

	private JPiereAttchmentBaseWindow baseWindow;

	static {
		autoPreviewList = new ArrayList<String>();
		autoPreviewList.add("image/jpeg");
		autoPreviewList.add("image/png");
		autoPreviewList.add("image/gif");
		autoPreviewList.add("text/plain");
		autoPreviewList.add("application/pdf");
		autoPreviewList.add("text/html");
		autoPreviewList.add("application/json");
		//autoPreviewList.add(Medias.CSV_MIME_TYPE);
		//autoPreviewList.add(Medias.EXCEL_MIME_TYPE);
		autoPreviewList.add(Medias.EXCEL_XML_MIME_TYPE);
	}

	public JPiereAttachmntFileRecordRenderer(JPiereAttachmentFileRecordListModel listModel, Boolean isAccessEditRecord, JPiereAttchmentBaseWindow baseWindow)
	{
		this.listModel = listModel;
		this.isAccessEditRecord = isAccessEditRecord;
		this.baseWindow = baseWindow;
	}

	@Override
	public void onEvent(Event arg0) throws Exception
	{
		;
	}

	@Override
	public void doCatch(Throwable var1) throws Throwable
	{
		;
	}

	@Override
	public void doFinally()
	{
		;
	}

	@Override
	public void doTry()
	{
		;
	}

	@Override
	public int getControls()
	{
		return DETACH_ON_RENDER;
	}

	@Override
	public Component newCell(Row var1)
	{
		return null;
	}

	@Override
	public Row newRow(Grid var1)
	{
		return null;
	}

	Grid grid = null;

	@Override
	public void render(Row row, Object[] data, int index) throws Exception
	{
		if (grid == null)
			grid = (Grid) row.getParent().getParent();

		this.adWindow = listModel.getJPiereAttachmentFileRecordGridTable().getADWindow();

		if (rowListener == null)
			rowListener = new RowListener((Grid)row.getParent().getParent(), adWindow);

		Cell div = null;
		for(int i = 0; i < data.length; i++)
		{
			if(i == 0)//DownLoad Button
			{
				div = new Cell();
		    	ToolBarButton btnExport = new ToolBarButton();
		    	btnExport.setAttribute("name","btnExport");
		        if (ThemeManager.isUseFontIconForImage())
		        	btnExport.setIconSclass("z-icon-Export");
		        else
		        	btnExport.setImage(ThemeManager.getThemeResource("images/Export16.png"));
		        btnExport.addEventListener(Events.ON_CLICK, rowListener);
		        btnExport.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnExport.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnExport);

		        div.appendChild(btnExport);
		        div.setStyle("width:30px;");


			}else if(i == 1){//Zoom Across Button

				if(isAccessEditRecord)
				{
					div = new Cell();
			    	ToolBarButton btnEditRecord = new ToolBarButton();
			    	btnEditRecord.setAttribute("name","btnEditRecord");
			        if (ThemeManager.isUseFontIconForImage())
			        {
			        	btnEditRecord.setIconSclass("z-icon-Edit");
			        }else {
			        	btnEditRecord.setImage(ThemeManager.getThemeResource("images/Editor16.png"));//Editor16.png or EditRecord16.png
			        }
			        btnEditRecord.addEventListener(Events.ON_CLICK, rowListener);
			        btnEditRecord.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
			        btnEditRecord.setStyle("vertical-align: middle;");
			        if (ThemeManager.isUseFontIconForImage())
			        	LayoutUtils.addSclass("large-toolbarbutton", btnEditRecord);

//			        btnZoomAcross.setDisabled(true);
			        div.appendChild(btnEditRecord);
			        div.setStyle("width:30px;");
				}

			}else if(i == 2) {//Review

				String mimeType = (String)data[i];

				div = new Cell();
		    	ToolBarButton btnReview = new ToolBarButton();
		    	btnReview.setAttribute("name","btnReview");
		        if (ThemeManager.isUseFontIconForImage() && autoPreviewList.contains(mimeType))
		        {
		        	btnReview.setIconSclass("z-icon-ZoomAcross");
		        	btnReview.addEventListener(Events.ON_CLICK, rowListener);

		        }else if(ThemeManager.isUseFontIconForImage() && !autoPreviewList.contains(mimeType)) {

		        	btnReview.setIconSclass("z-icon-ZoomAcross");
		        	btnReview.setDisabled(true);

		        }else if(autoPreviewList.contains(mimeType)) {

		        	btnReview.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		        	btnReview.addEventListener(Events.ON_CLICK, rowListener);
		        	
		        }else {

		        	btnReview.setImage(ThemeManager.getThemeResource("images/Zoom16.png"));
		        	btnReview.setDisabled(true);
		        }

		        btnReview.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnReview.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnReview);

		        div.appendChild(btnReview);
		        div.setStyle("width:30px;");

			}else if(i == 3) {

				div = new Cell();
				div.appendChild(new Label(data[i].toString()));

			}else if(i == 4) {//Delete Button


				boolean isDeleteable = (boolean)data[i];

				div = new Cell();
		    	ToolBarButton btnDelete = new ToolBarButton();
		    	btnDelete.setAttribute("name","btnDelete");

		        if (ThemeManager.isUseFontIconForImage() && isDeleteable )
		        {
		        	btnDelete.setIconSclass("z-icon-Trash");
		        	btnDelete.addEventListener(Events.ON_CLICK, rowListener);

		        }else if (ThemeManager.isUseFontIconForImage() && !isDeleteable ) {

		        	btnDelete.setIconSclass("z-icon-Trash");//z-icon-lock
		        	btnDelete.setDisabled(true);

		        }else if(isDeleteable) {

		        	btnDelete.setImage(ThemeManager.getThemeResource("images/Delete16.png"));
		        	btnDelete.addEventListener(Events.ON_CLICK, rowListener);

		        }else {

		        	btnDelete.setImage(ThemeManager.getThemeResource("images/Delete16.png"));//images/LockX16.png
		        	btnDelete.setDisabled(true);

		        }


		        btnDelete.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//Set RowIndex(Y-axis) and Column(X-axis) in ID of Cell(div)
		        btnDelete.setStyle("vertical-align: middle;");
		        if (ThemeManager.isUseFontIconForImage())
		        	LayoutUtils.addSclass("large-toolbarbutton", btnDelete);

		        div.appendChild(btnDelete);
		        div.setStyle("width:30px;");


			}

			row.appendChild(div);
		}

//		row.setStyle("cursor:pointer");
		row.addEventListener(Events.ON_CLICK, rowListener);
		row.setTooltiptext(data[1].toString());

		row.addEventListener(Events.ON_CLICK, rowListener);


	}

	class RowListener implements EventListener<Event>
	{

		private Grid _grid;

		private int rowIndex = 0;
		private int columnIndex = 0;
		private ADWindow adWindow = null;
		private int AD_Table_ID = 0;
		private int  Record_ID = 0;

		public RowListener(Grid grid, ADWindow adWindow) {
			_grid = grid;
			this.adWindow = adWindow;
			this.AD_Table_ID =adWindow.getADWindowContent().getADTab().getSelectedGridTab().getAD_Table_ID();
			this.Record_ID =  adWindow.getADWindowContent().getADTab().getSelectedGridTab().getRecord_ID();

		}

		public int getRowIndex()
		{
			return rowIndex;
		}

		public int getColumnIndex()
		{
			return columnIndex;
		}

		public void setRowIndex(int rowIndex)
		{
			this.rowIndex = rowIndex;
		}

		public void setColumnIndex(int columnIndex)
		{
			this.columnIndex = columnIndex;
		}

		public void onEvent(Event event) throws Exception
		{

			if(event.getTarget() instanceof ToolBarButton)//Get Row Index
			{
				String[] yx = ((ToolBarButton)event.getTarget()).getId().split("_");
				rowIndex =Integer.valueOf(yx[0]).intValue();
	            columnIndex =Integer.valueOf(yx[1]).intValue();


				if (Events.ON_CLICK.equals(event.getName()))
				{
					if(columnIndex == 0)	//Download File
					{
						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
						MAttachmentFileRecord  attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(),JP_AttachmentFileRecord_ID.intValue(), null);

						ArrayList<File> downloadFiles = new ArrayList<File>();
						File downloadFile = new File(attachmentFileRecord.getFileAbsolutePath());

						if(!downloadFile.exists())
						{
							FDialog.error(0, "AttachmentNotFound", attachmentFileRecord.getFileAbsolutePath());
							return ;

						}

						downloadFiles.add(downloadFile);
						MultiFileDownloadDialog downloadDialog = new MultiFileDownloadDialog(downloadFiles.toArray(new File[0]));
						downloadDialog.setPage(adWindow.getComponent().getPage());
						downloadDialog.setTitle(Msg.getMsg(Env.getCtx(), "Attachment"));
						Events.postEvent(downloadDialog, new Event(MultiFileDownloadDialog.ON_SHOW));

					}else if(columnIndex == 1) {	//Edit Record

						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
						AEnv.zoom(MAttachmentFileRecord.Table_ID, JP_AttachmentFileRecord_ID.intValue());

					}else if(columnIndex == 2) {	// Preview

						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];

						EventListener<Event> listener = new EventListener<Event>()
						{
							@Override
							public void onEvent(Event event) throws Exception {
//								toolbar.getButton("Attachment").setPressed(adTabbox.getSelectedGridTab().hasAttachment());
//								focusToActivePanel();
							}
						};

						AttachmentFileViewer attachmentPreviewWindow = new AttachmentFileViewer (listener);
						attachmentPreviewWindow.setRecord_ID(JP_AttachmentFileRecord_ID.intValue());

						if(attachmentPreviewWindow.isFileLoad())
						{
							AEnv.showCenterScreen(attachmentPreviewWindow);
							attachmentPreviewWindow.focus();
						}else {
							attachmentPreviewWindow = null;
						}


					}else if(columnIndex == 3) {	// Name

						;

					}else if(columnIndex == 4) {	// Delete

						ListModel<Object> model = _grid.getModel();
						Object[] row = (Object[] )model.getElementAt(rowIndex);
						Integer JP_AttachmentFileRecord_ID = (Integer)row[0];
						MAttachmentFileRecord  attachmentFileRecord = new MAttachmentFileRecord(Env.getCtx(),JP_AttachmentFileRecord_ID.intValue(), null);
						if(attachmentFileRecord.get_ID() != 0)
						{
							if(attachmentFileRecord.delete(true))
							{
								;//Noting to do
							}else {

								FDialog.error(0, "JP_CouldNotDeleteFile", attachmentFileRecord.getFileAbsolutePath());
								return ;

							}

						}

						List<Row> rowList = _grid.getRows().getChildren();
						rowList.remove(rowIndex);

						ArrayList<MAttachmentFileRecord>  attachmentFileRecordList = MAttachmentFileRecord.getAttachmentFileRecordPO(Env.getCtx(), AD_Table_ID, Record_ID, true, null);
						JPiereAttachmentFileRecordGridTable AFRGridTable= new JPiereAttachmentFileRecordGridTable(attachmentFileRecordList,adWindow);
						JPiereAttachmentFileRecordListModel listModel = new JPiereAttachmentFileRecordListModel(AFRGridTable);
						grid.setModel(listModel);
				        if(attachmentFileRecordList.size() == 0)
				        {
				        	baseWindow.btnExport.setDisabled(true);
							if(isAccessEditRecord)
					        {
								baseWindow.btnZoomAcross.setDisabled(true);
					        }

				        }else {

				        	baseWindow.btnExport.setDisabled(false);
							if(isAccessEditRecord)
					        {
								baseWindow.btnZoomAcross.setDisabled(false);
					        }
				        }


					}

				}
			}

		}
	}


}
