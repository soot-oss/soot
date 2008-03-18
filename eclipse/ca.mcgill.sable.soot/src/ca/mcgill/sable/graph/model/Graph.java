/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
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


package ca.mcgill.sable.graph.model;
import java.util.*;
import org.eclipse.ui.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.resource.*;


public class Graph extends Element implements IEditorInput {

	private ArrayList children = new ArrayList();
	private IResource resource;
	private String name;
	
	
	public Graph() {
		super();
	}

	public void addChild(SimpleNode child){
		if (getChildren() == null){
			setChildren(new ArrayList());
		}
		getChildren().add(child);
		fireStructureChange(GRAPH_CHILD, child);
	}
	
	public void removeChild(SimpleNode child){
		if (getChildren() == null)return;
		if (getChildren().contains(child)){
			getChildren().remove(child);
			fireStructureChange(GRAPH_CHILD, child);
		}
	}
	
	public void removeAllChildren(){
		setChildren(new ArrayList());
		fireStructureChange(GRAPH_CHILD, null);
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
	public IResource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
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
	
}
