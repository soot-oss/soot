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
		
	
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	
		this.setLayoutManager(layout);
		
		//XYLayout xylayout = new XYLayout();
		getRect().setLayoutManager(layout);
		//layout.setStretchMinorAxis(false);
		//this.setLayoutManager(layout);
		
		//layout.calculateMinimumSize(getRect(), -1, -1);	
	}
	

	
	public void updateFigure(){
		if (getData() == null) return;
		
		int height = 0;
		int width = 0;
		Iterator it = getData().iterator();
		while (it.hasNext()){
			String next = (String)it.next();
			
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

	
}
