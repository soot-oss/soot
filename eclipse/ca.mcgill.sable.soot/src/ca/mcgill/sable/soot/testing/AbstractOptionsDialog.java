package ca.mcgill.sable.soot.testing;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.*;
import org.eclipse.jface.dialogs.Dialog;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.launching.SootConfigNameInputValidator;
import ca.mcgill.sable.soot.launching.SootSavedConfiguration;


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
	
	/**
	 * Constructor for AbstractOptionsDialog.
	 * @param parentShell
	 */
	public AbstractOptionsDialog(Shell parentShell) {
		super(parentShell);
	}
	
	private HashMap defList;
	
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
		createButton(parent, 0, "Save Configuration", false);
		// create OK and Cancel buttons by default
		createButton(parent, 1, IDialogConstants.OK_LABEL, true);
		createButton(parent, 2, IDialogConstants.CANCEL_LABEL, false);
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
	
	protected abstract HashMap savePressed();
	
	private  void handleSaving() {
		InputDialog nameDialog = new InputDialog(this.getShell(), "Saving Configuration Name", "Enter name to save configuration with:", "", new SootConfigNameInputValidator()); 
		nameDialog.open();
		if (nameDialog.getReturnCode() == Dialog.OK) {
			System.out.println(nameDialog.getValue());
		
			IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
			int config_count = 0;
			try {
				config_count = settings.getInt("config_count");
			}
			catch (NumberFormatException e) {	
			}
			System.out.println("config_count: "+config_count);
			SootSavedConfiguration newConfig = new SootSavedConfiguration(nameDialog.getValue(), savePressed());
			config_count++;
			settings.put("config_count", config_count);
			settings.put("soot_run_config_"+config_count, nameDialog.getValue());
			System.out.println("added name to settings");
			settings.put(nameDialog.getValue(), newConfig.toSaveString());
			System.out.println(newConfig.toSaveString());
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

}
