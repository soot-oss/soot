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


package ca.mcgill.sable.soot.attributes;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.part.*;

import ca.mcgill.sable.soot.SootPlugin;


public class SootAttributeJavaSelectAction extends SootAttributeSelectAction {

	public SootAttributeJavaSelectAction(ResourceBundle bundle, String prefix, ITextEditor editor, IVerticalRulerInfo rulerInfo) {	
		super(bundle, prefix, editor, rulerInfo);
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.sable.soot.attributes.SootAttributeSelectAction#getMarkerLinks()
	 */
	public ArrayList getMarkerLinks(){
		SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile((IFile)getResource(getEditor()));
		ArrayList links = handler.getJavaLinks(getLineNumber()+1);
		Iterator it = links.iterator();
		
		return links;
	}
	
	protected int getLinkLine(LinkAttribute la){
		return la.getJavaLink();
	}

	
	public void findClass(String className){
		setLinkToEditor(getEditor());		
		String resource = removeExt(getResource(getEditor()).getName());
		
		String ext = getResource(getEditor()).getFileExtension();
	
		IProject proj = getResource(getEditor()).getProject();
		String slashedClassName = className.replaceAll("\\.", System.getProperty("file.separator"));
		String classNameToFind = (ext == null) ? slashedClassName : slashedClassName+"."+ext;
		IJavaProject jProj = JavaCore.create(proj);
		try {
	
			IPackageFragmentRoot [] roots = jProj.getAllPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++){
				if (!(roots[i].getResource() instanceof IContainer)) continue;
				IResource fileToFind = ((IContainer)roots[i].getResource()).findMember(classNameToFind);
				if (fileToFind == null) continue;
			
				if (!fileToFind.equals(resource)){
					try {
						setLinkToEditor((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FileEditorInput((IFile)fileToFind), fileToFind.getName()));
					}
					catch (PartInitException e){
					}
				}
			}
		}
		catch (JavaModelException e){
			setLinkToEditor(getEditor());
		}
	
	}
}
