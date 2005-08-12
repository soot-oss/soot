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


public class SootAttribute {

	private int javaEndLn;
	private int javaStartLn;
	private int jimpleEndLn;
	private int jimpleStartLn;
	private int jimpleStartPos;
	private int jimpleEndPos;
    private int javaStartPos;
    private int javaEndPos;

	private ArrayList colorList;
	private ArrayList textList;

	private ArrayList linkList;
	
	private int jimpleLength;
	private int javaLength;
	

	public ArrayList getAnalysisTypes(){
		ArrayList types = new ArrayList();
		if (getTextList() != null){
			Iterator it = getTextList().iterator();
			while (it.hasNext()){
				TextAttribute ta = (TextAttribute)it.next();
				if (!types.contains(ta.getType())){
					types.add(ta.getType());
				}
			}
		}
		if (getLinkList() != null){
			Iterator lit = getLinkList().iterator();
			while (lit.hasNext()){
				LinkAttribute la = (LinkAttribute)lit.next();
				if (!types.contains(la.getType())){
					types.add(la.getType());
				}
			}
		}
		
		if (getColorList() != null){
			Iterator cit = getColorList().iterator();
			while (cit.hasNext()){
				ColorAttribute ca = (ColorAttribute)cit.next();
				if (!types.contains(ca.type())){
					types.add(ca.type());
				}
			}
		}
		return types;
	}
	
	public void addColorAttr(ColorAttribute color){
		if (getColorList() == null){
			setColorList(new ArrayList());
		}
		getColorList().add(color);
		
	}
	
	private static final String NEWLINE = "\n";
	
	public void addLinkAttr(LinkAttribute link){
		if (getLinkList() == null){
			setLinkList(new ArrayList());
		}
		getLinkList().add(link);
		TextAttribute ta = new TextAttribute();
		ta.setInfo(link.getLabel());
		ta.setType(link.getType());
		addTextAttr(ta);
	}
	
	public ArrayList getAllLinkAttrs(){
		return getLinkList();
	}

	public void addTextAttr(TextAttribute text){
		if (getTextList() == null){
			setTextList(new ArrayList());
		}
		text.setInfo(formatText(text.getInfo()));
		getTextList().add(text);
	}
	
	public String formatText(String text){
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&amp;", "&");
		return text;
	}
	
	public StringBuffer getAllTextAttrs(String lineSep){
		StringBuffer sb = new StringBuffer();
		if (getTextList() != null){
			Iterator it = getTextList().iterator();
			while (it.hasNext()){
				TextAttribute ta = (TextAttribute)it.next();
				String next = ta.getInfo();
				if (lineSep.equals("<br>")){
					// implies java tooltip
					next = convertHTMLTags(next);
				}
				sb.append(next);
				sb.append(lineSep);
			}
		}
		return sb;
	}
	
	public StringBuffer getTextAttrsForType(String lineSep, String type){
		StringBuffer sb = new StringBuffer();
		
		if (getTextList() != null){
			Iterator it = getTextList().iterator();
			while (it.hasNext()){
				TextAttribute ta = (TextAttribute)it.next();
				if (ta.getType().equals(type)){
					String next = ta.getInfo();
					if (lineSep.equals("<br>")){
						// implies java tooltip
						next = convertHTMLTags(next);
					}
					sb.append(next);
					sb.append(lineSep);
				}
			}
		}
		return sb;
	}
	
	public String convertHTMLTags(String next){
		if (next == null) return null;
		else {
			next = next.replaceAll("<", "&lt;");
			next = next.replaceAll(">", "&gt;");
			return next;
		}
	}
	

	// these two are maybe not accurate maybe
	// need to check if ln in question is between
	// the start and end ln's
	public boolean attrForJimpleLn(int jimple_ln) {
		if (getJimpleStartLn() == jimple_ln) return true;
		else return false;
	}
	
	public boolean attrForJavaLn(int java_ln) {
		if (getJavaStartLn() == java_ln) return true;
		else return false;
	}
	
	public SootAttribute() {
	}
	
	

	/**
	 * @return
	 */
	public int getJimpleEndPos() {
		return jimpleEndPos;
	}

	/**
	 * @return
	 */
	public int getJimpleStartPos() {
		return jimpleStartPos;
	}


	/**
	 * @param i
	 */
	public void setJimpleEndPos(int i) {
		jimpleEndPos = i;
	}

	/**
	 * @param i
	 */
	public void setJimpleStartPos(int i) {
		jimpleStartPos = i;
	}

	/**
	 * @return
	 */
	public ArrayList getTextList() {
		return textList;
	}

	/**
	 * @param list
	 */
	public void setTextList(ArrayList list) {
		textList = list;
	}



	/**
	 * @return
	 */
	public ArrayList getLinkList() {
		return linkList;
	}

	/**
	 * @param list
	 */
	public void setLinkList(ArrayList list) {
		linkList = list;
	}

    /**
     * @return
     */
    public int getJavaEndPos() {
        return javaEndPos;
    }

    /**
     * @return
     */
    public int getJavaStartPos() {
        return javaStartPos;
    }

    /**
     * @param i
     */
    public void setJavaEndPos(int i) {
        javaEndPos = i;
    }

    /**
     * @param i
     */
    public void setJavaStartPos(int i) {
        javaStartPos = i;
    }



	/**
	 * @return
	 */
	public int getJavaEndLn() {
		return javaEndLn;
	}

	/**
	 * @return
	 */
	public int getJavaStartLn() {
		return javaStartLn;
	}

	/**
	 * @return
	 */
	public int getJimpleEndLn() {
		return jimpleEndLn;
	}

	/**
	 * @return
	 */
	public int getJimpleStartLn() {
		return jimpleStartLn;
	}

	/**
	 * @param i
	 */
	public void setJavaEndLn(int i) {
		javaEndLn = i;
	}

	/**
	 * @param i
	 */
	public void setJavaStartLn(int i) {
		javaStartLn = i;
	}

	/**
	 * @param i
	 */
	public void setJimpleEndLn(int i) {
		jimpleEndLn = i;
	}

	/**
	 * @param i
	 */
	public void setJimpleStartLn(int i) {
		jimpleStartLn = i;
	}


	/**
	 * @return
	 */
	public int getJavaLength() {
		return javaLength;
	}

	/**
	 * @return
	 */
	public int getJimpleLength() {
		return jimpleLength;
	}

	/**
	 * @param i
	 */
	public void setJavaLength(int i) {
		javaLength = i;
	}

	/**
	 * @param i
	 */
	public void setJimpleLength(int i) {
		jimpleLength = i;
	}

	/**
	 * @return
	 */
	public ArrayList getColorList() {
		return colorList;
	}

	/**
	 * @param list
	 */
	public void setColorList(ArrayList list) {
		colorList = list;
	}

}
