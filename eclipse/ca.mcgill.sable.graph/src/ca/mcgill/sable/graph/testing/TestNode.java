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


package ca.mcgill.sable.graph.testing;
import java.util.*;

public class TestNode {

	private String data;
	private ArrayList outputs = new ArrayList();
	private ArrayList children = new ArrayList();
	
	/**
	 * 
	 */
	public TestNode() {
		super();
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
