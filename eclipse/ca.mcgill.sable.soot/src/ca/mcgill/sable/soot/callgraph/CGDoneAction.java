/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jface.resource.*;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;

import org.eclipse.swt.printing.*;

import soot.toolkits.graph.interaction.InteractionHandler;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CGDoneAction implements IEditorActionDelegate {

	public static final String DONE = "done"; 
	/**
	 * @param editor
	 */
	public CGDoneAction() {
		//super(editor);
		//setToolTipText("Done");
		
		// TODO Auto-generated constructor stub
	}
	
	public void setActiveEditor(IAction action, IEditorPart editor){
		
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
	public void run(IAction action){
		System.out.println("done pressed");
		System.out.println("printer data: "+Printer.getDefaultPrinterData());
		InteractionHandler.v().cgDone(true);
		InteractionHandler.v().setInteractionCon();
	}		
	
	public void selectionChanged(IAction action, ISelection sel){
	}
	/*public void setEditorPart(IEditorPart part){
		super.setEditorPart(part);
	}
	
	protected void init() { 
		super.init(); 
		setId( DONE );
	}*/
}
