/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import ca.mcgill.sable.soot.cfg.model.*;
import ca.mcgill.sable.soot.cfg.figures.*;
import java.util.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.swt.graphics.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NodeDataEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener {

	
	/**
	 * 
	 */
	public NodeDataEditPart() {
		super();
		// TODO Auto-generated constructor stub
		//System.out.println("node data edit part created");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return new CFGNodeDataFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		//System.out.println("node data received event: "+evt.getPropertyName());
			
		if (evt.getPropertyName().equals(CFGElement.TEXT)){
			//System.out.println("received node data text");
			((CFGNodeDataFigure)getFigure()).setData((ArrayList)evt.getNewValue());
			((CFGNodeDataFigure)getFigure()).updateFigure();
			//((CFGNodeEditPart)getParent()).updateSize(this, getFigure(), getFigure().getBounds());
			((CFGNodeEditPart)getParent()).updateSize(getFigure().getBounds().width, getFigure().getBounds().height);
		}
		else if (evt.getPropertyName().equals(CFGElement.HEAD)){
			((CFGNodeDataFigure)getFigure()).getRect().setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,45,200)));
		}
		else if (evt.getPropertyName().equals(CFGElement.TAIL)){
			((CFGNodeDataFigure)getFigure()).getRect().setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,200,45)));
		}
		
		//getFigure().revalidate();
	}

	/**
	 * @return
	 */
	public CFGNodeData getNodeData() {
		return (CFGNodeData)getModel();
	}

	
	
	public void activate(){
		super.activate();
		getNodeData().addPropertyChangeListener(this);	
	}
	
	public void deactivate(){
		super.deactivate();
		getNodeData().removePropertyChangeListener(this);
	}

	public void markStop(){
		System.out.println(getNodeData().getText());
		ArrayList list = getNodeData().getText();
		System.out.println("list class type: "+list.get(0).getClass());
		((CFGNodeDataFigure)getFigure()).addStopIcon();
		// send event marking this unit
		soot.toolkits.graph.interaction.InteractionHandler.v().addToStopUnitList(((CFGNodeDataFigure)getFigure()).getUnit());
	}
	
	public void resetColors(){
		//((CFGNodeDataFigure)getFigure()).setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(255, 255 ,255)));
	}
}
