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

import org.adempiere.webui.action.IAction;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowToolbar;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.GridTab;
import org.compiere.model.MClientInfo;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import jpiere.modification.org.adempiere.model.MAttachmentFileRecord;



/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereAttachment implements IAction {


	@Override
	public void execute(Object target)
	{
		 if(target == null)
			 return ;

		 MClientInfo clientInfo = MClientInfo.get(Env.getCtx());
		 
		 if(clientInfo.get_ValueAsInt("JP_StorageAttachment_ID")  == 0)
		 {
			 Dialog.error(0, "Error", Msg.getMsg(Env.getCtx(), "NotFound")
					 + System.lineSeparator() + Msg.getElement(Env.getCtx(), "JP_StorageAttachment_ID"));
			 return ;
		 }

		ADWindow adWindow = (ADWindow)target;
		ADWindowToolbar toolbar = adWindow.getADWindowContent().getToolbar();

		EventListener<Event> listener = new EventListener<Event>()
		{
			@Override
			public void onEvent(Event event) throws Exception
			{
				toolbar.getButton("JPiereAttachment").setPressed(hasAttachment(adWindow.getADWindowContent().getADTab().getSelectedGridTab()));
			}
		};

		new JPiereAttachmentBaseWindow(adWindow,listener);
	}


	public boolean hasAttachment(GridTab gridTab)
	{
		return getAD_AttachmentID(gridTab) > 0;
	}	//	hasAttachment

	public int getAD_AttachmentID(GridTab gridTab)
	{
//		if (!canHaveAttachment())
//			return 0;
		int recordID = gridTab.getKeyID(gridTab.getCurrentRow());
		return MAttachmentFileRecord.getID(gridTab.getAD_Table_ID(), recordID);
	}	//	getAttachmentID

}
