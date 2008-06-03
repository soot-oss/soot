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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.ui.*;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;


public class VisManLauncher implements IWorkbenchWindowActionDelegate {

	private IProject proj;
	private IResource rec;
	
	
	public VisManLauncher() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		AnalysisVisManipDialog dialog = new AnalysisVisManipDialog(window.getShell());
		dialog.setFileList(getFilesFromCon(getProj()));
		dialog.setProj(getProj());
		dialog.open();
		if (dialog.getReturnCode() == Dialog.OK){
			if (dialog.getAllSelected() != null){
				Iterator selIt = dialog.getAllSelected().iterator();
				while (selIt.hasNext()){
					Object next = selIt.next();
					SootAttributesHandler handler = SootPlugin.getDefault().getManager().getAttributesHandlerForFile((IFile)next);
					Object [] elems;
					if ((dialog.getCurrentSettingsMap() != null) && (dialog.getCurrentSettingsMap().containsKey(next))){
						elems = (Object [])dialog.getCurrentSettingsMap().get(next);
					}
					else {
						elems = dialog.getCheckTypes().getCheckedElements();
					}
					ArrayList toShow = new ArrayList();
					for (int i = 0; i < elems.length; i++){
						toShow.add(elems[i]);
					}
					handler.setTypesToShow(toShow);
					handler.setShowAllTypes(false);
					// also update currently shown editor and legend
					handler.setUpdate(true);
					final IEditorPart activeEdPart = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
					SootPlugin.getDefault().getPartManager().updatePart(activeEdPart);
	
				}
			}
		}
		
	}

	public HashMap configureDataMap(){
		HashMap map = new HashMap();
		// get all .java and .jimple files in the project
		// for each determine if attr xml exist and if yes which 
		// kinds of attrs there are
		ArrayList files = getFilesFromCon(getProj());
		Iterator it = files.iterator();
		while (it.hasNext()){
			IFile next = (IFile)it.next();
			SootAttributesHandler handler;
			if (next.getFileExtension().equals("java")){
				JavaAttributesComputer jac = new JavaAttributesComputer();
				jac.setProj(getProj());
				jac.setRec(getRec());
				handler = jac.getAttributesHandler(next);
			}
			else {
				JimpleAttributesComputer jac = new JimpleAttributesComputer();
				jac.setProj(getProj());
				jac.setRec(getRec());
				handler = jac.getAttributesHandler(next);
			}
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
				map.put(next, types);
			}
			else {
				map.put(next, null);
			}
			
		}
		return map;	
	}
	
	
	
	public ArrayList getFilesFromCon(IContainer con){
		ArrayList files = new ArrayList();
		try {
			IResource [] recs = con.members();
			for (int i = 0; i < recs.length; i++){
				if (recs[i] instanceof IFile){
					IFile file = (IFile)recs[i];
					if (file.getFileExtension() == null) continue;
					if (file.getFileExtension().equals("jimple") || file.getFileExtension().equals("java")){
						files.add(recs[i]);
					}
				}
				else if (recs[i] instanceof IContainer){
					files.addAll(getFilesFromCon((IContainer)recs[i]));
				}
				else {
					throw new RuntimeException("unknown member type");
				}
			}
		}
		catch(CoreException e){
		}
		return files;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection){
			IStructuredSelection struct = (IStructuredSelection)selection;
			Iterator it = struct.iterator();
			while (it.hasNext()){
				Object next = it.next();
				if (next instanceof IResource) {
					setProj(((IResource)next).getProject());
					setRec((IResource)next);
				}
				else if (next instanceof IJavaElement) {
					IJavaElement jElem = (IJavaElement)next;
					setProj(jElem.getJavaProject().getProject());
					setRec(jElem.getResource());
				}
			}
		}
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
	public IResource getRec() {
		return rec;
	}

	/**
	 * @param resource
	 */
	public void setRec(IResource resource) {
		rec = resource;
	}

}
