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
import org.eclipse.ui.*;

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
	
	private StepForwardAction stepForward;
	private StepBackwardAction stepBackward;
	private FinishMethodAction finishMethod;
	private NextMethodAction nextMethod;
	private FlowSelectAction flowSelect;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		// TODO Auto-generated method stub
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		flowSelect = new FlowSelectAction(null);
		addAction(flowSelect);
		
		stepForward = new StepForwardAction(null);
		addAction(stepForward);
		stepBackward = new StepBackwardAction(null);
		addAction(stepBackward);
		finishMethod = new FinishMethodAction(null);
		addAction(finishMethod);
		nextMethod = new NextMethodAction(null);
		addAction(nextMethod);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		// TODO Auto-generated method stub

	}
	
	//  this is for zoom toolbar buttons
	public void contributeToToolBar(IToolBarManager toolBarManager){
		super.contributeToToolBar(toolBarManager);
		
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(StepForwardAction.STEP_FORWARD));
		toolBarManager.add(getAction(StepBackwardAction.STEP_BACKWARD));
		toolBarManager.add(getAction(FinishMethodAction.FINISH_METHOD));	
		toolBarManager.add(getAction(NextMethodAction.NEXT_METHOD));	

	}
	
	public void contributeToMenu(IMenuManager menuManager){
		super.contributeToMenu(menuManager);
		
		MenuManager viewMenu = new MenuManager("View");
		viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
		viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
		
		menuManager.insertAfter(IWorkbenchActionConstants.M_EDIT, viewMenu);
	}
	
	public void setActiveEditor(IEditorPart editor) { 
		super.setActiveEditor(editor); 
		stepForward.setEditorPart( editor );
		stepBackward.setEditorPart(editor);
		finishMethod.setEditorPart(editor);
		nextMethod.setEditorPart(editor);
		flowSelect.setEditorPart(editor);
	}

}
