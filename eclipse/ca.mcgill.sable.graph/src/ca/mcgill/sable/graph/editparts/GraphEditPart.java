/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GraphEditPart extends AbstractGraphicalEditPart 
	implements PropertyChangeListener {

	private int figureWidth = 20000;
	private int figureHeight = 20000;
	
	/**
	 * 
	 */
	public GraphEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}
	
	/*private void contributeNodes(DirectedGraph graph, HashMap map, SimpleNodeEditPart part ){
		if (part.isExpanded()){
			Iterator it = part.getChildren().iterator();
			while (it.hasNext()){
				Object next = it.next();
				if (next instanceof SimpleNodeEditPart){
					contributeNodes(graph, map, (SimpleNodeEditPart)next);	
				}
			}
		}
		else {
			part.contributeNodesToGraph(graph, map);
		}
	}*/
	
	public void contributeNodesToGraph(DirectedGraph graph, HashMap map){
		//System.out.println("adding nodes to graph - graph");
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				SimpleNodeEditPart child = (SimpleNodeEditPart)next;
				//contributeNodes(graph, map, child);
				child.contributeNodesToGraph(graph, map);
		
			}
		}
		
	}
	
	/*private void contributeEdges(DirectedGraph graph, HashMap map, SimpleNodeEditPart part ){
		if (part.isExpanded()){
			Iterator it = part.getChildren().iterator();
			while (it.hasNext()){
				Object next = it.next();
				if (next instanceof SimpleNodeEditPart){
					contributeEdges(graph, map, (SimpleNodeEditPart)next);	
				}
			}
		}
		else {
			part.contributeEdgesToGraph(graph, map);
		}
	}*/
	
	public void contributeEdgesToGraph(DirectedGraph graph, HashMap map){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof SimpleNodeEditPart){
				//contributeEdges(graph, map, (SimpleNodeEditPart)next);
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

	/*public void handleClickEvent(Object evt){
		((CFGGraph)getModel()).handleClickEvent(evt);
	}*/
}
