/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.actions.*;
import ca.mcgill.sable.graph.actions.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CollapseAction extends SimpleSelectAction {

	public static final String COLLAPSE = "collapse action";
	
	/**
	 * 
	 */
	public CollapseAction(IWorkbenchPart part) {
		super(part);
		// TODO Auto-generated constructor stub
	}

	

	
	protected void init(){
		super.init();
		setId(COLLAPSE);
		setText("Collapse");
	}

	public void run(){
		System.out.println("running collapse");
		System.out.println("sel: "+getSelection());
		//System.out.println("sel: "+getSelectedObjects().get(0));
		//if (getSelection() instanceof CallGraphNodeEditPart){
			CallGraphNodeEditPart cgPart = (CallGraphNodeEditPart)getSelectedObjects().get(0);
			cgPart.collapseGraph();
		//}
	}
	
	public boolean calculateEnabled(){
		System.out.println("part in calenab collapse");
		return true;
	}
}
