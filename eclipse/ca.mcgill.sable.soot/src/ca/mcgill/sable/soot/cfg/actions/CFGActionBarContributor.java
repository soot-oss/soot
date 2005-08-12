/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */


package ca.mcgill.sable.soot.cfg.actions;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import org.eclipse.ui.IWorkbenchActionConstants;
import ca.mcgill.sable.soot.interaction.*;
import org.eclipse.ui.*;


public class CFGActionBarContributor extends ActionBarContributor {

	
	public CFGActionBarContributor() {
		super();
	}
	
	private StepForwardAction stepForward;
	private StepBackwardAction stepBackward;
	private FinishMethodAction finishMethod;
	private NextMethodAction nextMethod;
	private FlowSelectAction flowSelect;
	private StopInteractionAction stopInteraction;


	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		flowSelect = new FlowSelectAction(null);
		addAction(flowSelect);
		
		stepBackward = new StepBackwardAction(null);
		addAction(stepBackward);
		stepForward = new StepForwardAction(null);
		addAction(stepForward);
		
		finishMethod = new FinishMethodAction(null);
		addAction(finishMethod);
		nextMethod = new NextMethodAction(null);
		addAction(nextMethod);
		
		stopInteraction = new StopInteractionAction(null);
		addAction(stopInteraction);
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
	}
	
	//  this is for zoom toolbar buttons
	public void contributeToToolBar(IToolBarManager toolBarManager){
		super.contributeToToolBar(toolBarManager);
		
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(StepBackwardAction.STEP_BACKWARD));
		
		toolBarManager.add(getAction(StepForwardAction.STEP_FORWARD));
		toolBarManager.add(getAction(FinishMethodAction.FINISH_METHOD));	
		toolBarManager.add(getAction(NextMethodAction.NEXT_METHOD));	

		toolBarManager.add(getAction(StopInteractionAction.STOP_INTERACTION));
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
		stopInteraction.setEditorPart(editor);
	}

}
