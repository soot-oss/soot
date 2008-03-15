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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import ca.mcgill.sable.soot.SootPlugin;


/**
 * Determines the soot-classpath for running Soot
 */
public class SootClasspath {

	private String separator = File.pathSeparator;
	protected URL[] urls = new URL[0];
	
	public void initialize(IJavaProject javaProject) {
		IWorkspace workspace = SootPlugin.getWorkspace();
		IClasspathEntry[] cp;
		try {
			cp = javaProject.getResolvedClasspath(true);
			URL[] urls = new URL[cp.length + 1];
			String uriString = workspace.getRoot().getFile(
					javaProject.getOutputLocation()).getLocationURI().toString()
					+ "/";
			urls[0] = new URI(uriString).toURL();
			int i = 1;
			for (IClasspathEntry entry : cp) {
				File file = entry.getPath().toFile();
				urls[i++] = file.toURI().toURL();
			}
			this.urls = urls;
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}
	
	public String getSootClasspath() {
		StringBuffer cp = new StringBuffer();
		for (URL url : urls) {
			cp.append(url.getPath());
			cp.append(separator);
		}
		
		return cp.toString();
	}

	/**
	 * Returns the separator.
	 * @return String
	 */
	public String getSeparator() {
		return separator;
	}


}
