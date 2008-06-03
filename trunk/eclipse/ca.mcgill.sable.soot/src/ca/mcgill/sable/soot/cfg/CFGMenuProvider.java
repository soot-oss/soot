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
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.ui.*;
import ca.mcgill.sable.graph.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGMenuProvider extends ContextMenuProvider {

	ActionRegistry registry;
	EditPartViewer viewer;
	IWorkbenchPart part;
	
	/**
	 * @param viewer
	 */
	public CFGMenuProvider(EditPartViewer viewer, ActionRegistry registry, IWorkbenchPart part) {
		super(viewer);
		setRegistry(registry);
		this.part = part;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		
		// TODO Auto-generated method stub
		//IAction showCode = new ShowCodeAction(part);
		//System.out.println("registry: "+getRegistry());
		//System.out.println("acion: "+getRegistry().getAction(StopAction.STOP));
		menu.add(getRegistry().getAction(StopAction.STOP));
		menu.add(getRegistry().getAction(UnStopAction.UN_STOP));
		//getRegistry().registerAction(showCode);
		//((GraphEditor)part).getGraphEditorSelectionActions().add(showCode);
	}

	/**
	 * @return
	 */
	public ActionRegistry getRegistry() {
		return registry;
	}

	/**
	 * @param registry
	 */
	public void setRegistry(ActionRegistry registry) {
		this.registry = registry;
	}

}
