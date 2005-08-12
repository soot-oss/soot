
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

import org.eclipse.jface.action.*;

/**
 * Soot Launcher for folders. 
 */
public class SootFolderLauncher extends SootLauncher {

	private String processPath;
	private String classpathAppend = null;

	public void run(IAction action) {
		super.run(action);
		classpathAppend = null;
		
		if (getSootSelection().getType() == SootSelection.PACKAGEROOT_SELECTED_TYPE){
			addJars();
			if (getSootSelection().getPackageFragmentRoot().getResource() != null){
				setProcessPath(platform_location+getSootSelection().getPackageFragmentRoot().getPath().toOSString());
			}
			else {
				setProcessPath(getSootSelection().getPackageFragmentRoot().getPath().toOSString());
			}
		}
	}
	
	/**
	 * Sets the classpathAppend.
	 * @param classpathAppend The classpathAppend to set
	 */
	public void setClasspathAppend(String ca) {
		if (this.classpathAppend == null){
			this.classpathAppend = ca;
		}
		else {
			this.classpathAppend = this.classpathAppend+getSootClasspath().getSeparator()+ca;
		}
	}


	/**
	 * Returns the processPath.
	 * @return String
	 */
	public String getProcessPath() {
		return processPath;
	}


	/**
	 * Sets the processPath.
	 * @param processPath The processPath to set
	 */
	public void setProcessPath(String processPath) {
		this.processPath = processPath;
	}

	/**
	 * @return
	 */
	public String getClasspathAppend() {
		return classpathAppend;
	}

}
