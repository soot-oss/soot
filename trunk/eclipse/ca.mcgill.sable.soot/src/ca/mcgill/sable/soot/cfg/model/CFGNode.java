/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package ca.mcgill.sable.soot.cfg.model;

import java.util.*;
import org.eclipse.draw2d.graph.*;

public class CFGNode extends CFGElement {

	
	private ArrayList inputs = new ArrayList();
	private ArrayList outputs = new ArrayList();
	private CFGFlowData before;
	private CFGFlowData after;
	private CFGNodeData data;
	
	private ArrayList children = new ArrayList();
	
	public CFGNode() {
		super();
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
	
	public void handleHighlightEvent(Object evt){
		firePropertyChange(HIGHLIGHT, this);
	}
}
