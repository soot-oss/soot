/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.figures;

import org.eclipse.draw2d.*;
import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeFigure extends Figure {

	private RectangleFigure rect;
	private int width;
	private ArrayList data;
	
	/**
	 * 
	 */
	public CFGNodeFigure() {
		super();
		setRect(new RectangleFigure());
		
		this.add(getRect());
		
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		
		getRect().setLayoutManager(layout);
		// TODO Auto-generated constructor stub
	}
	
	public void updateFigure(){
		if (getData() == null) return;
		this.getBounds().setSize(getWidth(), this.getBounds().height);
		getRect().getBounds().setSize(getWidth(), this.getBounds().height);
		Iterator it = getData().iterator();
		while (it.hasNext()){
			String next = (String)it.next();
			Label l = new Label(next);
			l.getBounds().setSize(getWidth(), l.getBounds().height);
			l.getInsets().top = 2;
			l.getInsets().bottom = 2;
			getRect().add(l);
		}
	}

	
	/**
	 * @return
	 */
	public RectangleFigure getRect() {
		return rect;
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
	public ArrayList getData() {
		return data;
	}

	/**
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param list
	 */
	public void setData(ArrayList list) {
		data = list;
	}

	/**
	 * @param i
	 */
	public void setWidth(int i) {
		width = i;
	}

}
