/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors.parser;

import java.util.*;

import ca.mcgill.sable.soot.editors.JimpleOutlineObject;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleMethod {


	private String val;
	private String label;
	private String type;
	private ArrayList modifiers;
	private int imageType;
		
	public static boolean isMethod(String val){
		if ((val.indexOf("(") != -1) && (val.indexOf(")") != -1)) return true; 
		return false;
	}
	
	public JimpleMethod(String val){
		setVal(val);
	}
	
	public void parseMethod(){
		StringTokenizer st = new StringTokenizer(getVal());
		int numTokens = st.countTokens();
		String tempLabel = "";
		boolean addLabel = false;
		while (st.hasMoreTokens()){
			String next = st.nextToken();
			System.out.println("jimple method next: "+next);
			if (JimpleModifier.isModifier(next)) {
				if (getModifiers() == null){
					setModifiers(new ArrayList());
				}
				getModifiers().add(next);
			}
			if (next.indexOf("(") != -1){
				addLabel = true;
			}
			if (addLabel){
				tempLabel = tempLabel + next;
				tempLabel = tempLabel + " ";
			}
		
		}
		setLabel(tempLabel);	
	}
	
	public void findImageType(){
		if (getModifiers() == null){
			setImageType(JimpleOutlineObject.NONE);
			return;
		}
		if (getModifiers().contains("public")) {
			setImageType(JimpleOutlineObject.PUBLIC_METHOD);
		}
		else if (getModifiers().contains("protected")) {
			setImageType(JimpleOutlineObject.PROTECTED_METHOD);
		}
		else if (getModifiers().contains("private")) {
			setImageType(JimpleOutlineObject.PRIVATE_METHOD);
		}
		else {
			setImageType(JimpleOutlineObject.NONE);
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
	
	/**
	 * @return
	 */
	public int getImageType() {
		return imageType;
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
	public String getType() {
		return type;
	}

	/**
	 * @return
	 */
	public String getVal() {
		return val;
	}

	/**
	 * @param i
	 */
	public void setImageType(int i) {
		imageType = i;
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
	public void setType(String string) {
		type = string;
	}

	/**
	 * @param string
	 */
	public void setVal(String string) {
		val = string;
	}

}
