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

import org.eclipse.jface.dialogs.IDialogSettings;

import ca.mcgill.sable.soot.*;

/**
 * Handles saving of Soot configurations, adding, updating and 
 * deleting
 */
public class SavedConfigManager {

	/**
	 * Constructor for SavedConfigManager.
	 */
	public SavedConfigManager() {
		super();
	}
	
	public void handleDeletes() {
		if (getDeleteList() != null) {
			Iterator it = getDeleteList().iterator();
			while (it.hasNext()) {
				String name = (String)it.next();
				remove(name);
			}
		}
	}
	
	private void remove(String name) {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int count = 0;
		try {
			count = settings.getInt("config_count");
		}
		catch (NumberFormatException e){
		}
		String [] pointers = new String [count];
		for (int i = 1; i <= count; i++) {
			pointers[i-1] = settings.get("soot_run_config_"+i);
		}

		int i = 1;
		int j = 0;
		while (j < count) {
			if (!pointers[j].equals(name)) {
			  	settings.put("soot_run_config_"+i, pointers[j]);
			  	i++;
			}
			j++;		
		}
		
		settings.put("soot_run_config_"+count, (String)null);
		count--;
		if (count < 0) {
			count = 0;
		}
		settings.put("config_count", count);
		settings.put(name, (String)null);	
		
	}
	
	public void handleEdits() {
		if (getEditMap() != null) {
			Iterator it = getEditMap().keySet().iterator();
			while (it.hasNext()) {
				String name = (String)it.next();
				if (alreadyInList(name)) {
					update(name, (ArrayList)getEditMap().get(name));
				}
				else {
					add(name, (ArrayList)getEditMap().get(name));
				}
			}
		}
	}
	
	
	private boolean alreadyInList(String name) {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int count = 0;
		try {
			count = settings.getInt("config_count");
		}
		catch (NumberFormatException e){
		}
		for (int i = 1; i <= count; i++){
			if (settings.get("soot_run_config_"+i).equals(name)){
				return true;
			}
		}
		return false;
	}

	//	TODO use this instead of with String, String
	private void update(String name, ArrayList val){
		String [] temp = new String [val.size()];
		val.toArray(temp);
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		settings.put(name, temp);
	}
	 
	// TODO use this instead of with String, String
	private void update(String name, String [] val){
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		settings.put(name, val);
	}

	// TODO stop using this
	private void update(String name, String val) {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		
		if (val != null) {
			settings.put(name, val);
		}
		else {
			settings.put(name, "default");
		}
	}
	
	// TODO use this instead of String, String
	private void add(String name, ArrayList val){
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int count = 0;
		try {
			count = settings.getInt("config_count");
		}
		catch(NumberFormatException e) {
		}
		count++;
		settings.put("config_count", count);
		settings.put("soot_run_config_"+count, name);
		update(name, val);	
	}
	
	// TODO stop using this
	private void add(String name, String val) {
		IDialogSettings settings = SootPlugin.getDefault().getDialogSettings();
		int count = 0;
		try {
			count = settings.getInt("config_count");
		}
		catch(NumberFormatException e) {
		}
		count++;
		settings.put("config_count", count);
		settings.put("soot_run_config_"+count, name);
		update(name, val);
	}
	
	private HashMap editMap;
	private ArrayList deleteList;
	

	/**
	 * Returns the deleteList.
	 * @return ArrayList
	 */
	public ArrayList getDeleteList() {
		return deleteList;
	}

	/**
	 * Returns the editMap.
	 * @return HashMap
	 */
	public HashMap getEditMap() {
		return editMap;
	}

	/**
	 * Sets the deleteList.
	 * @param deleteList The deleteList to set
	 */
	public void setDeleteList(ArrayList deleteList) {
		this.deleteList = deleteList;
	}

	/**
	 * Sets the editMap.
	 * @param editMap The editMap to set
	 */
	public void setEditMap(HashMap editMap) {
		this.editMap = editMap;
	}

}
