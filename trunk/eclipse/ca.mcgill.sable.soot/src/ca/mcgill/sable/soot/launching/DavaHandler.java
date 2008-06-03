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


package ca.mcgill.sable.soot.launching;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import ca.mcgill.sable.soot.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.jdt.core.*;

/**
 * After Soot runs handles any new dava files by either 
 * copying them to an already existing "Dava Project" or
 * creating a "Dava Project" and copying the files there
 * 
 * Currently disabled 
 */
public class DavaHandler {

	private IFolder sootOutputFolder;
	private boolean davaBefore;
	private ArrayList beforeList;
	private String davaProjName;
	private IJavaProject davaProj;
	private IFolder srcFolder;
	
	
	public DavaHandler(){
		setDavaProjName(Messages.getString("DavaHandler.Dava_Project"));
	}
	
	public boolean isDava(){
		if (!getSootOutputFolder().getFolder(Messages.getString("DavaHandler.dava")).exists()) return false; 
		return true;
	}
	
	// creates list of Dava files before Soot runs
	public void handleBefore() {
		if (isDava()){
			createBeforeList();	
		}
	}
	
	private void createBeforeList(){
		try {
			IResource [] elems = getSootOutputFolder().getFolder(Messages.getString("DavaHandler.dava")).getFolder(Messages.getString("DavaHandler.src")).members(); 
			for (int i = 0; i < elems.length; i++) {
				if (getBeforeList() == null){
					setBeforeList(new ArrayList());
				}
				getBeforeList().add(elems[i]);
			}
		}
		catch(CoreException e){
		}
		
	}
	
	// handles two things:
	// 1. if new dava files exist
	// 2. if already existing dava files have changed
	// if one of those two things is true then:
	// if no dava project exists asks to create one else
	// asks to copy files
	public void handleAfter() {
		ArrayList newMembers = new ArrayList();
		IPath jreLibPath = null;
		try {
			IResource [] elems = getSootOutputFolder().getFolder(Messages.getString("DavaHandler.dava")).getFolder(Messages.getString("DavaHandler.src")).members(); 
			for (int i = 0; i < elems.length; i++) {
				if (getBeforeList() == null){
					newMembers.add(elems[i]);
					if (elems[i] instanceof IFile){
					
						SootPlugin.getDefault().getManager().setToFalseRemove((IFile)elems[i]);
					}
				}
				else if (getBeforeList().contains(elems[i])) {
					if (elems[i] instanceof IFile){
					
						if (SootPlugin.getDefault().getManager().isFileMarkersRemove((IFile)elems[i])){
							newMembers.add(elems[i]);
							// this sets changed bit to 0 - so file doesn't stay on list indefinitely
											
							SootPlugin.getDefault().getManager().setToFalseRemove((IFile)elems[i]);
						}
					}
					
				}
				else if (!getBeforeList().contains(elems[i])){
					if (SootPlugin.getDefault().getManager().getChangedResources() == null){
					}
					else if (SootPlugin.getDefault().getManager().getChangedResources().containsKey(elems[i])){
						newMembers.add(elems[i]);
						// this sets changed bit to 0 - so file doesn't stay on list indefinitely
						if (elems[i] instanceof IFile){
					
							SootPlugin.getDefault().getManager().setToFalseRemove((IFile)elems[i]);
						}
					}
				}
			}
			
			// testing class lib copying
			IProject proj = getSootOutputFolder().getProject();
			IResource [] elements = proj.members();
			
		
			IJavaProject jProj = JavaCore.create(proj);
			IClasspathEntry [] paths = jProj.getRawClasspath();
			
			for (int i = 0; i < paths.length; i++){
				switch(paths[i].getEntryKind()){
					case IClasspathEntry.CPE_CONTAINER:{
						jreLibPath = paths[i].getPath();
						
						break;
					}
					
				}
			}
		}
		catch(CoreException e){
		}
		
		if (!newMembers.isEmpty()){
			
			// if is special dava project add src files there
			if (davaProjectExists()){
				setDavaProj(JavaCore.create(SootPlugin.getWorkspace().getRoot().getProject(getDavaProjName())));
				if (getDavaProj().isOpen()){
					if (shouldCopyFiles()){
						copyFiles(newMembers);
					}
				}
				else {
					openProject();
					if (shouldCopyFiles()){
						copyFiles(newMembers);
					}
				}
			}
			// if not special dava project ask user to create and add files there
			else {
				boolean result = createSpecialDavaProject(jreLibPath);
				if (result){
					copyFiles(newMembers);
				}
				
			}
		}
	}
	
