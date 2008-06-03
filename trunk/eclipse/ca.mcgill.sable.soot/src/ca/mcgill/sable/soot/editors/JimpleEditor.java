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


import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import ca.mcgill.sable.soot.SootPlugin;


public class JimpleEditor extends TextEditor {

	private ColorManager colorManager;
	protected JimpleContentOutlinePage page;
	private ISourceViewer viewer;
	
	/**
	 * Constructor for JimpleEditor.
	 */
	public JimpleEditor() {
		super();
		colorManager = SootPlugin.getDefault().getColorManager();
		setSourceViewerConfiguration(new JimpleConfiguration(colorManager, this));
		setDocumentProvider(new JimpleDocumentProvider());
		setViewer(this.getSourceViewer());
	
		
	}
	
	/**
	 * This method is what creates the Jimple Content Outliner
	 */
	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			//System.out.println("in getAdapter of editor");
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				setPage(new JimpleContentOutlinePage(((IFileEditorInput)input).getFile(), this));
				return getPage();
			}
		}
		return super.getAdapter(key);
	}
		
	public void dispose() {
		super.dispose();
	}
	
	/**
	 * @return
	 */
	public JimpleContentOutlinePage getPage() {
		return page;
	}

	/**
	 * @param page
	 */
	public void setPage(JimpleContentOutlinePage page) {
		this.page = page;
	}

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		setViewer(super.createSourceViewer(parent, ruler, styles));
		SootPlugin.getDefault().addEditorViewer(getViewer());
		return getViewer();
	}
	
	/**
	 * @return
	 */
	public ISourceViewer getViewer() {
		return viewer;
	}

	/**
	 * @param viewer
	 */
	public void setViewer(ISourceViewer viewer) {
		this.viewer = viewer;
	}

}
