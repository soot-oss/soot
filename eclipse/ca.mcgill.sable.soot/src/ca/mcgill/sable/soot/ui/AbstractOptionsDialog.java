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

package ca.mcgill.sable.soot.ui;


import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.launching.SavedConfigManager;
import ca.mcgill.sable.soot.launching.SootSavedConfiguration;
import ca.mcgill.sable.soot.testing.*;


public abstract class AbstractOptionsDialog extends TitleAreaDialog implements ISelectionChangedListener {

	private SashForm sashForm;
	private TreeViewer treeViewer;
	private Composite pageContainer;
	private HashMap config;
	private String configName;
	private HashMap editMap;
	private boolean canRun = true;
	private HashMap radioGroups;
	private ArrayList enableGroups;
	private HashMap eclipseDefList;
	private HashMap defList;
	private CheckboxTableViewer tableViewer;
	private	Button addButton;
	private	Button removeButton;
	private String sootMainClass;
	private String sootMainProject;
	
	/**
	 * Constructor for AbstractOptionsDialog.
	 * @param parentShell
	 */
	public AbstractOptionsDialog(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(SWT.RESIZE);
	}

	public void addToEclipseDefList(String key, Object val) {
		if (getEclipseDefList() == null) {
			setEclipseDefList(new HashMap());
		}
		getEclipseDefList().put(key, val);
		
		addToDefList(key, val);
		
	}
	
	public void addToDefList(String key, Object val) {
		if (getDefList() == null) {
			setDefList(new HashMap());
		}
		getDefList().put(key, val);
		
	} 

	public boolean isInDefList(String key) {
		if (getDefList().containsKey(key)) return true;
		else return false;
	}
	
	public boolean getBoolDef(String key) {
		Boolean temp = (Boolean)getDefList().get(key);
		return temp.booleanValue();
	}

	public String getStringDef(String key) {
		
		return (String)getDefList().get(key);
	}

	public String getArrayDef(String key){
		String res = "";
		if (getDefList().get(key) instanceof ArrayList){
		
		ArrayList list = (ArrayList)getDefList().get(key);
		Iterator it = list.iterator();
		while (it.hasNext()){
			if (res.equals("")){
				res = res + (String)it.next();
			}
			else {
				res = res + "\r\n" + (String)it.next();
			}
		}
		}
		else {
			res = (String)getDefList().get(key);
		}
		return res;
	}
	
