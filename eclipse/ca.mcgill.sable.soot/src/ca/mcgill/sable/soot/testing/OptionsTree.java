/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package ca.mcgill.sable.soot.testing;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;
//import org.eclipse.ui.views.*;

/**
 * @author jlhotak
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
public class OptionsTree extends ViewPart {
	private TreeViewer viewer;
	
	public OptionsTree() {
		super();
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new SootOptionsContentProvider());
		viewer.setLabelProvider(new SootOptionsLabelProvider());
		viewer.setInput(getInitialInput());
		viewer.expandAll();

	}
	
	private SootOption getInitialInput(){
		//SootOption root = new SootOption("");
		
		//SootOption page1 = new SootOption("Output");
		//root.addChild(page1);
		//SootOption page2 = new SootOption("Input");
		//root.addChild(page2);
		
		return null;
	}
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * Returns the viewer.
	 * @return TreeViewer
	 */
	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Sets the viewer.
	 * @param viewer The viewer to set
	 */
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

}
