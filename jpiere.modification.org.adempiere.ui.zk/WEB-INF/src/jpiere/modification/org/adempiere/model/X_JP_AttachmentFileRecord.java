/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package jpiere.modification.org.adempiere.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.compiere.model.I_Persistent;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.POInfo;
import org.compiere.util.Env;

/** Generated Model for JP_AttachmentFileRecord
 *  @author iDempiere (generated) 
 *  @version Release 9 - $Id$ */
@org.adempiere.base.Model(table="JP_AttachmentFileRecord")
public class X_JP_AttachmentFileRecord extends PO implements I_JP_AttachmentFileRecord, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220705L;

    /** Standard Constructor */
    public X_JP_AttachmentFileRecord (Properties ctx, int JP_AttachmentFileRecord_ID, String trxName)
    {
      super (ctx, JP_AttachmentFileRecord_ID, trxName);
      /** if (JP_AttachmentFileRecord_ID == 0)
        {
			setIsDeleteable (true);
// Y
			setIsSOTrx (false);
// N
			setJP_AttachmentFileName (null);
			setJP_AttachmentFileRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_JP_AttachmentFileRecord (Properties ctx, int JP_AttachmentFileRecord_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_AttachmentFileRecord_ID, trxName, virtualColumns);
      /** if (JP_AttachmentFileRecord_ID == 0)
        {
			setIsDeleteable (true);
// Y
			setIsSOTrx (false);
// N
			setJP_AttachmentFileName (null);
			setJP_AttachmentFileRecord_ID (0);
        } */
    }

    /** Load Constructor */
    public X_JP_AttachmentFileRecord (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 7 - System - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_AttachmentFileRecord[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID Performing or initiating organization
	*/
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1)
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_StorageProvider getAD_StorageProvider() throws RuntimeException
	{
		return (org.compiere.model.I_AD_StorageProvider)MTable.get(getCtx(), org.compiere.model.I_AD_StorageProvider.Table_ID)
			.getPO(getAD_StorageProvider_ID(), get_TrxName());
	}

	/** Set Storage Provider.
		@param AD_StorageProvider_ID Storage Provider
	*/
	public void setAD_StorageProvider_ID (int AD_StorageProvider_ID)
	{
		if (AD_StorageProvider_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_StorageProvider_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_StorageProvider_ID, Integer.valueOf(AD_StorageProvider_ID));
	}

	/** Get Storage Provider.
		@return Storage Provider	  */
	public int getAD_StorageProvider_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_StorageProvider_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_ID)
			.getPO(getAD_Table_ID(), get_TrxName());
	}

	/** Set Table.
		@param AD_Table_ID Database Table information
	*/
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_Table_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_Value (COLUMNNAME_AD_User_ID, null);
		else
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_BPartner getC_BPartner() throws RuntimeException
	{
		return (org.compiere.model.I_C_BPartner)MTable.get(getCtx(), org.compiere.model.I_C_BPartner.Table_ID)
			.getPO(getC_BPartner_ID(), get_TrxName());
	}

	/** Set Business Partner.
		@param C_BPartner_ID Identifies a Business Partner
	*/
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1)
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner.
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
	{
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_ID)
			.getPO(getC_Invoice_ID(), get_TrxName());
	}

	/** Set Invoice.
		@param C_Invoice_ID Invoice Identifier
	*/
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1)
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException
	{
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_ID)
			.getPO(getC_Order_ID(), get_TrxName());
	}

	/** Set Order.
		@param C_Order_ID Order
	*/
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1)
			set_Value (COLUMNNAME_C_Order_ID, null);
		else
			set_Value (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Payment getC_Payment() throws RuntimeException
	{
		return (org.compiere.model.I_C_Payment)MTable.get(getCtx(), org.compiere.model.I_C_Payment.Table_ID)
			.getPO(getC_Payment_ID(), get_TrxName());
	}

	/** Set Payment.
		@param C_Payment_ID Payment identifier
	*/
	public void setC_Payment_ID (int C_Payment_ID)
	{
		if (C_Payment_ID < 1)
			set_Value (COLUMNNAME_C_Payment_ID, null);
		else
			set_Value (COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
	}

	/** Get Payment.
		@return Payment identifier
	  */
	public int getC_Payment_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Payment_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Charge amount.
		@param ChargeAmt Charge Amount
	*/
	public void setChargeAmt (BigDecimal ChargeAmt)
	{
		set_Value (COLUMNNAME_ChargeAmt, ChargeAmt);
	}

	/** Get Charge amount.
		@return Charge Amount
	  */
	public BigDecimal getChargeAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ChargeAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Account Date.
		@param DateAcct Accounting Date
	*/
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Document Date.
		@param DateDoc Date of the Document
	*/
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc()
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Document No.
		@param DocumentNo Document sequence number of the document
	*/
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo()
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Grand Total.
		@param GrandTotal Total amount of document
	*/
	public void setGrandTotal (BigDecimal GrandTotal)
	{
		set_Value (COLUMNNAME_GrandTotal, GrandTotal);
	}

	/** Get Grand Total.
		@return Total amount of document
	  */
	public BigDecimal getGrandTotal()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_GrandTotal);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Records deletable.
		@param IsDeleteable Indicates if records can be deleted from the database
	*/
	public void setIsDeleteable (boolean IsDeleteable)
	{
		set_Value (COLUMNNAME_IsDeleteable, Boolean.valueOf(IsDeleteable));
	}

	/** Get Records deletable.
		@return Indicates if records can be deleted from the database
	  */
	public boolean isDeleteable()
	{
		Object oo = get_Value(COLUMNNAME_IsDeleteable);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Sales Transaction.
		@param IsSOTrx This is a Sales Transaction
	*/
	public void setIsSOTrx (boolean IsSOTrx)
	{
		set_Value (COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
	}

	/** Get Sales Transaction.
		@return This is a Sales Transaction
	  */
	public boolean isSOTrx()
	{
		Object oo = get_Value(COLUMNNAME_IsSOTrx);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Bill Amt.
		@param JPBillAmt Bill Amt
	*/
	public void setJPBillAmt (BigDecimal JPBillAmt)
	{
		set_Value (COLUMNNAME_JPBillAmt, JPBillAmt);
	}

	/** Get Bill Amt.
		@return Bill Amt	  */
	public BigDecimal getJPBillAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_JPBillAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Description of Attachment.
		@param JP_AttachmentFileDescription Description of Attachment
	*/
	public void setJP_AttachmentFileDescription (String JP_AttachmentFileDescription)
	{
		set_Value (COLUMNNAME_JP_AttachmentFileDescription, JP_AttachmentFileDescription);
	}

	/** Get Description of Attachment.
		@return Description of Attachment	  */
	public String getJP_AttachmentFileDescription()
	{
		return (String)get_Value(COLUMNNAME_JP_AttachmentFileDescription);
	}

	/** Set Name of Attachment.
		@param JP_AttachmentFileName Name of Attachment
	*/
	public void setJP_AttachmentFileName (String JP_AttachmentFileName)
	{
		set_ValueNoCheck (COLUMNNAME_JP_AttachmentFileName, JP_AttachmentFileName);
	}

	/** Get Name of Attachment.
		@return Name of Attachment	  */
	public String getJP_AttachmentFileName()
	{
		return (String)get_Value(COLUMNNAME_JP_AttachmentFileName);
	}

	/** Set Path of Attachment.
		@param JP_AttachmentFilePath Path of Attachment
	*/
	public void setJP_AttachmentFilePath (String JP_AttachmentFilePath)
	{
		set_ValueNoCheck (COLUMNNAME_JP_AttachmentFilePath, JP_AttachmentFilePath);
	}

	/** Get Path of Attachment.
		@return Path of Attachment	  */
	public String getJP_AttachmentFilePath()
	{
		return (String)get_Value(COLUMNNAME_JP_AttachmentFilePath);
	}

	/** Set JP_AttachmentFileRecord.
		@param JP_AttachmentFileRecord_ID JP_AttachmentFileRecord
	*/
	public void setJP_AttachmentFileRecord_ID (int JP_AttachmentFileRecord_ID)
	{
		if (JP_AttachmentFileRecord_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_AttachmentFileRecord_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_AttachmentFileRecord_ID, Integer.valueOf(JP_AttachmentFileRecord_ID));
	}

	/** Get JP_AttachmentFileRecord.
		@return JP_AttachmentFileRecord	  */
	public int getJP_AttachmentFileRecord_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_AttachmentFileRecord_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JP_AttachmentFileRecord_UU.
		@param JP_AttachmentFileRecord_UU JP_AttachmentFileRecord_UU
	*/
	public void setJP_AttachmentFileRecord_UU (String JP_AttachmentFileRecord_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_AttachmentFileRecord_UU, JP_AttachmentFileRecord_UU);
	}

	/** Get JP_AttachmentFileRecord_UU.
		@return JP_AttachmentFileRecord_UU	  */
	public String getJP_AttachmentFileRecord_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_AttachmentFileRecord_UU);
	}

//	public I_JP_ContractContent getJP_ContractContent() throws RuntimeException
//	{
//		return (I_JP_ContractContent)MTable.get(getCtx(), I_JP_ContractContent.Table_ID)
//			.getPO(getJP_ContractContent_ID(), get_TrxName());
//	}

	/** Set Contract Content.
		@param JP_ContractContent_ID Contract Content
	*/
	public void setJP_ContractContent_ID (int JP_ContractContent_ID)
	{
		if (JP_ContractContent_ID < 1)
			set_Value (COLUMNNAME_JP_ContractContent_ID, null);
		else
			set_Value (COLUMNNAME_JP_ContractContent_ID, Integer.valueOf(JP_ContractContent_ID));
	}

	/** Get Contract Content.
		@return Contract Content	  */
	public int getJP_ContractContent_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_ContractContent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

//	public I_JP_Contract getJP_Contract() throws RuntimeException
//	{
//		return (I_JP_Contract)MTable.get(getCtx(), I_JP_Contract.Table_ID)
//			.getPO(getJP_Contract_ID(), get_TrxName());
//	}

	/** Set Contract Document.
		@param JP_Contract_ID Contract Document
	*/
	public void setJP_Contract_ID (int JP_Contract_ID)
	{
		if (JP_Contract_ID < 1)
			set_Value (COLUMNNAME_JP_Contract_ID, null);
		else
			set_Value (COLUMNNAME_JP_Contract_ID, Integer.valueOf(JP_Contract_ID));
	}

	/** Get Contract Document.
		@return Contract Document	  */
	public int getJP_Contract_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Contract_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

//	public I_JP_Estimation getJP_Estimation() throws RuntimeException
//	{
//		return (I_JP_Estimation)MTable.get(getCtx(), I_JP_Estimation.Table_ID)
//			.getPO(getJP_Estimation_ID(), get_TrxName());
//	}

	/** Set Estimation &amp; Handwritten.
		@param JP_Estimation_ID Estimation &amp; Handwritten
	*/
	public void setJP_Estimation_ID (int JP_Estimation_ID)
	{
		if (JP_Estimation_ID < 1)
			set_Value (COLUMNNAME_JP_Estimation_ID, null);
		else
			set_Value (COLUMNNAME_JP_Estimation_ID, Integer.valueOf(JP_Estimation_ID));
	}

	/** Get Estimation &amp; Handwritten.
		@return Estimation &amp; Handwritten	  */
	public int getJP_Estimation_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_Estimation_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Hash of File.
		@param JP_Hash_File Hash of File
	*/
	public void setJP_Hash_File (String JP_Hash_File)
	{
		set_ValueNoCheck (COLUMNNAME_JP_Hash_File, JP_Hash_File);
	}

	/** Get Hash of File.
		@return Hash of File	  */
	public String getJP_Hash_File()
	{
		return (String)get_Value(COLUMNNAME_JP_Hash_File);
	}

	/** Set Media Content Type.
		@param JP_MediaContentType Media Content Type
	*/
	public void setJP_MediaContentType (String JP_MediaContentType)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MediaContentType, JP_MediaContentType);
	}

	/** Get Media Content Type.
		@return Media Content Type	  */
	public String getJP_MediaContentType()
	{
		return (String)get_Value(COLUMNNAME_JP_MediaContentType);
	}

	/** Set Media Format.
		@param JP_MediaFormat Media Format
	*/
	public void setJP_MediaFormat (String JP_MediaFormat)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MediaFormat, JP_MediaFormat);
	}

	/** Get Media Format.
		@return Media Format	  */
	public String getJP_MediaFormat()
	{
		return (String)get_Value(COLUMNNAME_JP_MediaFormat);
	}

	/** Set Process Now.
		@param JP_Processing1 Process Now
	*/
	public void setJP_Processing1 (String JP_Processing1)
	{
		set_Value (COLUMNNAME_JP_Processing1, JP_Processing1);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing1()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing1);
	}

	/** Set Process Now.
		@param JP_Processing2 Process Now
	*/
	public void setJP_Processing2 (String JP_Processing2)
	{
		set_Value (COLUMNNAME_JP_Processing2, JP_Processing2);
	}

	/** Get Process Now.
		@return Process Now	  */
	public String getJP_Processing2()
	{
		return (String)get_Value(COLUMNNAME_JP_Processing2);
	}

	/** Set Line No.
		@param Line Unique line for this document
	*/
	public void setLine (int Line)
	{
		set_Value (COLUMNNAME_Line, Integer.valueOf(Line));
	}

	/** Get Line No.
		@return Unique line for this document
	  */
	public int getLine()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Line);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Line Amount.
		@param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	*/
	public void setLineNetAmt (BigDecimal LineNetAmt)
	{
		set_Value (COLUMNNAME_LineNetAmt, LineNetAmt);
	}

	/** Get Line Amount.
		@return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
	  */
	public BigDecimal getLineNetAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_LineNetAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_M_InOut getM_InOut() throws RuntimeException
	{
		return (org.compiere.model.I_M_InOut)MTable.get(getCtx(), org.compiere.model.I_M_InOut.Table_ID)
			.getPO(getM_InOut_ID(), get_TrxName());
	}

	/** Set Shipment/Receipt.
		@param M_InOut_ID Material Shipment Document
	*/
	public void setM_InOut_ID (int M_InOut_ID)
	{
		if (M_InOut_ID < 1)
			set_Value (COLUMNNAME_M_InOut_ID, null);
		else
			set_Value (COLUMNNAME_M_InOut_ID, Integer.valueOf(M_InOut_ID));
	}

	/** Get Shipment/Receipt.
		@return Material Shipment Document
	  */
	public int getM_InOut_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
	{
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_ID)
			.getPO(getM_Product_ID(), get_TrxName());
	}

	/** Set Product.
		@param M_Product_ID Product, Service, Item
	*/
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1)
			set_Value (COLUMNNAME_M_Product_ID, null);
		else
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Name 2.
		@param Name2 Additional Name
	*/
	public void setName2 (String Name2)
	{
		set_Value (COLUMNNAME_Name2, Name2);
	}

	/** Get Name 2.
		@return Additional Name
	  */
	public String getName2()
	{
		return (String)get_Value(COLUMNNAME_Name2);
	}

	/** Set Order Reference.
		@param POReference Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	*/
	public void setPOReference (String POReference)
	{
		set_Value (COLUMNNAME_POReference, POReference);
	}

	/** Get Order Reference.
		@return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
	  */
	public String getPOReference()
	{
		return (String)get_Value(COLUMNNAME_POReference);
	}

	/** Set Payment amount.
		@param PayAmt Amount being paid
	*/
	public void setPayAmt (BigDecimal PayAmt)
	{
		set_Value (COLUMNNAME_PayAmt, PayAmt);
	}

	/** Get Payment amount.
		@return Amount being paid
	  */
	public BigDecimal getPayAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PayAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Record ID.
		@param Record_ID Direct internal record ID
	*/
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0)
			set_ValueNoCheck (COLUMNNAME_Record_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getSalesRep() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getSalesRep_ID(), get_TrxName());
	}

	/** Set Sales Rep.
		@param SalesRep_ID Sales Representative or Company Agent
	*/
	public void setSalesRep_ID (int SalesRep_ID)
	{
		if (SalesRep_ID < 1)
			set_Value (COLUMNNAME_SalesRep_ID, null);
		else
			set_Value (COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
	}

	/** Get Sales Rep.
		@return Sales Representative or Company Agent
	  */
	public int getSalesRep_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SalesRep_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Statement amount.
		@param StmtAmt Statement Amount
	*/
	public void setStmtAmt (BigDecimal StmtAmt)
	{
		set_Value (COLUMNNAME_StmtAmt, StmtAmt);
	}

	/** Get Statement amount.
		@return Statement Amount
	  */
	public BigDecimal getStmtAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_StmtAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Total Amount.
		@param TotalAmt Total Amount
	*/
	public void setTotalAmt (BigDecimal TotalAmt)
	{
		set_Value (COLUMNNAME_TotalAmt, TotalAmt);
	}

	/** Get Total Amount.
		@return Total Amount
	  */
	public BigDecimal getTotalAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Total Lines.
		@param TotalLines Total of all document lines
	*/
	public void setTotalLines (BigDecimal TotalLines)
	{
		set_Value (COLUMNNAME_TotalLines, TotalLines);
	}

	/** Get Total Lines.
		@return Total of all document lines
	  */
	public BigDecimal getTotalLines()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalLines);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Transaction Amount.
		@param TrxAmt Amount of a transaction
	*/
	public void setTrxAmt (BigDecimal TrxAmt)
	{
		set_Value (COLUMNNAME_TrxAmt, TrxAmt);
	}

	/** Get Transaction Amount.
		@return Amount of a transaction
	  */
	public BigDecimal getTrxAmt()
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TrxAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}