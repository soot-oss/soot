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


package ca.mcgill.sable.soot.cfg.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.gef.*;


public class FlowSelectAction extends SelectionAction {

	
	public static final String FLOW_SELECT = "flow select"; 
	
	/**
	 * @param part
	 */
	public FlowSelectAction(IWorkbenchPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}
	
	public void setEditorPart(IEditorPart part){
	}
	
	protected void init() { 
		super.init(); 
		setId( FLOW_SELECT );
	}
	
	public void run(){
		try {
			EditPart part = (EditPart)getSelectedObjects().get(0);
			System.out.println("part selected: "+part.getClass());
		}
		catch(ClassCastException e1){
		}
		catch(IndexOutOfBoundsException e2){
		}
	}
}
