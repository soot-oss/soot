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

import ca.mcgill.sable.graph.editparts.SimpleNodeEditPart;
import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import soot.*;
import org.eclipse.jface.resource.*;
import ca.mcgill.sable.soot.*;


public class CallGraphNodeEditPart extends SimpleNodeEditPart {

	
	public CallGraphNodeEditPart() {
		super();
	}
	Font f = new Font(null, "Arial", 8, SWT.NORMAL);
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
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
	
	protected void loadImages(){
		if (publicImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("public_co.gif");
			publicImage = desc.createImage();
		}
		if (protectedImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("protected_co.gif");
			protectedImage = desc.createImage();
		}
		if (privateImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("private_co.gif");
			privateImage = desc.createImage();
		}
	}
	
	protected void refreshVisuals(){
	
		Label cLabel = (Label)getFigure().getChildren().get(0);
		Label mLabel = (Label)getFigure().getChildren().get(1);
		if (getData() != null){
			SootMethod currMeth = (SootMethod)getData();
			String c = currMeth.getSignature().substring(0, currMeth.getSignature().indexOf(":")+1);
			String m = currMeth.getSignature().substring(currMeth.getSignature().indexOf(":")+1);
			cLabel.setText(c);
			mLabel.setText(m);
			int len = m.length() > c.length() ? m.length() : c.length();
			getFigure().setSize(len*7-6, 38);
			loadImages();
			Image image = null;
			if (currMeth.isPublic()){
				image = publicImage;
			}
			else if (currMeth.isProtected()){
				image = protectedImage;
			}
			else {
				image = privateImage;
			}
			((Label)getFigure().getChildren().get(0)).setIcon(image);
			getFigure().revalidate();
			
		}
		
	}

	
	
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

