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
public class SimpleNode extends Element {

	private ArrayList inputs;
	private ArrayList outputs;
	protected Object data;
	private ArrayList children = new ArrayList();
	
	
	/**
	 * 
	 */
	public SimpleNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void addInput(Edge e){
		if (getInputs() == null){
			setInputs(new ArrayList());
		}
		getInputs().add(e);
		fireStructureChange(INPUTS, e);
	}
	
	public void addOutput(Edge e){
		if (getOutputs() == null){
			setOutputs(new ArrayList());
		}
		getOutputs().add(e);
		fireStructureChange(OUTPUTS, e);
	}
	
	public void removeInput(Edge e){
		if (getInputs() == null) return;
		if (getInputs().contains(e)){
			getInputs().remove(e);
			fireStructureChange(INPUTS, e);
			//e.getSrc().removeOutput(e);
		}
	}
	
	public void removeOutput(Edge e){
		if (getOutputs() == null) return;
		if (getOutputs().contains(e)){
			getOutputs().remove(e);
			fireStructureChange(OUTPUTS, e);
			//e.getTgt().removeInput(e);
		}
	}
	
	public void removeAllInputs(){
		if (getInputs() == null) return;
		Iterator it = getInputs().iterator();
		while (it.hasNext()){
			Edge e = (Edge)it.next();
			e.getSrc().removeOutput(e);
		}
		setInputs(new ArrayList());
		fireStructureChange(INPUTS, null);
	}
	
	public void removeAllOutputs(){
		if (getOutputs() == null) return;
		Iterator it = getOutputs().iterator();
		while (it.hasNext()){
			Edge e = (Edge)it.next();
			e.getTgt().removeInput(e);
		}
		setOutputs(new ArrayList());
		fireStructureChange(OUTPUTS, null);		
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
	public Object getData() {
		return data;
	}

	/**
	 * @param string
	 */
	public void setData(Object string) {
		data = string;
		firePropertyChange(DATA, data.toString());
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
	
	public void addChild(SimpleNode sn){
		children.add(sn);
		fireStructureChange(COMPLEX_CHILD_ADDED, sn);
	}

}
