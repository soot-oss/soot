package ca.mcgill.sable.soot.launching;

import java.util.*;
import ca.mcgill.sable.soot.util.*;

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
public class SootSavedConfiguration {

	private HashMap config;
	private String name;
	private String saved;

	
	private static final String SPACE = " ";
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
	public SootSavedConfiguration(String name, String saved) {
		setName(name);
		setSaved(saved);
	}
	
	public HashMap toHashMap() {
		HashMap config = new HashMap();
		StringTokenizer st = new StringTokenizer(getSaved(), DASH);
		while (st.hasMoreTokens()) {
			StringTokenizer next = new StringTokenizer((String)st.nextToken());
			switch (next.countTokens()) {
				case 2: {
					// simple key value 
					config.put(next.nextToken(), next.nextToken());
					break;	
				}
				case 4: {
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
	
	public String toRunString() {
		StringBuffer toRun = new StringBuffer(); 
		String temp = getSaved();
		temp = temp.replaceAll("--", "&&");
		//System.
		StringTokenizer st = new StringTokenizer(temp, "&&");
		while (st.hasMoreTokens()) {
			StringTokenizer next = new StringTokenizer((String)st.nextToken());
			switch (next.countTokens()) {
				case 2: {
					String key = next.nextToken();
					System.out.print("key: "+key);
					String val = next.nextToken();
					val = val.trim();
					System.out.println("value: /"+val+"/");
					if ((val.equals("true")) || (val.equals("false"))) {
						toRun.append(DASH);
						toRun.append(key);
						toRun.append(SPACE);
					}
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
					///StringTokenizer valTemp = new StringTokenizer(next.nextToken(), ":");
					//System.out.println("valTemp: "+valTemp.toString());
					//key = key+SPACE+valTemp.nextToken();
					//System.out.print("key: "+key);
					//String val = valTemp.nextToken();
					//System.out.println("value: "+val);
					toRun.append(DASH);
					toRun.append(key);
					toRun.append(SPACE);
					//toRun.append(val);
					//toRun.append(SPACE);
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
	
	public String toSaveString() {
		
		Iterator it = getConfig().entrySet().iterator();
		System.out.println(getConfig().size());
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		StringBuffer toSave = new StringBuffer();
		
		
		Iterator keysIt = getConfig().keySet().iterator();
		//Iterator valsIt = getConfig().values().iterator();
		
		while (keysIt.hasNext()) {// && valsIt.hasNext()) {
			Object keyTemp = keysIt.next();
			//System.out.println(keyTemp+" is a "+keyTemp.getClass().toString());
			if (keyTemp instanceof String) {
				String key = (String)keyTemp;
				System.out.println("key is: "+key);
				Object valTemp = getConfig().get(key);
				String separator = SootCmdFormat.SPACE;
				String result = "";
				if (valTemp instanceof SootCmdFormat) {
					if (((SootCmdFormat)valTemp).getVal() instanceof String) {
						result = (String)((SootCmdFormat)valTemp).getVal();
						System.out.println("String result: "+result);
						//separator = ((SootCmdFormat)valTemp).getSeparator();
				
						//if ((result != null) && (result.length() != 0)) {
							/*toSave.append(DASH);
							toSave.append(key);
							toSave.append(separator);
							toSave.append(result);
							toSave.append(SPACE);	
						//}*/
					}
					else if (((SootCmdFormat)valTemp).getVal() instanceof Boolean) {
						result = ((Boolean)((SootCmdFormat)valTemp).getVal()).toString();
						System.out.println("bool result: "+result);
						//separator = ((SootCmdFormat)valTemp).getSeparator();
						/*if (separator.equals(SootCmdFormat.SPACE) && ((Boolean)((SootCmdFormat)valTemp).getVal()).booleanValue()) {
							System.out.println("bool opts that are true: "+key);
							toSave.append(DASH);
							toSave.append(key);
							toSave.append(separator);
						}
						/*else if (separator.equals(SootCmdFormat.SPACE) && !((Boolean)((SootCmdFormat)valTemp).getVal()).booleanValue()) {
							System.out.println("bool opts that are false: "+key);
						}*/
						/*else {
							toSave.append(DASH);
							toSave.append(key);
							toSave.append(separator);
							toSave.append(result);
							toSave.append(SPACE);
						}*/
					}
					/*else {
						result = "";		
						System.out.println("no result: "+result);
					}*/
					
					separator = ((SootCmdFormat)valTemp).getSeparator();
				
				
					toSave.append(DASH);
					toSave.append(key);
					toSave.append(separator);
					toSave.append(result);
					toSave.append(SPACE);	
				}
			}
				
		}
		
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

	

}
