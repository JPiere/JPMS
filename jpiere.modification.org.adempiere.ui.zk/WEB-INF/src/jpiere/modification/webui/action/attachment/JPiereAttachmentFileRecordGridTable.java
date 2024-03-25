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

import javax.swing.table.AbstractTableModel;

import org.adempiere.webui.adwindow.ADWindow;
import org.compiere.util.Util;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;


/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachmentFileRecordGridTable extends AbstractTableModel {

	private static final long serialVersionUID = 3847038829673684289L;

	private ArrayList<MAttachmentFileRecord> list_POs;

	private ADWindow adWindow = null;

	public JPiereAttachmentFileRecordGridTable(ArrayList<MAttachmentFileRecord> POs, ADWindow adWindow)
	{
		this.list_POs=POs;
		this.adWindow = adWindow;
	}

	public ADWindow getADWindow()
	{
		return adWindow;
	}


	@Override
	public int getRowCount()
	{
		return list_POs.size();
	}

	@Override
	public int getColumnCount()
	{
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(columnIndex == 0)//Download
		{
			return list_POs.get(rowIndex).getJP_AttachmentFileRecord_ID();

		}else if(columnIndex == 1) { //Edit Record

			if(Util.isEmpty(list_POs.get(rowIndex).getJP_AttachmentFileDescription()))
			{
				return "";
			}else {
				return list_POs.get(rowIndex).getJP_AttachmentFileDescription();
			}

		}else if(columnIndex == 2) { //Preview

			return list_POs.get(rowIndex).getJP_MediaContentType();

		}else if(columnIndex == 3) {//File Name

			return list_POs.get(rowIndex).getJP_AttachmentFileName();

		}else if(columnIndex == 4){ //Deleteable

			return list_POs.get(rowIndex).isDeleteable();
		}

		return list_POs.get(rowIndex).getJP_AttachmentFileRecord_ID();
	}

}
