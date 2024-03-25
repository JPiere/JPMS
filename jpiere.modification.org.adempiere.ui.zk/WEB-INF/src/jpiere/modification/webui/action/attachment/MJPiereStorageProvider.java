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

import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.base.Service;
import org.adempiere.base.ServiceQuery;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.IArchiveStore;
import org.compiere.model.X_AD_StorageProvider;
import org.compiere.util.CCache;
import org.compiere.util.Env;

/**
*
* JPIERE-0436: JPiere Attachment File
*
*
* @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
*
*/
public class MJPiereStorageProvider extends X_AD_StorageProvider {

	private static final long serialVersionUID = -3826309701401409584L;

	public MJPiereStorageProvider(Properties ctx, int AD_StorageProvider_ID, String trxName) {
		super(ctx, AD_StorageProvider_ID, trxName);
	}

	public MJPiereStorageProvider(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public IJPiereAttachmentStore getAttachmentStore() {
		ServiceQuery query=new ServiceQuery();
		String method = this.getMethod();
		if (method == null)
			method = "FileSystem";
		query.put("method", method);
		IJPiereAttachmentStore store = Service.locator().locate(IJPiereAttachmentStore.class, query).getService();
		if (store == null) {
			throw new AdempiereException("No attachment storage provider found");
		}
		return store;
	}

	public IArchiveStore getArchiveStore() {
//		ServiceQuery query=new ServiceQuery();
//		String method = this.getMethod();
//		if (method == null)
//			method = "DB";
//		query.put("method", method);
//		IArchiveStore store = Service.locator().locate(IArchiveStore.class, query).getService();
//		if (store == null) {
//			throw new AdempiereException("No archive storage provider found");
//		}
		return null;
	}

	/**	Cache				*/
	private static CCache<Integer,MJPiereStorageProvider>	s_cache = new CCache<Integer,MJPiereStorageProvider>(Table_Name, 60);
	
	public static MJPiereStorageProvider get(int AD_StorageProvider_ID)
	{
		Integer ii = Integer.valueOf(AD_StorageProvider_ID);
		MJPiereStorageProvider retValue = (MJPiereStorageProvider)s_cache.get(ii);
		if (retValue != null)
			return retValue;
		retValue = new MJPiereStorageProvider (Env.getCtx(), AD_StorageProvider_ID, null);
		if (retValue.get_ID () != 0)
			s_cache.put (AD_StorageProvider_ID, retValue);
		return retValue;
	}
	
}
