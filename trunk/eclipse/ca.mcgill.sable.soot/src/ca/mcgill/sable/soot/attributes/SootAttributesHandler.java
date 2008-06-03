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
	private String fileName;
	private HashMap projList;
	private long valuesSetTime;
	private boolean update = true;
	private ArrayList keyList;
	private ArrayList typesToShow;
	private boolean showAllTypes = true;

	private static final String NEWLINE = "\n\r";
	
	public SootAttributesHandler() {
				
	}
	
	public void setAttrList(ArrayList attrList) {
		this.attrList = new ArrayList();
		this.attrList.addAll(attrList);
	}

	public String getJimpleAttributes(int lnNum) {
		StringBuffer sb = new StringBuffer();
		if (getAttrList() == null) return sb.toString();
		Iterator it = getAttrList().iterator();
		
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)) {
				if (showAllTypes){
					sb.append(sa.getAllTextAttrs("\n"));
				}
				else {
					Iterator typesIt = typesToShow.iterator();
					while (typesIt.hasNext()){
						sb.append(sa.getTextAttrsForType("\n", (String)typesIt.next()));
					}
					
				}
				
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
				if (showAllTypes){
					sb.append(sa.getAllTextAttrs("<br>"));
				}
				else {
					Iterator typesIt = typesToShow.iterator();
					while (typesIt.hasNext()){
						sb.append(sa.getTextAttrsForType("<br>", (String)typesIt.next()));
					}
				}
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
			if (sa.attrForJavaLn(lnNum)){
				if (sa.getAllLinkAttrs() != null){
				
					list.addAll(sa.getAllLinkAttrs());
				}
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
		if (keyList == null) return keyList;
		if (isShowAllTypes()){
			return keyList;
		}
		else {
			ArrayList typeList = new ArrayList();
			Iterator kIt = keyList.iterator();
			while (kIt.hasNext()){
				AnalysisKey key = (AnalysisKey)kIt.next();
				if (getTypesToShow().contains(key.getType())){
					typeList.add(key);
				}
			}
			return typeList;
		}
	}

	/**
	 * @param list
	 */
	public void setKeyList(ArrayList list) {
		keyList = list;
	}

	/**
	 * @return
	 */
	public ArrayList getTypesToShow() {
		return typesToShow;
	}

	/**
	 * @param list
	 */
	public void setTypesToShow(ArrayList list) {
		typesToShow = list;
	}

	/**
	 * @return
	 */
	public boolean isShowAllTypes() {
		return showAllTypes;
	}

	/**
	 * @param b
	 */
	public void setShowAllTypes(boolean b) {
		showAllTypes = b;
	}

}
