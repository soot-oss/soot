/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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
package ca.mcgill.sable.soot.ui;

import java.util.*;

import org.eclipse.swt.widgets.*;
import ca.mcgill.sable.soot.ui.*;


public class EnableGroup {

	private String phaseAlias;
	private String subPhaseAlias; 
	private BooleanOptionWidget leader;
	private ArrayList controls;
	private boolean phaseOptType;
	
	public EnableGroup(){
		
	}
	
	public void addControl(ISootOptionWidget c){
		if (getControls() == null){
			setControls(new ArrayList());
		}
		getControls().add(c);
	}
	
	public void addControls(ArrayList c){
		if (getControls() == null){
			setControls(new ArrayList());
		}
		getControls().addAll(c);
	}
	
	public boolean isLeader(BooleanOptionWidget l){
		if (l.equals(getLeader())) return true;
		return false;
	}
	
	public void changeControlState(boolean enabled){
		if (getControls() == null) return;
		Iterator it = getControls().iterator();
		while (it.hasNext()){
			ISootOptionWidget control = (ISootOptionWidget)it.next();
			if (control.getControls() == null) continue;
			Iterator conIt = control.getControls().iterator();
			while (conIt.hasNext()){
				Object obj = conIt.next();
				((Control)obj).setEnabled(enabled);
			}
			
		}
	}
	
	/**
	 * @return
	 */
	public ArrayList getControls() {
		return controls;
	}

	/**
	 * @return
	 */
	public BooleanOptionWidget getLeader() {
		return leader;
	}

	/**
	 * @param list
	 */
	private void setControls(ArrayList list) {
		controls = list;
	}

	/**
	 * @param button
	 */
	public void setLeader(BooleanOptionWidget button) {
		leader = button;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Phase: "+getPhaseAlias()+" SubPhase: "+getSubPhaseAlias());
		sb.append("Leader: "+getLeader().getAlias()+" sel: "+getLeader().getButton().getSelection()+" enabled: "+getLeader().getButton().isEnabled()+"\n");
		if (getControls() != null){
			Iterator it = getControls().iterator();
			while (it.hasNext()){
				ISootOptionWidget next = (ISootOptionWidget)it.next();
				sb.append("control: "+next.getId()+"\n");
				if (next instanceof BooleanOptionWidget){
					sb.append("control is boolean and enable state: "+((BooleanOptionWidget)next).getButton().isEnabled()+"\n");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	public String getPhaseAlias() {
		return phaseAlias;
	}

	/**
	 * @return
	 */
	public String getSubPhaseAlias() {
		return subPhaseAlias;
	}

	/**
	 * @param string
	 */
	public void setPhaseAlias(String string) {
		phaseAlias = string;
	}

	/**
	 * @param string
	 */
	public void setSubPhaseAlias(String string) {
		subPhaseAlias = string;
	}

	/**
	 * @return
	 */
	public boolean isPhaseOptType() {
		return phaseOptType;
	}

	/**
	 * @param b
	 */
	public void setPhaseOptType(boolean b) {
		phaseOptType = b;
	}

}
