/*
 * Created on 27-Mar-2003
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
import ca.mcgill.sable.soot.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DavaHandler {

	private IFolder sootOutputFolder;
	private boolean davaBefore;
	private ArrayList beforeList;
	
	public boolean isDava(){
		if (!getSootOutputFolder().getFolder("dava").exists()) return false;
		return true;
	}
	
	public void handleBefore() {
		if (isDava()){
			createBeforeList();	
		}
	}
	
	private void createBeforeList(){
		try {
			IResource [] elems = getSootOutputFolder().getFolder("dava").getFolder("src").members();
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
	
	public void handleAfter() {
		ArrayList newMembers = new ArrayList();
		try {
			IResource [] elems = getSootOutputFolder().getFolder("dava").getFolder("src").members();
			for (int i = 0; i < elems.length; i++) {
				if (getBeforeList() == null){
					newMembers.add(elems[i]);
				}
				else if (!getBeforeList().contains(elems[i])){
					newMembers.add(elems[i]);
				}
			}
		}
		catch(CoreException e){
		}
		
		if (!newMembers.isEmpty()){
			// if is special dava project add src files there
			if (davaProjectExists()){
				copyFiles();
			}
			// if not special dava project ask user to create and add files there
			else {
				createSpecialDavaProject();
			}
		}
	}
	
	private void createSpecialDavaProject(){
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		MessageDialog create = new MessageDialog(window.getShell(), "Soot Question", null, "Would you like to create a new Dava Project with generated Dava src files?", 0, new String [] {"OK", "Cancel"}, 0);
		create.open();
		if (create.getReturnCode() == Dialog.OK){
			// copy files
		}	
	}
	
	private void copyFiles(){
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		MessageDialog copy = new MessageDialog(window.getShell(), "Soot Question", null, "Would you like to copy Dava src files to Dava project?", 0, new String [] {"OK", "Cancel"}, 0);
		copy.open();
		if (copy.getReturnCode() == Dialog.OK){
			// copy files
		}
		
	}
	
	private boolean davaProjectExists() {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		String dava_proj = settings.get("special_dava_project");
		if (dava_proj != null) {
			if ((SootPlugin.getWorkspace().getRoot().getProject(dava_proj).exists())
			&& (SootPlugin.getWorkspace().getRoot().getProject(dava_proj).isOpen())) return true;
		}
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

}
