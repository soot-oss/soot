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
public class JimpleFile {

	private String file;
	public static final String LEFT_BRACE = "{"; 
	public static final String RIGHT_BRACE = "}";
	public JimpleFile(String file){
		setFile(file);	
	}
	
	public boolean isJimpleFile(){
		
		// handles body section
		
		StringBuffer sb = new StringBuffer(getFile());
		int leftBracePos = getFile().indexOf(LEFT_BRACE);
		int rightBracePos = getFile().lastIndexOf(RIGHT_BRACE);
		
		JimpleBody jBody = new JimpleBody(sb.subSequence(leftBracePos, rightBracePos).toString());
		if (!jBody.isJimpleBody()) return false;
		
		return true;
	}
	
	public JimpleOutlineObject getOutline(){
		
		ArrayList list = new ArrayList();
		list.add("method 1");
		list.add("method 2");
		
		//HashMap outline = new HashMap();
		
		
		
		
		StringBuffer sb = new StringBuffer(getFile());
		int leftBracePos = getFile().indexOf(LEFT_BRACE);
		int rightBracePos = getFile().lastIndexOf(RIGHT_BRACE);
		
		// get key - class name
		StringTokenizer st = new StringTokenizer(sb.substring(0, leftBracePos));
		String className = null;
		while (true) {
			String token = st.nextToken();
			if (JimpleModifier.isModifier(token)) continue;
			if (isFileType(token)) continue;
			className = token;
			break;
		}
		System.out.println(className);
		
		JimpleOutlineObject outline = new JimpleOutlineObject("");
		
		JimpleOutlineObject file  = new JimpleOutlineObject(className);
		outline.addChild(file);
		
		
		//file.addChild(new JimpleOutlineObject("method 1"));
		//file.addChild(new JimpleOutlineObject("method 2"));
		//file.addChild(new JimpleOutlineObject("method 3"));
		//file.addChild(new JimpleOutlineObject("method 4"));
		
		// gets methods
		JimpleBody jBody = new JimpleBody(sb.substring(leftBracePos, rightBracePos));
		ArrayList methods = jBody.getMethods();
		
		Iterator it = methods.iterator();
		while (it.hasNext()){
			file.addChild(new JimpleOutlineObject(it.next().toString()));
		}
		/*HashMap outline = new HashMap();
		outline.put(className, methods);*/
		return outline;
	}
	
	
	private boolean isFileType(String token) {
			HashSet filetypes = new HashSet();
			filetypes.add("class");
			filetypes.add("interface");
					
			if (filetypes.contains(token)) return true;
			else return false;
	}
	
	
	/**
	 * @return String
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

}
