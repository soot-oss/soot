/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
import ca.mcgill.sable.soot.cfg.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InteractionController /*extends Thread*/ implements IInteractionController, IInteractionListener {

	private ArrayList listeners;
	private Thread sootThread;
	private boolean available;
	private InteractionEvent event;
	private Display display;
	private SootRunner parent;
	private soot.toolkits.graph.DirectedGraph currentGraph;
	private ModelCreator mc;
	
	/**
	 * 
	 */
	public InteractionController() {
		//super();
		// TODO Auto-generated constructor stub
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
		else if (getEvent().type() == IInteractionConstants.DONE){
			// remove controller and listener from soot
		}
		else if (getEvent().type() == IInteractionConstants.CLEARTO){
			handleClearEvent(getEvent().info());
		}
		else if (getEvent().type() == IInteractionConstants.REPLACE){
			handleReplaceEvent(getEvent().info());
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
		
		final Shell myShell = getShell();
		
		final boolean [] result = new boolean[1];
		final String analysisName = info.toString();
		getDisplay().syncExec(new Runnable() {
			public void run(){
		MessageDialog msgDialog = new MessageDialog(myShell, "Interaction Question",  null,"Do you want to interact with analysis: "+analysisName+" ?",0, new String []{"Yes", "No"}, 0);
		msgDialog.open();
		boolean res = msgDialog.getReturnCode() == 0 ? true: false;
		result[0] = res;
			};
		});
		InteractionHandler.v().setInteractThisAnalysis(result[0]);
		
	}
	
	private void handleCfgEvent(Object info){
	
		soot.toolkits.graph.DirectedGraph cfg = (soot.toolkits.graph.DirectedGraph)info;
		setCurrentGraph(cfg);
		setMc(new ModelCreator());
		getMc().setSootGraph(getCurrentGraph());
		String editorName = "CFG Editor";
				
		if (cfg instanceof soot.toolkits.graph.UnitGraph){
			soot.Body body = ((soot.toolkits.graph.UnitGraph)cfg).getBody();
			System.out.println("method: "+body.getMethod().getName()+" class: "+body.getMethod().getDeclaringClass().getName());
			editorName = body.getMethod().getDeclaringClass().getName()+"::"+body.getMethod().getName();
		}
		mc.setEditorName(editorName);
		
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable(){
			public void run(){
				mc.displayModel();
			}
		});
		
		waitForContinue();
		
	}
	
	private void waitForContinue(){
		soot.toolkits.graph.interaction.InteractionHandler.v().waitForContinue();
	}
	
	private void handleBeforeFlowEvent(Object info){
		FlowInfo fi = (FlowInfo)info;
		SootPlugin.getDefault().getDataKeeper().addFlowInfo(info);
		
		Iterator it = getCurrentGraph().iterator();
		
		final Shell myShell = getShell();
		//final String flowInfo = fi.info().toString();
		final FlowInfo flowBefore = fi;
		final ModelCreator mc = getMc();
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowBefore);
		};
		});
		waitForContinue();
		
	}
	
	private void handleAfterFlowEvent(Object fi){
		final Shell myShell = getShell();
		//final String flowInfo = ((FlowInfo)fi).info().toString();
		SootPlugin.getDefault().getDataKeeper().addFlowInfo(fi);
		
		
		final FlowInfo flowAfter = (FlowInfo)fi;
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowAfter);
			};
		});
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

}
