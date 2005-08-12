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

import java.util.ArrayList;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.*;

public class JimpleAttributesComputer extends AbstractAttributesComputer {

	protected ArrayList computeNames(IFile file){
		return getNames();
	}
	
	/* (non-Javadoc)
	 * @see ca.mcgill.sable.soot.attributes.AbstractAttributesComputer#computeNames(org.eclipse.ui.texteditor.AbstractTextEditor)
	 */
	protected ArrayList computeNames(AbstractTextEditor editor) {
		return getNames();
	}
	
	private ArrayList getNames(){
		ArrayList names = new ArrayList();
		names.add(fileToNoExt(getRec().getName()));
		return names;
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.sable.soot.attributes.AbstractAttributesComputer#init(org.eclipse.ui.texteditor.AbstractTextEditor)
	 */
	protected void init(AbstractTextEditor editor) {
		setRec(getResource(editor));
		setProj(getRec().getProject());

	}
	
	public IResource getResource(AbstractTextEditor textEditor) {
		IEditorInput input= textEditor.getEditorInput();
		return (IResource) ((IAdaptable) input).getAdapter(IResource.class);
	}
	
	public String fileToNoExt(String filename) {
		return filename.substring(0, filename.lastIndexOf('.'));
	}


	

}
