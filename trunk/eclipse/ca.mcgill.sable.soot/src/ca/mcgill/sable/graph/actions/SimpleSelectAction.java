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


package ca.mcgill.sable.graph.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

public class SimpleSelectAction extends SelectionAction {

	
	private final static String SIMPLE_SELECT = "simple select";
	private IWorkbenchPart part;
	
	/**
	 * @param part
	 */
	public SimpleSelectAction(IWorkbenchPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}
	
	public void run(){
	}

	/**
	 * @return
	 */
	public IWorkbenchPart getPart() {
		return part;
	}

	/**
	 * @param part
	 */
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}
	
	protected void init() { 
		super.init(); 
		setId( SIMPLE_SELECT );
	}

}
