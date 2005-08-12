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
public class SootSavedConfiguration {

	private HashMap config;
	private String name;
	private String saved;
	private ArrayList saveArray;
	private ArrayList runArray;
	private HashMap eclipseDefs;

	
	private static final String SPACE = " ";
	private static final String COLON = ":";
	private static final String DASH = "--";
	
	/**
	 * Constructor for SootSavedConfiguration.
	 */
	public SootSavedConfiguration(String name, HashMap config) {
		setName(name);
		setConfig(config);
	}
	
	/**
	 * Constructor for SootSavedConfiguration.
	 */
	public SootSavedConfiguration(String name, String [] saveArray) {
		setName(name);
		
		setSaveArray(new ArrayList());
		for (int i = 0; i < saveArray.length; i++){
			getSaveArray().add(saveArray[i]);
		}
	}
	
	
	/**
	 * Constructor for SootSavedConfiguration.
	 */
	public SootSavedConfiguration(String name, String saved) {
		setName(name);
		setSaved(saved);
	}
	
	// same as before (removes defs from HashMap)
	private void removeEclipseDefs(){
		if (getEclipseDefs() == null) return;
		Iterator it = getEclipseDefs().keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			if (getConfig().containsKey(key)) {
				String needsToMatch = "";
				Object val = getEclipseDefs().get(key);
				if (val instanceof String){
					needsToMatch = (String)val;
				}
				else if (val instanceof ArrayList){
					Iterator it2 = ((ArrayList)val).iterator();
					while (it2.hasNext()){
						if (needsToMatch.equals("")){
							needsToMatch = (String)it2.next();
						}
						else {
							needsToMatch = needsToMatch + "\r\n" + (String)it2.next();
						}
					}
				}
				if (getConfig().get(key).equals(needsToMatch) || getConfig().get(key).equals(val)) {
					getConfig().remove(key);
				}
			}
			else {
				getConfig().put(key, getOppositeVal(getEclipseDefs().get(key)) );
			}
		}
	}
	
	private Object getOppositeVal(Object val) {
		if (val instanceof Boolean) {
			if (((Boolean)val).booleanValue()) return new Boolean(false);
			else return new Boolean(true);
		}
		else {
			return "";
		}
	}
	
	// will use addEclipseDefs to Array instead
	private void addEclipseDefs() {
		if (getEclipseDefs() == null) return;
		Iterator it = getEclipseDefs().keySet().iterator();
		StringBuffer tempSaved = new StringBuffer(getSaved());
		while (it.hasNext()) {
			String key = (String)it.next();
			if (getSaved().indexOf((DASH+key)) != -1) {
				// already there don't add (implies user changed val)	
			}
			else {
				Object val = getEclipseDefs().get(key);
				if (val instanceof String){
				
					String res = (String)val;
					tempSaved.append(SPACE);
					tempSaved.append(DASH);
					tempSaved.append(key);
					tempSaved.append(SPACE);
					tempSaved.append(val);
					tempSaved.append(SPACE);
				}
				else {
					Iterator it2 = ((ArrayList)val).iterator();
					while (it2.hasNext()){
						String nextVal = (String)it2.next();
						tempSaved.append(SPACE);
						tempSaved.append(DASH);
						tempSaved.append(key);
						tempSaved.append(SPACE);
						tempSaved.append(nextVal);
						tempSaved.append(SPACE);
					}
				}
				
			}
		}
		setSaved(tempSaved.toString());
	}
	
	// will use this one in future and not addEclipseDefs
	private void addEclipseDefsToArray() {
		if (getEclipseDefs() == null) return;
		Iterator it = getEclipseDefs().keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			if (getSaveArray().contains(DASH+key)) {
				// already there don't add (implies user changed val)	
			}
			else {
				Object val = getEclipseDefs().get(key);
				if (val instanceof String){

					String res = (String)val;
					getSaveArray().add(DASH+key);
					getSaveArray().add(res);
					
				}
				else if (val instanceof Boolean){
					getSaveArray().add(DASH+key);
					getSaveArray().add(val.toString());
				}
			}
		}
	
	}
	
	public HashMap toHashMapFromArray(){
		addEclipseDefsToArray();
		HashMap config = new HashMap();
		BitSet bits = new BitSet(getSaveArray().size());
		for (int i = 0; i < getSaveArray().size(); i++){
			if (((String)getSaveArray().get(i)).indexOf("--") != -1){
				bits.set(i);
			}
		}
		int counter = 0;
		
		while (counter < getSaveArray().size()){
			if ((bits.get(counter+2)) || ((counter+2) >= getSaveArray().size())){
				// non phase opt
				// if key is already in map val = val + \n\r newVal
				String key = ((String)getSaveArray().get(counter)).substring(2);
				String val = (String)getSaveArray().get(counter+1);
				if (config.get(key) != null){
					String tempVal = (String)config.get(key);
					tempVal = tempVal + "\r\n" + val;
					config.put(key, tempVal); 
				}
				else {
					config.put(key,val);
				}
				counter = counter + 2;
			}
			else if ((bits.get(counter+3)) || ((counter+3) >= getSaveArray().size())){
				// phase opt
				String key = getSaveArray().get(counter)+SPACE+getSaveArray().get(counter+1);
				StringTokenizer valTemp = new StringTokenizer((String)getSaveArray().get(counter+2), ":");
				key = key+SPACE+valTemp.nextToken();
				String val = valTemp.nextToken();
				config.put(key.substring(2), val);
				counter = counter + 3;
			}
		}
		
		return config;
		
	}
	
	// goes from save String to HashMap
	public HashMap toHashMap() {
		
		// first add eclipse defs
		addEclipseDefs();
		HashMap config = new HashMap();
		String temp = getSaved();
		temp = temp.replaceAll("--", "&&");
		StringTokenizer st = new StringTokenizer(temp, "&&");
		while (st.hasMoreTokens()) {
			StringTokenizer next = new StringTokenizer((String)st.nextToken());
			switch (next.countTokens()) {
				case 2: {
					config.put(next.nextToken(), next.nextToken());
					break;	
				}
				case 3: {
					// phase options
					String key = next.nextToken()+SPACE+next.nextToken();
					StringTokenizer valTemp = new StringTokenizer(next.nextToken(), ":");
					key = key+SPACE+valTemp.nextToken();
					String val = valTemp.nextToken();
					config.put(key, val);
					break;
				}
				default: {
					//unhandled
					break;
				}
			}
		}
		return config;
	}
	
	// goes from save Array to run Array -
	//will use this and not toRunString in future 
	public ArrayList toRunArray() {
		addEclipseDefsToArray();
		if (getRunArray() == null){
			setRunArray(new ArrayList());
		}
	
		Iterator it = getSaveArray().iterator();
		String lastKey = "";
		while (it.hasNext()){
			String test = (String)it.next();
			String spliter = "\r\n";
			if (test.indexOf("\r\n") != -1){
				spliter = "\r\n";
			}
			else if (test.indexOf('\n') != -1){
				spliter = "\n";
			}
			if (test.equals("true")){
				// don't send 
			}
			else if (test.equals("false")){
				// don't send and also don't send key 
				int index = getRunArray().size() - 1;
				getRunArray().remove(index);
			}
			else if (test.indexOf(spliter) != -1){
				String [] tokens = test.split(spliter);
				getRunArray().add(tokens[0]);
				
				for (int i = 1; i < tokens.length; i++){
					getRunArray().add(lastKey);
					getRunArray().add(tokens[i]);
				}
			}
			else {
				getRunArray().add(test);
			}
			lastKey = test;
		}
		return getRunArray();	
	}
	
	// goes from save String to run String
	public String toRunString() {
		
		// first add eclipse defs
		addEclipseDefs();
		StringBuffer toRun = new StringBuffer(); 
		String temp = getSaved();
		temp = temp.replaceAll("--", "&&");
		StringTokenizer st = new StringTokenizer(temp, "&&");
		while (st.hasMoreTokens()) {
			StringTokenizer next = new StringTokenizer((String)st.nextToken());
			switch (next.countTokens()) {
				case 2: {
					String key = next.nextToken();
					String val = next.nextToken();
					val = val.trim();
					
					// if true its a boolean and want to send
					if (val.equals("true")) {
						toRun.append(DASH);
						toRun.append(key);
						toRun.append(SPACE);
					}
					// if false its a boolean but don't want to send
					else if (val.equals("false")) {
					}
					// non boolean
					else {
						toRun.append(DASH);
						toRun.append(key);
						toRun.append(SPACE);
						toRun.append(val);
						toRun.append(SPACE);
					}
					break;	
				}
				case 3: {
					// phase options
					String key = next.nextToken()+SPACE+next.nextToken()+SPACE+next.nextToken();
					toRun.append(DASH);
					toRun.append(key);
					toRun.append(SPACE);
					break;
				}
				default: {
					//unhandled
					break;
				}
			}
		}
		return toRun.toString();
	}
	
	// goes from HashMap to ArrayList -> will use this and
	// not toSaveString in future
	public ArrayList toSaveArray() {
		if (getSaveArray() == null) {
			setSaveArray(new ArrayList());
		}
		
		removeEclipseDefs();
		Iterator keysIt = getConfig().keySet().iterator();
		while (keysIt.hasNext()) {
			String key = (String)keysIt.next();
			StringTokenizer st = new StringTokenizer(key);
			Object val = getConfig().get(key);
			switch(st.countTokens()) {
				case 1: {
					String aliasName = st.nextToken();
					if (aliasName.equals("sootMainClass")) continue;
					if (val instanceof String) {
						String test = (String)val;
						if ((test == null) |(test.length() == 0)) { System.out.println("continuing" ); continue;}
					}
					getSaveArray().add(DASH+aliasName);
					if (val instanceof Boolean) {
						getSaveArray().add(val.toString());	
					}	
					else if (val instanceof String) {
						String test = (String)val;
						String spliter = "\r\n";
						if (test.indexOf("\r\n") != -1){
							spliter = "\r\n";
						}
						else if (test.indexOf('\n') != -1){
							spliter = "\n";
						}
						if (test.indexOf(spliter) != -1){
							String [] tokens = test.split(spliter);
							getSaveArray().add(tokens[0]);
				
							for (int i = 1; i < tokens.length; i++){
                                getSaveArray().add(DASH+aliasName);
                                getSaveArray().add(tokens[i]);
							}
						}
						else {
							getSaveArray().add(val);
						}
					}			
					break;
				}
				case 3: {
					getSaveArray().add(DASH+st.nextToken());
					getSaveArray().add(st.nextToken());
					String realVal = st.nextToken()+COLON;
					if (val instanceof Boolean) {
						realVal = realVal + val.toString();	
					}	
					else if (val instanceof String) {
						realVal = realVal + val;
					}			
					getSaveArray().add(realVal);
					break;
				}
				default: {
					//unhandled non option
					break;
				}
			}
		}
		
		return getSaveArray();
	}
	
	// goeas from HashMap to String - will use toSaveArray in future
	public String toSaveString() {
		
		// first remove eclipse defs
		removeEclipseDefs();
		StringBuffer toSave = new StringBuffer();
		Iterator keysIt = getConfig().keySet().iterator();
		while (keysIt.hasNext()) {
			String key = (String)keysIt.next();
			StringTokenizer st = new StringTokenizer(key);
			Object val = getConfig().get(key);
			switch(st.countTokens()) {
				case 1: {
					toSave.append(DASH);
					String aliasName = st.nextToken();
					toSave.append(aliasName);
					toSave.append(SPACE);
					if (val instanceof Boolean) {
						toSave.append(val.toString());	
					}	
					else if (val instanceof String) {
						if (((String)val).indexOf("\n") != -1){
							StringTokenizer listOptTokenizer = new StringTokenizer((String)val, "\n");
							while (listOptTokenizer.hasMoreTokens()){
								String next = listOptTokenizer.nextToken();
								toSave.append(next);
								if (listOptTokenizer.hasMoreTokens()){
									toSave.append(DASH);
									toSave.append(aliasName);
									toSave.append(SPACE);		
								}
							}
						}
						else {
							toSave.append(val);
						}
					}			
					toSave.append(SPACE);
					break;
				}
				case 3: {
					toSave.append(DASH);
					toSave.append(st.nextToken());
					toSave.append(SPACE);
					toSave.append(st.nextToken());
					toSave.append(SPACE);
					toSave.append(st.nextToken());
					toSave.append(COLON);
					if (val instanceof Boolean) {
						toSave.append(val.toString());	
					}	
					else if (val instanceof String) {
						toSave.append(val);
					}			
					toSave.append(SPACE);
					break;
				}
				default: {
					//unhandled non option
					break;
				}
			}
			
			
		}
		setSaved(toSave.toString());
		return toSave.toString();
	}
	
	/**
	 * Returns the config.
	 * @return HashMap
	 */
	public HashMap getConfig() {
		return config;
	}

	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the config.
	 * @param config The config to set
	 */
	public void setConfig(HashMap config) {
		this.config = config;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the saved.
	 * @return String
	 */
	public String getSaved() {
		return saved;
	}

	/**
	 * Sets the saved.
	 * @param saved The saved to set
	 */
	public void setSaved(String saved) {
		this.saved = saved;
	}

	

	/**
	 * Returns the eclipseDefs.
	 * @return HashMap
	 */
	public HashMap getEclipseDefs() {
		return eclipseDefs;
	}

	/**
	 * Sets the eclipseDefs.
	 * @param eclipseDefs The eclipseDefs to set
	 */
	public void setEclipseDefs(HashMap eclipseDefs) {
		this.eclipseDefs = eclipseDefs;
	}

	/**
	 * @return
	 */
	public ArrayList getSaveArray() {
		return saveArray;
	}

	/**
	 * @param list
	 */
	public void setSaveArray(ArrayList list) {
		saveArray = list;
	}

	/**
	 * @return
	 */
	public ArrayList getRunArray() {
		return runArray;
	}

	/**
	 * @param list
	 */
	public void setRunArray(ArrayList list) {
		runArray = list;
	}

}
