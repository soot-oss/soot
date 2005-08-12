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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.*;

import ca.mcgill.sable.soot.SootPlugin;

/**
 * Handles Soot ouptut dir. Potentially will do something with
 * Soot generated files such as open them automatically. 
 */
public class SootOutputFilesHandler {

	private IFolder sootOutputFolder;
	private ArrayList oldFilelist;
	private ArrayList newFilelist;
	private IWorkbenchWindow window;
	private ArrayList beforeFileList;
	
	
	/**
	 * Constructor for SootOutputFilesHandler.
	 */
	public SootOutputFilesHandler(IWorkbenchWindow window) {
		super();
		setWindow(window);
	}
	
	
		
	public void resetSootOutputFolder(IProject project) {
		try {
			setSootOutputFolder(project.getFolder("sootOutput"));
			if (!getSootOutputFolder().exists()) {
				getSootOutputFolder().create(false, true, null);
			}
		}
		catch (Exception e1) {
			System.out.println(e1.getMessage());
		}	
	}
	
	public void refreshAll(IProject project){
		try{
			project.refreshLocal(IResource.DEPTH_INFINITE, null);	
		}
		catch(CoreException e){
			System.out.println(e.getMessage());
		}
	}
	
	public void refreshFolder() {
		try {
			getSootOutputFolder().refreshLocal(IResource.DEPTH_INFINITE, null);
		} 
		catch (CoreException e1) {
			System.out.println(e1.getMessage());
		}
	}
	
	public void handleFilesChanged() {
		
		// files that were showing close
		if (getOldFilelist() != null) {
			Iterator it = getOldFilelist().iterator();
			while (it.hasNext()) {
				Object temp = it.next();
				if (temp instanceof IEditorPart) {
					getWindow().getActivePage().closeEditor((IEditorPart)temp, true);
				}
			}
		}
	
		try {
			IResource [] children = getSootOutputFolder().members();
		
			IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();		
		}
		catch (Exception e) {
			System.out.println("Open Editor ex: "+e.getMessage());
			System.out.println(e.getStackTrace());
		}
		// new files show
	}
	
	/**
	 * Returns the sootOuputFolder.
	 * @return IFolder
	 */
	public IFolder getSootOutputFolder() {
		return sootOutputFolder;
	}

	/**
	 * Sets the sootOuputFolder.
	 * @param sootOuputFolder The sootOuputFolder to set
	 */
	public void setSootOutputFolder(IFolder sootOutputFolder) {
		this.sootOutputFolder = sootOutputFolder;
	}


	/**
	 * Returns the newFilelist.
	 * @return ArrayList
	 */
	public ArrayList getNewFilelist() {
		return newFilelist;
	}

	/**
	 * Returns the oldFilelist.
	 * @return ArrayList
	 */
	public ArrayList getOldFilelist() {
		return oldFilelist;
	}

	/**
	 * Sets the newFilelist.
	 * @param newFilelist The newFilelist to set
	 */
	public void setNewFilelist(ArrayList newFilelist) {
		this.newFilelist = newFilelist;
	}

	/**
	 * Sets the oldFilelist.
	 * @param oldFilelist The oldFilelist to set
	 */
	public void setOldFilelist(ArrayList oldFilelist) {
		this.oldFilelist = oldFilelist;
	}

	/**
	 * Returns the window.
	 * @return IWorkbenchWindow
	 */
	public IWorkbenchWindow getWindow() {
		return window;
	}

	/**
	 * Sets the window.
	 * @param window The window to set
	 */
	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

}
