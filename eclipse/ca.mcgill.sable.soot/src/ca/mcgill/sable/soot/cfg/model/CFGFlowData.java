/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;


import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGFlowData extends CFGElement {

	private ArrayList children = new ArrayList();
	
	/**
	 * 
	 */
	public CFGFlowData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void addChild(CFGFlowInfo child){
		children.add(child);
		fireStructureChange(FLOW_CHILDREN, child);
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
