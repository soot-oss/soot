/*
 * Created on Mar 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.testing;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestNode {

	private String data;
	private ArrayList outputs = new ArrayList();
	private ArrayList children = new ArrayList();
	
	/**
	 * 
	 */
	public TestNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addOutput(TestNode tn){
		getOutputs().add(tn);
	}
	
	public void addChild(TestNode tn){
		getChildren().add(tn);
	}
	
	/**
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return
	 */
	public ArrayList getOutputs() {
		return outputs;
	}

	/**
	 * @param string
	 */
	public void setData(String string) {
		data = string;
	}

	/**
	 * @param list
	 */
	public void setOutputs(ArrayList list) {
		outputs = list;
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
