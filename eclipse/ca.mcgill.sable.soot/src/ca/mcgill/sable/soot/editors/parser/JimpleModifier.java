/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors.parser;

import java.util.HashSet;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleModifier {

		
	public static boolean isModifier(String token) {
		HashSet modifiers = new HashSet();
		modifiers.add("abstract");
		modifiers.add("final");
		modifiers.add("native");
		modifiers.add("public");
		modifiers.add("protected");
		modifiers.add("private");
		modifiers.add("static");
		modifiers.add("synchronized");
		modifiers.add("transient");
		modifiers.add("volatile");
		
		if (modifiers.contains(token)) return true;
		else return false;
	}
}
