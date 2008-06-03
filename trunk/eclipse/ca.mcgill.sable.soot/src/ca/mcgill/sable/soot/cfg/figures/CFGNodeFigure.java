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

package ca.mcgill.sable.soot.cfg.figures;

import org.eclipse.draw2d.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.draw2d.text.*;
import ca.mcgill.sable.soot.editors.*;
import org.eclipse.jface.resource.*;

public class CFGNodeFigure extends Figure {

	private Panel nodeFigure;//RectangleFigure rect;

	private XYAnchor srcAnchor;
	private XYAnchor tgtAnchor;	

	private CFGNodeDataFigure data;
	private CFGFlowFigure before;
	private CFGFlowFigure after;
	
	private boolean hasBefore;
	private boolean hasAfter;
	
	Font f = new Font(null, "Arial", 8, SWT.NORMAL);
		
	
	/**
	 * 
	 */
	public CFGNodeFigure() {
		super();
		
		ToolbarLayout layout2 = new ToolbarLayout();
		layout2.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	
		this.setLayoutManager(layout2);
		
		layout2.setStretchMinorAxis(false);
	
	}

	
	private int getLineBreak(String text){
		return text.lastIndexOf(" ", 50);
	}
	


	/**
	 * @return
	 */
	public Panel getNodeFigure() {
		return nodeFigure;
	}

	/**
	 * @param panel
	 */
	public void setNodeFigure(Panel panel) {
		nodeFigure = panel;
	}



	

	/**
	 * @param figure
	 */
	public void setAfter(CFGFlowFigure figure) {
		after = figure;
	}

	/**
	 * @param figure
	 */
	public void setBefore(CFGFlowFigure figure) {
		before = figure;
	}

	/**
	 * @param figure
	 */
	public void setData(CFGNodeDataFigure figure) {
		data = figure;
	}

	/**
	 * @return
	 */
	public CFGFlowFigure getAfter() {
		return after;
	}

	/**
	 * @return
	 */
	public CFGFlowFigure getBefore() {
		return before;
	}

	/**
	 * @return
	 */
	public CFGNodeDataFigure getData() {
		return data;
	}

	/**
	 * @return
	 */
	public XYAnchor getSrcAnchor() {
		int x = this.getBounds().x;
		int y = this.getBounds().y;
		int width = this.getBounds().width;
		int height = this.getBounds().height;
		org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(x+width/2, y+height);
		return new XYAnchor(p);
	}

	/**
	 * @return
	 */
	public XYAnchor getTgtAnchor() {
		return tgtAnchor;
	}

	Image indicatorImage = null;
	Label indicatorFigure = null;
	

	public void addIndicator(){
		
	
	}
	
	public void removeIndicator(){
		
	}
	

}
