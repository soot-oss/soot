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

package ca.mcgill.sable.soot.attributes;

import java.util.*;

public class SootAttributesHandler {

	private ArrayList attrList;
	//private HashMap fileList;
	private String fileName;
	private HashMap projList;
	private long valuesSetTime;
	private boolean update = true;
	private ArrayList keyList;

	private static final String NEWLINE = "\n\r";
	
	public SootAttributesHandler() {
				
	}
	
	public void setAttrList(ArrayList attrList) {
		if (this.attrList == null){
			this.attrList = new ArrayList();
		}
		this.attrList.addAll(attrList);
	}

	public String getJimpleAttributes(int lnNum) {
		StringBuffer sb = new StringBuffer();
		if (getAttrList() == null) return sb.toString();
		Iterator it = getAttrList().iterator();
		
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)) {
				//if (sa.getTextList() == null) return null;
				sb.append(sa.getAllTextAttrs("\n"));
			}
		}	
		String result = sb.toString();
		result = result.trim();
		if (result.length() == 0 ) return null;
		return result;
	}
	
	public ArrayList getJimpleLinks(int lnNum){
		Iterator it = getAttrList().iterator();
		ArrayList list = new ArrayList();
		while (it.hasNext()){
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)){
				list = sa.getAllLinkAttrs();
			}
		}
		return list;
	}
	
	public String getJavaAttribute(int lnNum) {
		StringBuffer sb = new StringBuffer();
		if (getAttrList() == null) return sb.toString();
		Iterator it = getAttrList().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJavaLn(lnNum)) {
				//System.out.println("Soot Attribute:");
				//System.out.println(sa);
				//if (sa.getTextList() == null) return null;
				sb.append(sa.getAllTextAttrs("<br>"));
			}
		}	
		return sb.toString();
	}

	public ArrayList getJavaLinks(int lnNum){
		ArrayList list = new ArrayList();
		if (getAttrList() == null) return list;
		Iterator it = getAttrList().iterator();
		while (it.hasNext()){
			SootAttribute sa = (SootAttribute)it.next();
			System.out.println("links for line: "+lnNum);
			if (sa.attrForJavaLn(lnNum)){
				if (sa.getAllLinkAttrs() != null){
				
					list.addAll(sa.getAllLinkAttrs());
				}
				System.out.println("list length: "+list.size());
			}
		}
		return list;
	}


	/**
	 * Returns the projList.
	 * @return HashMap
	 */
	public HashMap getProjList() {
		return projList;
	}

	/**
	 * Sets the projList.
	 * @param projList The projList to set
	 */
	public void setProjList(HashMap projList) {
		this.projList = projList;
	}

	/**
	 * Returns the attrList.
	 * @return Vector
	 */
	public ArrayList getAttrList() {
		return attrList;
	}

	/**
	 * Returns the fileName.
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the fileName.
	 * @param fileName The fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return
	 */
	public long getValuesSetTime() {
		return valuesSetTime;
	}

	/**
	 * @param l
	 */
	public void setValuesSetTime(long l) {
		valuesSetTime = l;
	}

	/**
	 * @return
	 */
	public boolean isUpdate() {
		return update;
	}

	/**
	 * @param b
	 */
	public void setUpdate(boolean b) {
		update = b;
	}

	/**
	 * @return
	 */
	public ArrayList getKeyList() {
		return keyList;
	}

	/**
	 * @param list
	 */
	public void setKeyList(ArrayList list) {
		keyList = list;
	}

}
