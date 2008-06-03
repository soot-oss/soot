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
 * The purpose of this class is to automajically generate a options
 * dialog in the event that options change
 * 
 * Taking options away - should not damage the dialog
 * Adding new sections of options - should not damage the dialog
 * Adding new otpions to sections (of known option type) - should not
 * damage the dialog
 *
 * Adding new option types will break the dialog (option type widgets
 * will need to be created)
 *
 */

package ca.mcgill.sable.soot.testing;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import ca.mcgill.sable.soot.SootPlugin;


public class OptionsDialog extends AbstractOptionsDialog {

	public OptionsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * each section gets initialize as a stack layer in pageContainer
	 * the area containing the options
	 */ 
	protected void initializePageContainer() {
		<xsl:for-each select="section">
		Composite <xsl:value-of select="translate(name[last()],'-. ','___')"/>Child = <xsl:value-of select="translate(name[last()],'-. ','___')"/>Create(getPageContainer());
		</xsl:for-each>
	}

	/**
	 * all options get saved as &#60;alias, value&#62; pair
	 */ 
	protected void okPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();

		Control [] elements;
		<xsl:for-each select="section"> 
		
		<xsl:for-each select="boolopt">
		settings.put(get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().getSelection());
		</xsl:for-each>
		
		<xsl:for-each select="macroopt">
		settings.put(get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().getSelection());
		</xsl:for-each>
		
		<xsl:for-each select="listopt">
		settings.put(get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getText().getText());
		</xsl:for-each>
		
		<xsl:for-each select="stropt">
		settings.put(get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getText().getText());
		</xsl:for-each>
		
		<xsl:for-each select="multiopt"> 
		settings.put(get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getSelectedAlias());
		</xsl:for-each>
		
		</xsl:for-each>
		super.okPressed();
				
	}

	

	/**
	 * the initial input of selection tree corresponds to each section
	 * at some point sections will have sub-sections which will be
	 * children of branches (ie phase - options)
	 */ 
	protected SootOption getInitialInput() {
		SootOption root = new SootOption("");
		<xsl:for-each select="section">
		SootOption <xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>");
		root.addChild(<xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch);
		</xsl:for-each>
		return root;
	
	}
	
	

	/**
	 * each setion gets initalized with a composite
	 * containing widgets of option type
	 */
	<xsl:for-each select="section">

	<xsl:for-each select="boolopt">
	private BooleanOptionWidget <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	private void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(BooleanOptionWidget widget) {
		<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	private BooleanOptionWidget get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>

	<xsl:for-each select="listopt">
	private PathOptionWidget <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	private void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(PathOptionWidget widget) {
		<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	private PathOptionWidget get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>

	<xsl:for-each select="stropt">
	private StringOptionWidget <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	private void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(StringOptionWidget widget) {
		<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	private StringOptionWidget get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>

	<xsl:for-each select="macroopt">
	private BooleanOptionWidget <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	private void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(BooleanOptionWidget widget) {
		<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	private BooleanOptionWidget get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>
	
	<xsl:for-each select="multiopt">
	MultiOptionWidget <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	private void set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(MultiOptionWidget widget) {
		<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	private MultiOptionWidget get<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>

	
	
	private Composite <xsl:value-of select="translate(name[last()],'-. ','___')"/>Create(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("<xsl:value-of select="name"/>");
		
		
		
		<xsl:for-each select="boolopt">
		set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>", "<xsl:apply-templates select="short_desc"/>")));
		</xsl:for-each>
		
		<xsl:for-each select="multiopt">
		
		OptionData [] data = new OptionData [] {
		<xsl:for-each select="value">
		new OptionData("<xsl:value-of select="value"/>",
		"<xsl:value-of select="alias"/>",
		"<xsl:apply-templates select="short_desc"/>",
		<xsl:if test="default">
		true),
		</xsl:if>
		<xsl:if test="not(default)">
		false),
		</xsl:if>
		
		</xsl:for-each>
		};
		
										
		set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>", "<xsl:apply-templates select="short_desc"/>")));
		</xsl:for-each>
		
		<xsl:for-each select="listopt">
		set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new PathOptionWidget(editGroup, SWT.NONE, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>", "<xsl:apply-templates select="short_desc"/>")));
		</xsl:for-each>
		
		<xsl:for-each select="stropt">
		set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>", "<xsl:apply-templates select="short_desc"/>")));
		</xsl:for-each>

		<xsl:for-each select="macroopt">
		set<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>",  "<xsl:apply-templates select="short_desc"/>")));
		</xsl:for-each>
		
		return editGroup;
	}

	</xsl:for-each>
	
}

</xsl:template>

  <xsl:template match="use_arg_label">
  <xsl:choose>
    <xsl:when test="ancestor::*/set_arg_label">
      <xsl:value-of select="translate(string(ancestor::*/set_arg_label),
                            'abcdefghijklmnopqrstuvwxyz',
                            'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:when>
    <xsl:otherwise>ARG</xsl:otherwise>
  </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
