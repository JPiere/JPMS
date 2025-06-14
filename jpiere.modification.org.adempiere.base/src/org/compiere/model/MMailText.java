/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.compiere.model;

import static org.adempiere.base.markdown.IMarkdownRenderer.MARKDOWN_CLOSING_TAG;
import static org.adempiere.base.markdown.IMarkdownRenderer.MARKDOWN_OPENING_TAG;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.Core;
import org.compiere.util.CCache;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * 	Mail Template Model.
 *	Cannot be cached as it holds PO/BPartner/User to parse.
 *  @author Jorg Janke
 *  @version $Id: MMailText.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MMailText extends X_R_MailText
{
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -6458808409321394821L;

    /**
     * UUID based Constructor
     * @param ctx  Context
     * @param R_MailText_UU  UUID key
     * @param trxName Transaction
     */
    public MMailText(Properties ctx, String R_MailText_UU, String trxName) {
        super(ctx, R_MailText_UU, trxName);
    }

	/**
	 * 	Standard Constructor
	 *	@param ctx context
	 *	@param R_MailText_ID id
	 *	@param trxName transaction
	 */
	public MMailText(Properties ctx, int R_MailText_ID, String trxName)
	{
		super (ctx, R_MailText_ID, trxName);
	}	//	MMailText

	/**
	 * 	Load Constructor
	 *	@param ctx context
	 *	@param rs result set
	 *	@param trxName transaction
	 */
	public MMailText (Properties ctx, ResultSet rs, String trxName)
	{
		super (ctx, rs, trxName);
	}	//	MMailText

	/**	Parse User			*/
	protected MUser		m_user = null;
	/** Parse BPartner		*/
	protected MBPartner	m_bpartner = null;
	/** Parse PO			*/
	protected PO			m_po = null;
	/** Translated Header	*/
	protected String		m_MailHeader = null;
	/** Translated Text		*/
	protected String		m_MailText = null;
	/** Translated Text 2	*/
	protected String		m_MailText2 = null;
	/** Translated Text 3	*/
	protected String		m_MailText3 = null;
	/** Translation Cache	*/
	protected static CCache<String,MMailTextTrl> s_cacheTrl = new CCache<String,MMailTextTrl> (Table_Name, 20);
	protected String m_language = null;
	
	/**
	 * 	Get translated and parsed Mail Text
	 *	@param all true to concatenate mailtext, mailtext2 and mailtext3
	 *	@return translated and parsed text
	 */
	public String getMailText(boolean all)
	{
		return getMailText(all, true);
	}
	
	/**
	 * 	Get translated and parsed (if parsed argument is true) Mail Text
	 *	@param all true to concatenate mailtext, mailtext2 and mailtext3
	 *  @param parsed true to parsed variables in text
	 *	@return translated and parsed (if parsed argument is true) text
	 */
	public String getMailText(boolean all, boolean parsed)
	{
		return getMailText(all, parsed, false);
	}

	/**
	 * 	Get translated and parsed (if parsed argument is true) Mail Text
	 *	@param all true to concatenate mailtext, mailtext2 and mailtext3
	 *  @param parsed true to parsed variables in text
	 *  @param keepEscapeSequence if true, keeps the escape sequence '@@' in the parsed string. Otherwise, the '@@' escape sequence is used to keep '@' character in the string.
	 *	@return translated and parsed (if parsed argument is true) text
	 */
	public String getMailText(boolean all, boolean parsed, boolean keepEscapeSequence)
	{
		translate();
		if (!all)
			return parsed ? parse(m_MailText, keepEscapeSequence) : m_MailText;
		//
		StringBuilder sb = new StringBuilder();
		sb.append(m_MailText);
		String s = m_MailText2;
		if (s != null && s.length() > 0)
			sb.append("\n").append(s);
		s = m_MailText3;
		if (s != null && s.length() > 0)
			sb.append("\n").append(s);
		//
		return parsed ? parse(sb.toString(), keepEscapeSequence) : sb.toString();
	}	//	getMailText

	/**
	 * 	Get translated and parsed Mail Text
	 *	@return translated and parsed text
	 */
	public String getMailText()
	{
		return getMailText(false, true);
	}	//	getMailText
	
	/**
	 * 	Get translated and parsed Mail Text 2
	 *	@return translated and parsed text
	 */
	public String getMailText2()
	{
		translate();
		return parse (m_MailText2);
	}	//	getMailText2

	/**
	 * 	Get translated and parsed Mail Text 3
	 *	@return translated and parsed text
	 */
	public String getMailText3()
	{
		translate();
		return parse (m_MailText3);
	}	//	getMailText3

	/**
	 * 	Get translated and parsed Mail Header
	 *	@return translated and parsed text
	 */
	public String getMailHeader()
	{
		return getMailHeader(true);
	}
	
	/**
	 * 	Get translated and parsed (if parsed argument is true) Header
	 *  @param parsed true to parse variable in text
	 *	@return translated and parsed (if parsed argument is true) text
	 */
	public String getMailHeader(boolean parsed)
	{
		translate();
		if (m_MailHeader == null)
			return "";
		return parsed ? parse(m_MailHeader) : m_MailHeader;
	}	//	getMailHeader

	/**
	 * 	Parse variables in text (@variable expression@)
	 *	@param text text
	 *	@return parsed text
	 */
	protected String parse (String text)
	{
		return parse(text, false);
	}

	/**
	 * 	Parse variables in text (@variable expression@)
	 *	@param text text
	 *  @param keepEscapeSequence if true, keeps the escape sequence '@@' in the parsed string. Otherwise, the '@@' escape sequence is used to keep '@' character in the string.
	 *	@return parsed text
	 */
	protected String parse (String text, boolean keepEscapeSequence)
	{
		if (Util.isEmpty(text) || text.indexOf('@') == -1)
		{
			if (isHtml() && hasMarkdownText(text)) 
			{
				text = Core.getMarkdownRenderer().renderToHtml(text);
			}
			return text;
		}
		//	Parse User
		text = parse (text, m_user, (keepEscapeSequence || (m_bpartner != null || m_po != null)));
		//	Parse BP
		text = parse (text, m_bpartner, (keepEscapeSequence || m_po != null));
		//	Parse PO
		text = parse (text, m_po, keepEscapeSequence);
		//
		if (isHtml() && hasMarkdownText(text)) 
		{
			text = Core.getMarkdownRenderer().renderToHtml(text);
		}
		return text;
	}	//	parse
	
	/**
	 * Is text contains markdown
	 * @param text
	 * @return true if text contains markdown
	 */
	private boolean hasMarkdownText(String text) {
		return !Util.isEmpty(text) && text.indexOf(MARKDOWN_OPENING_TAG) >= 0 && text.indexOf(MARKDOWN_CLOSING_TAG) > 0;
	}
	
	/**
	 * 	Parse variables in text (@variable expression@)
	 *	@param text text
	 *	@param po PO instance
	 *	@param keepEscapeSequence if true, keeps the escape sequence '@@' in the parsed string. Otherwise, the '@@' escape sequence is used to keep '@' character in the string.
	 *	@return parsed text
	 */
	protected String parse (String text, PO po, boolean keepEscapeSequence)
	{
		if (po == null || Util.isEmpty(text) || text.indexOf('@') == -1)
			return text;
		
		String inStr = text;
		String token;
		StringBuilder outStr = new StringBuilder();

		int i = inStr.indexOf('@');
		while (i != -1)
		{
			outStr.append(inStr.substring(0, i));			// up to @
			inStr = inStr.substring(i+1, inStr.length());	// from first @

			int j = inStr.indexOf('@');						// next @
			if (j < 0)										// no second tag
			{
				inStr = "@" + inStr;
				break;
			}

			token = inStr.substring(0, j);
			String parseValue = parseVariable(token, po, keepEscapeSequence);
			if (keepEscapeSequence && !(("@"+token+"@").equals(parseValue))) 
			{
				if (parseValue.contains("@"))
					parseValue = parseValue.replace("@", "@@");
			}
			outStr.append(parseValue);		// replace context

			inStr = inStr.substring(j+1, inStr.length());	// from second @
			i = inStr.indexOf('@');
		}

		outStr.append(inStr);           					//	add remainder
		return outStr.toString();
	}	//	parse

	/**
	 * 	Get value for a variable expression
	 *	@param variable variable expression
	 *	@param po po
	 *	@param keepEscapeSequence if true, keeps the escape sequence '@@' in the parsed string. Otherwise, the '@@' escape sequence is used to keep '@' character in the string.
	 *	@return value for variable or if not found the original variable expression
	 */
	protected String parseVariable (String variable, PO po, boolean keepEscapeSequence)
	{		
		//JPIERE-0579 for Password Reset Mail with IsSecure='Y'
		if(po instanceof I_AD_User)
		{
			MColumn col = MColumn.get(Env.getCtx(), po.get_TableName(), variable);
			String value = null;
			if (col != null && col.isSecure() && col.getName().equals("Password"))
			{
				value = po.get_ValueAsString(variable);
				if(value != null)
					return value;
			}
		}//JPIERE-0579
		
		return Env.parseVariable("@"+variable+"@", po, get_TrxName(), true, true, true, keepEscapeSequence);
	}	//	translate
	
	/**
	 * 	Set User for parsing of text
	 *	@param AD_User_ID user
	 */
	public void setUser (int AD_User_ID)
	{
		m_user = MUser.get (getCtx(), AD_User_ID);
	}	//	setUser
	
	/**
	 * 	Set User for parsing of text
	 *	@param user MUser instance
	 */
	public void setUser (MUser user)
	{
		m_user = user;
	}	//	setUser
	
	/**
	 * 	Set BPartner for parsing of text
	 *	@param C_BPartner_ID bp
	 */
	public void setBPartner (int C_BPartner_ID)
	{
		m_bpartner = new MBPartner (getCtx(), C_BPartner_ID, get_TrxName());
	}	//	setBPartner
	
	/**
	 * 	Set BPartner for parsing of text
	 *	@param bpartner MBPartner instance
	 */
	public void setBPartner (MBPartner bpartner)
	{
		m_bpartner = bpartner;
	}	//	setBPartner

	/**
	 * 	Set PO for parsing of text
	 *	@param po PO instance
	 */
	public void setPO (PO po)
	{
		m_po = po;
	}	//	setPO

	/**
	 * 	Set PO for parsing of text
	 *	@param po PO instance
	 *	@param analyse true to search for BPartner/User from po
	 */
	public void setPO (PO po, boolean analyse)
	{
		m_po = po;
		if (analyse)
		{
			int index = po.get_ColumnIndex("C_BPartner_ID");
			if (index > 0)
			{
				Object oo = po.get_Value(index);
				if (oo instanceof Integer)
				{
					int C_BPartner_ID = ((Integer)oo).intValue();
					setBPartner(C_BPartner_ID);
				}
			}
			index = po.get_ColumnIndex("AD_User_ID");
			if (index > 0)
			{
				Object oo = po.get_Value(index);
				if (oo instanceof Integer)
				{
					int AD_User_ID = ((Integer)oo).intValue();
					setUser(AD_User_ID);
				}
			}
		}
	}	//	setPO

	/**
	 * 	Translate to BPartner Language or language from {@link #setLanguage(String)} call.
	 */
	protected void translate()
	{
		//	Default if no Translation
		m_MailHeader = super.getMailHeader();
		m_MailText = super.getMailText();
		m_MailText2 = super.getMailText2();
		m_MailText3 = super.getMailText3();
		if ((m_bpartner != null && m_bpartner.getAD_Language() != null) || !Util.isEmpty(m_language))
		{
			String adLanguage = m_bpartner != null && m_bpartner.getAD_Language() != null ? m_bpartner.getAD_Language() : m_language;
			StringBuilder key = new StringBuilder().append(adLanguage).append(get_ID());
			MMailTextTrl trl = s_cacheTrl.get(key.toString());
			if (trl == null)
			{
				trl = getTranslation(adLanguage);
				if (trl != null)
					s_cacheTrl.put(key.toString(), trl);
			}
			if (trl != null)
			{
				m_MailHeader = trl.MailHeader;
				m_MailText = trl.MailText;
				m_MailText2 = trl.MailText2;
				m_MailText3 = trl.MailText3;
			}
		}
	}	//	translate
	
	/**
	 * 	Get Translation
	 *	@param AD_Language language
	 *	@return MMailTextTrl
	 */
	protected MMailTextTrl getTranslation (String AD_Language)
	{
		MMailTextTrl trl = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM R_MailText_Trl WHERE R_MailText_ID=? AND AD_Language=?";
		try
		{
			pstmt = DB.prepareStatement (sql, null);
			pstmt.setInt (1, getR_MailText_ID());
			pstmt.setString(2, AD_Language);
			rs = pstmt.executeQuery ();
			if (rs.next())
			{
				trl = new MMailTextTrl();
				trl.AD_Language = rs.getString("AD_Language");
				trl.MailHeader = rs.getString("MailHeader");
				trl.MailText = rs.getString("MailText");
				trl.MailText2 = rs.getString("MailText2");
				trl.MailText3 = rs.getString("MailText3");
			}
		}
		catch (Exception e)
		{
			log.log (Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return trl;
	}	//	getTranslation
	
	/**
	 *	MailText Translation VO
	 */
	static class MMailTextTrl
	{
		/** Language			*/
		String		AD_Language = null;
		/** Translated Header	*/
		String		MailHeader = null;
		/** Translated Text		*/
		String		MailText = null;
		/** Translated Text 2	*/
		String		MailText2 = null;
		/** Translated Text 3	*/
		String		MailText3 = null;
	}	//	MMailTextTrl
	
	/**
	 * Set language for translation of text
	 * @param language
	 */
	public void setLanguage(String language)
	{
		m_language = language;
	}

	/**
	 * @return PO instance
	 */
	public PO getPO()
	{
		return m_po;
	}

	/**
	 * @return MBPartner instance
	 */
	public MBPartner getBPartner()
	{
		return m_bpartner;
	}

	/**
	 * @return language for translation of text
	 */
	public String getLanguage()
	{
		return m_language;
	}

	/**
	 * @return MUser instance
	 */
	public MUser getUser()
	{
		return m_user;
	}

}	//	MMailText
