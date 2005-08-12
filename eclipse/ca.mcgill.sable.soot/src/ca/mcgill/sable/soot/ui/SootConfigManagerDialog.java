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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.*;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.launching.*;
import ca.mcgill.sable.soot.ui.PhaseOptionsDialog;

public class SootConfigManagerDialog extends TitleAreaDialog implements ISelectionChangedListener {

	private SashForm sashForm;
	private Composite selectionArea;
	private TreeViewer treeViewer;
	private String selected;
	private Composite buttonPanel;
	private SootConfiguration treeRoot;
	private HashMap editDefs;
	private SootLauncher launcher;
	
	private void addEclipseDefsToDialog(PhaseOptionsDialog dialog) {
		if (getEclipseDefList() == null) return;
		Iterator it = getEclipseDefList().keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			dialog.addToEclipseDefList(key, getEclipseDefList().get(key));
		}
	}
	
	private void setMainClassInDialog(PhaseOptionsDialog dialog, String mainClass){
		dialog.addToEclipseDefList("sootMainClass", mainClass);	
	}
	
	private HashMap eclipseDefList;

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
	
	public SootConfigManagerDialog(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(SWT.RESIZE);
	}
	
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText(Messages.getString("SootConfigManagerDialog.Manage_Configurations")); //$NON-NLS-1$
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
		topLayout.numColumns = 2;

		topComp.setLayout(topLayout);
		
		// Set the things that TitleAreaDialog takes care of

		setTitle(Messages.getString("SootConfigManagerDialog.Soot_Configurations_Manager")); //$NON-NLS-1$
		setMessage("");  //$NON-NLS-1$

				
		Composite selection = createSelectionArea(topComp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		
		selection.setLayoutData(gd);
		
		
		Control specialButtons = createSpecialButtonBar(topComp);
		gd = new GridData(GridData.FILL_BOTH);
		
		specialButtons.setLayoutData(gd);
				
		Label separator = new Label(topComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separator.setLayoutData(gd);
		
		
		dialogComp.layout(true);
		return dialogComp;
	}
	
		
	/**
	 * creates the tree of options sections
	 */
	private Composite createSelectionArea(Composite parent) {
	 	Composite comp = new Composite(parent, SWT.NONE);
		setSelectionArea(comp);
		
		GridLayout layout = new GridLayout();
	
		layout.numColumns = 1;
	
		
		comp.setLayout(layout);
		
		GridData gd = new GridData();
		
		TreeViewer tree = new TreeViewer(comp);
		gd = new GridData(GridData.FILL_BOTH);
	
		tree.getControl().setLayoutData(gd);
		
		tree.setContentProvider(new SootConfigContentProvider());
		tree.setLabelProvider(new SootConfigLabelProvider());
	  	tree.setInput(getInitialInput());
	
		setTreeViewer(tree);
		
		tree.addSelectionChangedListener(this);
	
		
		tree.expandAll();
		tree.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});
		 

		return comp;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (!selection.isEmpty()) {
			Object elem = selection.getFirstElement();
			if (elem instanceof SootConfiguration) {
				SootConfiguration sel = (SootConfiguration)elem;
				setSelected(sel.getLabel());
			}
			enableButtons();
		}
	}
	
	private void enableButtons(){
		Iterator it = specialButtonList.iterator();
		while (it.hasNext()){
			((Button)it.next()).setEnabled(true);	
		}
	}
	
	protected void handleKeyPressed(KeyEvent e) {
	}
	
	private SootConfiguration getInitialInput() {
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int numConfig = 0;
		try {
			numConfig = settings.getInt(Messages.getString("SootConfigManagerDialog.config_count")); //$NON-NLS-1$
		}
		catch(NumberFormatException e) {
		}

		SootConfiguration root = new SootConfiguration(""); //$NON-NLS-1$
		
		if (numConfig != 0) {		
			String [] configNames = new String[numConfig];
			
				
			for (int i = 0; i < numConfig; i++) {
				configNames[i] = settings.get(Messages.getString("SootConfigManagerDialog.soot_run_config")+(i+1)); //$NON-NLS-1$
				root.addChild(new SootConfiguration(configNames[i]));
			}
		
			
		}
		setTreeRoot(root);

		return root;
	}
	
	/*
	 * @see Dialog#createButtonBar(Composite)
	 */				
	protected Control createSpecialButtonBar(Composite parent) {
		Composite composite= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;

		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	
		
		applyDialogFont(composite);
		
		composite.setLayout(layout);

		GridData data =
			new GridData(
				GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_CENTER);
		composite.setLayoutData(data);

		// Add the buttons to the button bar.
		createSpecialButtonsForButtonBar(composite);

		return composite;
	}
	
	ArrayList specialButtonList = new ArrayList(); 
	
	protected Button createSpecialButton(
		Composite parent,
		int id,
		String label,
		boolean defaultButton, boolean enabled) {
	

		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);

		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			buttonPressed(((Integer) event.widget.getData()).intValue());
		}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		button.setFont(parent.getFont());
		if (!enabled){
			button.setEnabled(false);
		}

		setButtonLayoutData(button);
		specialButtonList.add(button);
		return button;
	}
	
	protected void createButtonsForButtonBar(Composite parent){
		//run and close will close dialog
		Button runButton = createButton(parent, 5, Messages.getString("SootConfigManagerDialog.Run"), false); //$NON-NLS-1$
		runButton.setEnabled(false);
		specialButtonList.add(runButton);
		createButton(parent, 6, Messages.getString("SootConfigManagerDialog.Close"), true); //$NON-NLS-1$
	}
	protected void createSpecialButtonsForButtonBar(Composite parent) {
		createSpecialButton(parent, 0, Messages.getString("SootConfigManagerDialog.New"), false, true); //$NON-NLS-1$
		createSpecialButton(parent, 1, Messages.getString("SootConfigManagerDialog.Edit"), false, false); //$NON-NLS-1$
		createSpecialButton(parent, 2, Messages.getString("SootConfigManagerDialog.Delete"), false, false); //$NON-NLS-1$
		createSpecialButton(parent, 3, Messages.getString("SootConfigManagerDialog.Rename"), false, false); //$NON-NLS-1$
		createSpecialButton(parent, 4, Messages.getString("SootConfigManagerDialog.Clone"), false, false); //$NON-NLS-1$
	
	}
	
	protected void buttonPressed(int id) {
		switch (id) {
			case 0: {
				newPressed();
				break;
			}
			case 1: {
				editPressed();
				break;
			}
			case 2: {
				deletePressed();
				break;
			}
			case 3: {
				renamePressed();
				break;
			}
			case 4: {
				clonePressed();
				break;
			}
			case 5: {
				runPressed();
				break;
			}
			case 6: {
				cancelPressed();
				break;
			}
			case 7: {
				break;
			}
			case 8: {
				break;
			}
			 
		}
	}
	
	// shows a phaseOptionsDialog with save and close buttons
	// only and asks for a name first
	private void newPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations before adding any
		int config_count = 0;
		try {
			config_count = settings.getInt(Messages.getString("SootConfigManagerDialog.config_count")); //$NON-NLS-1$
		}
		catch (NumberFormatException e) {	
		}
		
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get(Messages.getString("SootConfigManagerDialog.soot_run_config")+i)); //$NON-NLS-1$
		}
		
		// sets validator to know about already used names - but it doesn't use
		// them because then editing a file cannot use same file name
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
	
		// create dialog to get name
		InputDialog nameDialog = new InputDialog(this.getShell(), Messages.getString("SootConfigManagerDialog.Saving_Configuration_Name"), Messages.getString("SootConfigManagerDialog.Enter_name_to_save_configuration_with"), "", validator);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		nameDialog.open();
		
		if (nameDialog.getReturnCode() == Dialog.OK) {
			setEditDefs(null);
			int returnCode = displayOptions(nameDialog.getValue(), "soot.Main");
			//handle selection of main class here
			
			if (returnCode != Dialog.CANCEL) {
				getTreeRoot().addChild(new SootConfiguration(nameDialog.getValue()));
				refreshTree();
				
			}
		
		}
		else {
			// cancel and do nothing
		}
	}
		
	// saves the main class to run with this configuration
	private void saveMainClass(String configName, String mainClass){
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		settings.put(configName+"_mainClass", mainClass);	
	}
	
	//	returns the main class to run with this configuration
	private String getMainClass(String configName){
		 IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		 String mainClass = settings.get(configName+"_mainClass");
		 if ((mainClass == null) || (mainClass.length() == 0)){
		 	return "soot.Main";
		 }
		 else {
		 	return mainClass;
		 }	
	 }
		
	
	private ArrayList stringToList(String string){
		StringTokenizer st = new StringTokenizer(string, ",");
		ArrayList list = new ArrayList();
		while (st.hasMoreTokens()){
			list.add(st.nextToken());
		}
		return list;
	}
	
	private void refreshTree() {
		getTreeViewer().setInput(getTreeRoot());
		getTreeViewer().setExpandedState(getTreeRoot(), true);
		getTreeViewer().refresh(getTreeRoot(), false);
	}
	
	private int displayOptions(String name) {
		return displayOptions(name, "soot.Main");
	}
	
	private int displayOptions(String name, String mainClass) {
		
		PhaseOptionsDialog dialog = new PhaseOptionsDialog(getShell());
		addEclipseDefsToDialog(dialog);
		setMainClassInDialog(dialog, mainClass);
		if (getEditDefs() != null) {
			Iterator it = getEditDefs().keySet().iterator();
			while (it.hasNext()) {
				Object next = it.next();
				String key = (String)next;
				String val = (String)getEditDefs().get(key);
				if ((val.equals("true")) || (val.equals("false"))) { //$NON-NLS-1$ //$NON-NLS-2$
					dialog.addToDefList(key, new Boolean(val));
				}
				else {
					dialog.addToDefList(key, val);
				}
			}
		}
		
		
		dialog.setConfigName(name);
		dialog.setCanRun(false);
		dialog.open();
		if (dialog.getReturnCode() == Dialog.OK){
			//save main class
			saveMainClass(name, dialog.getSootMainClass());
		}
		return dialog.getReturnCode();
			// saved - should show up in tree
			
		
	}
		
	// same as newPressed except does not ask for name
	private void editPressed() {
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		String [] saveArray = settings.getArray(result);
		SootSavedConfiguration ssc = new SootSavedConfiguration(result, saveArray);
		setEditDefs(ssc.toHashMapFromArray());
		displayOptions(result, getMainClass(result));
		
				
	}
	
	// removes form tree
	private void deletePressed() {
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		
		// maybe ask if they are sure here first
		MessageDialog msgDialog = new MessageDialog(this.getShell(), Messages.getString("SootConfigManagerDialog.Soot_Configuration_Remove_Message"), null, Messages.getString("SootConfigManagerDialog.Are_you_sure_you_want_to_remove_this_configuration"), 0, new String [] {Messages.getString("SootConfigManagerDialog.Yes"), Messages.getString("SootConfigManagerDialog.No")}, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		msgDialog.open();
		if (msgDialog.getReturnCode() == 0) {
						
			// do the delete
			ArrayList toRemove = new ArrayList();
			toRemove.add(result);
			SavedConfigManager scm = new SavedConfigManager();
			scm.setDeleteList(toRemove);
			scm.handleDeletes();
			
			// remove also from tree
			getTreeRoot().removeChild(result);
			refreshTree();
		}
		
		
	}
	
	private void renamePressed(){
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations
		int config_count = 0;
		int oldNameCount = 0;
		try {
			config_count = settings.getInt(Messages.getString("SootConfigManagerDialog.config_count")); //$NON-NLS-1$
		}
		catch (NumberFormatException e) {	
		}

		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get(Messages.getString("SootConfigManagerDialog.soot_run_config")+i)); //$NON-NLS-1$
			if (((String)currentNames.get(i-1)).equals(result)){
				oldNameCount = i;
			}
		}

		
		// sets validator to know about already used names 
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		InputDialog nameDialog = new InputDialog(this.getShell(), Messages.getString("SootConfigManagerDialog.Rename_Saved_Configuration"), Messages.getString("SootConfigManagerDialog.Enter_new_name"), "", validator);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		nameDialog.open();
		if (nameDialog.getReturnCode() == Dialog.OK){
			settings.put(Messages.getString("SootConfigManagerDialog.soot_run_config")+oldNameCount, nameDialog.getValue()); //$NON-NLS-1$
			settings.put(nameDialog.getValue(), settings.getArray(result));
			getTreeRoot().renameChild(result, nameDialog.getValue());
			saveMainClass(nameDialog.getValue(), settings.get(result+"_mainClass"));
		}
		refreshTree();
	}
	
	

	private void clonePressed(){
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations
		int config_count = 0;
		try {
			config_count = settings.getInt(Messages.getString("SootConfigManagerDialog.config_count")); //$NON-NLS-1$
		}
		catch (NumberFormatException e) {	
		}
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get(Messages.getString("SootConfigManagerDialog.soot_run_config")+i)); //$NON-NLS-1$
			
		}

		
		// sets validator to know about already used names 
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		InputDialog nameDialog = new InputDialog(this.getShell(), Messages.getString("SootConfigManagerDialog.Clone_Saved_Configuration"), Messages.getString("SootConfigManagerDialog.Enter_new_name"), result, validator);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		nameDialog.open();
		if (nameDialog.getReturnCode() == Dialog.OK){
			config_count++;
			settings.put(Messages.getString("SootConfigManagerDialog.soot_run_config")+config_count, nameDialog.getValue()); //$NON-NLS-1$
			settings.put(nameDialog.getValue(), settings.getArray(result));
			settings.put(Messages.getString("SootConfigManagerDialog.config_count"), config_count); //$NON-NLS-1$
			getTreeRoot().addChild(new SootConfiguration(nameDialog.getValue()));
			saveMainClass(nameDialog.getValue(), settings.get(result+"_mainClass"));
		}
		refreshTree();
	}

	// runs the config
	private void runPressed() {
		super.okPressed();
		if (getSelected() == null) return;
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		String mainClass = settings.get(getSelected()+"_mainClass");
		
		if (getLauncher() instanceof SootConfigProjectLauncher) {
			((SootConfigProjectLauncher)getLauncher()).launch(getSelected(), mainClass);
		}
		else if (getLauncher() instanceof SootConfigJavaProjectLauncher){
			((SootConfigJavaProjectLauncher)getLauncher()).launch(getSelected(), mainClass);
		}
		else if (getLauncher() instanceof SootConfigFileLauncher) {
			((SootConfigFileLauncher)getLauncher()).launch(getSelected(), mainClass);
		}
		else if (getLauncher() instanceof SootConfigFromJavaFileLauncher){
			((SootConfigFromJavaFileLauncher)getLauncher()).launch(getSelected(), mainClass);
		}
		
		
	}
	
	private void importPressed(){
			
	}
	
	/**
	 * Returns the sashForm.
	 * @return SashForm
	 */
	public SashForm getSashForm() {
		return sashForm;
	}

	/**
	 * Sets the sashForm.
	 * @param sashForm The sashForm to set
	 */
	public void setSashForm(SashForm sashForm) {
		this.sashForm = sashForm;
	}

	/**
	 * Returns the selectionArea.
	 * @return Composite
	 */
	public Composite getSelectionArea() {
		return selectionArea;
	}

	/**
	 * Returns the treeViewer.
	 * @return TreeViewer
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Sets the selectionArea.
	 * @param selectionArea The selectionArea to set
	 */
	public void setSelectionArea(Composite selectionArea) {
		this.selectionArea = selectionArea;
	}

	/**
	 * Sets the treeViewer.
	 * @param treeViewer The treeViewer to set
	 */
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/**
	 * Returns the selected.
	 * @return String
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * Sets the selected.
	 * @param selected The selected to set
	 */
	public void setSelected(String selected) {
		this.selected = selected;
	}

	/**
	 * Returns the buttonPanel.
	 * @return Composite
	 */
	public Composite getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * Sets the buttonPanel.
	 * @param buttonPanel The buttonPanel to set
	 */
	public void setButtonPanel(Composite buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	
	/**
	 * Returns the treeRoot.
	 * @return SootConfiguration
	 */
	public SootConfiguration getTreeRoot() {
		return treeRoot;
	}

	/**
	 * Sets the treeRoot.
	 * @param treeRoot The treeRoot to set
	 */
	public void setTreeRoot(SootConfiguration treeRoot) {
		this.treeRoot = treeRoot;
	}

	/**
	 * Returns the editDefs.
	 * @return HashMap
	 */
	public HashMap getEditDefs() {
		return editDefs;
	}

	/**
	 * Sets the editDefs.
	 * @param editDefs The editDefs to set
	 */
	public void setEditDefs(HashMap editDefs) {
		this.editDefs = editDefs;
	}

	/**
	 * Returns the launcher.
	 * @return SootLauncher
	 */
	public SootLauncher getLauncher() {
		return launcher;
	}

	/**
	 * Sets the launcher.
	 * @param launcher The launcher to set
	 */
	public void setLauncher(SootLauncher launcher) {
		this.launcher = launcher;
	}

}
