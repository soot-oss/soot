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

/**
 * 
 * This represents a PDGNode that has more than 1 dependent but is not 
 * a loop header. This includes a conditional node, or a potentially
 * exceptional node.
 *
 */
public class ConditionalPDGNode extends PDGNode {
	
	
	public ConditionalPDGNode(Object obj, Type t)
	{		
		super(obj, t);
	}
	
	public ConditionalPDGNode(PDGNode node)
	{
		this(node.getNode(), node.getType());
		this.m_dependents.addAll(node.m_dependents);
		this.m_backDependents.addAll(node.m_backDependents);
		this.m_next = node.m_next;
		this.m_prev = node.m_prev;
		
	}
		
	
}