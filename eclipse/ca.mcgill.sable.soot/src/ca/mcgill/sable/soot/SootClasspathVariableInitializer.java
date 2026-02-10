/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
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
package ca.mcgill.sable.soot;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

/**
 * This adds a classpath variable "SOOTCLASSES" to the eclipse environment.
 * The variable points to lib/sootclasses.jar in the plugin ca.mcgill.sable.soot.lib.
 *
 * @author Eric Bodden
 */
public class SootClasspathVariableInitializer extends
		ClasspathVariableInitializer {

	//the following heavily depend on plugin.xml and the projects' structure!
	public static final String VARIABLE_NAME_CLASSES = "SOOTCLASSES";
	public static final String VARIABLE_NAME_SOURCE = "SOOTSRC";
	private static final String LIBRARY_PLUGIN_NAME = "ca.mcgill.sable.soot.lib";
	private static final String RELATIVE_PATH_CLASSES_JAR = "lib/sootclasses.jar";
	private static final String RELATIVE_PATH_SRC_ZIP = "lib/sootsrc.zip";

	@Override
	public void initialize(String variable) {
		if (variable.equals(VARIABLE_NAME_CLASSES)) { //$NON-NLS-1$
			String jarPath = getSootFilePath(RELATIVE_PATH_CLASSES_JAR);
			if(jarPath==null) return;
			try {
				JavaCore.setClasspathVariable(VARIABLE_NAME_CLASSES, //$NON-NLS-1$
						new Path(jarPath), new NullProgressMonitor());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		if (variable.equals(VARIABLE_NAME_SOURCE)) { //$NON-NLS-1$
			String jarPath = getSootFilePath(RELATIVE_PATH_SRC_ZIP);
			if(jarPath==null) return;
			try {
				JavaCore.setClasspathVariable(VARIABLE_NAME_SOURCE, //$NON-NLS-1$
						new Path(jarPath), new NullProgressMonitor());
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * Code copied from AJDT implementation http://www.eclipse.org/ajdt
	 * org.eclipse.ajdt.internal.core.AspectJRTInitializer
	 * @param relativePath 
	 */
	private static String getSootFilePath(String relativePath) {

		StringBuffer cpath = new StringBuffer();

		// This returns the bundle with the highest version or null if none
		// found
		// - for Eclipse 3.0 compatibility
		Bundle ajdeBundle = Platform
				.getBundle(LIBRARY_PLUGIN_NAME);

		String pluginLoc = null;
		// 3.0 using bundles instead of plugin descriptors
		if (ajdeBundle != null) {
			URL installLoc = ajdeBundle.getEntry("/"); //$NON-NLS-1$
			URL resolved = null;
			try {
				resolved = FileLocator.resolve(installLoc);
				pluginLoc = resolved.toExternalForm();
			} catch (IOException e) {
			}
		}
		if (pluginLoc != null) {
			if (pluginLoc.startsWith("file:")) { //$NON-NLS-1$
				cpath.append(pluginLoc.substring("file:".length())); //$NON-NLS-1$
				cpath.append(relativePath); //$NON-NLS-1$
			}
		}

		String sootJarPath = null;
		
		// Verify that the file actually exists at the plugins location
		// derived above. If not then it might be because we are inside
		// a runtime workbench. Check under the workspace directory.
		if (new File(cpath.toString()).exists()) {
			// File does exist under the plugins directory
			sootJarPath = cpath.toString();
		} else {
			// File does *not* exist under plugins. Try under workspace...
			IPath rootPath = SootPlugin.getWorkspace().getRoot()
					.getLocation();
			IPath installPath = rootPath.removeLastSegments(1);
			cpath = new StringBuffer().append(installPath.toOSString());
			cpath.append(File.separator);
			// TODO: what if the workspace isn't called workspace!!!
			cpath.append("workspace"); //$NON-NLS-1$
			cpath.append(File.separator);
			cpath.append(LIBRARY_PLUGIN_NAME);
			cpath.append(File.separator);
			cpath.append("aspectjrt.jar"); //$NON-NLS-1$

			// Only set the aspectjrtPath if the jar file exists here.
			if (new File(cpath.toString()).exists())
				sootJarPath = cpath.toString();
		}
		
		return sootJarPath;
	}

}
