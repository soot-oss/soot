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
public class NextMethodAction extends EditorPartAction {

	public static final String NEXT_METHOD = "next method"; 
	/**
	 * @param editor
	 */
	public NextMethodAction(IEditorPart editor) {
		super(editor);
		setImageDescriptor(SootPlugin.getImageDescriptor("next_method.gif"));
		setToolTipText("Next Method");
		
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
	 * continues to the next method if the current 
	 * one is finished
	 */
	public void run(){
		if (InteractionHandler.v().doneCurrent()){
			InteractionHandler.v().setInteractionCon();
		}
	}
	
	public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( NEXT_METHOD );
	}
}
