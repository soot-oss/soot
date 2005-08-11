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

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.*;

public class GraphActionBarContributor extends ActionBarContributor {

	/**
	 * 
	 */
	public GraphActionBarContributor() {
		super();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(IWorkbenchActionConstants.PRINT);
	}
	
	//  this is for zoom toolbar buttons
	public void contributeToToolBar(IToolBarManager toolBarManager){
		super.contributeToToolBar(toolBarManager);
		toolBarManager.add(new Separator());
	}
	
	public void contributeToMenu(IMenuManager menuManager){
		super.contributeToMenu(menuManager);
		
		MenuManager viewMenu = new MenuManager("View");
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		
		menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	}
	
	public void setActiveEditor(IEditorPart editor) { 
		super.setActiveEditor(editor); 
		
	}
	
}
