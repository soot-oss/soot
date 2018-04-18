<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no"/>
	<xsl:strip-space elements="*"/>

<xsl:template match="/options">

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
 * This class is generated automajically from xml - DO NOT EDIT - as
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

package ca.mcgill.sable.soot.ui;

import ca.mcgill.sable.soot.SootPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class PhaseOptionsDialog extends AbstractOptionsDialog implements SelectionListener {

	public PhaseOptionsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Each section gets initialize as a stack layer in pageContainer
	 * the area containing the options.
	 */ 
	protected void initializePageContainer() {<xsl:text/>

	<!--add section pages-->
	<xsl:apply-templates mode="pageCon" select="/options/section"/>

	<xsl:for-each select="section">
		<!--<xsl:variable name="parent" select="translate(name[last()],'-. ','___')"/>-->
		<xsl:for-each select="phaseopt">
			<xsl:apply-templates mode="pageCon" select="(phase|radio_phase)"/>

			<xsl:for-each select="(phase|radio_phase)">
				<xsl:apply-templates mode="pageCon" select="sub_phase">
					<xsl:with-param name="parent" select="translate(alias[last()],'-. ','___')"/>
				</xsl:apply-templates>
				<xsl:variable name="sectionParent" select="translate(alias[last()],'-. ','___')"/>
				<xsl:for-each select="sub_phase">
					<xsl:apply-templates mode="pageCon" select="section">
						<xsl:with-param name="parent" select="$sectionParent"/>
					</xsl:apply-templates>
				</xsl:for-each>
			</xsl:for-each>

		</xsl:for-each>
	</xsl:for-each>

		addOtherPages(getPageContainer());
		initializeRadioGroups();
		initializeEnableGroups();
	}

	private void initializeRadioGroups() {
		setRadioGroups(new HashMap&lt;&gt;());
		int counter = 0;
		ArrayList buttonList;<xsl:text/>

	<xsl:for-each select="section/phaseopt/radio_phase">
		buttonList = new ArrayList();<xsl:text/>

		<xsl:variable name="phase_alias" select="alias"/>
		<xsl:for-each select="sub_phase">

			<xsl:variable name="sub_phase_alias" select="translate(alias[last()],'-. ','___')"/>

			<xsl:for-each select="boolopt">
		if (isEnableButton("<xsl:value-of select="alias"/>")) {
			buttonList.add(get<xsl:value-of select="$phase_alias"/><xsl:value-of select="$sub_phase_alias"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget());	
			get<xsl:value-of select="$phase_alias"/><xsl:value-of select="$sub_phase_alias"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().addSelectionListener(this);
		}<xsl:text/>
			</xsl:for-each>
		</xsl:for-each>
		getRadioGroups().put(new Integer(counter), buttonList);

		counter++;<xsl:text/>
		</xsl:for-each>
	}

	private void initializeEnableGroups() {
		setEnableGroups(new ArrayList());

	<xsl:for-each select="section/phaseopt">
		<xsl:for-each select="(phase|radio_phase)">

			<xsl:variable name="phase_alias" select="alias"/>

		makeNewEnableGroup("<xsl:value-of select="$phase_alias"/>");<xsl:text/>

			<xsl:for-each select="(boolopt|stringopt|intopt|flopt|multiopt)">
		addToEnableGroup("<xsl:value-of select="$phase_alias"/>", get<xsl:value-of select="$phase_alias"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(), "<xsl:value-of select="alias"/>");<xsl:text/>
			</xsl:for-each>

			<xsl:for-each select="boolopt">
		get<xsl:value-of select="$phase_alias"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().addSelectionListener(this);<xsl:text/>
			</xsl:for-each>

			<xsl:for-each select="sub_phase">
				<xsl:variable name="sub_phase_alias_translated" select="translate(alias[last()],'-. ','___')"/>
				<xsl:variable name="sub_phase_alias" select="alias"/>

		makeNewEnableGroup("<xsl:value-of select="$phase_alias"/>", "<xsl:value-of select="alias"/>");<xsl:text/>

				<xsl:for-each select="(boolopt|stringopt|intopt|flopt|multiopt)">
		addToEnableGroup("<xsl:value-of select="$phase_alias"/>", "<xsl:value-of select="$sub_phase_alias"/>", get<xsl:value-of select="$phase_alias"/><xsl:value-of select="$sub_phase_alias_translated"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(), "<xsl:value-of select="alias"/>");<xsl:text/>
				</xsl:for-each>

				<xsl:for-each select="boolopt">
		get<xsl:value-of select="$phase_alias"/><xsl:value-of select="$sub_phase_alias_translated"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().addSelectionListener(this);<xsl:text/>
				</xsl:for-each>

				<xsl:for-each select="section">
					<xsl:for-each select="(boolopt|stringopt|intopt|flopt|multiopt)">
		addToEnableGroup("<xsl:value-of select="$phase_alias"/>", "<xsl:value-of select="$sub_phase_alias"/>", get<xsl:value-of select="$phase_alias"/><xsl:value-of select="$sub_phase_alias_translated"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(), "<xsl:value-of select="alias"/>");<xsl:text/>
					</xsl:for-each>

				</xsl:for-each>

			</xsl:for-each>

		</xsl:for-each>
	</xsl:for-each>

		updateAllEnableGroups();
	}

	public void widgetSelected(SelectionEvent e) {
		handleWidgetSelected(e);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	/**
	 * all options get saved as (alias, value) pair
	 */ 
	protected void okPressed() {
		if(createNewConfig())	
			super.okPressed();
		else {
			Shell defaultShell = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			String projectName = getSootMainProjectWidget().getText().getText();
			MessageDialog.openError(defaultShell, "Unable to find Soot Main Project", "Project "+projectName+" does not exist," +
					" is no Java project or is closed.");
		}
	}

	private boolean createNewConfig() {

		setConfig(new HashMap());

		boolean boolRes = false;
		String stringRes = "";
		boolean defBoolRes = false;
		String defStringRes = "";
		StringTokenizer listOptTokens;
		String nextListToken;

	<xsl:for-each select="section">
		<xsl:call-template name="createConfig">
			<xsl:with-param name="subParent" select="name"/>
		</xsl:call-template>

		<xsl:for-each select="(phaseopt/phase|phaseopt/radio_phase)">
			<xsl:call-template name="createConfig">
				<xsl:with-param name="subParent" select="alias"/>
			</xsl:call-template>

			<xsl:variable name="phaseAlias" select="alias"/>
			<xsl:for-each select="sub_phase">
				<xsl:call-template name="createConfig">
					<xsl:with-param name="parent" select="$phaseAlias"/>
					<xsl:with-param name="subParent" select="alias"/>
				</xsl:call-template>

				<xsl:variable name="subPhaseAlias" select="alias"/>
				<xsl:for-each select="section">
					<xsl:call-template name="createConfig">
						<xsl:with-param name="parent" select="$phaseAlias"/>
						<xsl:with-param name="subParent" select="$subPhaseAlias"/>
					</xsl:call-template>

				</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
	</xsl:for-each>

		setSootMainClass(getSootMainClassWidget().getText().getText());			
		return setSootMainProject(getSootMainProjectWidget().getText().getText());
	}

	protected HashMap savePressed() {

		createNewConfig();

		return getConfig();
	}



	/**
	 * the initial input of selection tree corresponds to each section
	 * at some point sections will have sub-sections which will be
	 * children of branches (ie phase - options)
	 */ 
	protected SootOption getInitialInput() {
		SootOption root = new SootOption("", "");
		SootOption parent;
		SootOption subParent;
		SootOption subSectParent;
		
<!--create branches for section pages-->
		<xsl:for-each select="section">
		SootOption <xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>", "<xsl:value-of select="translate(name[last()],'-. ','___')"/>");
		root.addChild(<xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch);
		parent = <xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch;		
		<xsl:for-each select="phaseopt">
		SootOption <xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>", "<xsl:value-of select="alias"/>");
		root.addChild(<xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch);

		parent = <xsl:value-of select="translate(name[last()],'-. ','___')"/>_branch;	

		<xsl:variable name="phase_opt_alias" select="alias"/>
		
		//<xsl:value-of select="name"/>
<!--create branches for phase pages-->
			<xsl:for-each select="(phase|radio_phase)">
			<xsl:variable name="parent" select="translate(alias[last()],'-. ','___')"/>
			//<xsl:value-of select="name"/>
			SootOption <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>", "<xsl:value-of select="translate(alias[last()],'-. ','___')"/>");
			parent.addChild(<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch);
			subParent = <xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch;


			<xsl:variable name="phase_alias" select="translate(alias[last()],'-. ','___')"/>
			
<!--create branches for sub-phase pages-->
			<xsl:for-each select="sub_phase">
			SootOption <xsl:value-of select="$parent"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>", "<xsl:value-of select="$phase_alias"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>");
			subParent.addChild(<xsl:value-of select="$parent"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch);


			
<!--create branches for sub-phase sub-section pages-->
			
			subSectParent = <xsl:value-of select="$parent"/>_<xsl:value-of select="translate(alias[last()],'-. ','___')"/>_branch;
			
			<xsl:for-each select="section">
			SootOption <xsl:value-of select="$parent"/>_<xsl:value-of select="translate((alias|name)[last()],'-. ','___')"/>_branch = new SootOption("<xsl:value-of select="name"/>", "<xsl:value-of select="$phase_alias"/><xsl:value-of select="translate(name[last()],'-. ','___')"/>");

			subSectParent.addChild(<xsl:value-of select="$parent"/>_<xsl:value-of select="translate((alias|name)[last()],'-. ','___')"/>_branch);
			</xsl:for-each>
			
			</xsl:for-each>
			</xsl:for-each>
		</xsl:for-each>
		
		</xsl:for-each>

		addOtherBranches(root);
		return root;
	
	}

<!--Object Creation-->
		
		<xsl:for-each select="/options/section">
		<xsl:call-template name="objCreation">
		<xsl:with-param name="subParent" select="name"/>
		</xsl:call-template>

		<xsl:for-each select="phaseopt">
	
		<xsl:for-each select="(phase|radio_phase)">
		
		<xsl:call-template name="objCreation">
		<xsl:with-param name="subParent" select="alias"/>
		</xsl:call-template>

		<xsl:variable name="phaseAlias" select="alias"/>

		<xsl:for-each select="sub_phase">
		
		<xsl:call-template name="objCreation">
		<xsl:with-param name="parent" select="$phaseAlias"/>
		<xsl:with-param name="subParent" select="alias"/>
		</xsl:call-template>

		<xsl:variable name="subPhaseAlias" select="alias"/>
		<xsl:for-each select="section">
		
		<xsl:call-template name="objCreation">
		<xsl:with-param name="parent" select="$phaseAlias"/>
		<xsl:with-param name="subParent" select="$subPhaseAlias"/>
		</xsl:call-template>
		
		</xsl:for-each>
		</xsl:for-each>
		</xsl:for-each>
		
		</xsl:for-each>
		</xsl:for-each>


<!--Component Initialization-->

		<xsl:for-each select="/options/section">
		<xsl:call-template name="compInit">
                <xsl:with-param name="description"><xsl:apply-templates select="short_desc"/></xsl:with-param>
		<xsl:with-param name="subParent" select="translate(name[last()],'-. ','___')"/>
		<xsl:with-param name="name" select="name"/>
		<xsl:with-param name="callName" select="translate(name[last()],'-. ','___')"/>
		</xsl:call-template>

		<xsl:for-each select="phaseopt">
		<xsl:variable name="phaseOptAlias" select="alias"/>
	
		<xsl:for-each select="(phase|radio_phase)">
		
		<xsl:call-template name="compInit">
                <xsl:with-param name="description"><xsl:apply-templates select="short_desc"/></xsl:with-param>
		<xsl:with-param name="subParent" select="translate(alias[last()],'-. ','___')"/>
		<xsl:with-param name="parentAlias" select="$phaseOptAlias"/>
		<xsl:with-param name="subParentAlias" select="alias"/>
		<xsl:with-param name="name" select="name"/>
		<xsl:with-param name="callName" select="translate(alias[last()],'-. ','___')"/>
		</xsl:call-template>

		<xsl:variable name="phaseAlias" select="alias"/>

		<xsl:for-each select="sub_phase">
		
		<xsl:call-template name="compInit">
                <xsl:with-param name="description"><xsl:apply-templates select="short_desc"/></xsl:with-param>
		<xsl:with-param name="parent" select="$phaseAlias"/>
		<xsl:with-param name="subParent" select="translate(alias[last()],'-. ','___')"/>
		<xsl:with-param name="parentAlias" select="$phaseOptAlias"/>
		<xsl:with-param name="subParentAlias" select="alias"/>
		<xsl:with-param name="name" select="name"/>
		<xsl:with-param name="callName" select="translate(alias[last()],'-. ','___')"/>
		</xsl:call-template>

		<xsl:variable name="subPhaseAlias" select="alias"/>
		<xsl:for-each select="section">
		
		<xsl:call-template name="compInit">
                <xsl:with-param name="description"><xsl:apply-templates select="short_desc"/></xsl:with-param>
		<xsl:with-param name="parent" select="$phaseAlias"/>
		<xsl:with-param name="subParent" select="translate($subPhaseAlias[last()],'-. ','___')"/>
		<xsl:with-param name="parentAlias" select="$phaseOptAlias"/>
		<xsl:with-param name="subParentAlias" select="$subPhaseAlias"/>
		<xsl:with-param name="name" select="name"/>
		<xsl:with-param name="callName" select="translate(name[last()],'-. ','___')"/>
		</xsl:call-template>
		
		</xsl:for-each>
		</xsl:for-each>
		</xsl:for-each>
		
		</xsl:for-each>
		</xsl:for-each>


}

</xsl:template>

<!--PAGE CONTAINER CREATION TEMPLATE-->

	<xsl:template mode="pageCon" match="section|phase|radio_phase|sub_phase">
		<xsl:param name="parent"/>
		<xsl:variable name="java_name" select="translate((alias|name)[last()],'-. ','___')"/>
		Composite <xsl:text/>
		<xsl:if test="$parent != ''">
			<xsl:copy-of select="$parent"/>
		</xsl:if>
		<xsl:text/>
		<xsl:copy-of select="$java_name"/>
		<xsl:text>Child = </xsl:text>
		<xsl:if test="$parent != ''">
			<xsl:copy-of select="$parent"/>
		</xsl:if>
		<xsl:text/>
		<xsl:copy-of select="$java_name"/>
		<xsl:text>Create(getPageContainer());</xsl:text>
	</xsl:template>


<!--CREATE CONFIG TEMPLATE-->
	<xsl:template name="createConfig">
		<xsl:param name="parent"/>
		<xsl:param name="subParent"/>

		<xsl:for-each select="boolopt|macroopt">
		boolRes = get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getButton().getSelection();<xsl:text/>

			<xsl:choose>
				<xsl:when test="default">
		defBoolRes = <xsl:value-of select="default"/>;<xsl:text/>
				</xsl:when>
				<xsl:otherwise>
		defBoolRes = false;<xsl:text/>
				</xsl:otherwise>
			</xsl:choose>

		if (boolRes &#33;&#61; defBoolRes) {
			getConfig().put(get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), new Boolean(boolRes));
		}<xsl:text/>
		</xsl:for-each>


		<xsl:for-each select="stropt|intopt|flopt|listopt">
		stringRes = get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getText().getText();<xsl:text/>

			<xsl:choose>
				<xsl:when test="default">
		defStringRes = "<xsl:value-of select="default"/>";<xsl:text/>
				</xsl:when>
				<xsl:otherwise>
		defStringRes = "";<xsl:text/>
				</xsl:otherwise>
			</xsl:choose>

		if ((&#33;(stringRes.equals(defStringRes))) &#38;&#38; (stringRes &#33;&#61; null) &#38;&#38; (stringRes.length() &#33;&#61; 0)) {
			getConfig().put(get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), stringRes);
		}<xsl:text/>
		</xsl:for-each>


		<xsl:for-each select="multiopt">
		stringRes = get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getSelectedAlias();<xsl:text/>

			<xsl:for-each select="value">
				<xsl:if test="default">
		defStringRes = "<xsl:value-of select="alias"/>";<xsl:text/>
				</xsl:if>
			</xsl:for-each>

		if (!stringRes.equals(defStringRes)) {
			getConfig().put(get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().getAlias(), stringRes);
		}<xsl:text/>
		</xsl:for-each><xsl:text/>

	</xsl:template>


