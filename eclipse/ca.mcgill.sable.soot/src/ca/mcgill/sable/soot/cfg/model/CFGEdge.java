/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGEdge extends CFGElement {

	private CFGNode from;
	private CFGNode to;
	
	/**
	 * 
	 */
	public CFGEdge(CFGNode from, CFGNode to) {
		setFrom(from);
		setTo(to);
		getFrom().addOutput(this);
		getTo().addInput(this);
	}

	/**
	 * @return
	 */
	public CFGNode getFrom() {
		return from;
	}

	/**
	 * @return
	 */
	public CFGNode getTo() {
		return to;
	}

	/**
	 * @param node
	 */
	public void setFrom(CFGNode node) {
		from = node;
	}

	/**
	 * @param node
	 */
	public void setTo(CFGNode node) {
		to = node;
		
	}
	
	public String toString(){
		return "from: "+getFrom()+" to: "+getTo();
	}

}
