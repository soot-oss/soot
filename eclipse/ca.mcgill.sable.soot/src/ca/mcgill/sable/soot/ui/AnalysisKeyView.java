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
 * Created on Nov 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.ui;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AnalysisKeyView extends ViewPart {

	private ArrayList inputKeys;
	private TableViewer viewer;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		viewer = new TableViewer(parent);
		viewer.setLabelProvider(new KeysLabelProvider());
		viewer.setContentProvider(new ArrayContentProvider());
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	public ArrayList getInputKeys() {
		return inputKeys;
	}

	/**
	 * @param list
	 */
	public void setInputKeys(ArrayList list) {
		inputKeys = list;
		viewer.setInput(inputKeys);
	}

	/*public void dispose(){
		System.out.println("lp class: "+viewer.getLabelProvider().getClass());
		if (viewer.getLabelProvider() instanceof KeysLabelProvider){
			((KeysLabelProvider)viewer.getLabelProvider()).dispose();
			System.out.println("has right label provider");
		}
		super.dispose();
	}*/
}
