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

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.compiere.print.ReportEngine;
import org.compiere.print.ReportViewerProvider;
import org.zkoss.zk.ui.Executions;

/**
*
* JPIERE-0598: JPiere Attachment File at Report
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereZkReportViewerProvider implements ReportViewerProvider {
	
	@Override
	public void openViewer(final ReportEngine report) {
		// IDEMPIERE-2499
		// detect ui thread by value of Executions.getCurrent(), not office method but work
		if (Executions.getCurrent() != null){
			openReportViewWindow (report);
		}else {
			AEnv.executeAsyncDesktopTask(new Runnable() {			
				@Override
				public void run() {
					openReportViewWindow (report);				
				}
			});
		}
	}
	
	protected void openReportViewWindow (ReportEngine report) {
		Window viewer = new JPiereZkReportViewer(report, report.getName());
		
    	viewer.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
    	viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
    	if(report.isReplaceTabContent())
    		viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.REPLACE);
    	
    	viewer.setAttribute(WindowContainer.DEFER_SET_SELECTED_TAB, Boolean.TRUE);
    	SessionManager.getAppDesktop().showWindow(viewer);
	}
}