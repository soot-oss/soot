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
public class FinishMethodAction extends EditorPartAction {

	public static final String FINISH_METHOD = "finish method"; 
	/**
	 * @param editor
	 */
	public FinishMethodAction(IEditorPart editor) {
		super(editor);
		setImageDescriptor(SootPlugin.getImageDescriptor("finish_method.gif"));
		setToolTipText("Finish Method");
		
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
	 * finishes displaying the flow sets for the current
	 * method 
	 */
	public void run(){
		if (SootPlugin.getDefault().getDataKeeper().inMiddle()){
			SootPlugin.getDefault().getDataKeeper().stepForwardAuto();
		}
		InteractionHandler.v().autoCon(true);
		if (!InteractionHandler.v().doneCurrent()){
			InteractionHandler.v().setInteractionCon();
		}
		
	}
	
	public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( FINISH_METHOD );
	}
}
