/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg;

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.actions.*;
import ca.mcgill.sable.graph.actions.*;
import ca.mcgill.sable.soot.cfg.editParts.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StopAction extends SimpleSelectAction {

	public static final String STOP = "mark stop action";
	
	/**
	 * 
	 */
	public StopAction(IWorkbenchPart part) {
		super(part);
		// TODO Auto-generated constructor stub
	}

	

	
	protected void init(){
		super.init();
		setId(STOP);
		setText("Mark Stop");
	}

	public void run(){
		System.out.println("running expand");
		System.out.println("sel: "+getSelection().getClass());
		System.out.println("sel: "+getSelectedObjects().get(0)+" class: "+getSelectedObjects().get(0).getClass());
		//if (getSelection() instanceof CallGraphNodeEditPart){
			NodeDataEditPart cfgPart = (NodeDataEditPart)getSelectedObjects().get(0);
			cfgPart.markStop();
		//}
	}
	
	public boolean calculateEnabled(){
		return true;
	}
}
