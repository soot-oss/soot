/*
 * Created on 19-Mar-2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.editors.parser;

import java.util.*;

/**
 * @author jlhotak
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JimpleBody {

	private String text;
	
	public JimpleBody(String text){
		setText(text); 
	}
	
	public boolean isJimpleBody() {
	
		return true;
	}
	
	public ArrayList getMethods() {
		
			
		// remove first and last braces
		setText(getText().replaceFirst("{", ""));
			
		StringBuffer sb = new StringBuffer(getText());	
		// remove method bodies
		
		System.out.println(sb.toString());
		while (true){
			System.out.println(sb.toString());
			if ((sb.indexOf(JimpleFile.LEFT_BRACE) != -1) ||
				(sb.indexOf(JimpleFile.RIGHT_BRACE) != -1)) break;
				
			int leftpos = sb.indexOf(JimpleFile.LEFT_BRACE);
			int rightpos = sb.indexOf(JimpleFile.RIGHT_BRACE);
			System.out.println(leftpos+" "+rightpos);
			sb.replace(leftpos, rightpos+1, "");	
		}
		
		StringTokenizer st = new StringTokenizer(getText());
		while (st.hasMoreTokens()){
			String next = st.nextToken();
			System.out.println(next);
			
		}
		return new ArrayList();
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

}
