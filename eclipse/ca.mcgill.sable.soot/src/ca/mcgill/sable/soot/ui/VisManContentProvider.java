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

import org.eclipse.jface.viewers.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import java.util.*;

public class VisManContentProvider implements ITreeContentProvider {

	private final Object [] EMPTY_ARRAY = new Object [0];
	public VisManContentProvider() {
		super();
	}
	
	private boolean includeCon(IContainer con){
		try {
			IResource [] members = con.members();
			for (int i = 0; i < members.length; i++){
				if (members[i] instanceof IFolder){
					if (includeCon((IFolder)members[i])){
						return true;
					}
				}
				else if (members[i] instanceof IFile){
					IFile file = (IFile)members[i];
					if (file.getFileExtension() == null) continue;
					if (file.getFileExtension().equals("jimple") || file.getFileExtension().equals("java")){
						return true;
					}
				}
			}
		}
		catch(CoreException e){
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContainer){
			try {
				IResource [] mems = ((IContainer)parentElement).members();
				ArrayList list = new ArrayList();
				for (int i = 0; i < mems.length; i++){
					if (mems[i] instanceof IFolder){
						if (includeCon((IFolder)mems[i])){
							list.add(mems[i]); 
						}
					}
					else if (mems[i] instanceof IFile){
						if (((IFile)mems[i]).getFileExtension().equals("jimple") || ((IFile)mems[i]).getFileExtension().equals("java")){
							list.add(mems[i]);
						}
					}		
				}
				Object [] result = new Object[list.size()];
				list.toArray(result);
				return result;
			}
			catch(CoreException e){
			}
			
		}
		return EMPTY_ARRAY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof IResource){
			return ((IResource)element).getParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof IContainer){
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
