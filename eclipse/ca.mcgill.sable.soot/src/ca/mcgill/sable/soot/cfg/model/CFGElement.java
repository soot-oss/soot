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
 * Created on Jan 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import java.beans.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGElement implements IPropertySource {

	//public static final String WIDTH = "node width";
	public static final String TEXT = "node text";
	public static final String CHILDREN = "children";
	public static final String INPUTS = "inputs";
	public static final String OUTPUTS = "outputs";
	public static final String HEAD = "head";
	public static final String TAIL = "tail";
	public static final String BEFORE_INFO = "before";
	public static final String AFTER_INFO = "after";
	public static final String NEW_FLOW_DATA = "new_flow_data";
	public static final String FLOW_INFO = "flow_info";
	public static final String FLOW_CHILDREN = "flow_children";
	public static final String PART_FLOW_CHILDREN = "part flow_children";
	
	public static final String NODE_DATA = "node_data";
	
	public static final String REVEAL = "reveal";
	public static final String HIGHLIGHT = "highlight";
	
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		listeners.addPropertyChangeListener(l);
	}
	
	protected void firePropertyChange(String name, Object oldVal, Object newVal){
		listeners.firePropertyChange(name, oldVal, newVal);
	}
	
	protected void firePropertyChange(String name, Object newVal){
		firePropertyChange(name, null, newVal);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		listeners.removePropertyChangeListener(l);
	}
	
	public void fireStructureChange(String name, Object newVal){
		firePropertyChange(name, null, newVal);
	}
	
	/**
	 * 
	 */
	public CFGElement() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// TODO Auto-generated method stub
		return new IPropertyDescriptor[1];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}