	// This sets the title in the shell that displays the
	// options dialog box
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText(Messages.getString("AbstractOptionsDialog.Soot_Options")); //$NON-NLS-1$
	}
	
	public boolean isEnableButton(String alias){
		if (alias.equals("enabled")) return true;
		return false;
	}
	
	
	public void handleWidgetSelected(SelectionEvent e){
		
		if (getRadioGroups() != null) {
			Iterator it = getRadioGroups().keySet().iterator();
			while (it.hasNext()){
				Integer key = (Integer)it.next();
				if (getRadioGroups().get(key) == null) break;
				ArrayList buttons = (ArrayList)getRadioGroups().get(key);
				Iterator itButtons = buttons.iterator();
				while (itButtons.hasNext()){
					if (((BooleanOptionWidget)itButtons.next()).getButton().equals(e.getSource())) {
						switchButtons(buttons, (Button)e.getSource());
					}
				}
			}
		}
		
		updateAllEnableGroups();
		
	}
	
	public void updateEnableGroup(Button button){
		if (getEnableGroups() == null) return;
		Iterator it = getEnableGroups().iterator();
		while (it.hasNext()){
			EnableGroup eGroup = (EnableGroup)it.next();
			if (eGroup.getLeader().getButton().equals(button)){
				// group found
                eGroup.changeControlState(eGroup.getLeader().getButton().getSelection());
                if (eGroup.getControls() != null){
                    Iterator itCon = eGroup.getControls().iterator();
                    while (itCon.hasNext()){
                        Object obj = itCon.next();
                        if (obj instanceof BooleanOptionWidget){
                            updateEnableGroup(((BooleanOptionWidget)obj).getButton());
                        }
                    }
                }
			}
		}
	}
	
	public void switchButtons(ArrayList buttons, Button change){
		if (change.getSelection()){
			Iterator it = buttons.iterator();
			while (it.hasNext()){
				BooleanOptionWidget nextWidget = (BooleanOptionWidget)it.next();
				if (nextWidget.getButton().equals(change)){
					nextWidget.getButton().setSelection(true);
				}
				else {
					nextWidget.getButton().setSelection(false);
				}
			}
		}
		else {
			Iterator it = buttons.iterator();
			while (it.hasNext()){
				BooleanOptionWidget defWidget = (BooleanOptionWidget)it.next();
				if (defWidget.getData().isDefaultVal()){
					defWidget.getButton().setSelection(true);
				}
				else {
					defWidget.getButton().setSelection(false);
				}
			}
		}
	}
	
	protected void makeNewEnableGroup(String phaseAlias){
		if (getEnableGroups() == null){
			setEnableGroups(new ArrayList());
		}
		
		EnableGroup eGroup = new EnableGroup();
		eGroup.setPhaseAlias(phaseAlias);
		
		getEnableGroups().add(eGroup);
	}
	
	protected void makeNewEnableGroup(String phaseAlias, String subPhaseAlias){
		if (getEnableGroups() == null){
			setEnableGroups(new ArrayList());
		}
		
		EnableGroup eGroup = new EnableGroup();
		eGroup.setPhaseAlias(phaseAlias);
		eGroup.setSubPhaseAlias(subPhaseAlias);
		
		getEnableGroups().add(eGroup);
	}
	
	protected void addToEnableGroup(String phaseAlias, ISootOptionWidget widget, String alias){
		EnableGroup eGroup = findEnableGroup(phaseAlias);
		if (widget instanceof BooleanOptionWidget){
			// could be leader
			if (isEnableButton(alias)){
				eGroup.setLeader(((BooleanOptionWidget)widget));
			}
			else {
				eGroup.addControl(widget);
			}
		}
		else {
			eGroup.addControl(widget);
		}
	}
	
	private EnableGroup findEnableGroup(String phaseAlias){
		Iterator it = getEnableGroups().iterator();
		while (it.hasNext()){
			EnableGroup next = (EnableGroup)it.next();
			if (next.getPhaseAlias().equals(phaseAlias) &&
				(next.getSubPhaseAlias() == null)) return next;
		}
		return null;
	}
	
	protected void addToEnableGroup(String phaseAlias, String subPhaseAlias, ISootOptionWidget widget, String alias){
		EnableGroup eGroup = findEnableGroup(phaseAlias, subPhaseAlias);
		if (widget instanceof BooleanOptionWidget){
			// could be leader
			
			if (isEnableButton(alias)){
				eGroup.setLeader(((BooleanOptionWidget)widget));
				addToEnableGroup(phaseAlias, widget, "");
			}
			else {
				eGroup.addControl(widget);
			}
		}
		else {
			eGroup.addControl(widget);
		}
	}

	private EnableGroup findEnableGroup(String phaseAlias, String subPhaseAlias){
		Iterator it = getEnableGroups().iterator();
		while (it.hasNext()){
			EnableGroup next = (EnableGroup)it.next();
			if (next.getSubPhaseAlias() == null) continue;
			if (next.getPhaseAlias().equals(phaseAlias) && 
				next.getSubPhaseAlias().equals(subPhaseAlias)) return next;
		}
		return null;
	}
	
	protected void updateAllEnableGroups(){
		if (getEnableGroups() == null) return;
		Iterator it = getEnableGroups().iterator();
		
		while (it.hasNext()){
			EnableGroup eGroup = (EnableGroup)it.next();
			if (eGroup.isPhaseOptType()){
				if (eGroup.getLeader() == null){
					continue;
				}
				if (eGroup.getLeader().getButton().getSelection() && eGroup.getLeader().getButton().isEnabled()){
					eGroup.changeControlState(true);
				}
				else {
					eGroup.changeControlState(false);
				}
			}
		}
		
		it = getEnableGroups().iterator();
		
		while (it.hasNext()){
			EnableGroup eGroup = (EnableGroup)it.next();
			if (!eGroup.isPhaseOptType()){
				if (eGroup.getLeader() == null){
					continue;
				}
				if (eGroup.getLeader().getButton().getSelection() && eGroup.getLeader().getButton().isEnabled()){
					eGroup.changeControlState(true);
				}
				else {
					eGroup.changeControlState(false);
				}
			}
		}
	}
	
	private void printEnableGroups(){
		if (getEnableGroups() == null) return;
		Iterator it = getEnableGroups().iterator();
		while (it.hasNext()){
			EnableGroup eGroup = (EnableGroup)it.next();
            System.out.println(eGroup);
		}
	}
	
		
	/**
	 * creates a sash form - one side for a selection tree 
	 * and the other for the options 
	 */
	protected Control createDialogArea(Composite parent) {
		GridData gd;
		
		Composite dialogComp = (Composite)super.createDialogArea(parent);
		Composite topComp = new Composite(dialogComp, SWT.NONE);
		
		gd = new GridData(GridData.FILL_BOTH);
		topComp.setLayoutData(gd);
		GridLayout topLayout = new GridLayout();
		topComp.setLayout(topLayout);
		
		// Set the things that TitleAreaDialog takes care of

		setTitle(Messages.getString("AbstractOptionsDialog.Soot_Launching_Options")); //$NON-NLS-1$ 
		setMessage("");  //$NON-NLS-1$

		// Create the SashForm that contains the selection area on the left,
		// and the edit area on the right
		setSashForm(new SashForm(topComp, SWT.NONE));
		getSashForm().setOrientation(SWT.HORIZONTAL);
		
		gd = new GridData(GridData.FILL_BOTH);
		getSashForm().setLayoutData(gd);
		
		Composite selection = createSelectionArea(getSashForm());
		
		setPageContainer(createEditArea(getSashForm()));

		initializePageContainer();

        // set general as first page
        Control [] pages = getPageContainer().getChildren();
        ((StackLayout)getPageContainer().getLayout()).topControl = pages[0];
        getPageContainer().layout();
        
        		
		try {
			getSashForm().setWeights(new int[] {30, 70});
		}
		catch(Exception e1) {
			System.out.println(e1.getMessage());
		}
		
		Label separator = new Label(topComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		separator.setLayoutData(gd);
		
		dialogComp.layout(true);
		
		return dialogComp;
	}

	// creates buttons Run and Close for a runnable dialog and
	// buttons Save and Close for a savable one	
	protected void createButtonsForButtonBar(Composite parent) {
		if (isCanRun()) {
			createButton(parent, 1, Messages.getString("AbstractOptionsDialog.Run"), true); //$NON-NLS-1$
			createButton(parent, 2, Messages.getString("AbstractOptionsDialog.Close"), false); //$NON-NLS-1$
		}
		else {
			createButton(parent, 0, Messages.getString("AbstractOptionsDialog.Save"), true);	 //$NON-NLS-1$
			createButton(parent, 2, Messages.getString("AbstractOptionsDialog.Close"), false); //$NON-NLS-1$
		}
	}
	
	
	protected void buttonPressed(int i){
		switch(i) {
			case 0: {
				handleSaving();
				break;
			} 
			case 1: {
				okPressed();
				break;
			}
			case 2: {
				cancelPressed();
				break;
			}
		}
		
	}
		
	protected abstract HashMap savePressed();
	
	private void handleSaving() {
		
		saveConfigToMap(this.getConfigName());
		
		SavedConfigManager scm = new SavedConfigManager();
		scm.setEditMap(getEditMap());
		scm.handleEdits();
		
		super.okPressed();
		
		
	}	
	
		
	private void saveConfigToMap(String name) {
		
		SootSavedConfiguration newConfig = new SootSavedConfiguration(name, savePressed());
		
		newConfig.setEclipseDefs(getEclipseDefList());
		if (getEditMap() == null) {
			setEditMap(new HashMap());
		}
		
		getEditMap().put(name, newConfig.toSaveArray());
					
	}
		
	protected abstract void initializePageContainer();
	protected abstract SootOption getInitialInput();
	
		
	/**
	 * initialize area containing options as a stack layout
	 */ 
	private Composite createEditArea(Composite parent) {
		Composite editArea = new Composite(parent, SWT.NONE);
		StackLayout layout = new StackLayout();
		editArea.setLayout(layout);
		return editArea;
	}
	
	/**
	 * creates the tree of options sections
	 */
	private Composite createSelectionArea(Composite parent) {
	 	Composite comp = new Composite(parent, SWT.NONE);
		setSelectionArea(comp);
		
		GridLayout layout = new GridLayout();

		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 5;
		
		comp.setLayout(layout);
		
		GridData gd = new GridData();
		
		TreeViewer tree = new TreeViewer(comp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.widthHint = 0;
		tree.getControl().setLayoutData(gd);
		
		tree.setContentProvider(new SootOptionsContentProvider());
		tree.setLabelProvider(new SootOptionsLabelProvider());
	  	tree.setInput(getInitialInput());
	
        
		setTreeViewer(tree);
		
		tree.addSelectionChangedListener(this);
		
		tree.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});
		 
		return comp;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (!selection.isEmpty()) {
			Object elem = selection.getFirstElement();
			if (elem instanceof SootOption) {
				SootOption sel = (SootOption)elem;
				Control [] children = getPageContainer().getChildren();
				String childTitle = null;
				for (int i = 0; i < children.length; i++) {

					if( children[i] instanceof Composite) {
						if (children[i] instanceof Group) {
							childTitle = (String)((Group)children[i]).getData("id");
							
						}
						if (childTitle.compareTo(sel.getAlias()) == 0) {
						  	((StackLayout)getPageContainer().getLayout()).topControl = children[i];
							getPageContainer().layout();
							
						}
						else {
							children[i].setVisible(false);
						}
					}
				}
			}
		}
	}
	
	public void addOtherBranches(SootOption root){
		SootOption sootMainClassBranch = new SootOption("Soot Main Class", "sootMainClass");
		root.addChild(sootMainClassBranch);		
	}
	
	public void addOtherPages(Composite parent){
		Composite mainClassChild = sootMainClassCreate(parent);
	}
	
	private Composite sootMainClassCreate(Composite parent) {
		
		Group editGroupSootMainClass = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		editGroupSootMainClass.setLayout(layout);

		editGroupSootMainClass.setText("Soot Main Class Manager");

		editGroupSootMainClass.setData("id", "sootMainClass");

		{
			//main class widget
			String desc = "Specify main class to run.";	
			Label descLabel = new Label(editGroupSootMainClass, SWT.WRAP);
			descLabel.setText(desc);
		
			String defKey = "sootMainClass";
			String defaultString;
			
			if (isInDefList(defKey)) {
				defaultString = getStringDef(defKey);	
			}
			else {
				defaultString = "";
			}
			setSootMainClassWidget(new StringOptionWidget(editGroupSootMainClass, SWT.NONE, new OptionData("Soot Main Class",  "", "","sootMainClass", "\nUses specified main class to run Soot.", defaultString)));
		}
		{
			//main project widget
			String desc = "Specify the Java project which the main class resides in.";	
			Label descLabel = new Label(editGroupSootMainClass, SWT.WRAP);
			descLabel.setText(desc);
		
			String defKey = "sootMainProject";
			String defaultString;
			
			if (isInDefList(defKey)) {
				defaultString = getStringDef(defKey);
				if(defaultString==null) defaultString = "";
			}
			else {
				defaultString = "";
			}
			setSootMainProjectWidget(new StringOptionWidget(editGroupSootMainClass, SWT.NONE, new OptionData("Soot Main Project",  "", "","sootMainProject", "\nThe Java project holding the main class.", defaultString)));
		}

		return editGroupSootMainClass;
	}
	
	
	
	private StringOptionWidget sootMainClassWidget, sootMainProjectWidget;

	private void setPageContainer(Composite comp) {
		pageContainer = comp;
	}
	
	protected Composite getPageContainer() {
		return pageContainer;
	}

	/**
	 * Returns the sashForm.
	 * @return SashForm
	 */
	private SashForm getSashForm() {
		return sashForm;
	}

	/**
	 * Sets the sashForm.
	 * @param sashForm The sashForm to set
	 */
	private void setSashForm(SashForm sashForm) {
		this.sashForm = sashForm;
	}
	
	protected void handleKeyPressed(KeyEvent event) {
	}
	
	private Composite selectionArea;
	
	private void setSelectionArea(Composite comp){
		selectionArea = comp;
	}

	private Composite getSelectionArea() {
		return selectionArea;
	}

	private void setTreeViewer(TreeViewer tree) {
		treeViewer = tree;
	}

	private TreeViewer getTreeViewer() {
		return treeViewer;
	}


	/**
	 * Returns the defList.
	 * @return HashMap
	 */
	public HashMap getDefList() {
		return defList;
	}

	/**
	 * Sets the defList.
	 * @param defList The defList to set
	 */
	public void setDefList(HashMap defList) {
		this.defList = defList;
	}

	/**
	 * Returns the config.
	 * @return HashMap
	 */
	public HashMap getConfig() {
		return config;
	}

	/**
	 * Sets the config.
	 * @param config The config to set
	 */
	public void setConfig(HashMap config) {
		this.config = config;
	}

	/**
	 * Returns the configName.
	 * @return String
	 */
	public String getConfigName() {
		return configName;
	}

	/**
	 * Sets the configName.
	 * @param configName The configName to set
	 */
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	/**
	 * Returns the eclipseDefList.
	 * @return HashMap
	 */
	public HashMap getEclipseDefList() {
		return eclipseDefList;
	}

	/**
	 * Sets the eclipseDefList.
	 * @param eclipseDefList The eclipseDefList to set
	 */
	public void setEclipseDefList(HashMap eclipseDefList) {
		this.eclipseDefList = eclipseDefList;
	}


	/**
	 * Returns the editMap.
	 * @return HashMap
	 */
	public HashMap getEditMap() {
		return editMap;
	}


	/**
	 * Sets the editMap.
	 * @param editMap The editMap to set
	 */
	public void setEditMap(HashMap editMap) {
		this.editMap = editMap;
	}

	/**
	 * Returns the canRun.
	 * @return boolean
	 */
	public boolean isCanRun() {
		return canRun;
	}

	/**
	 * Sets the canRun.
	 * @param canRun The canRun to set
	 */
	public void setCanRun(boolean canRun) {
		this.canRun = canRun;
	}

	/**
	 * @return
	 */
	public HashMap getRadioGroups() {
		return radioGroups;
	}

	/**
	 * @param map
	 */
	public void setRadioGroups(HashMap map) {
		radioGroups = map;
	}

	/**
	 * @return
	 */
	public ArrayList getEnableGroups() {
		return enableGroups;
	}

	/**
	 * @param map
	 */
	public void setEnableGroups(ArrayList list) {
		enableGroups = list;
	}

	/**
	 * @return
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @return
	 */
	public Button getRemoveButton() {
		return removeButton;
	}

	/**
	 * @param button
	 */
	public void setAddButton(Button button) {
		addButton = button;
	}

	/**
	 * @param button
	 */
	public void setRemoveButton(Button button) {
		removeButton = button;
	}

	/**
	 * @return
	 */
	public CheckboxTableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * @param viewer
	 */
	public void setTableViewer(CheckboxTableViewer viewer) {
		tableViewer = viewer;
	}

	/**
	 * @return
	 */
	public StringOptionWidget getSootMainClassWidget() {
		return sootMainClassWidget;
	}

	/**
	 * @param widget
	 */
	public void setSootMainClassWidget(StringOptionWidget widget) {
		sootMainClassWidget = widget;
	}
	
	/**
	 * @return
	 */
	public StringOptionWidget getSootMainProjectWidget() {
		return sootMainProjectWidget;
	}
	
	/**
	 * @param widget
	 */
	public void setSootMainProjectWidget(StringOptionWidget widget) {
		sootMainProjectWidget = widget;
	}
	

	/**
	 * @return
	 */
	public String getSootMainClass() {
		if(sootMainProject!=null && sootMainProject.length()>0) {
			return sootMainProject+":"+sootMainClass;
		} else {
			return sootMainClass;
		}
	}

	/**
	 * @param string
	 */
	public void setSootMainClass(String string) {
		sootMainClass = string;
	}
	
	/**
	 * @param projectName
	 * @return 
	 */
	public boolean setSootMainProject(String projectName) {
		if(projectName==null || projectName.length()==0) {
			sootMainProject = null;
			return true;
		}
		IProject project = SootPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			if(project.exists() && project.isOpen() && project.hasNature("org.eclipse.jdt.core.javanature")) {
				sootMainProject = projectName;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return sootMainProject!=null;
	}

}
