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

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import ca.mcgill.sable.soot.*;
import soot.toolkits.graph.interaction.*;
import org.eclipse.jface.resource.*;


public class NextMethodAction extends EditorPartAction {

	public static final String NEXT_METHOD = "next method"; 
	/**
	 * @param editor
	 */
	public NextMethodAction(IEditorPart editor) {
		super(editor);
		setImageDescriptor(SootPlugin.getImageDescriptor("next_method.gif"));
		setToolTipText("Next Method");
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
	 * continues to the next method if the current 
	 * one is finished
	 */
	public void run(){
		if (InteractionHandler.v().doneCurrent()){
			InteractionHandler.v().setInteractionCon();
		}
	}
	
	public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( NEXT_METHOD );
	}
}
