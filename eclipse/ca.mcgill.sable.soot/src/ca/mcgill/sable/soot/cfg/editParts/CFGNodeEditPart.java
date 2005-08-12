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

package ca.mcgill.sable.soot.cfg.editParts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.widgets.Display;

import ca.mcgill.sable.soot.cfg.editpolicies.FlowSelectPolicy;
import ca.mcgill.sable.soot.cfg.figures.CFGNodeFigure;
import ca.mcgill.sable.soot.cfg.model.CFGElement;
import ca.mcgill.sable.soot.cfg.model.CFGFlowData;
import ca.mcgill.sable.soot.cfg.model.CFGFlowInfo;
import ca.mcgill.sable.soot.cfg.model.CFGNode;
import ca.mcgill.sable.soot.cfg.model.CFGPartialFlowData;

public class CFGNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	/**
	 * 
	 */
	public CFGNodeEditPart() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new CFGNodeFigure();
	}

	public void contributeNodesToGraph(DirectedGraph graph, HashMap map){
		Node node = new Node(this);
		node.width = getFigure().getBounds().width;//getNode().getWidth();
		int height = 22;
		if (((CFGNode)getModel()).getBefore() != null){
			height += ((CFGNode)getModel()).getBefore().getChildren().size() * 22;
		}
		if (((CFGNode)getModel()).getAfter() != null){
			height += ((CFGNode)getModel()).getAfter().getChildren().size() * 22;
		}
		node.height = height;//getFigure().getBounds().height;
		graph.nodes.add(node);
		map.put(this, node);
	}
	
	public void contributeEdgesToGraph(DirectedGraph graph, HashMap map) {
		List outgoing = getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++){
			CFGEdgeEditPart edge = (CFGEdgeEditPart)outgoing.get(i);
			edge.contributeToGraph(graph, map);
		}
	}
	
	public void applyGraphResults(DirectedGraph graph, HashMap map){
		Node node = (Node)map.get(this);
		((CFGNodeFigure)getFigure()).setBounds(new Rectangle(node.x, node.y, node.width, node.height));//getFigure().getBounds().height));//getFigure().getBounds().height));
		List outgoing = getSourceConnections();
		for (int i = 0; i < outgoing.size(); i++){
			CFGEdgeEditPart edge = (CFGEdgeEditPart)outgoing.get(i);
			edge.applyGraphResults(graph, map);
		}
	
	}
	
	public void resetColors(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof FlowDataEditPart){
				((FlowDataEditPart)next).resetChildColors();
			}
			else if (next instanceof NodeDataEditPart){
				((NodeDataEditPart)next).resetColors();
				
			}
		}
		
		((CFGNodeFigure)getFigure()).removeIndicator();
	}
	
	public CFGNode getNode(){
		return (CFGNode)getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,new FlowSelectPolicy()); 
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
	
	public List getModelChildren(){
		return getNode().getChildren();
	}
	
	public void propertyChange(PropertyChangeEvent event){
		if (event.getPropertyName().equals(CFGElement.NODE_DATA)){
			refreshChildren();
			refreshVisuals();
		}
		else if (event.getPropertyName().equals(CFGElement.BEFORE_INFO)){
			refreshChildren();
			refreshVisuals();
			final EditPartViewer viewer = getViewer();
			final CFGNodeEditPart thisPart = this;
			Display.getDefault().syncExec(new Runnable(){
				public void run(){
					viewer.reveal(thisPart);
				};
			});
		}
		else if (event.getPropertyName().equals(CFGElement.AFTER_INFO)){
			refreshChildren();
			refreshVisuals();
			final EditPartViewer viewer = getViewer();
			final CFGNodeEditPart thisPart = this;
			Display.getDefault().syncExec(new Runnable(){
				public void run(){
					viewer.reveal(thisPart);
				};
			});
		}
		else if (event.getPropertyName().equals(CFGElement.INPUTS)){
			refreshTargetConnections();
		}
		else if (event.getPropertyName().equals(CFGElement.OUTPUTS)){
			refreshSourceConnections();
		}
		else if (event.getPropertyName().equals(CFGElement.REVEAL)){
			
			revealThis();
			resetColors();
		}
		else if (event.getPropertyName().equals(CFGElement.HIGHLIGHT)){
			highlightThis();
			revealThis();
		}
		
	}
	
	private void revealThis(){
		final EditPartViewer viewer = getViewer();
		final CFGNodeEditPart thisPart = this;
		Display.getDefault().syncExec(new Runnable(){
			public void run(){
				viewer.reveal(thisPart);
				viewer.select(thisPart);
			};
		});
	}
	
	protected void highlightThis(){
		if (((CFGNode)getModel()).getBefore() == null){
			CFGFlowData data = new CFGFlowData();
			CFGFlowInfo info = new CFGFlowInfo();
			info.setText("");
			CFGPartialFlowData pInfo = new CFGPartialFlowData();
			pInfo.addChild(info);
			data.addChild(pInfo);
			
			((CFGNode)getModel()).setBefore(data);
		}
		Iterator it = this.getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof NodeDataEditPart){
				((NodeDataEditPart)next).addIndicator();
			}
		}
		((CFGNodeFigure)getFigure()).addIndicator();
	}
	
	protected void refreshVisuals(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof FlowDataEditPart){
				((CFGNodeFigure)getFigure()).add(((FlowDataEditPart)next).getFigure());
			}
			else if (next instanceof NodeDataEditPart){
				((CFGNodeFigure)getFigure()).add(((NodeDataEditPart)next).getFigure());
			}
		}
	}

	public void updateSize(AbstractGraphicalEditPart part, IFigure child, Rectangle rect){
		this.setLayoutConstraint(part, child, rect);
	}
	
	public void updateSize(int width, int height){
		
		int w = ((CFGNodeFigure)getFigure()).getBounds().width;
		int h = ((CFGNodeFigure)getFigure()).getBounds().height;
		h += height;
		if (width > w){
			((CFGNodeFigure)getFigure()).setSize(width, h);
			((CFGNodeFigure)getFigure()).revalidate();
		}
	}
	
	
	public void handleClickEvent(Object evt){
		((CFGGraphEditPart)getParent()).handleClickEvent(evt);
	}
	
}
