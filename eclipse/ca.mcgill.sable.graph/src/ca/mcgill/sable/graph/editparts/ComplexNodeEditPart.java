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


package ca.mcgill.sable.graph.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.*;
import org.eclipse.draw2d.graph.*;
import java.util.*;
import ca.mcgill.sable.graph.model.*;
import ca.mcgill.sable.graph.figures.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import ca.mcgill.sable.graph.editpolicies.*;

public class ComplexNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	public ComplexNodeEditPart() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(getFigure());
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new ComplexNodeFigure();
	}

	public void contributeNodesToGraph(DirectedGraph graph, HashMap map){
		Node node = new Node(this);
		node.width = getFigure().getBounds().width;//getNode().getWidth();
		node.height = getFigure().getBounds().height;
		graph.nodes.add(node);
		map.put(this, node);
	}
	
	public void contributeEdgesToGraph(DirectedGraph graph, HashMap map) {
		List outgoing = getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++){
			EdgeEditPart edge = (EdgeEditPart)outgoing.get(i);
			edge.contributeToGraph(graph, map);
		}
	}
	
	public void applyGraphResults(DirectedGraph graph, HashMap map){
		SimpleNode node = (SimpleNode)map.get(this);
		List outgoing = getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++){
			EdgeEditPart edge = (EdgeEditPart)outgoing.get(i);
			edge.applyGraphResults(graph, map);
		}
	
	}
	
	public ComplexNode getNode(){
		return (ComplexNode)getModel();
	}
	
	public List getModelChildren(){
		return ((ComplexNode)getNode()).getChildren();
	}
	
	
	public void propertyChange(PropertyChangeEvent event){
		if (event.getPropertyName().equals(Element.COMPLEX_CHILD)){
			refreshChildren();
		}
	}
	
	protected void refreshVisuals(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				getFigure().add(((SimpleNodeEditPart)next).getFigure());
				getFigure().setSize(getFigure().getBounds().width+((SimpleNodeEditPart)next).getFigure().getBounds().width, getFigure().getBounds().height+((SimpleNodeEditPart)next).getFigure().getBounds().height);
			}	
			else if (next instanceof GraphEditPart){
				getFigure().add(((GraphEditPart)next).getFigure());
				getFigure().setSize(getFigure().getBounds().width+((GraphEditPart)next).getFigureWidth(), getFigure().getBounds().height+((GraphEditPart)next).getFigureHeight());
			}	
			
		}
	}

}
