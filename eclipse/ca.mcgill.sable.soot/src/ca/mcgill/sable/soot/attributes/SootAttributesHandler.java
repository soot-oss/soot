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
	private ArrayList typesToShow;
	private boolean showAllTypes = true;

	private static final String NEWLINE = "\n\r";
	
	public SootAttributesHandler() {
				
	}
	
	public void setAttrList(ArrayList attrList) {
		//if (this.attrList == null){
			this.attrList = new ArrayList();
		//}
		System.out.println("adding all attr");
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
				//System.out.println("Soot Attribute:");
				//System.out.println(sa);
				//if (sa.getTextList() == null) return null;
				if (showAllTypes){
					sb.append(sa.getAllTextAttrs("<br>"));
				}
				else {
					System.out.println("tooltips types to show: "+typesToShow);
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
			//System.out.println("links for line: "+lnNum);
			if (sa.attrForJavaLn(lnNum)){
				if (sa.getAllLinkAttrs() != null){
				
					list.addAll(sa.getAllLinkAttrs());
				}
				//System.out.println("list length: "+list.size());
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
		/*if (isShowAllTypes()){
			return attrList;
		}
		else{
			System.out.println("types to show: "+getTypesToShow());
			ArrayList typeList = new ArrayList();
			if (attrList != null){
				Iterator it = attrList.iterator();
				while (it.hasNext()){
					SootAttribute sa = (SootAttribute)it.next();
					boolean inclSa = true;
				
					Iterator typeIt = getTypesToShow().iterator();
					while (typeIt.hasNext()){
						String type = (String)typeIt.next();
						//System.out.println("next type: "+type);
						if (!sa.getAnalysisTypes().contains(type)){
							//System.out.println("not contained");
							inclSa = false;
							break;
						}
					}
					if (inclSa){
						typeList.add(sa);
					}
				}
			}
			return typeList;
		}*/
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
