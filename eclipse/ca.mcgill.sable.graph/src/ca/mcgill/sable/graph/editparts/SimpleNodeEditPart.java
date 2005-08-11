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
import ca.mcgill.sable.graph.*;

public class SimpleNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	private Object data;
	

	public SimpleNodeEditPart() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		RectangleFigure rect = new RectangleFigure();
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		rect.setLayoutManager(layout);
		
		Label label = new Label();
		label.getInsets().top = 1;
		label.getInsets().bottom = 1;
		label.getInsets().right = 1;
		label.getInsets().left = 1;
		rect.add(label);
		
		return rect;
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
		Node node = (Node)map.get(this);
		if (node != null){
			getFigure().setBounds(new Rectangle(node.x+10, node.y, node.width, node.height));//getFigure().getBounds().height));//getFigure().getBounds().height));
			List outgoing = getSourceConnections();
			for (int i = 0; i < outgoing.size(); i++){
				EdgeEditPart edge = (EdgeEditPart)outgoing.get(i);
				edge.applyGraphResults(graph, map);
			}
		}
	
	}
	
	
	
	public SimpleNode getNode(){
		return (SimpleNode)getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
	}

	public List getModelSourceConnections(){
		return getNode().getOutputs();
	}
	
	public List getModelTargetConnections(){
		return getNode().getInputs();
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

	public void activate(){
		super.activate();
		getNode().addPropertyChangeListener(this);
	}
	
	public void deactivate(){
		super.deactivate();
		getNode().removePropertyChangeListener(this);
	}
	
	
	
	public void propertyChange(PropertyChangeEvent event){
		if (event.getPropertyName().equals(Element.DATA)){
			//System.out.println("new val: "+event.getNewValue());
			setData(event.getNewValue());
			refreshVisuals();
		}
		else if (event.getPropertyName().equals(Element.INPUTS)){
			refreshTargetConnections();
		}
		else if (event.getPropertyName().equals(Element.OUTPUTS)){
			refreshSourceConnections();
		}
		
	}
	
	public List getModelChildren(){
		return getNode().getChildren();
	}
	
	protected void refreshVisuals(){
		Iterator it = getFigure().getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof Label){
				((Label)next).setText(getData().toString());
				if (getData() != null){
					getFigure().setSize((getData().toString().length()*7)+10, ((((Label)next).getBounds().height/2)+10));
					getFigure().revalidate();
					((GraphEditPart)getParent()).getFigure().revalidate();
				}
			}
		}
	}

	private boolean expanded;
	private boolean topLevel;
	private boolean bottomLevel;
	
	public void switchToComplex(){
		GraphPlugin.getDefault().getGenerator().expandGraph(getNode());
		
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
	public void setData(Object obj) {
		data = obj;
	}
	
	
	/**
	 * @return
	 */
	public boolean isBottomLevel() {
		return bottomLevel;
	}

	/**
	 * @return
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @return
	 */
	public boolean isTopLevel() {
		return topLevel;
	}

	/**
	 * @param b
	 */
	public void setBottomLevel(boolean b) {
		bottomLevel = b;
	}

	/**
	 * @param b
	 */
	public void setExpanded(boolean b) {
		expanded = b;
	}

	/**
	 * @param b
	 */
	public void setTopLevel(boolean b) {
		topLevel = b;
	}

}
