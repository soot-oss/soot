/*
 * Created on Mar 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;

import ca.mcgill.sable.graph.model.SimpleNode;
import soot.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CallGraphNode extends SimpleNode {

	private CallGraphGenerator generator;
	private boolean expand = true;
	
	public boolean isLeaf(){
		if ((getOutputs() == null) ||(getOutputs().size() == 0)) return true;
		return false;
	}
	
	/**
	 * 
	 */
	public CallGraphNode() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void setData(Object obj){
		if (obj instanceof SootMethod){
			data = obj;
			firePropertyChange(DATA, ((SootMethod)obj).getSignature());
		}
	}

	/**
	 * @return
	 */
	public CallGraphGenerator getGenerator() {
		return generator;
	}

	/**
	 * @param generator
	 */
	public void setGenerator(CallGraphGenerator generator) {
		this.generator = generator;
	}

	/**
	 * @return
	 */
	public boolean isExpand() {
		return expand;
	}

	/**
	 * @param b
	 */
	public void setExpand(boolean b) {
		expand = b;
	}

}
