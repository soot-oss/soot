/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.model;
import java.util.*;
import org.eclipse.ui.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.resource.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Graph extends Element implements IEditorInput {

	private ArrayList children = new ArrayList();
	private IResource resource;
	private String name;
	
	/**
	 * 
	 */
	public Graph() {
		super();
		// TODO Auto-generated constructor stub
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
