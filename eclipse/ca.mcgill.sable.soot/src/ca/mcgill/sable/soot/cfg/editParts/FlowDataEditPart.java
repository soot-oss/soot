/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package ca.mcgill.sable.soot.cfg.editParts;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import ca.mcgill.sable.soot.cfg.figures.*;
import ca.mcgill.sable.soot.cfg.model.*;
import java.util.*;
import org.eclipse.draw2d.geometry.*;


public class FlowDataEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener {

	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(CFGElement.FLOW_CHILDREN)){
			refreshChildren();
			refreshVisuals();
		}
	}
	
	
	protected void refreshVisuals(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof PartialFlowDataEditPart){
				((CFGFlowFigure)getFigure()).add(((PartialFlowDataEditPart)next).getFigure());
			}
			
		}
	}
	
	public void updateSize(FlowInfoEditPart childEdit, IFigure child, Rectangle rect){
		this.setLayoutConstraint(childEdit, child, rect);
		((CFGNodeEditPart)getParent()).setLayoutConstraint(this, getFigure(), new Rectangle(getFigure().getBounds().x, getFigure().getBounds().y, getFigure().getBounds().width, getFigure().getBounds().height));//.updateSize(getFigure(), getFigure().getBounds());
	}
	
	public void updateSize(int width){
		int w = ((CFGFlowFigure)getFigure()).getBounds().width;
		
		if (width > w){
			w = width;
		}
		
		int height = getChildren().size() * 20;
		
		((CFGNodeEditPart)getParent()).updateSize(w+10, height);
		((CFGFlowFigure)getFigure()).setSize(w+10, height);
			
	}
	
	public void resetChildColors(){
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof PartialFlowDataEditPart){
				((PartialFlowDataEditPart)next).resetChildColors();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new CFGFlowFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
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
