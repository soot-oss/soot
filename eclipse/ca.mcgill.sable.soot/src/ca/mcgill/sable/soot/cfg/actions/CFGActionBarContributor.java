/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg.actions;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.ui.IWorkbenchActionConstants;
import ca.mcgill.sable.soot.interaction.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGActionBarContributor extends ActionBarContributor {

	/**
	 * 
	 */
	public CFGActionBarContributor() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		// TODO Auto-generated method stub
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		// TODO Auto-generated method stub

	}
	
	/*public void contributeToToolBar(IToolBarManager toolBarManager){
		super.contributeToToolBar(toolBarManager);
		
		toolBarManager.add(new Separator());
		toolBarManager.add(new InteractionContinuer());	
	}*/
	
	public void contributeToMenu(IMenuManager menuManager){
		super.contributeToMenu(menuManager);
		
		MenuManager viewMenu = new MenuManager("View");
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		
		menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	}
	

}
