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

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jface.resource.*;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;

import org.eclipse.swt.printing.*;

import soot.toolkits.graph.interaction.InteractionHandler;


public class CGDoneAction implements IEditorActionDelegate {

	public static final String DONE = "done"; 
	/**
	 * @param editor
	 */
	public CGDoneAction() {
	}
	
	public void setActiveEditor(IAction action, IEditorPart editor){
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 * steps forward through flowsets unless method 
	 * is finished
	 */
	public void run(IAction action){
		InteractionHandler.v().cgDone(true);
		InteractionHandler.v().setInteractionCon();
	}		
	
	public void selectionChanged(IAction action, ISelection sel){
	}
	
}
