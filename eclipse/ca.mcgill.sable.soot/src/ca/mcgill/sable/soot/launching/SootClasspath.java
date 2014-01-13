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
import java.util.ArrayList;
import java.util.List;

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
		this.urls = projectClassPath(javaProject);		
	}

	public static URL[] projectClassPath(IJavaProject javaProject) {
		IWorkspace workspace = SootPlugin.getWorkspace();
		IClasspathEntry[] cp;
		try {
			cp = javaProject.getResolvedClasspath(true);
			List<URL> urls = new ArrayList<URL>();
			String uriString = workspace.getRoot().getFile(
					javaProject.getOutputLocation()).getLocationURI().toString()
					+ "/";
			urls.add(new URI(uriString).toURL());
			for (IClasspathEntry entry : cp) {
				File file = entry.getPath().toFile();
				URL url = file.toURI().toURL();
				urls.add(url);
			}
			URL[] array = new URL[urls.size()];
			urls.toArray(array);
			return array;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return new URL[0];
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new URL[0];
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new URL[0];
		}
	}
	
	public String getSootClasspath() {
		return urlsToString(urls);
	}

	public static String urlsToString(URL[] urls) {
		StringBuffer cp = new StringBuffer();
		for (URL url : urls) {
			cp.append(url.getPath());
			cp.append(File.pathSeparator);
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
