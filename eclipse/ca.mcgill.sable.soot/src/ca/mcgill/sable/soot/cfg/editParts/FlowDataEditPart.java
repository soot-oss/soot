/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editParts;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import ca.mcgill.sable.soot.cfg.figures.*;
import ca.mcgill.sable.soot.cfg.model.*;
import java.util.*;
import org.eclipse.draw2d.geometry.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FlowDataEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener {

	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getPropertyName().equals(CFGElement.FLOW_CHILDREN)){
			refreshChildren();
			refreshVisuals();
		}
		//((CFGFlowFigure)getFigure()).revalidate();
	}
	
	
	protected void refreshVisuals(){
		//System.out.println("refreshing visuals for flow data");
		Iterator it = getChildren().iterator();
		//int height = 0;
		//int width = 0;
		while (it.hasNext()){
			Object next = it.next();
			//System.out.println("next child: "+next.getClass());
			if (next instanceof FlowInfoEditPart){
				((CFGFlowFigure)getFigure()).add(((FlowInfoEditPart)next).getFigure());
				//height = ((FlowInfoEditPart)next).getFigure().getBounds().height;
				//width += ((FlowInfoEditPart)next).getFigure().getBounds().width;
			}
			
		}
		//((CFGFlowFigure)getFigure()).setSize(width, height);
	}
	
	public void updateSize(FlowInfoEditPart childEdit, IFigure child, Rectangle rect){
		this.setLayoutConstraint(childEdit, child, rect);
		((CFGNodeEditPart)getParent()).setLayoutConstraint(this, getFigure(), new Rectangle(getFigure().getBounds().x, getFigure().getBounds().y, getFigure().getBounds().width, getFigure().getBounds().height));//.updateSize(getFigure(), getFigure().getBounds());
	}
	
	public void updateSize(int width){
		System.out.println("flow data bounds width: "+getFigure().getBounds().width);
		System.out.println("flow data size width: "+getFigure().getSize().width);
		int w = ((CFGFlowFigure)getFigure()).getBounds().width;
		
		w += width;
		//System.out.println("width: "+w);
		//if (width > w){
		//	((CFGFlowFigure)getFigure()).setSize(w, getFigure().getBounds().height);
			//getFigure().revalidate();	
		//}
		/*Iterator it = getChildren().iterator();
		int height = 0;
		int w = 0;
		while (it.hasNext()){
			Object next = it.next();
			//System.out.println("next child: "+next.getClass());
			if (next instanceof FlowInfoEditPart){
				((CFGFlowFigure)getFigure()).add(((FlowInfoEditPart)next).getFigure());
				height = ((FlowInfoEditPart)next).getFigure().getBounds().height;
				w += ((FlowInfoEditPart)next).getFigure().getBounds().width;
				System.out.println("width: flow data edit: "+w);
			}
			
		}
		getFigure().setSize(w, height/2);*/
		//getFigure().revalidate();
		((CFGNodeEditPart)getParent()).updateSize(w+10, getFigure().getBounds().height/2);
		((CFGFlowFigure)getFigure()).setSize(w+10, getFigure().getBounds().height);
			
		//getFigure().validate();
	}
	
	public void resetChildColors(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof FlowInfoEditPart){
				((FlowInfoEditPart)next).resetColors();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		return new CFGFlowFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}

	public List getModelChildren(){
		return getFlowData().getChildren();
	}
	
	public void activate(){
		super.activate();
		getFlowData().addPropertyChangeListener(this);
	}
	
	public void deactivate(){
		super.deactivate();
		getFlowData().removePropertyChangeListener(this);
	}
	/**
	 * @return
	 */
	public CFGFlowData getFlowData() {
		return (CFGFlowData)getModel();
	}

	
	public void handleClickEvent(Object evt){
		((CFGNodeEditPart)getParent()).handleClickEvent(evt);
	}
}
