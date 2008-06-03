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

public class JimpleBody {

	private String text;
	private ArrayList textArr;
	private ArrayList methods;
	private ArrayList fields;
	
	
	public JimpleBody(String text, ArrayList textArr){
		setText(text); 
		setTextArr(textArr);
	}
	
	public boolean isJimpleBody() {
	
		return true;
	}
	
	
	public void parseBody(){
		
		// getTextArr().get(1) -> class line
		// ignore empty lines, first line with { and last
		// line with }
		setFields(new ArrayList());
		setMethods(new ArrayList());
		
		Iterator it = getTextArr().iterator();
		int counter = 0;
		boolean inMethod = false;
		while (it.hasNext()){
			String temp = (String)it.next();
			if ((temp.trim().equals("}")) && (inMethod)){
				inMethod = false;
			}
			if (!inMethod){
				if (counter < 2){
				}
				else if (JimpleField.isField(temp)){
					getFields().add(temp);
				}
				else if (JimpleMethod.isMethod(temp)){
					getMethods().add(temp);
					if (temp.indexOf(";") != -1){
					}
					else{
						inMethod = true;
					}
				}
			}
			counter++;
		}
		
	}
	
	/**
	 * @return String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 * @param text The text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return
	 */
	public ArrayList getTextArr() {
		return textArr;
	}

	/**
	 * @param list
	 */
	public void setTextArr(ArrayList list) {
		textArr = list;
	}

	/**
	 * @param list
	 */
	public void setFields(ArrayList list) {
		fields = list;
	}

	/**
	 * @param list
	 */
	public void setMethods(ArrayList list) {
		methods = list;
	}

	/**
	 * @return
	 */
	public ArrayList getFields() {
		return fields;
	}

	/**
	 * @return
	 */
	public ArrayList getMethods() {
		return methods;
	}

}
