<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" indent="no"/>
    <xsl:strip-space elements="*"/>

<xsl:template match="options">

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * This class is generated automajically from xml - DO NO EDIT - as
 * changes will be over written
 * 
 * 
 * The purpose of this class is to automajically generate a handler
 * for the options dialog the event that options change
 * 
 * Taking options away - should not damage the handler
 * Adding new sections of options - should not damage the handler
 * Adding new otpions to sections (of known option type) - should not
 * damage the handler
 *
 * Adding new option types may cause the handler to produce 
 * incorrect results
 *
 */

package ca.mcgill.sable.soot.testing;

import java.util.*;
import org.eclipse.jface.dialogs.IDialogSettings;
import ca.mcgill.sable.soot.SootPlugin;

public class TestOptionsDialogHandler {


	private final static String SPACE = " ";
	private final static String DASH = "--";
	
	public TestOptionsDialogHandler() {
	}

	public String getCmdLine() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		StringBuffer cmd = new StringBuffer();
		String path = null;
		String defaultVal = null;
		boolean value = false;
		
		<xsl:for-each select="section"> 
		
		<xsl:for-each select="boolean_option">
		value = settings.getBoolean("<xsl:value-of select="alias_name"/>");
		if (value) {
			cmd.append(DASH);
			cmd.append("<xsl:value-of select="alias_name"/>");
			cmd.append(SPACE);
		}
		</xsl:for-each>
		
		<xsl:for-each select="macro_option">
		value = settings.getBoolean("<xsl:value-of select="alias_name"/>");
		if (value) {
			cmd.append(DASH);
			cmd.append("<xsl:value-of select="alias_name"/>");
			cmd.append(SPACE);
		}
		</xsl:for-each>
		
		<xsl:for-each select="path_option">
		path = settings.get("<xsl:value-of select="alias_name"/>");
		if ((path &#33;&#61; null) &#38;&#38; (path.length() &#33;&#61; 0)) {

			StringTokenizer st = new StringTokenizer(path);
			while (st.hasMoreTokens()) {
			
				cmd.append(DASH);
				cmd.append("<xsl:value-of select="alias_name"/>");
				cmd.append(SPACE);
				cmd.append(st.nextToken());
				cmd.append(SPACE);
			}
		}
		</xsl:for-each>
		
		<xsl:for-each select="string_option">
		path = settings.get("<xsl:value-of select="alias_name"/>");
		if ((path &#33;&#61; null) &#38;&#38; (path.length() &#33;&#61; 0)) {
			cmd.append(DASH);
			cmd.append("<xsl:value-of select="alias_name"/>");
			cmd.append(SPACE);
			cmd.append(path);
			cmd.append(SPACE);
		}
		</xsl:for-each>
		
		<xsl:for-each select="multi_option"> 
		path = settings.get("<xsl:value-of select="alias_name"/>");
		<xsl:for-each select="value">
		<xsl:if test="default">
		defaultVal = "<xsl:value-of select="alias"/>";
		</xsl:if>
		</xsl:for-each>
		
		if ((path &#33;&#61; null) &#38;&#38; (path.length() &#33;&#61; 0) &#38;&#38; (!path.equals(defaultVal))) {
			cmd.append(DASH);
			cmd.append("<xsl:value-of select="alias_name"/>");
			cmd.append(SPACE);
			cmd.append(path);
			cmd.append(SPACE);
		}
				
		</xsl:for-each>
		
		</xsl:for-each>
			
		return cmd.toString();
	}

}

</xsl:template>

</xsl:stylesheet>
