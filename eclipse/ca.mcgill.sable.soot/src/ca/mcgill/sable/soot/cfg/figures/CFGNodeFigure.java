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
/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGNodeFigure extends Figure {

	private Panel nodeFigure;//RectangleFigure rect;
	//private RectangleFigure rect;
	//private Label beforeLabel;
	//private Label afterLabel;
	
	//private int width;
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
		
		//setRect(new RectangleFigure());
		//setNodeFigure(new Panel());
		//Font f = new Font(null, "Arial", 8, SWT.NORMAL);
		
		//setBeforeLabel(new Label());
		//getBeforeLabel().setFont(f);
		//this.add(getRect());
		
		//setAfterLabel(new Label());
		//getAfterLabel().setFont(f);
		
		//getNodeFigure().add(getBefore());
		//getNodeFigure().add(getData());
		//getNodeFigure().add(getAfter());
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
	
		this.setLayoutManager(layout);
		//getNodeFigure().setLayoutManager(layout);
		layout.setStretchMinorAxis(false);
	
	}
	
	public void resetColors(){
	//	getBeforeLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,0,0)));	
	//	getAfterLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,0,0)));	
		
	}
	
	private int getLineBreak(String text){
		return text.lastIndexOf(" ", 50);
	}
	
	/*public void addBeforeFigure(){
		if (getBefore() != null){
			int height = this.getSize().height;
			
			/* 
			// testing for splitting data into multiple lines
			String temp = getBefore();
			while (temp.length() > 50) {
				int lnBreak = getLineBreak(temp);
				String part = temp.substring(0, lnBreak);
				temp = temp.substring(lnBreak);
				System.out.println("part: "+part);			
			}
			*/
		/*	getBeforeLabel().setText(getBefore());
			getBeforeLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,153,0)));
			if (getBefore().length()*7 > getWidth()){
				setWidth(getBefore().length()*7);
			}
			if (!isHasBefore()){
				
				this.add(getBeforeLabel(),0);
				setHasBefore(true);
				height = height + getBeforeLabel().getSize().height/2;
			}
			
			this.setSize(getWidth()+10, height);
		}
	}
	
	/*public void addAfterFigure(){
		if (getAfter() != null){
			int height = this.getSize().height;//getNodeFigure().getBounds().height;
			getAfterLabel().setText(getAfter());
			getAfterLabel().setForegroundColor(SootPlugin.getDefault().getColorManager().getColor(new RGB(0,153,0)));
			
			if (getAfter().length()*7 > getWidth()){
				setWidth(getAfter().length()*7);
			}
			if (!isHasAfter()){
				this.add(getAfterLabel());
				setHasAfter(true);
				height = height + getAfterLabel().getBounds().height/2;
			}
			this.setSize(getWidth()+10, height);
		}
	}*/
	
	



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

}
