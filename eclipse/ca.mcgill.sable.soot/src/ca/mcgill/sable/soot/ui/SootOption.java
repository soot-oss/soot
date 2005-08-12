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

package ca.mcgill.sable.soot.ui;

import java.util.Vector;

public class SootOption {

	private Vector children;
	private String label;
	private SootOption parent;
	private String alias;
	
	public SootOption() {
	
	}
	
	/**
	 * Constructor for TreeOption.
	 */
	public SootOption(String label, String alias) {
		setLabel(label);
		setAlias(alias);
	}
	
	public void addChild(SootOption t) {
		if (getChildren() == null) {
			setChildren(new Vector());
		}
		t.setParent(this);
		getChildren().add(t);
	}
	

	/**
	 * Returns the children.
	 * @return Vector
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * Returns the label.
	 * @return String
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the children.
	 * @param children The children to set
	 */
	public void setChildren(Vector children) {
		this.children = children;
	}

	/**
	 * Sets the label.
	 * @param label The label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the parent.
	 * @return SootOption
	 */
	public SootOption getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	public void setParent(SootOption parent) {
		this.parent = parent;
	}

	/**
	 * @return
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param string
	 */
	public void setAlias(String string) {
		alias = string;
	}

}
