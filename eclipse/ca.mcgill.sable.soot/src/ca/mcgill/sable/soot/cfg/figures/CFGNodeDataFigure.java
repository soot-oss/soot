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
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.figures;

import org.eclipse.draw2d.*;
import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.draw2d.text.*;
import ca.mcgill.sable.soot.editors.*;
import org.eclipse.draw2d.geometry.*;
import org.eclipse.jface.resource.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeDataFigure extends Figure {

	private Panel nodeFigure;//RectangleFigure rect;
	private RectangleFigure rect;
	
	private ArrayList data;
	
	
	Font f = new Font(null, "Arial", 8, SWT.NORMAL);
		
	
	/**
	 * 
	 */
	public CFGNodeDataFigure() {
		super();
		setRect(new RectangleFigure());
	
		this.add(getRect());
		
		getRect().setBackgroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(255, 255 ,255)));
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		layout.setVertical(false);
		
		this.setLayoutManager(layout);
		
		//XYLayout xylayout = new XYLayout();
		getRect().setLayoutManager(layout);
		//layout.setStretchMinorAxis(false);
		//this.setLayoutManager(layout);
		
		//layout.calculateMinimumSize(getRect(), -1, -1);	
	}
	

	private soot.Unit unit;
	
	public void updateFigure(){
		if (getData() == null) return;
		
		int height = 0;
		int width = 0;
		Iterator it = getData().iterator();
		while (it.hasNext()){
			//String next = (String)it.next();
			unit = (soot.Unit)it.next();
			String next = unit.toString();
			
			Label l = new Label(next);
			l.setFont(f);
			l.getInsets().top = 1;
			l.getInsets().bottom = 1;
			l.getInsets().right = 1;
			l.getInsets().left = 1;
			height = height + l.getSize().height/2;
			int length = next.length()*7;
			width = length > width ? length : width;
			//l.setPreferredSize(-1, -1);
			getRect().add(l);
			
		}
			
		getRect().setSize(width+10, height+10);
		this.setSize(width+10, height+10);
		//this.setConstraint(getRect(), new org.eclipse.draw2d.geometry.Rectangle(getRect().getLocation(), getRect().getSize()));
		
	}

	
	/**
	 * @return
	 */
	public RectangleFigure getRect() {
		return rect;
	}

	Image stopImage = null;
	Image highlightImage = null;
	
	public void addStopIcon(){
		if (stopImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("stop_icon.gif");
			stopImage = desc.createImage();
		}
		/*if (highlightImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("highlight_icon.gif");
			highlightImage = desc.createImage();
		}*/
		//Label l = new Label(stopImage);
		
		((Label)getRect().getChildren().get(0)).setIcon(stopImage);
		//Label l = new Label(highlightImage);
		//this.add(l, 0);
	}
	
	Label indicatorLabel;
	
	public void addIndicator(){
		if (highlightImage == null){
			ImageDescriptor desc = SootPlugin.getImageDescriptor("indicator.gif");
			highlightImage = desc.createImage();
		}
		
		indicatorLabel = new Label(highlightImage);
		this.add(indicatorLabel, 0);
	}
	
	public void removeIndicator(){
		if (this.getChildren().get(0).equals(indicatorLabel)){
			this.remove(indicatorLabel);
		}
	}
	
	public void removeStopIcon(){
		((Label)getRect().getChildren().get(0)).setIcon(null);
	}
	
	

	/**
	 * @return
	 */
	public ArrayList getData() {
		return data;
	}

	/**
	 * @param list
	 */
	public void setData(ArrayList list) {
		data = list;
	}


	/**
	 * @param figure
	 */
	public void setRect(RectangleFigure figure) {
		rect = figure;
	}

	
	/**
	 * @return
	 */
	public soot.Unit getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 */
	public void setUnit(soot.Unit unit) {
		this.unit = unit;
	}

}
