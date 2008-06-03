/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

/*
 * Created on Nov 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.attributes;

public class AnalysisKey {
	private int red;
	private int green;
	private int blue;
	private String key;
	private String type;
	
	/**
	 * @return
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * @return
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return
	 */
	public int getRed() {
		return red;
	}

	/**
	 * @param i
	 */
	public void setBlue(int i) {
		blue = i;
	}

	/**
	 * @param i
	 */
	public void setGreen(int i) {
		green = i;
	}

	/**
	 * @param string
	 */
	public void setKey(String string) {
		key = string;
	}

	/**
	 * @param i
	 */
	public void setRed(int i) {
		red = i;
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
