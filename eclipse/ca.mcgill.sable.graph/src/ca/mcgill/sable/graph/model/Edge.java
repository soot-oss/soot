/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.model;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Edge extends Element {

	private SimpleNode src;
	private SimpleNode tgt;
	
	/**
	 * 
	 */
	public Edge(SimpleNode src, SimpleNode tgt) {
		setSrc(src);
		setTgt(tgt);
		src.addOutput(this);
		tgt.addInput(this);
	}

	/**
	 * @return
	 */
	public SimpleNode getSrc() {
		return src;
	}

	/**
	 * @param node
	 */
	public void setSrc(SimpleNode node) {
		src = node;
	}

	/**
	 * @return
	 */
	public SimpleNode getTgt() {
		return tgt;
	}

	/**
	 * @param node
	 */
	public void setTgt(SimpleNode node) {
		tgt = node;
	}

}
