/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.*;
import org.eclipse.draw2d.graph.*;
import java.util.*;
import ca.mcgill.sable.soot.cfg.model.*;
import ca.mcgill.sable.soot.cfg.figures.*;
import org.eclipse.draw2d.geometry.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	/**
	 * 
	 */
	public CFGNodeEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return new CFGNodeFigure();
	}

	public void contributeNodesToGraph(DirectedGraph graph, HashMap map){
		Node node = new Node(this);
		node.width = getFigure().getSize().width;//getNode().getWidth();
		node.height = getFigure().getSize().height;
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
		((CFGNodeFigure)getFigure()).resetColors();
	}
	
	public CFGNode getNode(){
		return (CFGNode)getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		// TODO Auto-generated method stub
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		// TODO Auto-generated method stub
		return new ChopboxAnchor(getFigure());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		// TODO Auto-generated method stub
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
		if (event.getPropertyName().equals(CFGElement.WIDTH)){
			
			refreshVisuals();
		}
		else if (event.getPropertyName().equals(CFGElement.TEXT)){
			refreshVisuals();
		}
		else if (event.getPropertyName().equals(CFGElement.INPUTS)){
			refreshTargetConnections();
		}
		else if (event.getPropertyName().equals(CFGElement.OUTPUTS)){
			refreshSourceConnections();
		}
		else if (event.getPropertyName().equals(CFGElement.HEAD)){
			((CFGNodeFigure)getFigure()).getRect().setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,45,200)));
		}
		else if (event.getPropertyName().equals(CFGElement.TAIL)){
			((CFGNodeFigure)getFigure()).getRect().setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,200,45)));
		}
		else if (event.getPropertyName().equals(CFGElement.BEFORE_INFO)){
			
			((CFGNodeFigure)getFigure()).setBefore(getNode().getBefore());
			((CFGNodeFigure)getFigure()).addBeforeFigure();
			final EditPartViewer viewer = getViewer();
			final CFGNodeEditPart thisPart = this;
			Display.getDefault().syncExec(new Runnable(){
				public void run(){
					viewer.reveal(thisPart);
				};
			});
		}
		else if (event.getPropertyName().equals(CFGElement.AFTER_INFO)){
			((CFGNodeFigure)getFigure()).setAfter(getNode().getAfter());
			((CFGNodeFigure)getFigure()).addAfterFigure();
			final EditPartViewer viewer = getViewer();
			final CFGNodeEditPart thisPart = this;
			Display.getDefault().syncExec(new Runnable(){
				public void run(){
					viewer.reveal(thisPart);
				};
			});
			
		}
	}
	
	protected void refreshVisuals(){
		
		((CFGNodeFigure)getFigure()).setWidth(getNode().getWidth());
		((CFGNodeFigure)getFigure()).setData(getNode().getText());
		
		((CFGNodeFigure)getFigure()).updateFigure();
			
	}


}
