package ca.mcgill.sable.soot.attributes;

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

public class SootAttributesHandler {

	private Vector attrList;
	//private HashMap fileList;
	private String fileName;
	private HashMap projList;

	private static final String NEWLINE = "\r\n";
	
	public SootAttributesHandler() {
		//setFileList(new HashMap());
		//setProjList(new HashMap());
		//setAttrList(new Vector());		
	}
	
	public void setAttrList(Vector attrList) {
		this.attrList = attrList;
	}
	
	public void setAttrListForFilename(Vector attrList, String filename, String project) {
		
		System.out.println("Project: "+project+" filename: "+filename);
		if (attrList == null ) {
			System.out.println("attrList is null");
		}
		System.out.println(attrList.capacity());
		Iterator it = attrList.iterator();
		System.out.println("it is: "+it);
		
		while (it.hasNext()) {
			System.out.println(it.next().toString());
			//String temp = (String)it.next();
			System.out.println("in while loop");
			//System.out.println(temp);
		}
		
		HashMap temp = new HashMap();
		temp.put(filename, attrList);
		
		//Object temp = getProjList().get(project);		
		//if (temp == null) {
		//	System.out.println("temp was null");
			//getProjList().put(project, new HashMap());
			//if (((HashMap)getProjList().get(project)).get(filename) == null) {
		//		System.out.println("filename was null");
				getProjList().put(project, temp);
		//		System.out.println(getProjList().get(project));
		//	}
		//	else {
		//		System.out.println("filename was not null");
		//		((HashMap)((HashMap)getProjList().get(project)).get(filename)).put(filename, attrList);
		//	}
		//}
		
		//getProjList().put(project, ((HashMap)projList().get(filename)).put()	
		//getFileList().put(filename, attrList);
	//	System.out.println("AttributeHandler "+filename+attrList.toString());
	}

	public void printAttrs() {
		System.out.println(getAttrList());
		if (getAttrList() == null) return;
		
		Iterator it = getAttrList().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			System.out.println("New Attribute");
			System.out.println("Java Line: "+sa.getJava_ln());
			System.out.println("Jimple Line: "+sa.getJimple_ln());
			System.out.println("Text: "+sa.getText());
			System.out.println();
		}
	}
	 
	public void printAttributes() {
		Set s = getProjList().keySet();
		Collection c = getProjList().values();
		Iterator it1 = s.iterator();
		Iterator it2 = c.iterator();
		while (it1.hasNext()) {
			String proj = (String)it1.next();
			System.out.println("AttributeHandler"+proj);
			Object files = getProjList().get(proj);
			if (files == null) System.out.println("files is null");
			
			Set s2 = ((HashMap)files).keySet();
			Collection c2 = ((HashMap)files).values();
			Iterator it5 = s2.iterator();
			Iterator it6 = c2.iterator();
			
			while (it5.hasNext()) {
				String filename = (String)it5.next();
				System.out.println("AttributeHandler"+filename);
				Vector attrList = (Vector)it6.next();
				//System.out.println(attrList.toString());
				Iterator it3 = attrList.iterator();
				while (it3.hasNext()) {
					SootAttribute sa = (SootAttribute)it3.next();
					System.out.println("New Attribute");
					System.out.println("Java Line: "+sa.getJava_ln());
					System.out.println("Jimple Line: "+sa.getJimple_ln());
					System.out.println("Text: "+sa.getText());
					System.out.println();
				}
			}
		}
	}
	
	public String getJimpleAttributes(int lnNum) {
		Iterator it = getAttrList().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJimpleLn(lnNum)) {
				sb.append(sa.getText());
				sb.append(NEWLINE);
			}
		}	
		String result = sb.toString();
		result = result.trim();
		return result;
	}
	
	public String getJimpleAttributes(String proj, String filename, int lineNum) {
		
		System.out.println("Project: "+proj+" Filename: "+filename);
		if (getProjList().get(proj) == null) {
			return null;
		}
		if (((HashMap)getProjList().get(proj)).get(filename) == null) {
			return null;
		}
		else {
			Vector attrs = (Vector)((HashMap)getProjList().get(proj)).get(filename);
			Iterator it = attrs.iterator();
			StringBuffer sb = new StringBuffer();
			while (it.hasNext()) {
				SootAttribute sa = (SootAttribute)it.next();
				if (sa.attrForJimpleLn(lineNum)) {
					sb.append(sa.getText());
					sb.append(NEWLINE);
				}
			}	
			return sb.toString();
		}
	}
	
	public String getJavaAttribute(int lnNum) {
		Iterator it = getAttrList().iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			if (sa.attrForJavaLn(lnNum)) {
				sb.append(sa.getText());
				sb.append(NEWLINE);
			}
		}	
		return sb.toString();
	}

	
	public String getJavaAttribute(String proj, String filename, int lineNum) {
		System.out.println("Project: "+proj+" Filename: "+filename);
		if (getProjList().get(proj) == null) {
			return null;
		}
		if (((HashMap)getProjList().get(proj)).get(filename) == null) {
			return null;
		}
		else {
			Vector attrs = (Vector)((HashMap)getProjList().get(proj)).get(filename);
			Iterator it = attrs.iterator();
			StringBuffer sb = new StringBuffer();
			while (it.hasNext()) {
				SootAttribute sa = (SootAttribute)it.next();
				if (sa.attrForJavaLn(lineNum)) {
					sb.append(sa.getText());
					sb.append(NEWLINE);
				}
			}	
			return sb.toString();
		}
	}

	/*public boolean attrExistsForFile(String filename) {
		System.out.println(filename);
		if (getFileList().get(filename) == null) return false;
		else return true;
	}
	
	
	/**
	 * Returns the fileList.
	 * @return HashMap
	 */
	/*public HashMap getFileList() {
		return fileList;
	}

	/**
	 * Sets the fileList.
	 * @param fileList The fileList to set
	 */
/*	public void setFileList(HashMap fileList) {
		this.fileList = fileList;
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
	public Vector getAttrList() {
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

}
