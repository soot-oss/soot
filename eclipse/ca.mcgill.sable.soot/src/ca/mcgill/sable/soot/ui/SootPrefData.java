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
package ca.mcgill.sable.soot.ui;

import java.util.*;


public class SootPrefData {
	
	public SootPrefData(String classes, String selected){
		StringTokenizer st = new StringTokenizer(classes, ",");
		while (st.hasMoreTokens()){
			if (getClassesArray() == null){
				setClassesArray(new ArrayList());		
			}
			getClassesArray().add(st.nextToken());
		}
		setSelected(selected);
	}
	
	ArrayList classesArray;
	String selected;
	
	public void addToClassesArray(String value){
		if (getClassesArray() == null){
			setClassesArray(new ArrayList());
		}
		getClassesArray().add(value);	
	}
	
	public void removeFromClassesArray(String value){
		if (value.equals(getSelected())){
			getClassesArray().remove(value);
			setSelected((String)getClassesArray().get(0));
		}
		else {
			getClassesArray().remove(value);
		}
		
	}
	
	public String getSaveString(){
		Iterator it = getClassesArray().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()){
			sb.append((String)it.next());
			sb.append(",");
		}
		sb.substring(0, sb.lastIndexOf(",")-1);
		return sb.toString();
	}
	
	/**
	 * @return
	 */
	public ArrayList getClassesArray() {
		return classesArray;
	}

	/**
	 * @param list
	 */
	public void setClassesArray(ArrayList list) {
		classesArray = list;
	}

	/**
	 * @return
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * @param string
	 */
	public void setSelected(String string) {
		selected = string;
	}

}
