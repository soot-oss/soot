/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;

import java.util.*;
import org.eclipse.ui.*;
import org.eclipse.jface.resource.*;
import org.eclipse.core.runtime.*;
import org.eclipse.draw2d.graph.*;
import org.eclipse.core.resources.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGGraph extends CFGElement implements IEditorInput {

	private String name;
	//private ArrayList nodes;
	//private ArrayList edges;
	private ArrayList children = new ArrayList();
	private IResource resource;
	
	
	/**
	 * 
	 */
	public CFGGraph() {
		//setNodes(new ArrayList());
		//setEdges(new ArrayList());	
	}

	
	public void addChild(CFGNode child){
		children.add(child);
		//System.out.println("added child to graph");
		fireStructureChange(CHILDREN, child);
	}
		
	/**
	 * @return
	 */
	/*public ArrayList getEdges() {
		return edges;
	}*/

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	/*public ArrayList getNodes() {
		return nodes;
	}*/

	/**
	 * @param list
	 */
	/*public void setEdges(ArrayList list) {
		edges = list;
		if (getChildren() == null){
			setChildren(new ArrayList());
		}
		getChildren().addAll(edges);
	}*/

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param list
	 */
	/*public void setNodes(ArrayList list) {
		nodes = list;
		if (getChildren() == null){
			setChildren(new ArrayList());
		}
		getChildren().addAll(nodes);
	}*/

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
		//System.out.println("c class: "+c);
		
		if (c == IResource.class){
			//System.out.println("return from adaptable: "+getResource());
			return getResource();	
		}
		//System.out.println("will return null");
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

}