<!--COMPOSITE INITIALIZATION TEMPLATE-->
<xsl:template name="compInit">

<xsl:param name="description"/>
<xsl:param name="parent"/>
<xsl:param name="subParent"/>
<xsl:param name="parentAlias"/>
<xsl:param name="subParentAlias"/>
<xsl:param name="callName"/>
<xsl:param name="name"/>

	private Composite <xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>Create(Composite parent) {
		String defKey;
		String defaultString;
		boolean defaultBool = false;
	    String defaultArray;
       
		Group editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/> = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>.setLayout(layout);
	
	 	editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>.setText("<xsl:value-of select="$name"/>");
	 	
		editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>.setData("id", "<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>");
		
		String desc<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/> = "<xsl:call-template name="string-replace"><xsl:with-param name="text" select="$description"/><xsl:with-param name="from" select="'&#10;'"/><xsl:with-param name="to" select="'&#92;n'"/></xsl:call-template>";	
		if (desc<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>.length() > 0) {
			Label descLabel<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/> = new Label(editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>, SWT.WRAP);
			descLabel<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>.setText(desc<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>);
		}
		OptionData [] data;	
		
<!--Boolean and Macro Widget-->		
		<xsl:for-each select="boolopt|macroopt"><xsl:text/>

		defKey = "<xsl:value-of select="$parentAlias"/>"+" "+"<xsl:value-of select="$subParentAlias"/>"+" "+"<xsl:value-of select="alias"/>";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultBool = getBoolDef(defKey);	
		} else {<xsl:text/>
			<xsl:choose>
				<xsl:when test="default">
			defaultBool = <xsl:value-of select="default"/>;<xsl:text/>
				</xsl:when>
				<xsl:otherwise>
			defaultBool = false;<xsl:text/>
				</xsl:otherwise>
			</xsl:choose>
		}

		set<xsl:value-of select="$parent"/><xsl:value-of select="$subParent"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new BooleanOptionWidget(editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>, SWT.NONE, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="$parentAlias"/>", "<xsl:value-of select="$subParentAlias"/>","<xsl:value-of select="alias"/>", "<xsl:call-template name="format-tooltips"><xsl:with-param name="text"><xsl:apply-templates select="long_desc"/></xsl:with-param></xsl:call-template>", defaultBool)));<xsl:text/>
		</xsl:for-each>
		
<!--Multi Widget-->
	<xsl:for-each select="multiopt">

		data = new OptionData [] {
		<xsl:for-each select="value">
				new OptionData("<xsl:value-of select="name"/>",
						"<xsl:value-of select="alias"/>",
						"<xsl:call-template name="format-tooltips"><xsl:with-param name="text"><xsl:apply-templates select="long_desc"/></xsl:with-param></xsl:call-template>",<xsl:text/>
			<xsl:choose>
				<xsl:when test="default">
						true),</xsl:when>
				<xsl:otherwise>
						false),</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		};


		set<xsl:value-of select="$parent"/><xsl:value-of select="$subParent"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new MultiOptionWidget(editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>, SWT.NONE, data, new OptionData("<xsl:value-of select="name"/>", "<xsl:value-of select="$parentAlias"/>", "<xsl:value-of select="$subParentAlias"/>","<xsl:value-of select="alias"/>", "<xsl:call-template name="format-tooltips"><xsl:with-param name="text"><xsl:apply-templates select="long_desc"/></xsl:with-param></xsl:call-template>")));

		defKey = "<xsl:value-of select="$parentAlias"/>"+" "+"<xsl:value-of select="$subParentAlias"/>"+" "+"<xsl:value-of select="alias"/>";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);

			get<xsl:value-of select="$parent"/><xsl:value-of select="$subParent"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget().setDef(defaultString);
		}</xsl:for-each>
		
