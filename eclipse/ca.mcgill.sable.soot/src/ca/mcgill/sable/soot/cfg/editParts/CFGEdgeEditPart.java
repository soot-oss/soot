/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.graph.*;
import java.util.*;
import ca.mcgill.sable.soot.cfg.model.*;
import java.beans.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGEdgeEditPart extends AbstractConnectionEditPart 
	{

	/**
	 * 
	 */
	public CFGEdgeEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}

	protected IFigure createFigure(){
		PolylineConnection conn = new PolylineConnection();
		
		conn.setTargetDecoration(new PolygonDecoration());
		conn.setConnectionRouter(new BendpointConnectionRouter());
		return conn;
	}
	
	public void contributeToGraph(DirectedGraph graph, HashMap map){
		Node source = (Node)map.get(getSource());
		Node target = (Node)map.get(getTarget());
		if (!source.equals(target)){
			Edge e = new Edge(this, source, target);
			graph.edges.add(e);
			map.put(this, e);		
		}
	}
	
	public void applyGraphResults(DirectedGraph graph, HashMap map){
		Edge e = (Edge)map.get(this);
		if (e != null) {
			NodeList nl = e.vNodes;
			PolylineConnection conn = (PolylineConnection)getConnectionFigure();
			if (nl != null){
				ArrayList bends = new ArrayList();
				for (int i = 0; i < nl.size(); i++){
					Node n = nl.getNode(i);
					int x = n.x;
					int y = n.y;
					if (e.isFeedback){
						bends.add(new AbsoluteBendpoint(x, y + n.height));
						bends.add(new AbsoluteBendpoint(x, y));
					}
					else {
						bends.add(new AbsoluteBendpoint(x, y));
						bends.add(new AbsoluteBendpoint(x, y + n.height));
					}
				}
				conn.setRoutingConstraint(bends);
			}
			else {
				conn.setRoutingConstraint(Collections.EMPTY_LIST);
			}
		}
		else {
			PolylineConnection conn = (PolylineConnection)getConnectionFigure();
			Node n = (Node)map.get(getSource());
			ArrayList bends = new ArrayList();
			bends.add(new AbsoluteBendpoint(n.width/2 + n.x + 8, n.y + n.height + 8));
			bends.add(new AbsoluteBendpoint(n.width + n.x + 8, n.y + n.height + 8));
			bends.add(new AbsoluteBendpoint(n.x + n.width + 8, n.y - 16));
			bends.add(new AbsoluteBendpoint(n.x + n.width/2 + 16, n.y - 16));
			
			conn.setRoutingConstraint(bends);
		}
	}
	
	
	public CFGEdge getEdge(){
		return (CFGEdge)getModel();
	}
	

}
