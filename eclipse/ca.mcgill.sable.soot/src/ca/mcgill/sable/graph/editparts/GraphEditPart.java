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

import org.eclipse.draw2d.*;
import org.eclipse.gef.editparts.*;
import org.eclipse.draw2d.graph.*;
import org.eclipse.gef.*;
import org.eclipse.draw2d.geometry.*;
import java.util.*;
import ca.mcgill.sable.graph.model.*;
import java.beans.*;

public class GraphEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener {

	private int figureWidth = 20000;
	private int figureHeight = 20000;
	

	public GraphEditPart() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure f = new Figure() {
			public void setBound(Rectangle rect){
				int x = bounds.x; 
				int y = bounds.y;

				boolean resize = (rect.width != bounds.width) || (rect.height != bounds.height),
		  			translate = (rect.x != x) || (rect.y != y);

				if (isVisible() && (resize || translate))
					erase();
				if (translate) {
					int dx = rect.x - x;
					int dy = rect.y - y;
					primTranslate(dx, dy);
				}
				bounds.width = rect.width;
				bounds.height = rect.height;
	
				if (resize || translate) {
					fireMoved();
					repaint();
				}
			}
		};
		f.setLayoutManager(new GraphLayoutManager(this));
		return f;
	}

	protected void setFigure(IFigure figure){
		figure.getBounds().setSize(getFigureWidth(),getFigureHeight());
		super.setFigure(figure);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
	}
	
	public void contributeNodesToGraph(DirectedGraph graph, HashMap map){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				SimpleNodeEditPart child = (SimpleNodeEditPart)next;
				child.contributeNodesToGraph(graph, map);
		
			}
		}
		
	}
	
	
	public void contributeEdgesToGraph(DirectedGraph graph, HashMap map){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				((SimpleNodeEditPart)next).contributeEdgesToGraph(graph, map);

			}
		}
		
	}

	public void applyGraphResults(DirectedGraph graph, HashMap map){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				((SimpleNodeEditPart)next).applyGraphResults(graph, map);
			}
		}
		determineGraphBounds(graph);
	}
	
	
	
	private void determineGraphBounds(DirectedGraph graph){
		Iterator it = graph.nodes.iterator();
		int width = 0;
		int height = 0;
		while (it.hasNext()){
			Node n = (Node)it.next();
			if (width < n.x){
				width = n.x + 300;
			}
			height = max(height, n.height);
		}
		setFigureWidth(width);
		setFigureHeight(height);
	}
	
	private int max(int i, int j){
		return i < j ? j : i;
	}
	
	public Graph getGraph(){
		return (Graph)getModel();
	}
	
	public List getModelChildren(){
		return getGraph().getChildren();
	}

	public void activate(){
		super.activate();
		getGraph().addPropertyChangeListener(this);
	}
	
	public void deactivate(){
		super.deactivate();
		getGraph().removePropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent event){
		if (event.getPropertyName().equals(Element.GRAPH_CHILD)){
			refreshChildren();
		}
		
		getFigure().revalidate();
		((GraphicalEditPart)(getViewer().getContents())).getFigure().revalidate();
	
	}
	/**
	 * @return
	 */
	public int getFigureHeight() {
		return figureHeight;
	}

	/**
	 * @return
	 */
	public int getFigureWidth() {
		return figureWidth;
	}

	/**
	 * @param i
	 */
	public void setFigureHeight(int i) {
		figureHeight = i;
	}

	/**
	 * @param i
	 */
	public void setFigureWidth(int i) {
		figureWidth = i;
	}

}
