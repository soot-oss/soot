/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.gef.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FlowSelectAction extends SelectionAction {

	
	public static final String FLOW_SELECT = "flow select"; 
	
	/**
	 * @param part
	 */
	public FlowSelectAction(IWorkbenchPart part) {
		super(part);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( FLOW_SELECT );
	}
	
	public void run(){
		try {
			EditPart part = (EditPart)getSelectedObjects().get(0);
			System.out.println("part selected: "+part.getClass());
		}
		catch(ClassCastException e1){
		}
		catch(IndexOutOfBoundsException e2){
		}
	}
}
