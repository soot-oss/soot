
package ca.mcgill.sable.soot.interaction;

import soot.toolkits.graph.interaction.*;
import java.util.*;

/**
 * @author jlhotak
 *
 * 
 */

public class DataKeeper {

	private List flowInfoList;
	private FlowInfo current;
	private int total;
	private int repeat;
	private InteractionController controller;
	
	public DataKeeper(InteractionController controller){
		setController(controller);
	}
	
	public void stepBack(){
		// this will be called from InteractionStepBack
		// it will cause the most recent flow info data 
		// to be removed resulting in a blank if on iteration 
		// 0 or the results at this node from the
		// previous iteration
		// if it is the very fist node and 
		// very first iteration nothing will happen
		
		// no where to go back to
		if (getCurrent() == null) return;
		int index = getFlowInfoList().indexOf(getCurrent());
		FlowInfo previous;
		FlowInfo clearTo;
		// on first iter need to replace with empty
		//if (getCurrent().iteration() == 1){
			if (index > 0) {
				previous = (FlowInfo)getFlowInfoList().get(index-1);
				
			}
			else {
				// at first node and want to go back
				previous = null;
			}
			clearTo = findLast();//new FlowInfo("", getCurrent().unit(), getCurrent().isBefore(), 1);
		//}
		// need to replace with previous
		//else {
		//	previous = (FlowInfo)getFlowInfoList().get(index-1);
		//	clearTo = findLast();
		//}
		
		setCurrent(previous);
		getController().setEvent(new InteractionEvent(IInteractionConstants.CLEARTO, clearTo));
		getController().handleEvent();
		if (previous != null){
			getController().setEvent(new InteractionEvent(IInteractionConstants.REPLACE, previous));
			getController().handleEvent();
		}
		
	}
	
	private FlowInfo findLast(){
		Iterator it = getFlowInfoList().iterator();
		FlowInfo retInfo = new FlowInfo("", getCurrent().unit(), getCurrent().isBefore());
		
		while (it.hasNext()){
			FlowInfo next = (FlowInfo)it.next();
			System.out.println("next: "+next.info()+" unit: "+next.unit());
			
			if (getCurrent().equals(next)) break;
			//System.out.println("next: "+next.info()+" unit: "+next.unit());
			if (getCurrent().unit().equals(next.unit()) && (getCurrent().isBefore() == next.isBefore())){
				retInfo = next;
				System.out.println("found retInfo: "+retInfo.info());
		
			}
		}
		System.out.println("returning retInfo: "+retInfo.info());
		return retInfo;
	}
	
	public void addFlowInfo(Object fi){
		if (getFlowInfoList() == null){
			setFlowInfoList(new ArrayList());
		}
		getFlowInfoList().add(fi);
		System.out.println("added to flowinfo list: "+getFlowInfoList());
		setCurrent((FlowInfo)fi);
	}
	
	public boolean inMiddle(){
		if (getFlowInfoList() == null) return false;
		if (getFlowInfoList().indexOf(getCurrent()) == getFlowInfoList().size()-1) return false;
		return true;
	}
	
	public void stepForward(){
		int index = getFlowInfoList().indexOf(getCurrent());
		FlowInfo next = (FlowInfo)getFlowInfoList().get(index+1);
		getController().setEvent(new InteractionEvent(IInteractionConstants.REPLACE, next));
		getController().handleEvent();
		setCurrent(next);
		
		
	}
	
	/**
	 * @return
	 */
	public FlowInfo getCurrent() {
		return current;
	}

	/**
	 * @return
	 */
	public List getFlowInfoList() {
		return flowInfoList;
	}

	/**
	 * @return
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @return
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param info
	 */
	public void setCurrent(FlowInfo info) {
		current = info;
	}

	/**
	 * @param list
	 */
	public void setFlowInfoList(List list) {
		flowInfoList = list;
	}

	/**
	 * @param i
	 */
	public void setRepeat(int i) {
		repeat = i;
	}

	/**
	 * @param i
	 */
	public void setTotal(int i) {
		total = i;
	}

	/**
	 * @return
	 */
	public InteractionController getController() {
		return controller;
	}

	/**
	 * @param controller
	 */
	public void setController(InteractionController controller) {
		this.controller = controller;
	}

}
