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

	public void switchToComplex(){
		((CallGraphNode)getNode()).getGenerator().expandGraph((CallGraphNode)getNode());
	}
}
