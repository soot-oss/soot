/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import ca.mcgill.sable.graph.model.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PartFactory implements EditPartFactory {

	/**
	 * 
	 */
	public PartFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart arg0, Object arg1) {
		// TODO Auto-generated method stub
		EditPart part = null;
		if (arg1 instanceof Graph){
			part = new GraphEditPart();
		}
		else if (arg1 instanceof SimpleNode){
			part = new SimpleNodeEditPart();
		}
		else if (arg1 instanceof Edge){
			part = new EdgeEditPart();	
		}
		else if (arg1 instanceof ComplexNode){
			part = new ComplexNodeEditPart();
		}
		
		part.setModel(arg1);
		return part;
	}

}
