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

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.ui.*;
import ca.mcgill.sable.graph.*;


public class CGMenuProvider extends ContextMenuProvider {

	ActionRegistry registry;
	EditPartViewer viewer;
	IWorkbenchPart part;
	
	/**
	 * @param viewer
	 */
	public CGMenuProvider(EditPartViewer viewer, ActionRegistry registry, IWorkbenchPart part) {
		super(viewer);
		setRegistry(registry);
		this.part = part;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		menu.add(getRegistry().getAction(ExpandAction.EXPAND));
		menu.add(getRegistry().getAction(CollapseAction.COLLAPSE));
		
		menu.add(getRegistry().getAction(ShowCodeAction.SHOW_IN_CODE));
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
