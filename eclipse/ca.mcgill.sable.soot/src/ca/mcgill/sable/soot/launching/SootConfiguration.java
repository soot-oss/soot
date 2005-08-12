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

import java.util.*;

public class SootConfiguration {


	private Vector children;
	private String label;
	private SootConfiguration parent;
	
	/**
	 * Constructor for SootConfiguration.
	 */
	public SootConfiguration(HashMap aliasValPairs, String name) {
		super();
		setAliasValPairs(aliasValPairs);
		setName(name);
	}
	
	public SootConfiguration(String label) {
		setLabel(label);
	}
	
	private HashMap aliasValPairs;
	private String name;

	public void addChild(SootConfiguration t) {
		if (getChildren() == null) {
			setChildren(new Vector());
		}
		t.setParent(this);
		getChildren().add(t);
	}
	
	public void removeChild(String name) {
		Iterator it = getChildren().iterator();
		SootConfiguration toRemove = null;
		while (it.hasNext()) {
			SootConfiguration temp = (SootConfiguration)it.next();
			if (temp.getLabel().equals(name)) {
				toRemove = temp;
			}
		}
		if (toRemove != null) {
			getChildren().remove(toRemove);
		}
	}

	public void renameChild(String oldName, String newName){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			SootConfiguration temp = (SootConfiguration)it.next();
			if (temp.getLabel().equals(oldName)){
				temp.setLabel(newName);	
			}
		}
	}
	
	/**
	 * Returns the aliasValPairs.
	 * @return HashMap
	 */
	public HashMap getAliasValPairs() {
		return aliasValPairs;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the aliasValPairs.
	 * @param aliasValPairs The aliasValPairs to set
	 */
	public void setAliasValPairs(HashMap aliasValPairs) {
		this.aliasValPairs = aliasValPairs;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Returns the parent.
	 * @return SootConfiguration
	 */
	public SootConfiguration getParent() {
		return parent;
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
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	public void setParent(SootConfiguration parent) {
		this.parent = parent;
	}

}
