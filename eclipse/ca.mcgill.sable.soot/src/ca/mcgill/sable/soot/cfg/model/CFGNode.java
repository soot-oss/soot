/*
 * Created on Jan 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.model;

import java.util.*;
import org.eclipse.draw2d.graph.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNode extends CFGElement {

	
	private ArrayList inputs = new ArrayList();
	private ArrayList outputs = new ArrayList();
	private CFGFlowData before;
	private CFGFlowData after;
	private CFGNodeData data;
	
	private ArrayList children = new ArrayList();
	
	/**
	 * 
	 */
	public CFGNode() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void addInput(CFGEdge input){
		getInputs().add(input);
		fireStructureChange(CFGElement.INPUTS, input);
	}
	
	public void addOutput(CFGEdge output){
		getOutputs().add(output);
		fireStructureChange(CFGElement.OUTPUTS, output);
	}

	/**
	 * @return
	 */
	public ArrayList getInputs() {
		return inputs;
	}

	/**
	 * @return
	 */
	public ArrayList getOutputs() {
		return outputs;
	}

	/**
	 * @param list
	 */
	public void setInputs(ArrayList list) {
		inputs = list;
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
	public CFGFlowData getAfter() {
		return after;
	}

	/**
	 * @return
	 */
	public CFGFlowData getBefore() {
		return before;
	}

	/**
	 * @param string
	 */
	public void setAfter(CFGFlowData data) {
		after = data;
		int last = getChildren().size() - 1;
		if (getChildren().get(last) instanceof CFGFlowData){
			getChildren().remove(last);
		}
		getChildren().add(after);
		
		firePropertyChange(AFTER_INFO, after);
	}

	/**
	 * @param string
	 */
	public void setBefore(CFGFlowData data) {
		before = data;
		if (getChildren().get(0) instanceof CFGFlowData){
			getChildren().remove(0);
		}
		getChildren().add(0, before);
		
		firePropertyChange(BEFORE_INFO, before);
	}

	/**
	 * @return
	 */
	public CFGNodeData getData() {
		return data;
	}

	/**
	 * @param data
	 */
	public void setData(CFGNodeData data) {
		this.data = data;
		getChildren().add(data);
		firePropertyChange(NODE_DATA, data);
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

	public void handleClickEvent(Object evt){
		firePropertyChange(REVEAL, this);
	}
}
