/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.graph.*;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGGraphLayoutManager extends AbstractLayout {

	private CFGGraphEditPart graphPart;
	
	/**
	 * 
	 */
	public CFGGraphLayoutManager(CFGGraphEditPart graphPart) {
		setGraphPart(graphPart);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure, int, int)
	 */
	protected Dimension calculatePreferredSize(
		IFigure arg0,
		int arg1,
		int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
	 */
	public void layout(IFigure arg0) {
		// TODO Auto-generated method stub
		DirectedGraph graph = new DirectedGraph();
		HashMap map = new HashMap();
		// add nodes and edges to graph
		// retrieve them from CFGGraphEditPart
		getGraphPart().contributeNodesToGraph(graph, map);
		getGraphPart().contributeEdgesToGraph(graph, map);
		if (graph.nodes.size() != 0){
			DirectedGraphLayout layout = new DirectedGraphLayout();
			layout.visit(graph);
			
			getGraphPart().applyGraphResults(graph, map);
		}
		
	}

	/**
	 * @return
	 */
	public CFGGraphEditPart getGraphPart() {
		return graphPart;
	}

	/**
	 * @param part
	 */
	public void setGraphPart(CFGGraphEditPart part) {
		graphPart = part;
	}

}
