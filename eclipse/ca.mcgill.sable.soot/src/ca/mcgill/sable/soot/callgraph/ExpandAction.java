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
public class ExpandAction extends SimpleSelectAction {

	public static final String EXPAND = "expand action";
	
	/**
	 * 
	 */
	public ExpandAction(IWorkbenchPart part) {
		super(part);
		// TODO Auto-generated constructor stub
	}

	

	
	protected void init(){
		super.init();
		setId(EXPAND);
		setText("Expand");
	}

	public void run(){
		System.out.println("running expand");
		System.out.println("sel: "+getSelection().getClass());
		//System.out.println("sel: "+getSelectedObjects().get(0));
		//if (getSelection() instanceof CallGraphNodeEditPart){
			CallGraphNodeEditPart cgPart = (CallGraphNodeEditPart)getSelectedObjects().get(0);
			cgPart.expandGraph();
		//}
	}
	
	public boolean calculateEnabled(){
		return true;
	}
}
