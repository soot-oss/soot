package ca.mcgill.sable.soot.ui;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
//import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
//import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;
//import org.eclipse.jface.dialogs.Dialog;
//import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.launching.SavedConfigManager;
//import ca.mcgill.sable.soot.launching.SootConfigNameInputValidator;
import ca.mcgill.sable.soot.launching.SootSavedConfiguration;
import ca.mcgill.sable.soot.testing.SootOption;
import ca.mcgill.sable.soot.testing.SootOptionsContentProvider;
import ca.mcgill.sable.soot.testing.SootOptionsLabelProvider;


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
public abstract class AbstractOptionsDialog extends TitleAreaDialog implements ISelectionChangedListener {

	private SashForm sashForm;
	private TreeViewer treeViewer;
	private Composite pageContainer;
	private HashMap config;
	private String configName;
	private HashMap editMap;
	//private ArrayList deleteList;
	private boolean canRun = true;
	
	
	/**
	 * Constructor for AbstractOptionsDialog.
	 * @param parentShell
	 */
	public AbstractOptionsDialog(Shell parentShell) {
		super(parentShell);
	}
	private HashMap eclipseDefList;
	
	private HashMap defList;
	
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
		System.out.println("adding to defList: key: "+key+" val: "+val);
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

	protected void configureShell(Shell shell){
		super	.configureShell(shell);
		shell.setText("Soot Options");
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
		// TODO: externalize this title
		setTitle("Soot Launching Options");
		//Image i = new Image(device, "icons/soot.jpg");
		//setTitleImage(i); 
		setMessage(""); 

		// Create the SashForm that contains the selection area on the left,
		// and the edit area on the right
		setSashForm(new SashForm(topComp, SWT.NONE));
		getSashForm().setOrientation(SWT.HORIZONTAL);
		
		gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 7;
		getSashForm().setLayoutData(gd);
		
		Composite selection = createSelectionArea(getSashForm());
		//gd = new GridData(GridData.FILL_VERTICAL);
		//gd.horizontalSpan = 1;
		//selection.setLayoutData(gd);
		
		setPageContainer(createEditArea(getSashForm()));
		
		//gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalSpan = 4;
		
		initializePageContainer();
		
		try {
			getSashForm().setWeights(new int[] {30, 70});
		}
		catch(Exception e1) {
			System.out.println(e1.getMessage());
		}
		
		Label separator = new Label(topComp, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		//gd.horizontalSpan = 7;
		separator.setLayoutData(gd);
		
		dialogComp.layout(true);
		
		return dialogComp;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		if (isCanRun()) {
			//createButton(parent, 0, "Save", false);
			// create OK and Cancel buttons by default
			createButton(parent, 1, "Run", true);
			createButton(parent, 2, "Close", false);
		}
		else {
			createButton(parent, 0, "Save", true);	
			createButton(parent, 2, "Close", false);
		}
	}
	
	
	protected void buttonPressed(int i){
		switch(i) {
			case 0: {
				System.out.println("Saving");
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
	
	//private HashMap okMap;
	
	protected abstract HashMap savePressed();
	
	private void handleSaving() {
		
		saveConfigToMap(this.getConfigName());
		
		SavedConfigManager scm = new SavedConfigManager();
		scm.setEditMap(getEditMap());
		scm.handleEdits();
		
		super.okPressed();
		
		/*IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		// gets current number of configurations before adding any
		int config_count = 0;
		try {
			config_count = settings.getInt("config_count");
		}
		catch (NumberFormatException e) {	
		}
		
		// gets a list of current config names
		ArrayList currentNames = new ArrayList();
		for (int i = 1; i <= config_count; i++) {
			currentNames.add(settings.get("soot_run_config_"+i));
		}
		
		Iterator temp = currentNames.iterator();
		while (temp.hasNext()) {
			System.out.println("Current Name: "+(String)temp.next());
		}
		
		// sets validator to know about already used names - but it doesn't use
		// them because then editing a file cannot use same file name
		SootConfigNameInputValidator validator = new SootConfigNameInputValidator();
		validator.setAlreadyUsed(currentNames);
		
		//boolean nameOk = false;
		while (true) {
			// create dialog to get name
			InputDialog nameDialog = new InputDialog(this.getShell(), "Saving Configuration Name", "Enter name to save configuration with:", getConfigName(), validator); 
			nameDialog.open();
		
			if (nameDialog.getReturnCode() == Dialog.OK) {
				
				if ((currentNames.contains(nameDialog.getValue())) && !(nameDialog.getValue().equals(getConfigName()))) {
					System.out.println("both cond true");
					MessageDialog msgDialog = new MessageDialog(this.getShell(), "Soot Configuration Saving Message", null, "This name has already been used okay to overwrite?", 0, new String [] {"Yes", "No"}, 0);
					msgDialog.open();
					if (msgDialog.getReturnCode() == 0) {
						
						saveConfigToMap(nameDialog.getValue());
			
						break;			
					}
					else {
						// continue and ask again
					}
				
				}
				//incConfigCount(config_count, nameDialog.getValue());
				saveConfigToMap(nameDialog.getValue());
				break;
			}
			else if (nameDialog.getReturnCode() == Dialog.CANCEL) {
				break;
			}
		
		}*/
	}	
	
	/*private void incConfigCount(int config_count, String name) {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		System.out.println("config_count: "+config_count);
		config_count++;
		settings.put("config_count", config_count);
		settings.put("soot_run_config_"+config_count, name);
		System.out.println("added name to settings");
		//return config_count;
	}*/
		
	private void saveConfigToMap(String name) {
		//IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		//System.out.println("config_count: "+config_count);
		SootSavedConfiguration newConfig = new SootSavedConfiguration(name, savePressed());
		//config_count++;
		//settings.put("config_count", config_count);
		newConfig.setEclipseDefs(getEclipseDefList());
		System.out.println("about to add config to editMap");
		if (getEditMap() == null) {
			setEditMap(new HashMap());
		}
		// TODO switch lines
		getEditMap().put(name, newConfig.toSaveArray());
		System.out.println("put in editMap: "+name);
		//getEditMap().put(name, newConfig.toSaveString());
		//System.out.println("added config to editMap");
		//System.out.println("Save String: "+newConfig.toSaveString());
					
	}
		//settings.put("New_Config","Smile");
		//settings.put("New Config", "Hello");
		
		/*ElementListSelectionDialog configChooser = new ElementListSelectionDialog(this.getShell(), new LabelProvider());
		System.out.println("configChooser created");
		configChooser.setElements(new Object [] {"Smile", "Jennifer"});
		System.out.println("set elements");
		configChooser.setTitle("Soot Configuration Chooser");
		configChooser.setMessage("Select:");
		configChooser.setMultipleSelection(false);
		configChooser.open();*/
	//}
	
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
		
		//tree.expandAll();
		tree.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});
		 
		return comp;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (selection.isEmpty()) {
			System.out.println("selection empty");	
		}
		else {
			Object elem = selection.getFirstElement();
			if (elem instanceof SootOption) {
				SootOption sel = (SootOption)elem;
				System.out.println(sel.getLabel());
				Control [] children = getPageContainer().getChildren();
				String childTitle = null;
				for (int i = 0; i < children.length; i++) {

					if( children[i] instanceof Composite) {
						if (children[i] instanceof Group) {
							childTitle = ((Group)children[i]).getText();
							
						}
						if (childTitle.compareTo(sel.getLabel()) == 0) {
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
	 * Returns the okMap.
	 * @return HashMap
	 */
	/*public HashMap getOkMap() {
		return okMap;
	}

	/**
	 * Sets the okMap.
	 * @param okMap The okMap to set
	 */
	/*public void setOkMap(HashMap okMap) {
		this.okMap = okMap;
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
	 * Returns the deleteList.
	 * @return ArrayList
	 */
	/*public ArrayList getDeleteList() {
		return deleteList;
	}

	/**
	 * Returns the editMap.
	 * @return HashMap
	 */
	public HashMap getEditMap() {
		return editMap;
	}

	/**
	 * Sets the deleteList.
	 * @param deleteList The deleteList to set
	 */
	/*public void setDeleteList(ArrayList deleteList) {
		this.deleteList = deleteList;
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

}
