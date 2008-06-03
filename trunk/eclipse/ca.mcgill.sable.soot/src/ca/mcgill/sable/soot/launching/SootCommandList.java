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

package ca.mcgill.sable.soot.launching;

import java.util.*;

/**
 * Handles command list by making Strings separated by spaces into 
 * list
 */
public class SootCommandList {

	private ArrayList list;
	private static final String SPACE =  " ";
	
	/**
	 * Constructor for SootCommandList.
	 */
	public SootCommandList() {
		setList(new ArrayList());
	}
	
	/**
	 * @param key
	 */
	public void addSingleOpt(ArrayList key){
		getList().addAll(key);	
	}
	
	/**
	 * Method addSingleOpt.
	 * @param key
	 */
	public void addSingleOpt(String key) {
		StringTokenizer st = new StringTokenizer(key);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();	
			getList().add(token);
		}
	}
	
	/**
	 * Method addDoubleOpt.
	 * @param key
	 * @param val
	 */
	public void addDoubleOpt(String key, String val) {
		addSingleOpt(key);
		addSingleOpt(val);
	}
	
	public void addDashes(){
	
		ArrayList withDashes = new ArrayList();
			
		Iterator it = getList().iterator();
		while (it.hasNext()) {
			String temp = (String)it.next();
			temp = "-- "+temp;
			withDashes.add(temp);	
		}
		
		setList(withDashes);
	}

	public void printList(){
		Iterator it = list.iterator();
		while (it.hasNext()){
			System.out.println(it.next());
		}
	}
	/**
	 * Returns the list.
	 * @return ArrayList
	 */
	public ArrayList getList() {
		return list;
	}

	/**
	 * Sets the list.
	 * @param list The list to set
	 */
	public void setList(ArrayList list) {
		this.list = list;
	}

}
