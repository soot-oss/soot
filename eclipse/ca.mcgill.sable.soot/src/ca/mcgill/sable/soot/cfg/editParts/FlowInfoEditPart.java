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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.*;
import ca.mcgill.sable.soot.cfg.editpolicies.*;
import ca.mcgill.sable.soot.cfg.model.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.*;

public class FlowInfoEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener {

	Font f = new Font(null, "Arial", 8, SWT.NORMAL);
	

	public FlowInfoEditPart() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return new Label();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,new FlowSelectPolicy()); 
	
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(CFGElement.FLOW_INFO)){
			
			((Label)getFigure()).setText(evt.getNewValue().toString());
			((Label)getFigure()).setFont(f);
			((Label)getFigure()).setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,153,0)));
			((Label)getFigure()).setSize(evt.getNewValue().toString().length()*7, getFigure().getBounds().height);
			((PartialFlowDataEditPart)getParent()).updateSize(evt.getNewValue().toString().length()*7+10);
			
		}
	}
	
	public void resetColors(){
		((Label)getFigure()).setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0, 0, 0)));
	}

	/**
	 * @return
	 */
	public CFGFlowInfo getFlowInfo() {
		return (CFGFlowInfo)getModel();
	}



	public void activate(){
		super.activate();
		getFlowInfo().addPropertyChangeListener(this);
	}
	
	public void deactivate(){
		super.deactivate();
		getFlowInfo().removePropertyChangeListener(this);
	}
	
	public void handleClickEvent(Object evt){
		System.out.println(getParent().getClass());
		((PartialFlowDataEditPart)getParent()).handleClickEvent(evt);
	}
}
