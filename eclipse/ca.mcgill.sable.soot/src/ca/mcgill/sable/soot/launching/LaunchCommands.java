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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A bunch of Soot commands (used in the plugin). It is possible to break
 * these by changing soot_options.xml but atleast only have to change
 * here.
 */
public class LaunchCommands {

	private static final String RESOURCE_BUNDLE= "ca.mcgill.sable.soot.launching.launchingCmds";//$NON-NLS-1$

	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	public static final String SOOT_CLASSPATH = "cp";
	public static final String XML_ATTRIBUTES = "xml-attributes";
	public static final String KEEP_LINE_NUMBER = "keep-line-number";
	public static final String OUTPUT = "f ";
	public static final String JIMPLE_OUT = "J";
	public static final String PROCESS_PATH = "process-dir";
	public static final String DAVA = "f dava";
	public static final String APP = "app ";
	public static final String OUTPUT_DIR = "d";
	public static final String INTRA_PROC = "O --p jop.cse disabled:false --f J ";
	public static final String EVERYTHING = "W --O --p wjop.si insert-null-checks:false --p jop.cse disabled:false --app --f dava ";
	public static final String SRC_PREC = "src-prec";
	public static final String JIMPLE_IN = "J";
	public static final String CLASS_IN = "class ";
	public static final String GRIMP_OUT = "g";
	public static final String INLINING = "--W --app --f grimp ";
	public static final String STATIC = "--W --app --p wjop.smb diasabled:false --p wjop.si disabled:true --f grimp ";
    public static final String JAVA_IN = "java";

	private LaunchCommands() {
		// prevent instantiation of class
	}

	/**
	 * Returns the resource object with the given key in
	 * the resource bundle. If there isn't any value under
	 * the given key, the key is returned, surrounded by '!'s.
	 *
	 * @param key the resource name
	 * @return the string
	 */	
	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "";
		}
	}
	

}