<!--Path Widget-->
	<xsl:for-each select="listopt">

		defKey = "<xsl:value-of select="$parentAlias"/>"+" "+"<xsl:value-of select="$subParentAlias"/>"+" "+"<xsl:value-of select="alias"/>";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getArrayDef(defKey);	
		} else {<xsl:text/>
		<xsl:choose>
			<xsl:when test="default">
			defaultString = "<xsl:value-of select="default"/>";</xsl:when>
			<xsl:otherwise>
			defaultString = "";</xsl:otherwise>
		</xsl:choose>
		}

		set<xsl:value-of select="$parent"/><xsl:value-of select="$subParent"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new ListOptionWidget(editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>, SWT.NONE, new OptionData("<xsl:value-of select="name"/>",  "<xsl:value-of select="$parentAlias"/>", "<xsl:value-of select="$subParentAlias"/>","<xsl:value-of select="alias"/>", "<xsl:call-template name="format-tooltips"><xsl:with-param name="text"><xsl:apply-templates select="long_desc"/></xsl:with-param></xsl:call-template>", defaultString)));
		</xsl:for-each>
		
<!--String, Int and Float Widget-->
	<xsl:for-each select="stropt|intopt|flopt">

		defKey = "<xsl:value-of select="$parentAlias"/>"+" "+"<xsl:value-of select="$subParentAlias"/>"+" "+"<xsl:value-of select="alias"/>";
		defKey = defKey.trim();

		if (isInDefList(defKey)) {
			defaultString = getStringDef(defKey);	
		} else {<xsl:text/>
			<xsl:choose>
				<xsl:when test="default">
			defaultString = "<xsl:value-of select="default"/>";</xsl:when>
				<xsl:otherwise>
			defaultString = "";</xsl:otherwise>
			</xsl:choose>
		}

		set<xsl:value-of select="$parent"/><xsl:value-of select="$subParent"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(new StringOptionWidget(editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>, SWT.NONE, new OptionData("<xsl:value-of select="name"/>",  "<xsl:value-of select="$parentAlias"/>", "<xsl:value-of select="$subParentAlias"/>","<xsl:value-of select="alias"/>", "<xsl:call-template name="format-tooltips"><xsl:with-param name="text"><xsl:apply-templates select="long_desc"/></xsl:with-param></xsl:call-template>", defaultString)));
		</xsl:for-each>


		return editGroup<xsl:value-of select="$parent"/><xsl:value-of select="$callName"/>;
	}

