package ca.mcgill.sable.soot.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
//import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.*;

import ca.mcgill.sable.soot.SootPlugin;
import ca.mcgill.sable.soot.launching.*;
import ca.mcgill.sable.soot.testing.PhaseOptionsDialog;

/**
 * @author jlhotak
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
public class SootConfigManagerDialog extends TitleAreaDialog implements ISelectionChangedListener {//, MouseListener {

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
	
	private HashMap eclipseDefList;
	
	
	/*public void addToEclipseDefList(String key, Object val) {
		if (getEclipseDefList() == null) {
			setEclipseDefList(new HashMap());
		}
		getEclipseDefList().put(key, val);
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
	
	public SootConfigManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText("Manage Configurations");
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
		//topLayout.makeColumnsEqualWidth = false;
		//topLayout.horizontalSpacing = 20;
		topComp.setLayout(topLayout);
		
		// Set the things that TitleAreaDialog takes care of
		// TODO: externalize this title
		setTitle("Soot Configurations Manager");
		//Image i = new Image(device, "icons/soot.jpg");
		//setTitleImage(i); 
		setMessage(""); 

		// Create the SashForm that contains the selection area on the left,
		// and the edit area on the right
		//setSashForm(new SashForm(topComp, SWT.NONE));
		//getSashForm().setOrientation(SWT.HORIZONTAL);
		
		//gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 7;
		//getSashForm().setLayoutData(gd);
		
		Composite selection = createSelectionArea(topComp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		
		selection.setLayoutData(gd);
		
		// here need buttons
		//setButtonPanel(createButtonPanel(getSashForm()));
		
		//gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 4;
		
		//initializePageContainer();
		
		//try {
		//	getSashForm().setWeights(new int[] {70, 30});
		//}
		//catch(Exception e1) {
		//	System.out.println(e1.getMessage());
		//}
		Control specialButtons = createSpecialButtonBar(topComp);
		gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 1;
		
		specialButtons.setLayoutData(gd);
				
		Label separator = new Label(topComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separator.setLayoutData(gd);
		
		//topComp.layout(true);
		
		
		
		dialogComp.layout(true);
		return dialogComp;
	}
	
	/*private Composite createButtonPanel(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		Button newButton = new Button(comp, SWT.PUSH);
		newButton.setText("New");
		newButton.addMouseListener(this);
		Button editButton = new Button(comp, SWT.PUSH);
		editButton.setText("Edit");
		Button removeButton = new Button(comp, SWT.PUSH);
		removeButton.setText("Remove");
		
		return comp;
	}
	
	public void mouseUp(MouseEvent e) {
		//e.button
	}*/
	
	/**
	 * creates the tree of options sections
	 */
	private Composite createSelectionArea(Composite parent) {
	 	Composite comp = new Composite(parent, SWT.NONE);
		setSelectionArea(comp);
		
		GridLayout layout = new GridLayout();
	
		layout.numColumns = 1;
		
		//layout.marginHeight = 0;
		//layout.marginWidth = 0;
		
		comp.setLayout(layout);
		
		GridData gd = new GridData();
		
		TreeViewer tree = new TreeViewer(comp);
		gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 6;
		//gd.widthHint = 0;
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
		 
		//buttonBar = createButtonBar(comp); 
		return comp;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (selection.isEmpty()) {
			System.out.println("selection empty");	
		}
		else {
			Object elem = selection.getFirstElement();
			System.out.println(elem.getClass().toString());
			if (elem instanceof SootConfiguration) {
				SootConfiguration sel = (SootConfiguration)elem;
				System.out.println("selected: "+sel.getLabel());
				setSelected(sel.getLabel());
			}
		}
	}
	
	private void enableButtons(){
			
	}
	
	protected void handleKeyPressed(KeyEvent e) {
	}
	
	private SootConfiguration getInitialInput() {
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int numConfig = 0;
		try {
			numConfig = settings.getInt("config_count");
		}
		catch(NumberFormatException e) {
		}

		System.out.println("config_count: "+numConfig);
		SootConfiguration root = new SootConfiguration("");
		
		if (numConfig != 0) {		
			String [] configNames = new String[numConfig];
			
				
			for (int i = 0; i < numConfig; i++) {
				configNames[i] = settings.get("soot_run_config_"+(i+1));
				root.addChild(new SootConfiguration(configNames[i]));
				System.out.println("added to tree "+configNames[i]);
			}
			
		}
		setTreeRoot(root);
		//SootConfiguration root = new SootConfiguration("");
		
		
		return root;
	}
	
	/*
	 * @see Dialog#createButtonBar(Composite)
	 */				
	protected Control createSpecialButtonBar(Composite parent) {
		Composite composite= new Composite(parent, SWT.NULL);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		//layout.marginHeight= 0;
		//layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		//layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	
		
		applyDialogFont(composite);
		//super.createButtonBar(composite);
		//return composite;
		
		
				//layout.numColumns = 0; // this is incremented by createButton
		//layout.makeColumnsEqualWidth = true;
		//layout.marginWidth =
		//	convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		//layout.marginHeight =
		//	convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		//layout.horizontalSpacing =
		//	convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		//layout.verticalSpacing =
		//	convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);

		composite.setLayout(layout);

		GridData data =
			new GridData(
				GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_CENTER);
		composite.setLayoutData(data);

		//composite.setFont(parent.getFont());

		// Add the buttons to the button bar.
		createSpecialButtonsForButtonBar(composite);

		return composite;
	}
	
	protected Button createSpecialButton(
		Composite parent,
		int id,
		String label,
		boolean defaultButton) {
		
		// increment the number of rows in the button bar
		//((GridLayout) parent.getLayout())..numColumns++;

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
		//buttons.put(new Integer(id), button);
		setButtonLayoutData(button);

		return button;
	}
	
	protected void createButtonsForButtonBar(Composite parent){
		//run and close will close dialog
		createButton(parent, 5, "Run", false);
		createButton(parent, 6, "Close", true);
	}
	protected void createSpecialButtonsForButtonBar(Composite parent) {
		createSpecialButton(parent, 0, "New", false);
		createSpecialButton(parent, 1, "Edit", false);
		// create OK and Cancel buttons by default
		createSpecialButton(parent, 2, "Delete", false);
		createSpecialButton(parent, 3, "Rename", false);
		createSpecialButton(parent, 4, "Clone", false);
		//createSpecialButton(parent, 5, "Run", false);
		//createSpecialButton(parent, 6, "Close", true);
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
			 
		}
	}
	
	// shows a phaseOptionsDialog with save and close buttons
	// only and asks for a name first
	private void newPressed() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations before adding any
		int config_count = 0;
		try {
			config_count = settings.getInt("config_count");
		}
		catch (NumberFormatException e) {	
		}
		
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get("soot_run_config_"+i));
		}
		
		// for debugging
		//Iterator temp = currentNames.iterator();
		//while (temp.hasNext()) {
		//	System.out.println("Current Name: "+(String)temp.next());
		//}
		
		// sets validator to know about already used names - but it doesn't use
		// them because then editing a file cannot use same file name
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		//boolean nameOk = false;
		//while (true) {
			// create dialog to get name
		InputDialog nameDialog = new InputDialog(this.getShell(), "Saving Configuration Name", "Enter name to save configuration with:", "", validator); 
		nameDialog.open();
		
		if (nameDialog.getReturnCode() == Dialog.OK) {
			setEditDefs(null);
			int returnCode = displayOptions(nameDialog.getValue());
			System.out.println("return code: "+returnCode);
			if (returnCode != Dialog.CANCEL) {
				getTreeRoot().addChild(new SootConfiguration(nameDialog.getValue()));
				refreshTree();
				//getTreeViewer().setInput(getTreeRoot());
				//getTreeViewer().setExpandedState(getTreeRoot(), true);
				//getTreeViewer().refresh(getTreeRoot(), false);
				//getTreeViewer().expandAll();
				System.out.println("updated tree");
			}
		
		}
		else {
			// cancel and do nothing
		}
	}
		
	private void refreshTree() {
		getTreeViewer().setInput(getTreeRoot());
		getTreeViewer().setExpandedState(getTreeRoot(), true);
		getTreeViewer().refresh(getTreeRoot(), false);
	}
	private int displayOptions(String name) {
		/*Object [] temp = this.getSelectedElements();
		String result = (String)temp[0];
		System.out.println("result selected: "+result);
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		String saved = settings.get(result);
		System.out.println("saved: "+saved);
		SootSavedConfiguration ssc = new SootSavedConfiguration(result, saved);
		HashMap structConfig = ssc.toHashMap();*/
		PhaseOptionsDialog dialog = new PhaseOptionsDialog(getShell());
		addEclipseDefsToDialog(dialog);
		
		System.out.println("created dialog");
		if (getEditDefs() != null) {
			Iterator it = getEditDefs().keySet().iterator();
			while (it.hasNext()) {
				String key = (String)it.next();
				String val = (String)getEditDefs().get(key);
				if ((val.equals("true")) || (val.equals("false"))) {
					dialog.addToDefList(key, new Boolean(val));
				}
				else {
					dialog.addToDefList(key, val);
				}
			}
		}
		
		System.out.println("added defaults to dialog");
		//dialog.setConfigName(result);
		dialog.setConfigName(name);
		dialog.setCanRun(false);
		System.out.println("about to open dialog");
		dialog.open();
		return dialog.getReturnCode();
			// saved - should show up in tree
			
		//setEditMap(dialog.getEditMap());
	}
		
	// same as newPressed except does not ask for name
	private void editPressed() {
		if (getSelected() == null) return;
		
		//Object [] temp = this.getSelected();
		String result = this.getSelected();
		System.out.println("result selected: "+result);
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		// TODO switch these 2 lines 
		String [] saveArray = settings.getArray(result);
		//String saved = settings.get(result);
		//System.out.println("saved: "+saved);
		// TODO switch these 2 lines
		SootSavedConfiguration ssc = new SootSavedConfiguration(result, saveArray);
		//SootSavedConfiguration ssc = new SootSavedConfiguration(result, saved);
		
		// TODO switch these 2 lines
		setEditDefs(ssc.toHashMapFromArray());
		//setEditDefs(ssc.toHashMap());
		displayOptions(result);
	}
	
	// removes form tree
	private void deletePressed() {
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		System.out.println("will remove: "+result);
		
		// maybe ask if they are sure here first
		MessageDialog msgDialog = new MessageDialog(this.getShell(), "Soot Configuration Remove Message", null, "Are you sure you want to remove this configuration?", 0, new String [] {"Yes", "No"}, 0);
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
			//getTreeViewer().setInput(getTreeRoot());
			//getTreeViewer().setExpandedState(getTreeRoot(), true);
			//getTreeViewer().refresh(getTreeRoot(), false);
		}
		
		
	}
	
	private void renamePressed(){
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		System.out.println("will rename: "+result);
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations
		int config_count = 0;
		int oldNameCount = 0;
		try {
			config_count = settings.getInt("config_count");
		}
		catch (NumberFormatException e) {	
		}

		System.out.println("config_count: "+config_count);
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get("soot_run_config_"+i));
			System.out.println(currentNames.get(i-1));
			if (((String)currentNames.get(i-1)).equals(result)){
				oldNameCount = i;
			}
		}

		
		// sets validator to know about already used names 
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		InputDialog nameDialog = new InputDialog(this.getShell(), "Rename Saved Configuration", "Enter new name:", "", validator); 
		nameDialog.open();
		if (nameDialog.getReturnCode() == Dialog.OK){
			settings.put("soot_run_config_"+oldNameCount, nameDialog.getValue());
			System.out.println("setting pointer: "+oldNameCount+" with: "+nameDialog.getValue());
			settings.put(nameDialog.getValue(), settings.getArray(result));
			getTreeRoot().renameChild(result, nameDialog.getValue());
		}
		refreshTree();
	}
	
	

	private void clonePressed(){
		if (getSelected() == null) return;
		
		String result = this.getSelected();
		System.out.println("will clone: "+result);
		
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations
		int config_count = 0;
		try {
			config_count = settings.getInt("config_count");
		}
		catch (NumberFormatException e) {	
		}

		System.out.println("config_count: "+config_count);
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get("soot_run_config_"+i));
			
		}

		
		// sets validator to know about already used names 
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		InputDialog nameDialog = new InputDialog(this.getShell(), "Clone Saved Configuration", "Enter new name:", "", validator); 
		nameDialog.open();
		if (nameDialog.getReturnCode() == Dialog.OK){
			config_count++;
			settings.put("soot_run_config_"+config_count, nameDialog.getValue());
			//System.out.println("setting pointer: "+oldNameCount+" with: "+nameDialog.getValue());
			settings.put(nameDialog.getValue(), settings.getArray(result));
			settings.put("config_count", config_count);
			getTreeRoot().addChild(new SootConfiguration(nameDialog.getValue()));
		}
		refreshTree();
	}

	// runs the config
	private void runPressed() {
		super.okPressed();
		if (getSelected() == null) return;
		
		if (getLauncher() instanceof SootConfigProjectLauncher) {
			((SootConfigProjectLauncher)getLauncher()).launch(getSelected());
		}
		else if (getLauncher() instanceof SootConfigFileLauncher) {
			((SootConfigFileLauncher)getLauncher()).launch(getSelected());
		}
		
		
		// only necessary for viewing code while running
		// i.e. for demos??
		//this.close();
		
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
