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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import java.util.*;
import org.eclipse.core.resources.*;
import ca.mcgill.sable.soot.attributes.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.ui.*;

public class AnalysisTypeView extends ViewPart implements ICheckStateListener {

	private CheckboxTableViewer viewer;
	private ArrayList inputTypes;
	private boolean allTypesChecked;
	private ArrayList typesChecked;
	private IFile file;
	
	public AnalysisTypeView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		Table table = new Table(parent, SWT.CHECK);
		setViewer(new CheckboxTableViewer(table));
		getViewer().setContentProvider(new ArrayContentProvider());
		getViewer().setLabelProvider(new LabelProvider());
		getViewer().addCheckStateListener(this);
	}

	public void checkStateChanged(CheckStateChangedEvent event){
		SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile(getFile());
					
		ArrayList toShow = new ArrayList();
		for (int i = 0; i < getViewer().getCheckedElements().length; i++){
			toShow.add(getViewer().getCheckedElements()[i]);
		}
		handler.setTypesToShow(toShow);
		handler.setShowAllTypes(false);
		// also update currently shown editor and legend
		handler.setUpdate(true);
		final IEditorPart activeEdPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		SootPlugin.getDefault().getPartManager().updatePart(activeEdPart);
	
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	/**
	 * @return
	 */
	public CheckboxTableViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(CheckboxTableViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @return
	 */
	public ArrayList getInputTypes() {
		return inputTypes;
	}

	/**
	 * @param list
	 */
	public void setInputTypes(ArrayList list) {
		inputTypes = list;
		getViewer().setInput(inputTypes);
		if (isAllTypesChecked()){
			getViewer().setAllChecked(true);
		}
		else {
			getViewer().setAllChecked(false);
			getViewer().setCheckedElements(getTypesChecked().toArray());
		}
	}

	
	/**
	 * @return
	 */
	public boolean isAllTypesChecked() {
		return allTypesChecked;
	}

	/**
	 * @return
	 */
	public ArrayList getTypesChecked() {
		return typesChecked;
	}

	/**
	 * @param b
	 */
	public void setAllTypesChecked(boolean b) {
		allTypesChecked = b;
	}

	/**
	 * @param list
	 */
	public void setTypesChecked(ArrayList list) {
		typesChecked = list;
	}

	/**
	 * @return
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * @param file
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

}
