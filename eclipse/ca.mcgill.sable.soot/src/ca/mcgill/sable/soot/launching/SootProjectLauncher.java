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

import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.*;

/**
 * Handles launching Soot on project.
 */
public class SootProjectLauncher extends SootLauncher {

	//private String output_location;
	private String process_path;
	private ArrayList javaProcessPath;
	private String classpathAppend = null;
	
	public void run(IAction action) {
		super.run(action);
		classpathAppend = null;
		//super.resetSootOutputFolder();
		try {
			setProcess_path(platform_location+getSootSelection().getJavaProject().getOutputLocation().toOSString());
			IPackageFragmentRoot [] roots = getSootSelection().getJavaProject().getAllPackageFragmentRoots();
			
			for (int i = 0; i < roots.length; i++){
				if (!roots[i].isArchive() && roots[i].getKind() == IPackageFragmentRoot.K_SOURCE){
				
					String next = platform_location+roots[i].getPath();
					
					if (getJavaProcessPath() == null){
						setJavaProcessPath(new ArrayList());
					}
					
					getJavaProcessPath().add(next);
				}
			}
			
			//setJavaProcessPath(platform_location+getSootSelection().getJavaProject().get)
			addJars();
		}
		catch(Exception e1) {
			System.out.println(e1.getMessage());
		}
		//setOutput_location(platform_location+getSootOutputFolder().getFullPath().toOSString());
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
		System.out.println("classpathAppend: "+this.classpathAppend);
	}

	/**
	 * Returns the output_location.
	 * @return String
	 */
	/*public String getOutput_location() {
		return output_location;
	}

	/**
	 * Returns the process_path.
	 * @return String
	 */
	public String getProcess_path() {
		return process_path;
	}

	/**
	 * Sets the output_location.
	 * @param output_location The output_location to set
	 */
	/*public void setOutput_location(String output_location) {
		this.output_location = output_location;
	}

	/**
	 * Sets the process_path.
	 * @param process_path The process_path to set
	 */
	public void setProcess_path(String process_path) {
		this.process_path = process_path;
	}

	/**
	 * @return
	 */
	public String getClasspathAppend() {
		return classpathAppend;
	}

	/**
	 * @return
	 */
	public ArrayList getJavaProcessPath() {
		return javaProcessPath;
	}

	/**
	 * @param string
	 */
	public void setJavaProcessPath(ArrayList list) {
		javaProcessPath = list;
	}

}
