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
		
		System.out.println("About to find Jimple body methods");
		//System.out.println(getText());
		// remove first and last braces
		setText(getText().substring(1));
		System.out.println("removed first {");	
		StringBuffer sb = new StringBuffer(getText());	
		// remove method bodies
		
		//System.out.println(sb.toString());
		while (true){
			//System.out.println(sb.toString());
			if ((sb.indexOf(JimpleFile.LEFT_BRACE) == -1) ||
				(sb.indexOf(JimpleFile.RIGHT_BRACE) == -1)) break;
				
			int leftpos = sb.indexOf(JimpleFile.LEFT_BRACE);
			int rightpos = sb.indexOf(JimpleFile.RIGHT_BRACE);
			System.out.println("Braces Pos: "+leftpos+" "+rightpos);
			sb.replace(leftpos, rightpos+1, "METHODBODY");	
		}
		
		StringTokenizer st = new StringTokenizer(sb.toString());
		while (st.hasMoreTokens()){
			String next = st.nextToken();
			System.out.println(next);
			
		}
		
		//ArrayList fields = new ArrayList();
		ArrayList methods = new ArrayList();
		//StringBuffer allFields = new StringBuffer(sb.substring(0, sb.lastIndexOf(";")));
		StringBuffer allMethods;
		if (sb.lastIndexOf(";") != -1) {
			allMethods = new StringBuffer(sb.substring(sb.lastIndexOf(";")+1, sb.length()));
		}
		else {
			allMethods = sb;
		}
		System.out.println(allMethods);
		//StringTokenizer removeFields = new StringTokenizer(sb.toString(), ";");
		StringTokenizer findMethods = new StringTokenizer(allMethods.toString());
		StringBuffer method = new StringBuffer();
		while (findMethods.hasMoreTokens()) {
		
			
			String next = findMethods.nextToken();
			if (next.equals("METHODBODY")) {
				System.out.println(method);
				methods.add(method);
				method = new StringBuffer();
				continue;
			}
			method.append(next+" ");
		}
		return methods;
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
