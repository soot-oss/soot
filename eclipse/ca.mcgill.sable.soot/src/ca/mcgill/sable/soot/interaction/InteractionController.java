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

	public void fireInteractionEvent(InteractionEvent event){
		/*if (listeners == null) return;
		Iterator it = listeners.iterator();
		while (it.hasNext()){
			((IInteractionListener)it.next()).handleEvent(event);
		}*/
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
	
	/*public void run(){
		while (true){
			handleEvent();
		}
	}*/
	
	public /*synchronized*/ void handleEvent(){
		//while (!isAvailable()){
			/*try {
				System.out.println("eclipse wait");
				wait();
			}
			catch(InterruptedException e){
			}*/
		//}
		//setAvailable(false);
		//System.out.println("getEvent: "+getEvent());
		//System.out.println("received event"); 
		if (getEvent().type() == IInteractionConstants.NEW_ANALYSIS){
			handleNewAnalysisEvent(event.info());
			//System.out.println("got new analysis event from soot");
			//System.out.println("current thread in eclipse :"+Thread.currentThread());
			//InteractionHandler.v().setInteractThisAnalysis(true);
			//InteractionHandler.v().setInteractionCon(true);
			//((SootThread)getSootThread()).go();
			//getSootThread().notify();
		}
		else if (getEvent().type() == IInteractionConstants.NEW_CFG){
			handleCfgEvent(getEvent().info());
			//System.out.println("got cfg event");
			//System.out.println("cfg: "+getEvent().info());
			//InteractionHandler.v().setInteractionCon(true);
			
			// process and update graph
		}
		else if (getEvent().type() == IInteractionConstants.NEW_BEFORE_ANALYSIS_INFO){
			
			handleBeforeFlowEvent(getEvent().info());
			//System.out.println("got new analysis info event");
			//System.out.println("analysis info: "+getEvent().info());
			//InteractionHandler.v().setInteractionCon(true);
			
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.NEW_AFTER_ANALYSIS_INFO){
			handleAfterFlowEvent(getEvent().info());
			//System.out.println("got new analysis info event");
			//System.out.println("analysis info: "+getEvent().info());
			//InteractionHandler.v().setInteractionCon(true);
			
			// process and update graph ui
		}
		else if (getEvent().type() == IInteractionConstants.DONE){
			// remove controller and listener from soot
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
		//boolean result = getParent().handleNewAnalysis(info.toString());
		//System.out.println("result of question: "+result[0]);
		InteractionHandler.v().setInteractThisAnalysis(result[0]);
		//InteractionHandler.v().setInteractionCon();//true);
		
	}
	
	private void handleCfgEvent(Object info){
	
		//final Shell myShell = getShell();
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
		//System.out.println("ed name: "+editorName);
		mc.setEditorName(editorName);
		
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable(){
			public void run(){
				/*ModelCreator mc = new ModelCreator();
				mc.setSootGraph((soot.toolkits.graph.DirectedGraph)cfg);
				String editorName = "CFG Editor";
				System.out.println("running cfg event");
				if (cfg instanceof soot.toolkits.graph.UnitGraph){
					soot.Body body = ((soot.toolkits.graph.UnitGraph)cfg).getBody();
					System.out.println("method: "+body.getMethod().getName()+" class: "+body.getMethod().getDeclaringClass().getName());
					editorName = body.getMethod().getDeclaringClass().getName()+"::"+body.getMethod().getName();
				}
				System.out.println("ed name: "+editorName);
				mc.setEditorName(editorName);*/
				mc.displayModel();
			}
		});
		
		waitForContinue();
		//InteractionHandler.v().setInteractionCon(true);
		
	}
	
	private void waitForContinue(){
		soot.toolkits.graph.interaction.InteractionHandler.v().waitForContinue();
	}
	
	private void handleBeforeFlowEvent(Object info){
		FlowInfo fi = (FlowInfo)info;
		Iterator it = getCurrentGraph().iterator();
		/*while (it.hasNext()){
			if (it.next().equals(fi.unit())){
				System.out.println("real unit match found");
			}
			if (it.next().toString().equals(fi.unit().toString())){
				System.out.println("info match found");
			}
			
		}*/
		//getMc().updateNode(fi);
		final Shell myShell = getShell();
		final String flowInfo = fi.info().toString();
		final FlowInfo flowBefore = fi;
		final ModelCreator mc = getMc();
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowBefore);
		//MessageDialog msgDialog = new MessageDialog(myShell, "Before Flow Info",  null, flowInfo,0, new String []{"OK", "Cancel"}, 0);
		//msgDialog.open();
			};
		});
		waitForContinue();
		//InteractionHandler.v().setInteractionCon(true);
		
	}
	
	private void handleAfterFlowEvent(Object fi){
		final Shell myShell = getShell();
		final String flowInfo = ((FlowInfo)fi).info().toString();
		final FlowInfo flowAfter = (FlowInfo)fi;
		final ModelCreator mc = getMc();
		
		getDisplay().syncExec(new Runnable() {
			public void run(){
				mc.updateNode(flowAfter);
		//MessageDialog msgDialog = new MessageDialog(myShell, "After Flow Info",  null, flowInfo,0, new String []{"OK", "Cancel"}, 0);
		//msgDialog.open();
			};
		});
		waitForContinue();
		//InteractionHandler.v().setInteractionCon(true);
		
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
	 * @param b
	 */
	public synchronized void setAvailable(){//boolean b) {
		//available = b;
		this.notify();
		System.out.println("Eclipse notify");
	}
	
	public void setAvailable(boolean b){
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
