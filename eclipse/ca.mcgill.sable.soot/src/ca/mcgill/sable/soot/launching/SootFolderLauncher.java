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
 * @author jlhotak
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SootFolderLauncher extends SootLauncher {

	private String processPath;
	//private String outputLocation;

	public void run(IAction action) {
		super.run(action);
		
		//super.resetSootOutputFolder();
		//setOutputLocation(platform_location+getSootOutputFolder().getFullPath().toOSString());
		
		if (getSootSelection().getType() == SootSelection.PACKAGEROOT_SELECTED_TYPE){
			setProcessPath(platform_location+getSootSelection().getPackageFragmentRoot().getPath().toOSString());
		}
	}

	/**
	 * Returns the outputLocation.
	 * @return String
	 */
	/*public String getOutputLocation() {
		return outputLocation;
	}

	/**
	 * Returns the processPath.
	 * @return String
	 */
	public String getProcessPath() {
		return processPath;
	}

	/**
	 * Sets the outputLocation.
	 * @param outputLocation The outputLocation to set
	 */
	/*public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}

	/**
	 * Sets the processPath.
	 * @param processPath The processPath to set
	 */
	public void setProcessPath(String processPath) {
		this.processPath = processPath;
	}

}
