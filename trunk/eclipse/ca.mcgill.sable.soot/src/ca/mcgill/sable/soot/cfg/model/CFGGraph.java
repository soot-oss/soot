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

package ca.mcgill.sable.soot.cfg.model;

import java.util.*;
import org.eclipse.ui.*;
import org.eclipse.jface.resource.*;
import org.eclipse.core.runtime.*;
import org.eclipse.draw2d.graph.*;
import org.eclipse.core.resources.*;

public class CFGGraph extends CFGElement implements IEditorInput {

	private String name;
	private ArrayList children = new ArrayList();
	private IResource resource;
	
	
	public CFGGraph() {
	}

	
	public void addChild(CFGNode child){
		children.add(child);
		fireStructureChange(CHILDREN, child);
	}
		
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	
	
	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	

	public boolean exists(){
		return false;
	}
	
	public ImageDescriptor getImageDescriptor(){
		return null;
	}
	
	public IPersistableElement getPersistable(){
		return null;
	}
	
	public String getToolTipText(){
		return getName();
	}
	
	public Object getAdapter(Class c){
		if (c == IResource.class){
			return getResource();	
		}
		return null;
	}
		
	/**
	 * @return
	 */
	public ArrayList getChildren() {
		return children;
	}

	/**
	 * @param list
	 */
	public void setChildren(ArrayList list) {
		children = list;
	}

	/**
	 * @return
	 */
	public IResource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	
	public void newFlowData(){
		firePropertyChange(CFGElement.NEW_FLOW_DATA, null);
	}

	public void handleClickEvent(Object evt){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			CFGNode child = (CFGNode)it.next();
			ArrayList list = child.getData().getText();
			if (list.size() == 1){
				if (list.get(0).equals(evt)){
					child.handleClickEvent(evt);
				}
			}
		}
	}
}
