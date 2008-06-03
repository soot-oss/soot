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


package ca.mcgill.sable.soot.util;


public class SootCmdFormat {

	public static final String SPACE = " ";
	public static final String COLON = ":";
	private String separator;
	private Object val;
	
	
	/**
	 * Constructor for SootCmdFormat.
	 */
	public SootCmdFormat(String separator, Object val) {
		setSeparator(separator);
		setVal(val);
	}

	/**
	 * Returns the separator.
	 * @return String
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Returns the val.
	 * @return String
	 */
	public Object getVal() {
		return val;
	}

	/**
	 * Sets the separator.
	 * @param separator The separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Sets the val.
	 * @param val The val to set
	 */
	public void setVal(Object val) {
		this.val = val;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getSeparator());
		sb.append(getVal());
		return sb.toString();
	}

}
