/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.editpolicies;

import org.eclipse.gef.editpolicies.*;
import org.eclipse.draw2d.*;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.*;
import ca.mcgill.sable.graph.editparts.*;


/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleNodeSelectPolicy extends SelectionEditPolicy {

	/**
	 * 
	 */
	public SimpleNodeSelectPolicy() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#hideSelection()
	 */
	protected void hideSelection() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionEditPolicy#showSelection()
	 */
	protected void showSelection() {
		// TODO Auto-generated method stub
		((SimpleNodeEditPart)getHost()).switchToComplex();
		//getHost().setFocus(false);
		((SimpleNodeEditPart)getHost()).getViewer().deselect(getHost());		//this.setSelectedState(EditPart.SELECTED_NONE);
	}
	
	
	
	public void showTargetFeedback(Request request){
		//super.showTargetFeedback(request);
		/*System.out.println("req type: "+request.getType());
		System.out.println("req class: "+request.getClass());
		System.out.println("part sel state: "+getHost().getSelected());
		if (getHost().getSelected() == EditPart.SELECTED){
			System.out.println("edit part is selected");
		}
		if (request instanceof SelectionRequest){
		//if (request.getType().equals(RequestConstants.REQ_SELECTION_HOVER)){
		//}
		//else if (request.getType().equals(RequestConstants.REQ_SELECTION)){
			SelectionRequest req = (SelectionRequest)request;
			System.out.println("any button pressed: "+req.isAnyMouseButtonPressed());
			System.out.println("left pressed: "+req.isLeftMouseButtonPressed());
			System.out.println(getHost().getClass());
			if (getHost().getSelected() == EditPart.SELECTED_PRIMARY){
				((SimpleNodeEditPart)getHost()).switchToComplex();
			}
		}*/
		
		
	}
	
	public boolean understandsRequest(Request req){
		if (req.getType() == RequestConstants.REQ_SELECTION){
			return true;
		}
		else {
			return false;
		}
	}
	

}
