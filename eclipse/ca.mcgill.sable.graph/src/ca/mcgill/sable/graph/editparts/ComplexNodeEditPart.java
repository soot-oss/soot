/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

import com.sun.rsasign.t;

import ca.mcgill.sable.graph.editpolicies.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComplexNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	/**
	 * 
	 */
	public ComplexNodeEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
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


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
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
		//((CFGNodeFigure)getFigure()).setBounds(new Rectangle(node.x, node.y, node.width, node.height));//getFigure().getBounds().height));//getFigure().getBounds().height));
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
