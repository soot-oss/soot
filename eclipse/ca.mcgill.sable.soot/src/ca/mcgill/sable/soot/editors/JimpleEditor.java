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

//import java.util.*;

//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.ui.IEditorInput;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import ca.mcgill.sable.soot.*;

//import org.eclipse.ui.texteditor.MarkerUtilities;
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
public class JimpleEditor extends TextEditor {

	private ColorManager colorManager;
	protected JimpleContentOutlinePage page;
	private ISourceViewer viewer;
	
	/**
	 * Constructor for JimpleEditor.
	 */
	public JimpleEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new JimpleConfiguration(colorManager, this));
		setDocumentProvider(new JimpleDocumentProvider());
		setViewer(this.getSourceViewer());
		//if (this.getSourceViewer() == null){
			//System.out.println("JimpleEditor viewer is always null");
		//}
		//ColorManager colorManager = new ColorManager();
		//getViewer().setTextColor(colorManager.getColor(IJimpleColorConstants.JIMPLE_ATTRIBUTE_GOOD), 0, 10, true);
		
		
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
		colorManager.dispose();
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
		//System.out.println("source viewer created");
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
