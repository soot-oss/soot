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

/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.editpolicies;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.*;
import ca.mcgill.sable.soot.cfg.figures.*;
import ca.mcgill.sable.soot.cfg.editParts.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FlowSelectPolicy extends SelectionEditPolicy {

	/**
	 * 
	 */
	public FlowSelectPolicy() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#hideSelection()
	 */
	protected void hideSelection() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#showSelection()
	 */
	protected void showSelection() {
		// TODO Auto-generated method stub
		//((FlowInfoEditPart)getHost()).handleClickEvent(((Label)getHostFigure()).getText());
		
	}
	
	
	
	public void showTargetFeedback(Request request){
		/*if (request instanceof SelectionRequest){
			//System.out.println("get host figure: "+getHostFigure()+" loc: "+((SelectionRequest)request).getLocation());
		//}
		//else if (request instanceof LocationRequest){
			System.out.println("request type: "+((LocationRequest)request).getLocation());
			if (getHostFigure() instanceof Label){
				System.out.println("clicked on: "+((Label)getHostFigure()).getText());
				//((FlowInfoEditPart)getHost()).handleClickEvent(((Label)getHostFigure()).getText());
			}
			//System.out.println("after label loc: "+((CFGNodeFigure)getHostFigure()).getAfterLabel().getLocation());
			//((CFGNodeFigure)getHostFigure()).getBeforeLabel().translateToRelative(((LocationRequest)request).getLocation());

			//System.out.println("translate: "+((LocationRequest)request).getLocation());

			
		}
		*/
	}
	

}
