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

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeFigure extends Figure {

	private Panel nodeFigure;//RectangleFigure rect;
	private RectangleFigure rect;
	private Label beforeLabel;
	private Label afterLabel;
	
	private int width;
	private ArrayList data;
	private String before;
	private String after;
	
	private boolean hasBefore;
	private boolean hasAfter;
	
	
	
	/**
	 * 
	 */
	public CFGNodeFigure() {
		super();
		setRect(new RectangleFigure());//RectangleFigure());
		setNodeFigure(new Panel());
		//Font f = new Font(null, "Arial", 8, SWT.NORMAL);
		//Label test = new Label("this is a very long label for testing purposes");
		//test.setFont(f);
		//this.add(test);
		//this.add(getNodeFigure());
		
		setBeforeLabel(new Label());
		//getNodeFigure().add(getBeforeLabel());
		
		//getNodeFigure().add(getRect());
		this.add(getRect());
		
		setAfterLabel(new Label());
		//getNodeFigure().add(getAfterLabel());
		
		ToolbarLayout layout = new ToolbarLayout();
		//XYLayout layout = new XYLayout();
		//StackLayout layout = new StackLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	
		this.setLayoutManager(layout);
		getRect().setLayoutManager(layout);
		//getNodeFigure().setLayoutManager(layout);
		//test.setSize(test.getPreferredSize());
		//this.setSize(getNodeFigure().getPreferredSize());
		//this.setLayoutManager(layout);
		// TODO Auto-generated constructor stub
		layout.setStretchMinorAxis(false);
	}
	
	public void resetColors(){
		getBeforeLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,0,0)));	
		getAfterLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,0,0)));	
		
	}
	
	public void addBeforeFigure(){
		if (getBefore() != null){
			int height = this.getSize().height;
			//System.out.println("before this height: "+height);
			//String beforeText = getBefore();
			//if ((beforeText == null) || (beforeText == "")){
			//	beforeText = 
			//}
			getBeforeLabel().setText(getBefore());
			getBeforeLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,153,0)));
			//Label bl = new Label(getBefore());
			//System.out.println("before len: "+getBefore().length()*7+" width: "+getWidth());
			if (getBefore().length()*7 > getWidth()){
			//	System.out.println("changing width");
				setWidth(getBefore().length()*7);
			}
			//getBeforeLabel().setPreferredSize(-1,-1);
			//bl.getBounds().setSize(getWidth(),
				//bl.getBounds().height);
			//getBeforeLabel().getInsets().top = 2;
			//getBeforeLabel().getInsets().bottom = 2;
			//System.out.println("bl: "+bl);
			//System.out.println("rect: "+getRect());
			
			//getNodeFigure().add(bl, 0);
			//this.add(bl,0);
			//System.out.println("width :"+getWidth());
			if (!isHasBefore()){
				//getNodeFigure().add(getBeforeLabel(), 0);
				
				this.add(getBeforeLabel(),0);
				setHasBefore(true);
				height = height + getBeforeLabel().getSize().height/2;
				//System.out.println("add before height: "+height);
			}
			//getBeforeLabel().setSize(getBeforeLabel().getPreferredSize());
			//System.out.println("height: "+height);
			//this.setSize(getWidth(), height);
			//getNodeFigure().setSize(getWidth(), height);
			//this.getLayoutManager().layout(getNodeFigure());
			//this.setSize(getNodeFigure().getPreferredSize());
			//getNodeFigure().setSize(getWidth(), height);
		
			this.setSize(getWidth()+10, height);
		}
	}
	
	public void addAfterFigure(){
		if (getAfter() != null){
			int height = this.getSize().height;//getNodeFigure().getBounds().height;
			//System.out.println("after heigth: "+height);
			getAfterLabel().setText(getAfter());
			getAfterLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,153,0)));
			
			//Label al = new Label(getBefore());
			if (getAfter().length()*7 > getWidth()){
				setWidth(getAfter().length()*7);
			}
			//getAfterLabel().setPreferredSize(-1,-1);
			//getAfterLabel().setSize(getWidth(),
			//getAfterLabel().getBounds().height);
			//getAfterLabel().getInsets().top = 2;
			//getAfterLabel().getInsets().bottom = 2;
			//System.out.println("bl: "+al);
			//System.out.println("rect: "+getRect());
			
			//getNodeFigure().add(al);
			//getRect().getLayoutManager().layout(getRect());
			//this.add(al);
			if (!isHasAfter()){
				this.add(getAfterLabel());
				setHasAfter(true);
				height = height + getAfterLabel().getBounds().height/2;
				//System.out.println("add after height: "+height);
			}
			//this.setSize(getWidth(), height+2);
			//getNodeFigure().setSize(getWidth(), height+2);
			//this.getLayoutManager().layout(getNodeFigure());
			//getAfterLabel().setSize(getWidth(), getAfterLabel().getBounds().height);
			this.setSize(getWidth()+10, height);
		}
	}
	
	public void updateFigure(){
		if (getData() == null) return;
		//this.getBounds().setSize(getWidth(), this.getBounds().height);
		//getRect().getBounds().setSize(getWidth(), this.getBounds().height);
		/*if (getBefore() != null){
			Label bl = new Label(getBefore());
			getRect().add(bl);
		}*/
		int height = 0;
		Iterator it = getData().iterator();
		while (it.hasNext()){
			String next = (String)it.next();
			//System.out.println("adding label: "+next);
			Label l = new Label(next);
			//System.out.println("label preferred size: "+l.getPreferredSize());
			//System.out.println("label text dim: "+l.getTextBounds().getSize());
			//System.out.println("font: "+l.getFont());
			//FigureUtilities.getTextWidth(next, l.getFont());
			//l.setPreferredSize(getWidth(), l.getBounds().height);
			//l.setSize(getWidth(),
			//	l.getBounds().height);
			//l.setPreferredSize(-1,-1);
			l.getInsets().top = 2;
			l.getInsets().bottom = 2;
			l.getInsets().right = 2;
			l.getInsets().left = 2;
			height = height + l.getSize().height/2;
			//System.out.println("height: "+height);
			//l.setSize(getWidth(), l.getSize().height);
			//this.add(l);
			getRect().add(l);
			//getRect().getLayoutManager().layout(l);
		
			
		}
		//getRect().setSize(getRect().getPreferredSize());
		//getNodeFigure().getLayoutManager().layout(getRect());
		//this.getLayoutManager().layout(getNodeFigure());
		
		//getNodeFigure().setSize(getNodeFigure().getPreferredSize());
		//this.setSize(getNodeFigure().getPreferredSize());
		
		//getRect().setPreferredSize(-1,-1);
		//getNodeFigure().setPreferredSize(-1,-1);
		////this.setPreferredSize(getWidth(), height);
		//this.setSize(getWidth(), height);
		////getRect().setPreferredSize(getWidth(), height);
		
		getRect().setSize(/*getRect().getSize().width*/getWidth()+10, height+6);
		this.setSize(getWidth()+10, height+6);
		
		//getNodeFigure().setSize(getWidth(), height+2);
		/*if (getAfter() != null){
			Label al = new Label(getAfter());
			getRect().add(al);
		}*/
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

	/**
	 * @return
	 */
	public String getAfter() {
		return after;
	}

	/**
	 * @return
	 */
	public String getBefore() {
		return before;
	}

	/**
	 * @param string
	 */
	public void setAfter(String string) {
		after = string;
	}

	/**
	 * @param string
	 */
	public void setBefore(String string) {
		before = string;
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
	public void setRect(RectangleFigure figure) {
		rect = figure;
	}

	/**
	 * @return
	 */
	public Label getAfterLabel() {
		return afterLabel;
	}

	/**
	 * @return
	 */
	public Label getBeforeLabel() {
		return beforeLabel;
	}

	/**
	 * @param label
	 */
	public void setAfterLabel(Label label) {
		afterLabel = label;
	}

	/**
	 * @param label
	 */
	public void setBeforeLabel(Label label) {
		beforeLabel = label;
	}

	/**
	 * @return
	 */
	public boolean isHasAfter() {
		return hasAfter;
	}

	/**
	 * @return
	 */
	public boolean isHasBefore() {
		return hasBefore;
	}

	/**
	 * @param b
	 */
	public void setHasAfter(boolean b) {
		hasAfter = b;
	}

	/**
	 * @param b
	 */
	public void setHasBefore(boolean b) {
		hasBefore = b;
	}

}
