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

package ca.mcgill.sable.soot.interaction;

import soot.AbstractTrap;
import soot.toolkits.graph.interaction.*;
import java.util.*;

import org.eclipse.ui.*;

import ca.mcgill.sable.soot.launching.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.callgraph.*;
import ca.mcgill.sable.soot.cfg.*;
import soot.jimple.toolkits.annotation.callgraph.*;
import soot.*;
public class InteractionController  implements IInteractionController, IInteractionListener {

	private ArrayList listeners;
	private Thread sootThread;
	private boolean available;
	private InteractionEvent event;
	private Display display;
	private SootRunner parent;
	private soot.toolkits.graph.DirectedGraph currentGraph;
	private ModelCreator mc;
	private CallGraphGenerator generator;
	
	public InteractionController() {
	}

	
	public void addListener(IInteractionListener listener){
		if (listeners == null){
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}
	
	public void removeListener(IInteractionListener listener){
		if (listeners == null) return;
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	
	
	public void handleEvent(){
		if (getEvent().type() == IInteractionConstants.NEW_ANALYSIS){
			handleNewAnalysisEvent(event.info());
		
		}
		else if (getEvent().type() == IInteractionConstants.NEW_CFG){
			handleCfgEvent(getEvent().info());
			// process and update graph
		}
		else if (getEvent().type() == IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO){
			
			handleBeforeFlowEvent(getEvent().info());
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.NEW_AFTER_ANALYSIS_INFO){
			handleAfterFlowEvent(getEvent().info());
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO_AUTO){
			
			handleBeforeFlowEventAuto(getEvent().info());
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.NEW_AFTER_ANALYSIS_INFO_AUTO){
			handleAfterFlowEventAuto(getEvent().info());
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.DONE){
			// remove controller and listener from soot
			waitForContinue();
		}
		else if (getEvent().type() == IInteractionConstants.STOP_AT_NODE){
			handleStopAtNodeEvent(getEvent().info());
		}
		else if (getEvent().type() == IInteractionConstants.CLEARTO){
			handleClearEvent(getEvent().info());
		}
		else if (getEvent().type() == IInteractionConstants.REPLACE){
			handleReplaceEvent(getEvent().info());
		}
		
		else if (getEvent().type() == IInteractionConstants.CALL_GRAPH_START){
			handleCallGraphStartEvent(getEvent().info());
		}
		else if (getEvent().type() == IInteractionConstants.CALL_GRAPH_NEXT_METHOD){
			handleCallGraphNextMethodEvent(getEvent().info());
		}
		
		else if (getEvent().type() == IInteractionConstants.CALL_GRAPH_PART){
			handleCallGraphPartEvent(getEvent().info());
		}
	}	
	
	private Shell getShell(){
		IWorkbenchWindow window = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		final Shell [] shell = new Shell [1];
		getDisplay().syncExec(new Runnable(){
			public void run(){
				shell[0] = getDisplay().getDefault().getActiveShell();
			}
		});
		return shell[0];
	}
		
	private void handleNewAnalysisEvent(Object info){
		
		SootPlugin.getDefault().setDataKeeper(new DataKeeper(this));
		InteractionHandler.v().setInteractThisAnalysis(true);
		
	}
	
	private void handleCfgEvent(Object info){
	
		soot.toolkits.graph.DirectedGraph cfg = (soot.toolkits.graph.DirectedGraph)info;
		setCurrentGraph(cfg);
		setMc(new ModelCreator());
		getMc().setSootGraph(getCurrentGraph());
		String editorName = "CFG Editor";
				
		if (cfg instanceof soot.toolkits.graph.UnitGraph){
			soot.Body body = ((soot.toolkits.graph.UnitGraph)cfg).getBody();
			editorName = body.getMethod().getDeclaringClass().getName()+"."+body.getMethod().getName();
		}
		mc.setEditorName(editorName);
		
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable(){
			public void run(){
				mc.displayModel();
			}
		});
		InteractionHandler.v().autoCon(false);
		
		waitForContinue();
		
	}
	
	private void waitForContinue(){
		soot.toolkits.graph.interaction.InteractionHandler.v().waitForContinue();
	}
	
	private void handleBeforeFlowEvent(Object info){
		handleBeforeEvent(info);
		waitForContinue();
		
	}
	
	private void handleBeforeEvent(Object info){
		FlowInfo fi = (FlowInfo)info;
		SootPlugin.getDefault().getDataKeeper().addFlowInfo(info);
		
		Iterator it = getCurrentGraph().iterator();
		
		final Shell myShell = getShell();
		final FlowInfo flowBefore = fi;
		final ModelCreator mc = getMc();
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowBefore);
		};
		});
	}
	
	private void handleBeforeFlowEventAuto(Object info){
		handleBeforeEvent(info);
		
	}
	
	private void handleAfterFlowEvent(Object fi){
		handleAfterEvent(fi);
		waitForContinue();
		
	}
	
	private void handleAfterEvent(Object fi){
		final Shell myShell = getShell();
		SootPlugin.getDefault().getDataKeeper().addFlowInfo(fi);
		
		
		final FlowInfo flowAfter = (FlowInfo)fi;
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowAfter);
			};
		});
		
	}
	
	private void handleAfterFlowEventAuto(Object fi){
		handleAfterEvent(fi);
	}
	
	private void handleStopAtNodeEvent(Object info){
		// highlight box being waited on 
		InteractionHandler.v().autoCon(false);
		
		final soot.Unit stopUnit = (soot.Unit)info;
		final ModelCreator mc = getMc();
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.highlightNode(stopUnit);
			};
		});
		// then wait
		waitForContinue();
	}
	
	private void handleClearEvent(Object info){
		final FlowInfo fi = (FlowInfo)info;
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(fi);
			};
		});
	}
	
	private void handleReplaceEvent(Object info){
		final FlowInfo fi = (FlowInfo)info;
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(fi);
			};
		});
	}
	
	
	private void handleCallGraphStartEvent(Object info){
		if (getGenerator() == null){
        	setGenerator(new CallGraphGenerator());
        }
		getGenerator().setInfo((CallGraphInfo)info);
		getGenerator().setController(this);
		final CallGraphGenerator cgg = getGenerator();
		getDisplay().syncExec(new Runnable(){
			public void run(){
				cgg.run();
			}
		});
		waitForContinue();
	}
	
	private void handleCallGraphNextMethodEvent(Object info){
		SootMethod meth = (SootMethod)info;
		InteractionHandler.v().setNextMethod(meth);
		InteractionHandler.v().setInteractionCon();
	}
	
	private void handleCallGraphPartEvent(Object info){
		final CallGraphGenerator cgg = getGenerator();
		final Object cgInfo = info;
		getDisplay().asyncExec(new Runnable(){
			public void run(){
				cgg.addToGraph(cgInfo);
		}
		});
		waitForContinue();
	}
	
	/**
	 * @return
	 */
	public Thread getSootThread() {
		return sootThread;
	}

	/**
	 * @param thread
	 */
	public void setSootThread(Thread thread) {
		sootThread = thread;
	}

	/**
	 * @return
	 */
	public boolean isAvailable() {
		return available;
	}

	

	/**
	 * @return
	 */
	public InteractionEvent getEvent() {
		return event;
	}

	/**
	 * @param event
	 */
	public void setEvent(InteractionEvent event) {
		this.event = event;
	}

	/**
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * @return
	 */
	public SootRunner getParent() {
		return parent;
	}

	/**
	 * @param runner
	 */
	public void setParent(SootRunner runner) {
		parent = runner;
	}

	/**
	 * @return
	 */
	public soot.toolkits.graph.DirectedGraph getCurrentGraph() {
		return currentGraph;
	}

	/**
	 * @return
	 */
	public ArrayList getListeners() {
		return listeners;
	}

	/**
	 * @param graph
	 */
	public void setCurrentGraph(soot.toolkits.graph.DirectedGraph graph) {
		currentGraph = graph;
	}

	/**
	 * @param list
	 */
	public void setListeners(ArrayList list) {
		listeners = list;
	}

	/**
	 * @return
	 */
	public ModelCreator getMc() {
		return mc;
	}

	/**
	 * @param creator
	 */
	public void setMc(ModelCreator creator) {
		mc = creator;
	}

	/**
	 * @return
	 */
	public CallGraphGenerator getGenerator() {
		return generator;
	}

	/**
	 * @param generator
	 */
	public void setGenerator(CallGraphGenerator generator) {
		this.generator = generator;
	}

}
