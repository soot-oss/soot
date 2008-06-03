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


package ca.mcgill.sable.soot.callgraph;

import ca.mcgill.sable.graph.model.*;
import soot.*;

public class CallGraphNode extends SimpleNode {

	private CallGraphGenerator generator;
	private boolean expand = true;
	private boolean expandCollape = false;
	private boolean collapse = false;
	
	public boolean isLeaf(){
		if ((getOutputs() == null) ||(getOutputs().size() == 0)) return true;
		return false;
	}

	public CallGraphNode() {
		super();
	}
	
	public void setData(Object obj){
		if (obj instanceof SootMethod){
			data = obj;
			firePropertyChange(DATA, obj);
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

	/**
	 * @return
	 */
	public boolean isCollapse() {
		return collapse;
	}

	/**
	 * @return
	 */
	public boolean isExpandCollape() {
		return expandCollape;
	}

	/**
	 * @param b
	 */
	public void setCollapse(boolean b) {
		collapse = b;
	}

	/**
	 * @param b
	 */
	public void setExpandCollape(boolean b) {
		expandCollape = b;
	}

}
