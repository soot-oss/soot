/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.model;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComplexNode extends Element {

	private ArrayList children = new ArrayList();
	
	/**
	 * 
	 */
	public ComplexNode() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void addChild(Element child){
		if (getChildren() == null){
			setChildren(new ArrayList());
		}
		getChildren().add(child);
		fireStructureChange(COMPLEX_CHILD, child);
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

}
