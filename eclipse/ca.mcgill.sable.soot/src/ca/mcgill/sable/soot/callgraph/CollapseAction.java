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
package ca.mcgill.sable.soot.callgraph;

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.actions.*;
import ca.mcgill.sable.graph.actions.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CollapseAction extends SimpleSelectAction {

	public static final String COLLAPSE = "collapse action";
	
	/**
	 * 
	 */
	public CollapseAction(IWorkbenchPart part) {
		super(part);
		// TODO Auto-generated constructor stub
	}

	

	
	protected void init(){
		super.init();
		setId(COLLAPSE);
		setText("Collapse");
	}

	public void run(){
		System.out.println("running collapse");
		System.out.println("sel: "+getSelection());
		//System.out.println("sel: "+getSelectedObjects().get(0));
		//if (getSelection() instanceof CallGraphNodeEditPart){
			CallGraphNodeEditPart cgPart = (CallGraphNodeEditPart)getSelectedObjects().get(0);
			cgPart.collapseGraph();
		//}
	}
	
	public boolean calculateEnabled(){
		System.out.println("part in calenab collapse");
		return true;
	}
}
