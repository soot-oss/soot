/*
 * Created on Jan 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class VisManContentProvider implements ITreeContentProvider {

	private final Object [] EMPTY_ARRAY = new Object [0];
	/**
	 * 
	 */
	public VisManContentProvider() {
		super();
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		System.out.println("Parent: "+parentElement.getClass());
		if (parentElement instanceof IContainer){
			try {
				IResource [] mems = ((IContainer)parentElement).members();
				ArrayList list = new ArrayList();
				//Object [] result = new Object[mems.length];
				for (int i = 0; i < mems.length; i++){
					if (mems[i] instanceof IFolder){
						//Object [] children = getChildren(mems[i]);
						if (includeCon((IFolder)mems[i])){
							list.add(mems[i]); 
						}
						/*for (int j = 0; j < children.length; j++){
							list.add(children[j]);
						}*/
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
		// TODO Auto-generated method stub
		if (element instanceof IResource){
			return ((IResource)element).getParent();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof IContainer){
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
