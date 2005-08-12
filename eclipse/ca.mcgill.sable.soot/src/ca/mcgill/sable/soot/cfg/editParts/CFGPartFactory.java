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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import ca.mcgill.sable.soot.cfg.model.*;


public class CFGPartFactory implements EditPartFactory {

	
	public CFGPartFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart arg0, Object arg1) {
		EditPart part = null;
		if (arg1 instanceof CFGGraph){
			part = new CFGGraphEditPart();
		}
		else if (arg1 instanceof CFGNode){
			part = new CFGNodeEditPart();
		}
		else if (arg1 instanceof CFGEdge){
			part = new CFGEdgeEditPart();	
		}
		else if (arg1 instanceof CFGFlowData){
			part = new FlowDataEditPart();
		}
		else if (arg1 instanceof CFGPartialFlowData){
			part = new PartialFlowDataEditPart();
		}
		else if (arg1 instanceof CFGFlowInfo){
			part = new FlowInfoEditPart();
		}
		else if (arg1 instanceof CFGNodeData){
			part = new NodeDataEditPart();
		}
		part.setModel(arg1);
		return part;
	}

}
