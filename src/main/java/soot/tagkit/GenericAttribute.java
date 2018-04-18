/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

/* This program is designed by Patrice Pominville and Feng Qian
 */

package soot.tagkit;

import java.util.*;

import soot.UnitBox;

/**
 * Represents a general attribute which can be attached to implementations of
 * Host. It can be directly used to add attributes of class files, fields, and
 * methods.
 * 
 * @see CodeAttribute
 */
public class GenericAttribute implements Attribute {
	private final String mName;
	private byte[] mValue;

	public GenericAttribute(String name, byte[] value) {
		if (value == null)
			value = new byte[0];
		mName = name;
		mValue = value;
	}

	public String getName() {
		return mName;
	}

	public byte[] getValue() {
		return mValue;
	}

	public String toString() {
		return mName + " " + Base64.encode(mValue).toString();
	}

	public void setValue(byte[] value) {
		mValue = value;
	}

	public List<UnitBox> getUnitBoxes() {
		return Collections.emptyList();
	}
}
