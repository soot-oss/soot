/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.actions;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import ca.mcgill.sable.soot.*;
import soot.toolkits.graph.interaction.*;
import org.eclipse.jface.resource.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StepForwardAction extends EditorPartAction {

	public static final String STEP_FORWARD = "step forward"; 
	/**
	 * @param editor
	 */
	public StepForwardAction(IEditorPart editor) {
		super(editor);
		setImageDescriptor(SootPlugin.getImageDescriptor("resume_co.gif"));
		setToolTipText("Step Forward");
		
		// TODO Auto-generated constructor stub
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 * steps forward through flowsets unless method 
	 * is finished
	 */
	public void run(){
		System.out.println("is done: "+InteractionHandler.v().doneCurrent());
		if (SootPlugin.getDefault().getDataKeeper().inMiddle()){
			SootPlugin.getDefault().getDataKeeper().stepForward();
		}
		else {
			if (!InteractionHandler.v().doneCurrent()){
				InteractionHandler.v().setInteractionCon();//true);
			}
		}
	}
	
	public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( STEP_FORWARD );
	}
}
