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
	private ArrayList children = new ArrayList();
	private IResource resource;
	
	
	/**
	 * 
	 */
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

}
