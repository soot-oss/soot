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


package ca.mcgill.sable.soot.editors.parser;

import java.util.*;

import ca.mcgill.sable.soot.editors.JimpleOutlineObject;

public class JimpleField {

	private String val;
	private String label;
	private String type;
	private ArrayList modifiers;
	private int imageType;
	
	public JimpleField(String val){
		setVal(val);
	}
	
	public void parseField(){
		StringTokenizer st = new StringTokenizer(getVal());
		int numTokens = st.countTokens();
		int counter = 1;
		String tempType = "";
		while (st.hasMoreTokens()){
			String next = st.nextToken();
			if (JimpleModifier.isModifier(next)) {
				if (getModifiers() == null){
					setModifiers(new ArrayList());
				}
				getModifiers().add(next);
			}
			if (!JimpleModifier.isModifier(next) && (counter != numTokens)){
				tempType = tempType + next;
			}
			if (counter == numTokens){
				setType(tempType);
				setLabel(next.substring(0, next.indexOf(";"))+" : "+getType());
			}
			counter++;
		}
	}
	
	public void findImageType(){
		if (getModifiers() == null){
			setImageType(JimpleOutlineObject.NONE_FIELD);
			return;
		}
		if (getModifiers().contains("public")) {
			setImageType(JimpleOutlineObject.PUBLIC_FIELD);
		}
		else if (getModifiers().contains("protected")) {
			setImageType(JimpleOutlineObject.PROTECTED_FIELD);
		}
		else if (getModifiers().contains("private")) {
			setImageType(JimpleOutlineObject.PRIVATE_FIELD);
		}
		else {
			setImageType(JimpleOutlineObject.NONE_FIELD);
		}
	}
	
	public BitSet findDecorators() {
		BitSet bits = new BitSet();
		if (getModifiers() == null) return bits;
		if (getModifiers().contains("abstract")){
			bits.set(JimpleOutlineObject.ABSTRACT_DEC);	
		}
		if (getModifiers().contains("final")){
			bits.set(JimpleOutlineObject.FINAL_DEC);	
		}
		if (getModifiers().contains("static")){
			bits.set(JimpleOutlineObject.STATIC_DEC);	
		}
		if (getModifiers().contains("synchronized")){
			bits.set(JimpleOutlineObject.SYNCHRONIZED_DEC);	
		}
		return bits;
	}
	
	public static boolean isField(String val){
		
		if ((val.indexOf("(") != -1) || (val.indexOf(")") != -1)) return false;
		if (val.indexOf(";") == -1) return false;
		return true;
	}
	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return
	 */
	public ArrayList getModifiers() {
		return modifiers;
	}

	/**
	 * @return
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		label = string;
	}

	/**
	 * @param list
	 */
	public void setModifiers(ArrayList list) {
		modifiers = list;
	}

	/**
	 * @param string
	 */
	public void setVal(String string) {
		val = string;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * @return
	 */
	public int getImageType() {
		return imageType;
	}

	/**
	 * @param i
	 */
	public void setImageType(int i) {
		imageType = i;
	}

}
