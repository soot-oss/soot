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
 * Created on Mar 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;

import ca.mcgill.sable.graph.editparts.SimpleNodeEditPart;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CallGraphNodeEditPart extends SimpleNodeEditPart {

	/**
	 * 
	 */
	public CallGraphNodeEditPart() {
		super();
		// TODO Auto-generated constructor stub
	}
	Font f = new Font(null, "Arial", 8, SWT.NORMAL);
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// TODO Auto-generated method stub
		RectangleFigure rect = new RectangleFigure();
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		rect.setLayoutManager(layout);
		
		Label cLabel = new Label();
		cLabel.setFont(f);
		cLabel.getInsets().bottom = 1;
		cLabel.getInsets().top = 1;
		cLabel.getInsets().left = 1;
		cLabel.getInsets().right = 1;

		rect.add(cLabel);
		
		Label mLabel = new Label();
		mLabel.setFont(f);
		mLabel.getInsets().bottom = 1;
		mLabel.getInsets().top = 1;
		mLabel.getInsets().left = 1;
		mLabel.getInsets().right = 1;
		
		
		rect.add(mLabel);
		
		return rect;
	}
	
	Image publicImage = null;
	Image privateImage = null;
	Image protectedImage = null;
	
	
	protected void refreshVisuals(){
		Label cLabel = (Label)getFigure().getChildren().get(0);
		Label mLabel = (Label)getFigure().getChildren().get(1);
		if (getData() != null){
			String c = getData().substring(0, getData().indexOf(":")+1);
			String m = getData().substring(getData().indexOf(":")+1);
			cLabel.setText(c);
			mLabel.setText(m);
			int len = m.length() > c.length() ? m.length() : c.length();
			getFigure().setSize(len*7-6, 32);
			getFigure().revalidate();
		}
		/*Iterator it = getFigure().getChildren().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next instanceof Label){
				((Label)next).setText(getData());
				//System.out.println("data: "+getData());
				if (getData() != null){
					//((Label)next).setSize(getData().length()*7, ((Label)next).getBounds().height);
					getFigure().setSize((getData().length()*7)+10, ((((Label)next).getBounds().height/2)+10));
					getFigure().revalidate();
					((GraphEditPart)getParent()).getFigure().revalidate();
				}
			}
		}*/
	}

	/*public void switchToComplex(){
		((CallGraphNode)getNode()).getGenerator().expandGraph((CallGraphNode)getNode());
	}*/
	
	public void expandGraph(){
		((CallGraphNode)getNode()).getGenerator().expandGraph((CallGraphNode)getNode());
	}
	
	public void collapseGraph(){
		((CallGraphNode)getNode()).getGenerator().collapseGraph((CallGraphNode)getNode());
	}
	
	public void showInCode(){
		((CallGraphNode)getNode()).getGenerator().showInCode((CallGraphNode)getNode());
	}
}

