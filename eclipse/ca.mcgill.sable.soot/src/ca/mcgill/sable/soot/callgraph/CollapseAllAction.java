/*
 * Created on Jun 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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

import org.eclipse.ui.IEditorPart;

import soot.toolkits.graph.interaction.InteractionHandler;

import ca.mcgill.sable.soot.SootPlugin;

import org.eclipse.ui.*;

/**
 * @author jlhotak
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CollapseAllAction implements IEditorActionDelegate {

	public static final String COLLAPSE_ALL = "collapse all"; 
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	public CollapseAllAction() {
		///setImageDescriptor(SootPlugin.getImageDescriptor("collapse_all.gif"));
		//setToolTipText("CollapseAll");
		
		// TODO Auto-generated constructor stub
	}
	
	public void run(IAction action){
		System.out.println("running collapse all");
		InteractionHandler.v().setCgReset(true);
		InteractionHandler.v().setInteractionCon();
		
	}
	
	public void setActiveEditor(IAction action, IEditorPart part){
		//super.setEditorPart(part);
	}
	
	protected void init() { 
		//super.init(); 
		//setId( COLLAPSE_ALL );
	}
	
	public void selectionChanged(IAction action, ISelection sel){
	}

}
