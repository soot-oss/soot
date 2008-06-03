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

package ca.mcgill.sable.soot.callgraph;

import ca.mcgill.sable.graph.editparts.PartFactory;
import org.eclipse.gef.*;


public class CallGraphPartFactory extends PartFactory {


	public CallGraphPartFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart arg0, Object arg1) {
		EditPart part = super.createEditPart(arg0, arg1);
		
		if (arg1 instanceof CallGraphNode){
			part = new CallGraphNodeEditPart();
		}
		part.setModel(arg1);
		return part;
	}
}
