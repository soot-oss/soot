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
import ca.mcgill.sable.graph.editpolicies.*;
import ca.mcgill.sable.graph.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleNodeEditPart
	extends AbstractGraphicalEditPart
	implements NodeEditPart, PropertyChangeListener {

	private String data;
	
	/**
	 * 
	 */
	public SimpleNodeEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
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
			//System.out.println("edge src: "+edge.getSource()+" edge tgt: "+edge.getTarget());
			//if (map.containsKey(edge.getSource()) && map.containsKey(edge.getTarget())){
			//System.out.println("contribute edge: src: "+((ca.mcgill.sable.graph.model.Edge)edge.getModel()).getSrc().getData()+" tgt: "+((ca.mcgill.sable.graph.model.Edge)edge.getModel()).getTgt().getData());
				edge.contributeToGraph(graph, map);
			//}
		}
	}
	
	public void applyGraphResults(DirectedGraph graph, HashMap map){
		Node node = (Node)map.get(this);
		if (node != null){
			//System.out.println("node.x: "+node.x+" node.y: "+node.y);
			getFigure().setBounds(new Rectangle(node.x+10, node.y, node.width, node.height));//getFigure().getBounds().height));//getFigure().getBounds().height));
			List outgoing = getSourceConnections();
			for (int i = 0; i < outgoing.size(); i++){
				EdgeEditPart edge = (EdgeEditPart)outgoing.get(i);
				//if (map.containsKey(edge.getSource()) && map.containsKey(edge.getTarget())){
					edge.applyGraphResults(graph, map);
				//}
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
		// TODO Auto-generated method stub
		//installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleNodeSelectPolicy()); 
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
		if (event.getPropertyName().equals(Element.DATA)){
			//System.out.println("new val: "+event.getNewValue());
			setData(event.getNewValue().toString());
			refreshVisuals();
		}
		else if (event.getPropertyName().equals(Element.INPUTS)){
			refreshTargetConnections();
		}
		else if (event.getPropertyName().equals(Element.OUTPUTS)){
			refreshSourceConnections();
		}
		//else if (event.getPropertyName().equals(Element.COMPLEX_CHILD_ADDED)){
			//System.out.println("receive COMPLEX_ADDED event");
			//System.out.println("child: "+getModelChildren().get(0).getClass());
			//refreshChildren();
		//}
		
	}
	
	public List getModelChildren(){
		return getNode().getChildren();
	}
	
	protected void refreshVisuals(){
		Iterator it = getFigure().getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof Label){
				((Label)next).setText(getData());
				//System.out.println("data: "+getData());
				if (getData() != null){
					//((Label)next).setSize(getData().length()*7, ((Label)next).getBounds().height);
					getFigure().setSize((getData().length()*7)+10, ((((Label)next).getBounds().height/2)+10));
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
	public String getData() {
		return data;
	}

	/**
	 * @param string
	 */
	public void setData(String string) {
		data = string;
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
	
	/*public void performRequest(Request req){
		System.out.println("req: "+req.getType());
	}*/

}
