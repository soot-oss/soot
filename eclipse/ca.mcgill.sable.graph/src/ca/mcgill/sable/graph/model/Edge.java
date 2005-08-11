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

public class Edge extends Element {

	private SimpleNode src;
	private SimpleNode tgt;
	private String label;
	
	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
		fireStructureChange(EDGE_LABEL, label);
	}
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
