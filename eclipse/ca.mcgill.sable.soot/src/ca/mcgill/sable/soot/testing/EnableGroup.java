/**
 * @author jlhotak
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
package ca.mcgill.sable.soot.testing;

import java.util.*;

import org.eclipse.swt.widgets.*;


public class EnableGroup {

	private Button leader;
	private ArrayList controls;
	
	public EnableGroup(){
		
	}
	
	public void addControl(Control c){
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
	
	public boolean isLeader(Button l){
		if (l.equals(getLeader())) return true;
		return false;
	}
	
	public void changeControlState(boolean enabled){
		if (getControls() == null) return;
		Iterator it = getControls().iterator();
		while (it.hasNext()){
			((Control)it.next()).setEnabled(enabled);
		}
	}
	
	/**
	 * @return
	 */
	private ArrayList getControls() {
		return controls;
	}

	/**
	 * @return
	 */
	public Button getLeader() {
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
	public void setLeader(Button button) {
		leader = button;
	}

}
