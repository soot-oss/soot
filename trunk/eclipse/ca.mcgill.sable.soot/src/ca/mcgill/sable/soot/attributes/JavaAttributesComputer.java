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


package ca.mcgill.sable.soot.attributes;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.*;


public class JavaAttributesComputer extends AbstractAttributesComputer {

	protected ArrayList computeNames(IFile file){
		IJavaElement jElem = getJavaElement(file);
		ICompilationUnit cu = (ICompilationUnit)jElem;
		return getNames(cu);
	}
	
	/**
	 * compute top-level names
	 */
	protected ArrayList computeNames(AbstractTextEditor editor){
		IJavaElement jElem = getJavaElement(editor);
		ArrayList names = new ArrayList();
		if (jElem instanceof ICompilationUnit){
			ICompilationUnit cu = (ICompilationUnit)jElem;
			return getNames(cu);
		}
		else {
			return names;
		}
	}
	
	private ArrayList getNames(ICompilationUnit cu){
		ArrayList names = new ArrayList();					
		try {
			IType [] topLevelDecls = cu.getTypes();
			for (int i = 0; i < topLevelDecls.length; i++){
				names.add(topLevelDecls[i].getFullyQualifiedName());
			}
		}
		catch(JavaModelException e){
		}
		return names;
	}
	
	/**
	 * initialize rec and proj
	 */
	protected void init(AbstractTextEditor editor){
		IJavaElement jElem = getJavaElement(editor);
		setProj(jElem.getResource().getProject());
		setRec(jElem.getResource());
	}
	
	public IJavaElement getJavaElement(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IJavaElement) ((IAdaptable) input).getAdapter(IJavaElement.class);
	}
	
	public IJavaElement getJavaElement(IFile file) {
		return (IJavaElement) ((IAdaptable) file).getAdapter(IJavaElement.class);
	}
	
	
}
