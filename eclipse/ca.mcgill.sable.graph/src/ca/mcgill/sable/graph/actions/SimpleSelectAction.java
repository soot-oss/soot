/*
 * Created on Mar 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.graph.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleSelectAction extends SelectionAction {

	
	private final static String SIMPLE_SELECT = "simple select";
	private IWorkbenchPart part;
	
	/**
	 * @param part
	 */
	public SimpleSelectAction(IWorkbenchPart part) {
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
	
	public void run(){
		System.out.println("simple select: "+getPart().getClass());
	}

	/**
	 * @return
	 */
	public IWorkbenchPart getPart() {
		return part;
	}

	/**
	 * @param part
	 */
	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}
	
	protected void init() { 
		super.init(); 
		setId( SIMPLE_SELECT );
	}

}
