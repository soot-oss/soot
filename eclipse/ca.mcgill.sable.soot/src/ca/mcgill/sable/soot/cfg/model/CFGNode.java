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

	private boolean head;
	private boolean tail;
	private ArrayList succs;
	private ArrayList preds;
	private int xpos;
	private int ypos;
	private int width;
	private Object in_out_gen_kill;
	private ArrayList text;
	private ArrayList inputs = new ArrayList();
	private ArrayList outputs = new ArrayList();
	
	
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
	
	public String toString(){
		return getText().toString();
	}
	
	/**
	 * @return
	 */
	public boolean isHead() {
		return head;
	}

	/**
	 * @return
	 */
	public Object getIn_out_gen_kill() {
		return in_out_gen_kill;
	}

	/**
	 * @return
	 */
	public ArrayList getPreds() {
		return preds;
	}

	/**
	 * @return
	 */
	public ArrayList getSuccs() {
		return succs;
	}

	/**
	 * @return
	 */
	public boolean isTail() {
		return tail;
	}

	/**
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return
	 */
	public int getXpos() {
		return xpos;
	}

	/**
	 * @return
	 */
	public int getYpos() {
		return ypos;
	}

	/**
	 * @param b
	 */
	public void setHead(boolean b) {
		head = b;
		firePropertyChange(HEAD, new Boolean(head));
	}

	/**
	 * @param object
	 */
	public void setIn_out_gen_kill(Object object) {
		in_out_gen_kill = object;
	}

	/**
	 * @param list
	 */
	public void setPreds(ArrayList list) {
		preds = list;
	}

	/**
	 * @param list
	 */
	public void setSuccs(ArrayList list) {
		succs = list;
	}

	/**
	 * @param b
	 */
	public void setTail(boolean b) {
		tail = b;
		firePropertyChange(TAIL, new Boolean(tail));
	}

	/**
	 * @param i
	 */
	public void setWidth(int i) {
		width = i;
		firePropertyChange(WIDTH, new Integer(width));
	}

	/**
	 * @param i
	 */
	public void setXpos(int i) {
		xpos = i;
	}

	/**
	 * @param i
	 */
	public void setYpos(int i) {
		ypos = i;
	}

	/**
	 * @return
	 */
	public ArrayList getText() {
		return text;
	}

	/**
	 * @param string
	 */
	public void setText(ArrayList list) {
		
		text = list;
		firePropertyChange(TEXT, text);
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

}
