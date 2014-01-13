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


package ca.mcgill.sable.soot.editors;

import java.io.*;
import java.util.ArrayList;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.views.contentoutline.*;

import ca.mcgill.sable.soot.editors.parser.JimpleFile;

public class JimpleContentOutlinePage extends ContentOutlinePage implements ISelectionChangedListener  {

	private IFile input;
	private JimpleEditor ed;
	private JimpleFile jimpleFileParser;
	private TreeViewer viewer;
	
	public JimpleContentOutlinePage(IFile file, JimpleEditor ed) {
		super();
		setInput(file);
		setEd(ed);
		
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		setViewer(getTreeViewer());
		getViewer().setContentProvider(new JimpleOutlineContentProvider());
		getViewer().setLabelProvider(new JimpleOutlineLabelProvider());
		getViewer().setInput(getContentOutline());
		getViewer().expandAll();
		
		getViewer().addSelectionChangedListener(this);
		
	}
	
	public JimpleOutlineObject getContentOutline(){
		setJimpleFileParser(new JimpleFile(getInput()));
		return getJimpleFileParser().getOutline();
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		if (!selection.isEmpty()) {
			Object elem = selection.getFirstElement();
			if (elem instanceof JimpleOutlineObject) {
				String toHighlight = ((JimpleOutlineObject)elem).getLabel();
				int start = getJimpleFileParser().getStartOfSelected(toHighlight);
				int length = getJimpleFileParser().getLength(toHighlight);
				getEd().selectAndReveal(start, length);
			}
		}
	}
	
	/**
	 * @return IFile
	 */
	public IFile getInput() {
		return input;
	}

	/**
	 * Sets the input.
	 * @param input The input to set
	 */
	public void setInput(IFile input) {
		this.input = input;
	}

	/**
	 * @return
	 */
	public JimpleEditor getEd() {
		return ed;
	}

	/**
	 * @param editor
	 */
	public void setEd(JimpleEditor editor) {
		ed = editor;
	}

	/**
	 * @return
	 */
	public JimpleFile getJimpleFileParser() {
		return jimpleFileParser;
	}

	/**
	 * @param file
	 */
	public void setJimpleFileParser(JimpleFile file) {
		jimpleFileParser = file;
	}

	/**
	 * @return
	 */
	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

}
