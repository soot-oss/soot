/*
 * Created on 21-Mar-2003
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

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleOutlineObject {

	private Vector children;
	private String label;
	private JimpleOutlineObject parent;
	
	public JimpleOutlineObject(String label){
		setLabel(label);	
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

}
