package ca.mcgill.sable.soot.launching;

import java.util.*;

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
			//if (!getList().contains(token)) {
			getList().add(token);
			//}
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
		/*if (!getList().contains((key+SPACE+val))) {
			getList().add((key+SPACE+val));
		}*/
	}
	
	/*public void appendToSootClasspath(String val) {
		Iterator it = getList().iterator();
		while (it.hasNext()) {
			//System.out.println(it.next().getClass().toString());
			String temp = (String)it.next();
			System.out.println(temp);
			if (temp.indexOf(LaunchCommands.SOOT_CLASSPATH) != -1) {
				System.out.println("match found");
				getList()..remove(temp);
				int index =
				temp = temp+System.getProperty("path.separator")+val;
				System.out.println(temp);
				getList().add(temp);
				System.out.println(temp);
			}
		}
	}*/
	
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
