/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
 *
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
 */



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

//import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.*;
//import org.eclipse.swt.*;
//import org.eclipse.swt.layout.*;
//import ca.mcgill.sable.soot.SootPlugin;


public class OptionsDialog {//extends AbstractOptionsDialog {

	public OptionsDialog(Shell parentShell) {
		//super(parentShell);
	}
	
	/*/**
	 * each section gets initialize as a stack layer in pageContainer
	 * the area containing the options
	 */ 
	/*protected void initializePageContainer() {
		
		Composite generalOptsChild = generalOptsCreate(getPageContainer());
		
		Composite inputOptsChild = inputOptsCreate(getPageContainer());
		
		Composite outputOptsChild = outputOptsCreate(getPageContainer());
		
		Composite processingOptsChild = processingOptsCreate(getPageContainer());
		
		Composite singleFileOptsChild = singleFileOptsCreate(getPageContainer());
		
		Composite appOptsChild = appOptsCreate(getPageContainer());
		
		Composite inputAttrOptsChild = inputAttrOptsCreate(getPageContainer());
		
		Composite annotationOptsChild = annotationOptsCreate(getPageContainer());
		
		Composite miscOptsChild = miscOptsCreate(getPageContainer());
		
	}

	/**
	 * all options get saved as <alias, value> pair
	 */ 
	/*protected void okPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();

		Control [] elements;
		
		settings.put(gethelp_widget().getAlias(), gethelp_widget().getButton().getSelection());
		
		settings.put(getversion_widget().getAlias(), getversion_widget().getButton().getSelection());
		
		settings.put(getverbose_widget().getAlias(), getverbose_widget().getButton().getSelection());
		
		settings.put(getappMode_widget().getAlias(), getappMode_widget().getButton().getSelection());
		
		settings.put(getclasspath_widget().getAlias(), getclasspath_widget().getText().getText());
		 
		settings.put(getsrcPrec_widget().getAlias(), getsrcPrec_widget().getSelectedAlias());
		
		settings.put(getoutputDir_widget().getAlias(), getoutputDir_widget().getText().getText());
		 
		settings.put(getoutputFormat_widget().getAlias(), getoutputFormat_widget().getSelectedAlias());
		
		settings.put(getoptimize_widget().getAlias(), getoptimize_widget().getButton().getSelection());
		
		settings.put(getwholeOptimize_widget().getAlias(), getwholeOptimize_widget().getButton().getSelection());
		
		settings.put(getprocPath_widget().getAlias(), getprocPath_widget().getText().getText());
		
		settings.put(getanalyzeContext_widget().getAlias(), getanalyzeContext_widget().getButton().getSelection());
		
		settings.put(getincPackage_widget().getAlias(), getincPackage_widget().getText().getText());
		
		settings.put(getexcPackage_widget().getAlias(), getexcPackage_widget().getText().getText());
		
		settings.put(getdynClasses_widget().getAlias(), getdynClasses_widget().getText().getText());
		
		settings.put(getdynPath_widget().getAlias(), getdynPath_widget().getText().getText());
		
		settings.put(getdynPackage_widget().getAlias(), getdynPackage_widget().getText().getText());
		
		settings.put(getkeepLineNum_widget().getAlias(), getkeepLineNum_widget().getButton().getSelection());
		
		settings.put(getkeepByteOffset_widget().getAlias(), getkeepByteOffset_widget().getButton().getSelection());
		
		settings.put(getnullPointerAnn_widget().getAlias(), getnullPointerAnn_widget().getButton().getSelection());
		
		settings.put(getarrayBoundsAnn_widget().getAlias(), getarrayBoundsAnn_widget().getButton().getSelection());
		
		settings.put(gettime_widget().getAlias(), gettime_widget().getButton().getSelection());
		
		settings.put(getsubGC_widget().getAlias(), getsubGC_widget().getButton().getSelection());
		
		super.okPressed();
				
	}

	

	/**
	 * the initial input of selection tree corresponds to each section
	 * at some point sections will have sub-sections which will be
	 * children of branches (ie phase - options)
	 */ 
	/*protected SootOption getInitialInput() {
		SootOption root = new SootOption("");
		
		SootOption generalOpts_branch = new SootOption("General Options");
		root.addChild(generalOpts_branch);
		
		SootOption inputOpts_branch = new SootOption("Input Options");
		root.addChild(inputOpts_branch);
		
		SootOption outputOpts_branch = new SootOption("Output Options");
		root.addChild(outputOpts_branch);
		
		SootOption processingOpts_branch = new SootOption("Processing Options");
		root.addChild(processingOpts_branch);
		
		SootOption singleFileOpts_branch = new SootOption("Single File Mode Options");
		root.addChild(singleFileOpts_branch);
		
		SootOption appOpts_branch = new SootOption("Application Mode Options");
		root.addChild(appOpts_branch);
		
		SootOption inputAttrOpts_branch = new SootOption("Input Attribute Options");
		root.addChild(inputAttrOpts_branch);
		
		SootOption annotationOpts_branch = new SootOption("Annotation Options");
		root.addChild(annotationOpts_branch);
		
		SootOption miscOpts_branch = new SootOption("Miscellaneous Options");
		root.addChild(miscOpts_branch);
		
		return root;
	
	}
	
	

	/**
	 * each setion gets initalized with a composite
	 * containing widgets of option type
	 */
	
