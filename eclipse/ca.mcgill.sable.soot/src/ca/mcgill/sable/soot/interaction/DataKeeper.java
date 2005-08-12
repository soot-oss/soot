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

import soot.toolkits.graph.interaction.*;
import java.util.*;

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
        if (index > 0) {
            previous = (FlowInfo)getFlowInfoList().get(index-1);
            
        }
        else {
            // at first node and want to go back
            previous = null;
        }
        clearTo = findLast();
		
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
			
			if (getCurrent().equals(next)) break;
			if (getCurrent().unit().equals(next.unit()) && (getCurrent().isBefore() == next.isBefore())){
				retInfo = next;
		
			}
		}
		return retInfo;
	}
	
	public void addFlowInfo(Object fi){
		if (getFlowInfoList() == null){
			setFlowInfoList(new ArrayList());
		}
		getFlowInfoList().add(fi);
		setCurrent((FlowInfo)fi);
	}
	
	public boolean inMiddle(){
		if (getFlowInfoList() == null) return false;
		if (getFlowInfoList().indexOf(getCurrent()) == getFlowInfoList().size()-1) return false;
		return true;
	}
	
	public boolean canGoBack(){
		if (getFlowInfoList() == null) return false;
		
		if (getFlowInfoList().size() == 0) return false;
		
		if (getCurrent().equals(getFlowInfoList().get(0))) return false;
		
		return true;
	}
	
	public void stepForward(){
		int index = getFlowInfoList().indexOf(getCurrent());
		FlowInfo next = (FlowInfo)getFlowInfoList().get(index+1);
		getController().setEvent(new InteractionEvent(IInteractionConstants.REPLACE, next));
		getController().handleEvent();
		setCurrent(next);
		
	}
	
	public void stepForwardAuto(){
		int index = getFlowInfoList().indexOf(getCurrent());
		for (int i = index + 1; i < getFlowInfoList().size(); i++){
			FlowInfo next = (FlowInfo)getFlowInfoList().get(i);
			getController().setEvent(new InteractionEvent(IInteractionConstants.REPLACE, next));
			getController().handleEvent();
			setCurrent(next);
		}
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