	private boolean createSpecialDavaProject(IPath jreLibPath){
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		MessageDialog create = new MessageDialog(window.getShell(), Messages.getString("DavaHandler.Soot_Question"), null, Messages.getString("DavaHandler.Would_you_like_to_create_a_new_Dava_Project_with_generated_Dava_src_files"), 0, new String [] {Messages.getString("DavaHandler.OK"), Messages.getString("DavaHandler.Cancel")}, 0); 
		create.open();
		if (create.getReturnCode() == Dialog.OK){
			// create proj
			IProject proj = SootPlugin.getWorkspace().getRoot().getProject(getDavaProjName());
			if (!proj.exists()){
				try {
					proj.create(null);
					proj.open(null);
					IProjectDescription pd = proj.getDescription();
					String [] natures = new String [] {Messages.getString("org.eclipse.jdt.core.javanature")}; 
					
					pd.setNatureIds(natures);
					proj.setDescription(pd, null);
					
					setDavaProj(JavaCore.create(proj));
					IFolder folder = proj.getFolder(Messages.getString("DavaHandler.src")); //$NON-NLS-1$
					if (!folder.exists()){
						folder.create(false, true, null);
					}
					setSrcFolder(folder);
					IFolder out = proj.getFolder(Messages.getString("DavaHandler.bin")); //$NON-NLS-1$
					if (!folder.exists()){
						folder.create(false, true, null);
					}
					getDavaProj().setOutputLocation(out.getFullPath(), null);
					IClasspathEntry [] entries = new IClasspathEntry [2];
					entries[0] = JavaCore.newSourceEntry(folder.getFullPath());
					if (jreLibPath != null){
						entries[1] = JavaCore.newContainerEntry(jreLibPath);
					}
					getDavaProj().setRawClasspath(entries, null);
					return true;
				} 
				catch (CoreException e) {
					e.printStackTrace();
					return false;
				}
			}
			
		}	
		return false;
	}
	
	private boolean shouldCopyFiles() {
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		MessageDialog copy = new MessageDialog(window.getShell(), Messages.getString("DavaHandler.Soot_Question"), null, Messages.getString("DavaHandler.Would_you_like_to_copy_Dava_src_files_to_the_Dava_Project"), 0, new String [] {Messages.getString("DavaHandler.OK"), Messages.getString("DavaHandler.Cancel")}, 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		copy.open();
		if (copy.getReturnCode() == Dialog.OK) return true;
		return false;
	}
	
	private void copyFiles(ArrayList newFiles){
		// copy new files
		Iterator it = newFiles.iterator();
		IPath srcPath = getDavaProj().getProject().getFolder(Messages.getString("DavaHandler.src")).getFullPath();
		while (it.hasNext()){
		
			try {
				IResource next = (IResource)it.next();
				IPath copyTo = srcPath.append(System.getProperty("file.separator")+next.getName()); //$NON-NLS-1$
				if (getDavaProj().getProject().getFolder(Messages.getString("DavaHandler.src")).getFile(next.getName()).exists()) { //$NON-NLS-1$
					getDavaProj().getProject().getFolder(Messages.getString("DavaHandler.src")).getFile(next.getName()).delete(false, null);
				}
				next.copy(copyTo, false, null);
			} 
			catch (CoreException e) {
				e.printStackTrace();
				
			}
		}
	
		
	}
	
	private void openProject(){
		try {
			getDavaProj().open(null);
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private boolean davaProjectExists() {
		if (SootPlugin.getWorkspace().getRoot().getProject(getDavaProjName()).exists()) return true;
		return false;
	}
	public void updateDavaProject(){
		
	}
	/**
	 * @return
	 */
	public IFolder getSootOutputFolder() {
		return sootOutputFolder;
	}

	/**
	 * @param folder
	 */
	public void setSootOutputFolder(IFolder folder) {
		sootOutputFolder = folder;
	}

	/**
	 * @return
	 */
	public boolean isDavaBefore() {
		return davaBefore;
	}

	/**
	 * @param b
	 */
	public void setDavaBefore(boolean b) {
		davaBefore = b;
	}

	/**
	 * @return
	 */
	public ArrayList getBeforeList() {
		return beforeList;
	}

	/**
	 * @param list
	 */
	public void setBeforeList(ArrayList list) {
		beforeList = list;
	}

	/**
	 * @return
	 */
	public String getDavaProjName() {
		return davaProjName;
	}

	/**
	 * @param string
	 */
	public void setDavaProjName(String string) {
		davaProjName = string;
	}

	/**
	 * @return
	 */
	public IJavaProject getDavaProj() {
		return davaProj;
	}

	/**
	 * @param project
	 */
	public void setDavaProj(IJavaProject project) {
		davaProj = project;
	}

	/**
	 * @return
	 */
	public IFolder getSrcFolder() {
		return srcFolder;
	}

	/**
	 * @param folder
	 */
	public void setSrcFolder(IFolder folder) {
		srcFolder = folder;
	}

}
