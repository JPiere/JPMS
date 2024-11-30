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
package org.compiere.util;

import java.util.logging.Level;

import org.compiere.process.SvrProcess;

/**
 *	JPIERE-0283
 *  Reset Cache of PostgreSQL
 *
 * 	@author 	Hideaki Hagiwara
 */
public class CacheReset_PostgreSQL extends SvrProcess 
{
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
	}	//	prepare


	/**
	 *  Perform process.
	 *  @return Message to be translated
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		int total = 0;
		int counter = 0;
		CacheInterface[] instances = CacheMgt.get().getInstancesAsArray();
		for (CacheInterface stored : instances)
		{
			if (stored != null && stored instanceof CCache)
			{
				CCache<?, ?> cc = (CCache<?, ?>)stored;
				if (cc.getName().startsWith("DB_PostgreSQL_Convert_Cache"))		//JPIERE-0283
				{
					{
						if (log.isLoggable(Level.FINE)) log.fine("(all) - " + stored);
						total += stored.reset();
						counter++;
					}
				}
			}
		}
		
		return "Cache Reset - SQL of PostgreSQL #Count: "+ counter + " #Total: " + total;
	}	//	doIt

}	//	CacheReset