</xsl:template>

<!--OBJECT CREATION TEMPLATE-->

<xsl:template name="objCreation">

<xsl:param name="parent"/>
<xsl:param name="subParent"/>
<!--<xsl:param name="alias"/>-->
<!--<xsl:variable name="subParent" select="translate((alias|name)[last()],'-. ','___')"/>
-->

<!--Boolean and Macro Object Creation-->

	<xsl:for-each select="boolopt|macroopt">
	private BooleanOptionWidget <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	
	private void set<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(BooleanOptionWidget widget) {
		<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	
	public BooleanOptionWidget get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	</xsl:for-each>

<!--Path Object Creation-->

	<xsl:for-each select="listopt">

	private ListOptionWidget <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	
	private void set<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(ListOptionWidget widget) {
		<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	
	public ListOptionWidget get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	
	</xsl:for-each>

<!--String, Int and Float Object Creation-->

	<xsl:for-each select="stropt|intopt|flopt">
	
	private StringOptionWidget <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	
	private void set<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(StringOptionWidget widget) {
		<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	
	public StringOptionWidget get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}
	
	</xsl:for-each>

	
<!--Multi Object Creation-->

	<xsl:for-each select="multiopt">
	
	private MultiOptionWidget <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	
	private void set<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget(MultiOptionWidget widget) {
		<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget = widget;
	}
	
	public MultiOptionWidget get<xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget() {
		return <xsl:value-of select="$parent"/><xsl:value-of select="translate($subParent[last()],'-. ','___')"/><xsl:value-of select="translate(alias[last()],'-. ','___')"/>_widget;
	}	
	
	</xsl:for-each>
	

</xsl:template>

 <!-- reusable replace-string function -->
 <xsl:template name="string-replace">
    <xsl:param name="text"/>
    <xsl:param name="from"/>
    <xsl:param name="to"/>

    <xsl:choose>
      <xsl:when test="contains($text, $from)">

	<xsl:variable name="before" select="substring-before($text, $from)"/>
	<xsl:variable name="after" select="substring-after($text, $from)"/>
	<xsl:variable name="prefix" select="concat($before, $to)"/>

	<xsl:value-of select="$before"/>
	<xsl:value-of select="$to"/>
        <xsl:call-template name="string-replace">
	  <xsl:with-param name="text" select="$after"/>
	  <xsl:with-param name="from" select="$from"/>
	  <xsl:with-param name="to" select="$to"/>
	</xsl:call-template>
      </xsl:when> 
      <xsl:otherwise>
        <xsl:value-of select="$text"/>  
      </xsl:otherwise>
    </xsl:choose>            
 </xsl:template>

	<xsl:template name="format-tooltips">
		<xsl:param name="text"/>
		<xsl:call-template name="format-tooltips-guts">
			<xsl:with-param name="text" select="translate(normalize-space($text),'&#10;',' ')"/>
			<xsl:with-param name="width" select='0'/>
		</xsl:call-template>
	</xsl:template>

  <xsl:template name="format-tooltips-guts">
    <xsl:param name="text"/>
    <xsl:param name="width"/>
    <xsl:variable name="print" select="concat(substring-before(concat($text,' '),' '),' ')"/>
    <xsl:choose>
      <xsl:when test="string-length($print) > number($width)">
      <xsl:text>&#92;n</xsl:text>
          <xsl:call-template name="format-tooltips-guts">
            <xsl:with-param name="text" select="$text"/>
            <xsl:with-param name="width" select='65'/>
          </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
        <xsl:copy-of select="substring($print,1,string-length($print)-1)"/>
        <xsl:if test="contains($text,' ')">
          <xsl:if test="string-length($print) > 1">
            <xsl:text> </xsl:text>
          </xsl:if>
          <xsl:call-template name="format-tooltips-guts">
            <xsl:with-param name="text" select="substring-after($text,' ')"/>
            <xsl:with-param name="width" select="number($width) - string-length($print)"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
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
