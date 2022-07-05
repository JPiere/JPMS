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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
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
public class JPiereAttachmentFileSystem implements IJPiereAttachmentStore {

	private final CLogger log = CLogger.getCLogger(getClass());


    /** MD2アルゴリズム */
    public static final String MD2 = "MD2";
    /** MD5アルゴリズム */
    public static final String MD5 = "MD5";
    /** SHA-1アルゴリズム */
    public static final String SHA_1 = "SHA-1";
    /** SHA-256アルゴリズム */
    public static final String SHA_256 = "SHA-256";
    /** SHA-512アルゴリズム */
    public static final String SHA_512 = "SHA-512";
	
	@Override
	public boolean upLoadFile(MAttachmentFileRecord attachmentFileRecord, byte[] data, MJPiereStorageProvider prov)
	{

		StringBuilder folderPath = getDirectoryAbsolutePath(attachmentFileRecord, prov);
		if (folderPath == null) {
			log.severe("no attachmentPath defined");
			return false;
		}

		if (data == null)
			return true;
		if (log.isLoggable(Level.FINE)) log.fine("TextFileSize=" + data.length);
		if (data.length == 0)
			return true;


		final File folder = new File(folderPath.toString());
		if(!folder.exists()){
			if(!folder.mkdirs()){
				log.warning("unable to create folder: " + folder.getPath());
			}
		}

		FileOutputStream fos = null;

		attachmentFileRecord.setJP_AttachmentFilePath(getAttachmentRelativePath(attachmentFileRecord));

		String filePath = folderPath.append(File.separator).append(attachmentFileRecord.getJP_AttachmentFileName()).toString();
		final File destFile = new File(filePath);
		try
		{
			fos = new FileOutputStream(destFile);
			try {
				fos.write(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		//Get Hash
		String JP_ATTACHMENT_HASH_FILE_LIST = MSysConfig.getValue("JP_ATTACHMENT_HASH_FILE_LIST", "NONE", attachmentFileRecord.getAD_Client_ID());
		String hash = null;
		boolean isHash = false;
		
		if(Util.isEmpty(JP_ATTACHMENT_HASH_FILE_LIST) || "NONE".equalsIgnoreCase(JP_ATTACHMENT_HASH_FILE_LIST))
		{
			isHash = false;
			
		}else if("ALL".equalsIgnoreCase(JP_ATTACHMENT_HASH_FILE_LIST)) {
			
			isHash = true;
			
		}else {
			
			String[] extensions = JP_ATTACHMENT_HASH_FILE_LIST.split(",");
			for(String extension:extensions)
			{
				if(attachmentFileRecord.getJP_MediaFormat().equalsIgnoreCase(extension))
				{				
					isHash = true;
					break;
				}
			}	
		}
		
		if(isHash)
		{
			hash = getfileHash(filePath,SHA_512);
			attachmentFileRecord.setJP_Hash_File(hash);		
		}
		
		
		//Save
		attachmentFileRecord.saveEx();

		return true;
	}


	@Override
	public boolean deleteFile(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		final File deleteFile = new File(getFileAbsolutePath(attach,prov).toString());

		if(!deleteFile.exists())
		{
			return true;
		}

		return deleteFile.delete();
	}

	@Override
	public StringBuilder getFileAbsolutePath(MAttachmentFileRecord attach, MJPiereStorageProvider prov)
	{
		return getDirectoryAbsolutePath(attach,prov).append(File.separator).append(attach.getJP_AttachmentFileName());
	}

	@Override
	public StringBuilder getDirectoryAbsolutePath(MAttachmentFileRecord attachmentFileRecord, MJPiereStorageProvider prov)
	{
		String rootPath = null;
		if(attachmentFileRecord.getAD_StorageProvider_ID() == 0)
		{
			rootPath = getAttachmentRootRoot(prov);
		}else {
			
			int AD_StorageProvider_ID = attachmentFileRecord.getAD_StorageProvider_ID() ;
			prov = MJPiereStorageProvider.get(AD_StorageProvider_ID);
			rootPath = getAttachmentRootRoot(prov);
		}
		
		if (Util.isEmpty(rootPath)) {
			log.severe("no attachmentPath defined");
			return null;
		}

		String relativePath = getAttachmentRelativePath(attachmentFileRecord);

		return new StringBuilder(rootPath).append(relativePath);
	}


	private String getAttachmentRelativePath(MAttachmentFileRecord attachmentFileRecord)
	{
		if(!Util.isEmpty(attachmentFileRecord.getJP_AttachmentFilePath()))
		{
			return attachmentFileRecord.getJP_AttachmentFilePath();
		}
		
		String tableName = MTable.getTableName(Env.getCtx(), attachmentFileRecord.getAD_Table_ID());
		int record_ID = attachmentFileRecord.getRecord_ID();
		String id_10million = "00000000"; 	//10 million
		String id_10thousand = "00000";		//10 thousand
		if(record_ID >= 10000000)
		{
			id_10million = String.valueOf((record_ID / 10000000) * 10000000);//10 million
		}

		if(record_ID >= 10000)
		{
			id_10thousand = String.valueOf((record_ID / 10000) * 10000);//10 thousand
		}
		
		StringBuilder msgreturn = new StringBuilder().append(attachmentFileRecord.getAD_Client_ID()).append(File.separator).append(attachmentFileRecord.getAD_Org_ID())
										.append(File.separator).append(tableName).append(File.separator).append(id_10million)
										.append(File.separator).append(id_10thousand).append(File.separator).append(attachmentFileRecord.getRecord_ID());
		return msgreturn.toString();
	}


	private String getAttachmentRootRoot(MJPiereStorageProvider prov)
	{
		String attachmentPathRoot = prov.getFolder();
		if (attachmentPathRoot == null)
			attachmentPathRoot = "";
		if (Util.isEmpty(attachmentPathRoot)) {
			log.severe("no attachmentPath defined");
		} else if (!attachmentPathRoot.endsWith(File.separator)){
			attachmentPathRoot = attachmentPathRoot + File.separator;
			log.fine(attachmentPathRoot);
		}
		return attachmentPathRoot;
	}

	 public static String getfileHash(String filePath, String algorithmName) {

	        Path path = Paths.get(filePath);

	        byte[] hash = null;

	        // アルゴリズム取得
	        MessageDigest md = null;
	        try {
	            md = MessageDigest.getInstance(algorithmName);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }

	        try (
	                // 入力ストリームの生成
	                DigestInputStream dis = new DigestInputStream(
	                        new BufferedInputStream(Files.newInputStream(path)), md)) {

	            // ファイルの読み込み
	            while (dis.read() != -1) {
	            }

	            // ハッシュ値の計算
	            hash = md.digest();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        // ハッシュ値（byte）を文字列に変換し返却
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hash) {
	            String hex = String.format("%02x", b);
	            sb.append(hex);
	        }
	        return sb.toString();
	    }
	 
}
