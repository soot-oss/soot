/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors.parser;

import java.util.*;

//import ca.mcgill.sable.soot.editors.JimpleOutlineObject;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
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
			//System.out.println("temp: "+temp);
			if ((temp.trim().equals("}")) && (inMethod)){
				inMethod = false;
			}
			if (!inMethod){
				if (counter < 2){
				}
				else if (JimpleField.isField(temp)){
					getFields().add(temp);
					//System.out.println("is field");	
				}
				else if (JimpleMethod.isMethod(temp)){
					getMethods().add(temp);
					if (temp.indexOf(";") != -1){
					}
					else{
						inMethod = true;
					}
					//System.out.println("is method");
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
