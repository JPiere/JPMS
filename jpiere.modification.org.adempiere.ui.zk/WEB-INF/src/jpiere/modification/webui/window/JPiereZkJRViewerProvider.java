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

import org.adempiere.report.jasper.JRViewerProvider;
import org.adempiere.report.jasper.JRViewerProviderList;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.part.WindowContainer;
import org.adempiere.webui.session.SessionManager;
import org.compiere.model.PrintInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;


/**
*
* JPIERE-0599: JPiere Attachment File at Jasper Report
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class JPiereZkJRViewerProvider implements JRViewerProvider, JRViewerProviderList {

	public void openViewer(final JasperPrint jasperPrint, final String title, final PrintInfo printInfo) throws JRException 
	{
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Window viewer = new JPiereZkJRViewer(jasperPrint, title, printInfo);

				viewer.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
				viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
				viewer.setAttribute(WindowContainer.DEFER_SET_SELECTED_TAB, Boolean.TRUE);
				SessionManager.getAppDesktop().showWindow(viewer);
			}
		};
		AEnv.executeAsyncDesktopTask(runnable);
	}

	public void openViewer(final List<JasperPrint> jasperPrintList, final String title , final PrintInfo printInfo) throws JRException 
	{
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Window viewer = new JPiereZkJRViewer(jasperPrintList, title, printInfo);

				viewer.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
				viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
				viewer.setAttribute(WindowContainer.DEFER_SET_SELECTED_TAB, Boolean.TRUE);
				SessionManager.getAppDesktop().showWindow(viewer);
			}
		};
		AEnv.executeAsyncDesktopTask(runnable);
	}

}
