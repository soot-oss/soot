/*
 * Created on Mar 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;

import ca.mcgill.sable.graph.editparts.PartFactory;
import org.eclipse.gef.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CallGraphPartFactory extends PartFactory {

	/**
	 * 
	 */
	public CallGraphPartFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart arg0, Object arg1) {
		// TODO Auto-generated method stub
		EditPart part = super.createEditPart(arg0, arg1);
		
		if (arg1 instanceof CallGraphNode){
			part = new CallGraphNodeEditPart();
		}
		part.setModel(arg1);
		return part;
	}
}
