/*
 * Created on Feb 26, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGPartialFlowFigure extends Figure {

	Panel background;
	/**
	 * 
	 */
	public CFGPartialFlowFigure() {
		super();
		//background = new Panel();
		//this.add(background);
		FlowLayout layout = new FlowLayout(true);
		layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		//layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		//layout.setStretchMinorAxis(true);
		this.setLayoutManager(layout);
	}

}
