/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999-2010 Hossein Sadat-Mohtasham
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
package soot.toolkits.graph.pdg;


import java.util.ArrayList;
import java.util.List;

import soot.toolkits.graph.Block;


/**
 *
 * This class defines a Node in the Program Dependence Graph. There might be a need to store
 * additional information in the PDG nodes. In essence, the PDG nodes represent (within them)
 *  either CFG nodes or Region nodes.
 * 
 * 
 *
 * @author Hossein Sadat-Mohtasham
 * June 2009
 */

public class PDGNode {
	
	public enum Type{REGION, CFGNODE};
	public enum Attribute{NORMAL, ENTRY, CONDHEADER, LOOPHEADER};
	
	protected Type m_type;
	protected Object m_node = null;
	protected List<PDGNode> m_dependents = new ArrayList<PDGNode>();
	protected List<PDGNode> m_backDependents = new ArrayList<PDGNode>();
	//This is used to keep an ordered list of the nodes in a region, based on the control-flow
	//between them (if any).
	protected PDGNode m_next = null;
	protected PDGNode m_prev = null;
	
	protected Attribute m_attrib = Attribute.NORMAL;
	
	
	public PDGNode(Object obj, Type t)
	{
		this.m_node = obj;
		this.m_type = t;
	}
	
	public Type getType()
	{
		return this.m_type;
	}
	public void setType(Type t)
	{
		this.m_type = t;
	}
	
	public Object getNode()
	{
		return this.m_node;
	}
	
	public void setNext(PDGNode n)
	{
		this.m_next = n;
	}
	public PDGNode getNext()
	{
		return this.m_next;
	}
	
	public void setPrev(PDGNode n)
	{
		this.m_prev = n;
	}
	public PDGNode getPrev()
	{
		return this.m_prev;
	}
	
	//The following is used to keep track of the nodes that are visited in post-order traversal. This should
	//probably be moved into an aspect.
	protected boolean m_visited = false;
	public void setVisited(boolean v)
	{
		this.m_visited = v;
	}
	public boolean getVisited()
	{
		return this.m_visited;
	}
	public void setNode(Object obj)
	{
		this.m_node = obj;
	}
	
	public Attribute getAttrib()
	{
		return this.m_attrib;
	}
	public void setAttrib(Attribute a)
	{
		this.m_attrib = a;
	}
	
	public void addDependent(PDGNode node)
	{
		if(!this.m_dependents.contains(node))
			this.m_dependents.add(node);
	}
	public void addBackDependent(PDGNode node)
	{
		this.m_backDependents.add(node);
	}
	
	public void removeDependent(PDGNode node)
	{
		this.m_dependents.remove(node);
	}
	
	public List<PDGNode> getDependets()
	{
		return this.m_dependents;
	}
	
	public List<PDGNode> getBackDependets()
	{
		return this.m_backDependents;
	}
	public String toString()
	{
		String s = new String();
		s = "Type: " + ((this.m_type == Type.REGION)? "REGION: " : "CFGNODE: ");
		s += this.m_node;
		return s;
		
	}
	
	public String toShortString()
	{
		String s = new String();
		s = "Type: " + ((this.m_type == Type.REGION)? "REGION: " : "CFGNODE: ");
		if(this.m_type == Type.REGION)
			s += ((IRegion)this.m_node).getID();
		else
			s += ((Block)this.m_node).toShortString();
		
		return s;
	}
	
}