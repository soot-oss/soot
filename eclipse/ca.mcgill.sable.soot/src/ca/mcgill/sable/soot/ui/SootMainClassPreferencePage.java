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
package ca.mcgill.sable.soot.ui;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.jface.dialogs.Dialog;

import ca.mcgill.sable.soot.SootPlugin;


public class SootMainClassPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	CheckboxTableViewer tableViewer;
	Button addButton;
	Button editButton;
	Button removeButton;
	
	public void init(IWorkbench workbench){
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);				

		Composite inner = new Composite(composite, SWT.NONE);
		GridLayout innerLayout= new GridLayout();
		innerLayout.numColumns= 2;
		innerLayout.marginHeight= 0;
		innerLayout.marginWidth= 0;
		inner.setLayout(innerLayout);
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan= 2;
		inner.setLayoutData(gd);
		Table table= new Table(inner, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint= convertWidthInCharsToPixels(3);
		data.heightHint= convertHeightInCharsToPixels(10);
		table.setLayoutData(data);
				
		table.setHeaderVisible(true);
		table.setLinesVisible(true);		

		TableLayout tableLayout= new TableLayout();
		table.setLayout(tableLayout);

		TableColumn column1= new TableColumn(table, SWT.NONE);		
		column1.setText(Messages.getString("SootMainClassPreferencePage.Main_Class")); //$NON-NLS-1$
		column1.setWidth(320);
		//TableColumn column2= new TableColumn(table, SWT.NONE);		
		//column2.setText("Main Class");
	
		
		//System.out.println("created table");
		
		setTableViewer(new CheckboxTableViewer(table));		
		getTableViewer().setLabelProvider(new SootPrefLabelProvider());
		getTableViewer().setContentProvider(new SootPrefContentProvider());

		
		
		
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				enableButtons();
			}
		});

		getTableViewer().addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				// TODO : finish this method
				System.out.println("something was checked"); //$NON-NLS-1$
				System.out.println(event.getElement()+" "+event.getChecked()); //$NON-NLS-1$
				handleCheckedEvent(event);
			}
		});

		Composite buttons= new Composite(inner, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		buttons.setLayout(layout);
		
		setAddButton(new Button(buttons, SWT.PUSH));
		getAddButton().setText(Messages.getString("SootMainClassPreferencePage.Add"));  //$NON-NLS-1$
		getAddButton().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				add();
			}
		});

		setRemoveButton(new Button(buttons, SWT.PUSH));
		getRemoveButton().setText(Messages.getString("SootMainClassPreferencePage.Remove")); //$NON-NLS-1$
		getRemoveButton().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				remove();
			}
		});

		enableButtons();
		initializeValues();	
		return composite;
	}

	private void handleCheckedEvent(CheckStateChangedEvent event){
		// ensure only one box checked at a time
		SootPrefData spd = (SootPrefData)getTableViewer().getInput();
		if (event.getChecked()){
			getTableViewer().setAllChecked(false);
			getTableViewer().setChecked(event.getElement(), true);
			spd.setSelected(event.getElement().toString());	
		}
		else {
			getTableViewer().setChecked(getTableViewer().getElementAt(0), true);
			spd.setSelected(getTableViewer().getElementAt(0).toString());	
		}
		
	}
	
	private void enableButtons() {
		getAddButton().setEnabled(true);
		int selectionCount= ((IStructuredSelection)getTableViewer().getSelection()).size();
		getRemoveButton().setEnabled(selectionCount > 0 && selectionCount < getTableViewer().getTable().getItemCount());
	}
		
	private void add(){
		InputDialog classDialog = new InputDialog(this.getShell(), Messages.getString("SootMainClassPreferencePage.New_Main_Class"), Messages.getString("SootMainClassPreferencePage.Enter_main_class"), "", null);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		classDialog.open();
		
		if (classDialog.getReturnCode() == Dialog.OK) {
			Object currData = getTableViewer().getInput();
			System.out.println(currData.getClass().toString());
			if (currData instanceof SootPrefData) {
				((SootPrefData)currData).addToClassesArray(classDialog.getValue());
				getTableViewer().refresh();
			}
		}	
	}
	
	private void remove() {
		MessageDialog msgDialog = new MessageDialog(this.getShell(), Messages.getString("SootMainClassPreferencePage.Main_Class_Remove_Message"), null, Messages.getString("SootMainClassPreferencePage.Are_you_sure_you_want_to_remove_this_main_class"), 0, new String [] {Messages.getString("SootMainClassPreferencePage.Yes"), Messages.getString("SootMainClassPreferencePage.No")}, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		msgDialog.open();
		if (msgDialog.getReturnCode() == 0) {
			Object currData = getTableViewer().getInput();
			System.out.println(currData.getClass().toString());
			if (currData instanceof SootPrefData) {
				IStructuredSelection sel = (IStructuredSelection)getTableViewer().getSelection();
				System.out.println("sel to remove: "+sel.getFirstElement().toString()); //$NON-NLS-1$
				((SootPrefData)currData).removeFromClassesArray(sel.getFirstElement().toString());
				getTableViewer().setChecked(((SootPrefData)currData).getSelected(), true);
				getTableViewer().refresh();
			}
		}
	}
	
	public IPreferenceStore getPreferenceStore() {
	   return SootPlugin.getDefault().getPreferenceStore();
	}
	
	public IPreferenceStore doGetPreferenceStore() {
	   return SootPlugin.getDefault().getPreferenceStore();
	}
	
	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		
		getTableViewer().setInput(new SootPrefData(store.getString(Messages.getString("SootMainClassPreferencePage.classes")), store.getString(Messages.getString("SootMainClassPreferencePage.selected")))); //$NON-NLS-1$ //$NON-NLS-2$
		getTableViewer().setChecked(store.getString(Messages.getString("SootMainClassPreferencePage.selected")), true); //$NON-NLS-1$
	}

	
	public boolean performOk(){
		storeValues();
		return true;
	}
	
	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();
		SootPrefData spd = (SootPrefData)getTableViewer().getInput();
		System.out.println(spd.getSaveString()+" "+spd.getSelected()); //$NON-NLS-1$
		store.setValue(Messages.getString("SootMainClassPreferencePage.classes"), spd.getSaveString()); //$NON-NLS-1$
		store.setValue(Messages.getString("SootMainClassPreferencePage.selected"), spd.getSelected()); //$NON-NLS-1$
		
	}

	// for when user presses restore default button
	private void initializeDefaults() {
	   	IPreferenceStore store = getPreferenceStore();
		getTableViewer().setInput(new SootPrefData(store.getDefaultString(Messages.getString("SootMainClassPreferencePage.classes")), store.getDefaultString(Messages.getString("SootMainClassPreferencePage.selected")))); //$NON-NLS-1$ //$NON-NLS-2$
		getTableViewer().setChecked(store.getDefaultString(Messages.getString("SootMainClassPreferencePage.selected")), true); //$NON-NLS-1$
	
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
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @return
	 */
	public Button getEditButton() {
		return editButton;
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
	public void setEditButton(Button button) {
		editButton = button;
	}

	/**
	 * @param button
	 */
	public void setRemoveButton(Button button) {
		removeButton = button;
	}

}
