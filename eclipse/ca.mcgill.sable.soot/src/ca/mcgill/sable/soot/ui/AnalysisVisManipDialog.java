/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.*;
import java.util.*;
import org.eclipse.core.resources.*;
import ca.mcgill.sable.soot.attributes.*;

public class AnalysisVisManipDialog
	extends TitleAreaDialog implements ISelectionChangedListener {

	private HashMap dataMap;
	private ArrayList fileList;
	private IProject proj;
	private ArrayList allSelected;
	private HashMap currentSettingsMap;
	
	
	/**
	 * @param parentShell
	 */
	public AnalysisVisManipDialog(Shell parentShell) {
		super(parentShell);
	}

	
	
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText(Messages.getString("AnalysisVisManDialog.Title")); 
	}
	
	public void setDataMap(HashMap map){
		dataMap = map;
	}
	
	protected Control createDialogArea(Composite parent) {
		GridData gd;
		
		Composite dialogComp = (Composite)super.createDialogArea(parent);
		Composite topComp = new Composite(dialogComp, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		topComp.setLayoutData(gd);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topComp.setLayout(topLayout);
		
		setTitle("Project: "+getProj().getName());
		setMessage("");
		
		TabFolder tabFolder = new TabFolder(topComp, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gd);
		
		TabItem singleFileItem = new TabItem(tabFolder, SWT.NONE);
		singleFileItem.setText("By File");
		TabItem globalItem = new TabItem(tabFolder, SWT.NONE);
		globalItem.setText("By Project");
		
		/* create single file tab */
		SashForm sash = new SashForm(tabFolder, SWT.NONE);
		singleFileItem.setControl(sash);
		sash.setOrientation(SWT.HORIZONTAL);
		
		gd = new GridData(GridData.FILL_BOTH);
		sash.setLayoutData(gd);
		
		Composite selection = createSelectionArea(sash);
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.horizontalSpan = 1;
		
		selection.setLayoutData(gd);
		
		Composite types = createCheckArea(sash);
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.horizontalSpan = 1;
		
		types.setLayoutData(gd);
		
		/* create global tab */
		Composite global = new Composite(tabFolder, SWT.NONE);
		GridLayout globalLayout = new GridLayout();
		
		global.setLayout(globalLayout);
		
		Table allTable = new Table(global, SWT.CHECK);
		TableViewer allTypesList = new TableViewer(allTable);
		allTypesList.setContentProvider(new ArrayContentProvider());
		allTypesList.setLabelProvider(new LabelProvider());
		
		gd = new GridData(GridData.FILL_BOTH);
		allTypesList.getControl().setLayoutData(gd);
		
		Composite buttonPanel = new Composite(global, SWT.NONE);
		GridLayout bpLayout = new GridLayout();
		bpLayout.numColumns = 2;
		buttonPanel.setLayout(bpLayout);
		
		Button selectAll = new Button(buttonPanel, SWT.PUSH);
		selectAll.setText("Select All");
		gd = new GridData();
		gd.horizontalSpan = 1;
		selectAll.setLayoutData(gd);
		
		Button deselectAll = new Button(buttonPanel, SWT.PUSH);
		deselectAll.setText("De-select All");
		gd = new GridData();
		gd.horizontalSpan = 1;
		deselectAll.setLayoutData(gd);
		
		globalItem.setControl(global);
		
		return dialogComp;
	}
	
	private CheckboxTableViewer checkTypes;
	
	private Composite createCheckArea(Composite parent){
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
	
		layout.numColumns = 1;
	
		
		comp.setLayout(layout);
		
		GridData gd = new GridData();
		Table table = new Table(comp, SWT.CHECK);
		checkTypes = new CheckboxTableViewer(table);
		
		gd = new GridData(GridData.FILL_BOTH);
	
		checkTypes.getControl().setLayoutData(gd);
		
		checkTypes.setContentProvider(new ArrayContentProvider());
		checkTypes.setLabelProvider(new LabelProvider());
		return comp;
	}
	

	
	private Composite createSelectionArea(Composite parent) {
	 	Composite comp = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
	
		layout.numColumns = 1;
	
		
		comp.setLayout(layout);
		
		GridData gd = new GridData();
		
		TreeViewer files = new TreeViewer(comp);
		gd = new GridData(GridData.FILL_BOTH);
	
		files.getControl().setLayoutData(gd);
		
		files.setContentProvider(new VisManContentProvider());
		files.setLabelProvider(new VisManLabelProvider());
	  	files.setInput(getInitialInput());
	
		files.addSelectionChangedListener(this);
	
		
		files.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});
		 

		return comp;
	}
	


	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (selection.isEmpty()) {
			checkTypes.setInput(getCheckInput(""));
		}
		else {
			Object elem = selection.getFirstElement();
			if (!(elem instanceof IFile)) return;
			ArrayList list = (ArrayList)getCheckInput(elem);
			handleLast();
			checkTypes.setInput(list);
			Object [] checkElems;
			if ((currentSettingsMap != null) && (currentSettingsMap.containsKey(elem))){
				checkElems = (Object [])currentSettingsMap.get(elem);
				checkTypes.setCheckedElements(checkElems);
			}
			else {
				SootAttributesHandler handler = getHandlerForFile((IFile)elem);
				if (handler != null) {
					if (handler.isShowAllTypes()){
						if (list != null){
							Object [] elems = new Object[list.size()];  
				
							for (int i = 0; i < list.size(); i++){
								elems[i] = checkTypes.getElementAt(i);
					
							}
							checkTypes.setCheckedElements(elems);
						}
					}
					else {
						Iterator it = handler.getTypesToShow().iterator();
						Object [] elems = new Object[handler.getTypesToShow().size()];
						int i = 0;
						while (it.hasNext()){
							elems[i] = it.next();
							i++;
						}
						checkTypes.setCheckedElements(elems);
						
						
					}
				}
			}
			if (getAllSelected() == null){
				setAllSelected(new ArrayList());
			}
			getAllSelected().add(elem);
		}
	}
	
	protected void okPressed(){
		handleLast();
		super.okPressed();
	}
	
	private void handleLast(){
		if (getAllSelected() == null) return;
		Object lastElem = getAllSelected().get(getAllSelected().size()-1);
		if (lastElem != null){
			Object [] checkElems = checkTypes.getCheckedElements();
			if (currentSettingsMap == null){
				currentSettingsMap = new HashMap();
			}
			currentSettingsMap.put(lastElem, checkElems);
			
		}
	}
	
	private IContainer getInitialInput(){
		IContainer proj = getProj();
		return proj;
	}
	
	private SootAttributesHandler getHandlerForFile(IFile next){
		SootAttributesHandler handler = null;
		String fileExtension = next.getFileExtension();
		if (fileExtension != null && fileExtension.equals("java")){
			JavaAttributesComputer jac = new JavaAttributesComputer();
			jac.setProj(getProj());
			jac.setRec(next);
			handler = jac.getAttributesHandler(next);
		}
		else {
			JimpleAttributesComputer jac = new JimpleAttributesComputer();
			jac.setProj(getProj());
			jac.setRec(next);
			handler = jac.getAttributesHandler(next);
		}
		return handler;
	}
	
	private ArrayList getAllTypesInput(){
		ArrayList allTypes = new ArrayList();
		Iterator it = getFileList().iterator();
		while (it.hasNext()){
			SootAttributesHandler handler = getHandlerForFile((IFile)it.next());
			if ((handler != null) && (handler.getAttrList() != null)){
				Iterator attrsIt = handler.getAttrList().iterator();
			
				while (attrsIt.hasNext()){
					SootAttribute sa = (SootAttribute)attrsIt.next();
					Iterator typesIt = sa.getAnalysisTypes().iterator();
					while (typesIt.hasNext()){
						String val = (String)typesIt.next();
						if (!allTypes.contains(val)){
							allTypes.add(val);
						}
					}
				}
			}
			
		}
		
		return allTypes;
	}
	
	private ArrayList getCheckInput(Object key){
		ArrayList list = new ArrayList();
		if (!(key instanceof IFile)) return list;
		IFile next = (IFile)key;
		SootAttributesHandler handler = getHandlerForFile(next);
		
		if ((handler != null) && (handler.getAttrList() != null)){
			Iterator attrsIt = handler.getAttrList().iterator();
			ArrayList types = new ArrayList();
			while (attrsIt.hasNext()){
				SootAttribute sa = (SootAttribute)attrsIt.next();
				Iterator typesIt = sa.getAnalysisTypes().iterator();
				while (typesIt.hasNext()){
					String val = (String)typesIt.next();
					if (!types.contains(val)){
						types.add(val);
					}
				}
			}
			return types;
		}
		else {
			return null;
		}
	}
	
	private void handleKeyPressed(KeyEvent e){
	}
	
	/**
	 * @return
	 */
	public ArrayList getFileList() {
		return fileList;
	}

	/**
	 * @param list
	 */
	public void setFileList(ArrayList list) {
		fileList = list;
	}

	/**
	 * @return
	 */
	public IProject getProj() {
		return proj;
	}

	/**
	 * @param project
	 */
	public void setProj(IProject project) {
		proj = project;
	}

	/**
	 * @return
	 */
	public ArrayList getAllSelected() {
		return allSelected;
	}

	/**
	 * @param list
	 */
	public void setAllSelected(ArrayList list) {
		allSelected = list;
	}

	/**
	 * @return
	 */
	public HashMap getCurrentSettingsMap() {
		return currentSettingsMap;
	}

	/**
	 * @param map
	 */
	public void setCurrentSettingsMap(HashMap map) {
		currentSettingsMap = map;
	}

	/**
	 * @return
	 */
	public CheckboxTableViewer getCheckTypes() {
		return checkTypes;
	}

	/**
	 * @param viewer
	 */
	public void setCheckTypes(CheckboxTableViewer viewer) {
		checkTypes = viewer;
	}

}
