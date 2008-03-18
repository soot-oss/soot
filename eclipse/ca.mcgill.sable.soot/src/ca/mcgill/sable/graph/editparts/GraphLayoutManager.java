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

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.draw2d.graph.*;
import java.util.*;

public class GraphLayoutManager extends AbstractLayout {

	private GraphEditPart graphPart;
	

	public GraphLayoutManager(GraphEditPart graphPart) {
		setGraphPart(graphPart);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure, int, int)
	 */
	protected Dimension calculatePreferredSize(
		IFigure arg0,
		int arg1,
		int arg2) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
	 */
	public void layout(IFigure arg0) {
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
	public GraphEditPart getGraphPart() {
		return graphPart;
	}

	/**
	 * @param part
	 */
	public void setGraphPart(GraphEditPart part) {
		graphPart = part;
	}

}