	/*private BooleanOptionWidget help_widget;
	private void sethelp_widget(BooleanOptionWidget widget) {
		help_widget = widget;
	}
	private BooleanOptionWidget gethelp_widget() {
		return help_widget;
	}	
	
	private BooleanOptionWidget version_widget;
	private void setversion_widget(BooleanOptionWidget widget) {
		version_widget = widget;
	}
	private BooleanOptionWidget getversion_widget() {
		return version_widget;
	}	
	
	private BooleanOptionWidget verbose_widget;
	private void setverbose_widget(BooleanOptionWidget widget) {
		verbose_widget = widget;
	}
	private BooleanOptionWidget getverbose_widget() {
		return verbose_widget;
	}	
	
	private BooleanOptionWidget appMode_widget;
	private void setappMode_widget(BooleanOptionWidget widget) {
		appMode_widget = widget;
	}
	private BooleanOptionWidget getappMode_widget() {
		return appMode_widget;
	}	
	

	
	
	private Composite generalOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("General Options");
		
		
		
		
		sethelp_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Help", "h", "display help and exit")));
		
		setversion_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Version", "version", "output version information and exit")));
		
		setverbose_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Verbose", "v", "verbose mode")));
		
		setappMode_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Application Mode", "app", "runs in application mode")));
		
		
		return editGroup;
	}

	
	private ListOptionWidget classpath_widget;
	private void setclasspath_widget(ListOptionWidget widget) {
		classpath_widget = widget;
	}
	private ListOptionWidget getclasspath_widget() {
		return classpath_widget;
	}	
	
	MultiOptionWidget srcPrec_widget;
	private void setsrcPrec_widget(MultiOptionWidget widget) {
		srcPrec_widget = widget;
	}
	private MultiOptionWidget getsrcPrec_widget() {
		return srcPrec_widget;
	}	
	

	
	
	private Composite inputOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Input Options");
		
		
		
		
		
		OptionData [] data = new OptionData [] {
		
		new OptionData("Class File",
		"c",
		"Use class for source of Soot",
		
		true),
		
		new OptionData("Jimple File",
		"J",
		"Use Jimple for source of Soot",
		
		false),
		
		};
		
										
		setsrcPrec_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Input Source Precedence", "src-prec", "sets the source precedence for Soot")));
		
		setclasspath_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Soot Classpath", "cp", "uses given PATH as the classpath for finding classes for Soot processing")));
		
		
		return editGroup;
	}

	
	private StringOptionWidget outputDir_widget;
	private void setoutputDir_widget(StringOptionWidget widget) {
		outputDir_widget = widget;
	}
	private StringOptionWidget getoutputDir_widget() {
		return outputDir_widget;
	}	
	
	MultiOptionWidget outputFormat_widget;
	private void setoutputFormat_widget(MultiOptionWidget widget) {
		outputFormat_widget = widget;
	}
	private MultiOptionWidget getoutputFormat_widget() {
		return outputFormat_widget;
	}	
	

	
	
	private Composite outputOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Output Options");
		
		
		
		
		
		OptionData [] data = new OptionData [] {
		
		new OptionData("Jimp File",
		"j",
		"produce .jimp (abbreviated .jimple) files",
		
		false),
		
		new OptionData("Njimple File",
		"njimple",
		"produce .njimple files",
		
		false),
		
		new OptionData("Jimple File",
		"J",
		"produce .jimple code",
		
		false),
		
		new OptionData("Baf File",
		"B",
		"produce .baf code",
		
		false),
		
		new OptionData("Aggregated Baf File",
		"b",
		"produce .b (abbreviated .baf) files",
		
		false),
		
		new OptionData("Grimp File",
		"g",
		"produce .grimp (abbreviated .grimple) files",
		
		false),
		
		new OptionData("Grimple File",
		"G",
		"produce .grimple files",
		
		false),
		
		new OptionData("Xml File",
		"X",
		"produce .xml files",
		
		false),
		
		new OptionData("No Output File",
		"n",
		"produces no output",
		
		false),
		
		new OptionData("Jasmin File",
		"s",
		"produce .jasmin files",
		
		false),
		
		new OptionData("Jasmin Through Grimp File",
		"jasmin-through-grimp",
		"produce .jasmin files using grimp as final IR?",
		
		false),
		
		new OptionData("Class File",
		"c",
		"produce .class files",
		
		true),
		
		new OptionData("Class Through Grimp File",
		"class-through-grimp",
		"produce .class files using grimp as final IR?",
		
		false),
		
		new OptionData("Dava Decompiled File",
		"d",
		"produce dava decompiled .java files",
		
		false),
		
		};
		
										
		setoutputFormat_widget(new MultiOptionWidget(editGroup, SWT.NONE, data, new OptionData("Output Format", "o", "sets the source precedence for Soot")));
		
		setoutputDir_widget(new StringOptionWidget(editGroup, SWT.NONE, new OptionData("Output Directory", "d", "store produced files in PATH")));
		
		
		return editGroup;
	}

	
	private BooleanOptionWidget optimize_widget;
	private void setoptimize_widget(BooleanOptionWidget widget) {
		optimize_widget = widget;
	}
	private BooleanOptionWidget getoptimize_widget() {
		return optimize_widget;
	}	
	
	private BooleanOptionWidget wholeOptimize_widget;
	private void setwholeOptimize_widget(BooleanOptionWidget widget) {
		wholeOptimize_widget = widget;
	}
	private BooleanOptionWidget getwholeOptimize_widget() {
		return wholeOptimize_widget;
	}	
	

	
	
	private Composite processingOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Processing Options");
		
		
		
		
		setoptimize_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Optimize", "O",  "perform scalar optimizations on the classfiles")));
		
		setwholeOptimize_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Whole Program Optimize", "W",  "perform whole program optimizations on the classfiles")));
		
		
		return editGroup;
	}

	
	private ListOptionWidget procPath_widget;
	private void setprocPath_widget(ListOptionWidget widget) {
		procPath_widget = widget;
	}
	private ListOptionWidget getprocPath_widget() {
		return procPath_widget;
	}	
	

	
	
	private Composite singleFileOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Single File Mode Options");
		
		
		
		
		setprocPath_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Process Path", "process-path", "process all classes on the PATH")));
		
		
		return editGroup;
	}

	
	private BooleanOptionWidget analyzeContext_widget;
	private void setanalyzeContext_widget(BooleanOptionWidget widget) {
		analyzeContext_widget = widget;
	}
	private BooleanOptionWidget getanalyzeContext_widget() {
		return analyzeContext_widget;
	}	
	
	private ListOptionWidget incPackage_widget;
	private void setincPackage_widget(ListOptionWidget widget) {
		incPackage_widget = widget;
	}
	private ListOptionWidget getincPackage_widget() {
		return incPackage_widget;
	}	
	
	private ListOptionWidget excPackage_widget;
	private void setexcPackage_widget(ListOptionWidget widget) {
		excPackage_widget = widget;
	}
	private ListOptionWidget getexcPackage_widget() {
		return excPackage_widget;
	}	
	
	private ListOptionWidget dynClasses_widget;
	private void setdynClasses_widget(ListOptionWidget widget) {
		dynClasses_widget = widget;
	}
	private ListOptionWidget getdynClasses_widget() {
		return dynClasses_widget;
	}	
	
	private ListOptionWidget dynPath_widget;
	private void setdynPath_widget(ListOptionWidget widget) {
		dynPath_widget = widget;
	}
	private ListOptionWidget getdynPath_widget() {
		return dynPath_widget;
	}	
	
	private ListOptionWidget dynPackage_widget;
	private void setdynPackage_widget(ListOptionWidget widget) {
		dynPackage_widget = widget;
	}
	private ListOptionWidget getdynPackage_widget() {
		return dynPackage_widget;
	}	
	

	
	
	private Composite appOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Application Mode Options");
		
		
		
		
		setanalyzeContext_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Analyze Context", "a", "label context classes as library")));
		
		setincPackage_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Include Package", "i", "marks classfiles in PACKAGE (e.g. java.util.)as application classes")));
		
		setexcPackage_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Exclude Package", "x", "marks classfiles in PACKAGE (e.g. java.) as context classes")));
		
		setdynClasses_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Classes", "dynamic-classes", "marks CLASSES (separated by colons) as potentially dynamic classes")));
		
		setdynPath_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Path", "dynamic-path", "marks all class files in PATH as potentially dynamic classes")));
		
		setdynPackage_widget(new ListOptionWidget(editGroup, SWT.NONE, new OptionData("Dynamic Package", "dynamic-package", "marks classfiles in PACKAGES (separated by commas) as potentially dynamic classes")));
		
		
		return editGroup;
	}

	
	private BooleanOptionWidget keepLineNum_widget;
	private void setkeepLineNum_widget(BooleanOptionWidget widget) {
		keepLineNum_widget = widget;
	}
	private BooleanOptionWidget getkeepLineNum_widget() {
		return keepLineNum_widget;
	}	
	
	private BooleanOptionWidget keepByteOffset_widget;
	private void setkeepByteOffset_widget(BooleanOptionWidget widget) {
		keepByteOffset_widget = widget;
	}
	private BooleanOptionWidget getkeepByteOffset_widget() {
		return keepByteOffset_widget;
	}	
	

	
	
	private Composite inputAttrOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE );
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Input Attribute Options");
		
		
		
		
		setkeepLineNum_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Keep Line Number", "keep-line-number", "keep line number tables")));
		
		setkeepByteOffset_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Keep Bytecode Offset", "keep-bytecode-offset", "attach bytecode offset to jimple statement")));
		
		
		return editGroup;
	}

	
	private BooleanOptionWidget nullPointerAnn_widget;
	private void setnullPointerAnn_widget(BooleanOptionWidget widget) {
		nullPointerAnn_widget = widget;
	}
	private BooleanOptionWidget getnullPointerAnn_widget() {
		return nullPointerAnn_widget;
	}	
	
	private BooleanOptionWidget arrayBoundsAnn_widget;
	private void setarrayBoundsAnn_widget(BooleanOptionWidget widget) {
		arrayBoundsAnn_widget = widget;
	}
	private BooleanOptionWidget getarrayBoundsAnn_widget() {
		return arrayBoundsAnn_widget;
	}	
	

	
	
	private Composite annotationOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE );
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Annotation Options");
		
		
		
		
		setnullPointerAnn_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Null Pointer Annotation", "annot-nullpointer",  "turn on the annotation for null pointer")));
		
		setarrayBoundsAnn_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Array Bounds Annotation", "annot-arraybounds",  "turn on the annotation for array bounds check")));
		
		
		return editGroup;
	}

	
	private BooleanOptionWidget time_widget;
	private void settime_widget(BooleanOptionWidget widget) {
		time_widget = widget;
	}
	private BooleanOptionWidget gettime_widget() {
		return time_widget;
	}	
	
	private BooleanOptionWidget subGC_widget;
	private void setsubGC_widget(BooleanOptionWidget widget) {
		subGC_widget = widget;
	}
	private BooleanOptionWidget getsubGC_widget() {
		return subGC_widget;
	}	
	

	
	
	private Composite miscOptsCreate(Composite parent) {

		Group editGroup = new Group(parent, SWT.NONE );
		GridLayout layout = new GridLayout();
		editGroup.setLayout(layout);
	
	 	editGroup.setText("Miscellaneous Options");
		
		
		
		
		settime_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Time", "time", "print out time statistics about tranformations")));
		
		setsubGC_widget(new BooleanOptionWidget(editGroup, SWT.NONE, new OptionData("Subtract Garbage Collection Time", "subtract-gc", "attempt to subtract the gc from the time stats")));
		
		
		return editGroup;
	}*/

	
	
}

