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


package ca.mcgill.sable.soot.editors;

import java.util.*;

public class JimpleOutlineObject {

	private Vector children;
	private String label;
	private JimpleOutlineObject parent;
	private int type;
	private BitSet decorators;
	
	public static final int NONE = 100;
	public static final int CLASS = 10;
	public static final int INTERFACE = 11;
	public static final int METHOD = 1;
	public static final int PUBLIC_METHOD = 2;
	public static final int PRIVATE_METHOD = 3;
	public static final int PROTECTED_METHOD = 4;
	public static final int NONE_METHOD = 30;
	public static final int FIELD = 5;
	public static final int PUBLIC_FIELD = 6;
	public static final int PRIVATE_FIELD = 7;
	public static final int PROTECTED_FIELD = 8;
	public static final int NONE_FIELD = 31;
	
	public static final int FINAL_DEC = 20;
	public static final int STATIC_DEC = 21;
	public static final int SYNCHRONIZED_DEC = 22;
	public static final int ABSTRACT_DEC = 23;
	
	public JimpleOutlineObject(String label, int type, BitSet dec){
		setLabel(label);	
		setType(type);
		setDecorators(dec);
	}
	
	public void addChild(JimpleOutlineObject t) {
		if (getChildren() == null) {
			setChildren(new Vector());
		}
		t.setParent(this);
		getChildren().add(t);
	}
	
	/**
	 * @return
	 */
	public Vector getChildren() {
		return children;
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
	public JimpleOutlineObject getParent() {
		return parent;
	}

	/**
	 * @param vector
	 */
	public void setChildren(Vector vector) {
		children = vector;
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		label = string;
	}

	/**
	 * @param object
	 */
	public void setParent(JimpleOutlineObject object) {
		parent = object;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param i
	 */
	public void setType(int i) {
		type = i;
	}



	/**
	 * @return
	 */
	public BitSet getDecorators() {
		return decorators;
	}

	/**
	 * @param list
	 */
	public void setDecorators(BitSet list) {
		decorators = list;
	}

}
