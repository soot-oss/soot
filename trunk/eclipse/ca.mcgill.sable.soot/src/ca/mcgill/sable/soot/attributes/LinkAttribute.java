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

package ca.mcgill.sable.soot.attributes;

public class LinkAttribute {
	
	private String label;
	private int jimpleLink;
	private int javaLink;
	
	private String className;
	private String type;
	
		
	/**
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return
	 */
	public int getJimpleLink() {
		return jimpleLink;
	}

	/**
	 * @param string
	 */
	public void setClassName(String string) {
		className = string;
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		
		string = string.replaceAll("&lt;", "<");
		label = string.replaceAll("&gt;", ">");
	}

	/**
	 * @param string
	 */
	public void setJimpleLink(int l) {
		jimpleLink = l;
	}

	/**
	 * @return
	 */
	public int getJavaLink() {
		return javaLink;
	}

	/**
	 * @param i
	 */
	public void setJavaLink(int i) {
		javaLink = i;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

}
