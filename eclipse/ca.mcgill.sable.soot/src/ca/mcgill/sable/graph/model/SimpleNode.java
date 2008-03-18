/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */


package ca.mcgill.sable.graph.model;
import java.util.*;

public class SimpleNode extends Element {

	private ArrayList inputs;
	private ArrayList outputs;
	protected Object data;
	private ArrayList children = new ArrayList();
	

	public SimpleNode() {
		super();
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
		}
	}
	
	public void removeOutput(Edge e){
		if (getOutputs() == null) return;
		if (getOutputs().contains(e)){
			getOutputs().remove(e);
			fireStructureChange(OUTPUTS, e);
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
