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
	private ArrayList arr;
	private ArrayList fields;
	private ArrayList methods;
	private ArrayList modifiers;
	private int imageType;
	
	public static final String LEFT_BRACE = "{"; 
	public static final String RIGHT_BRACE = "}";
		
	public JimpleFile(ArrayList file){
		setArr(file);
		Iterator it = file.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()){
			sb.append((String)it.next());
		}
		setFile(sb.toString());	
	}
	
	public boolean isJimpleFile(){
		
		// handles body section
		
		StringBuffer sb = new StringBuffer(getFile());
		int leftBracePos = getFile().indexOf(LEFT_BRACE);
		int rightBracePos = getFile().lastIndexOf(RIGHT_BRACE);
		
		JimpleBody jBody = new JimpleBody(sb.subSequence(leftBracePos, rightBracePos).toString(), getArr());
		if (!jBody.isJimpleBody()) return false;
		
		return true;
	}
	
	private int findImageType() {
		if (getModifiers() == null) return JimpleOutlineObject.CLASS;
		else {
			if (getModifiers().contains("interface")) return JimpleOutlineObject.INTERFACE;
			else return JimpleOutlineObject.CLASS;
		}
	}
	
	private BitSet findDecorators() {
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
	
	public JimpleOutlineObject getOutline(){
		
		
		StringBuffer sb = new StringBuffer(getFile());
		int leftBracePos = getFile().indexOf(LEFT_BRACE);
		int rightBracePos = getFile().lastIndexOf(RIGHT_BRACE);
		
		// get key - class name
		StringTokenizer st = new StringTokenizer(sb.substring(0, leftBracePos));
		String className = null;
		while (true) {
			String token = st.nextToken();
			if (JimpleModifier.isModifier(token)) {
				if (getModifiers() == null){
					setModifiers(new ArrayList());
				}
				getModifiers().add(token);
				continue; 
			} 
			if (isFileType(token)) {
				if (getModifiers() == null){
					setModifiers(new ArrayList());
				}
				getModifiers().add(token);
				continue;
			} 
			className = token;
			break;
		}
		System.out.println(className);
		
		JimpleOutlineObject outline = new JimpleOutlineObject("", JimpleOutlineObject.NONE, null);
		
		
		JimpleOutlineObject file  = new JimpleOutlineObject(className, findImageType(), findDecorators());
		outline.addChild(file);
		
		// gets methods
		JimpleBody jBody = new JimpleBody(sb.substring(leftBracePos, rightBracePos), getArr());
		
		jBody.parseBody();
		ArrayList fieldLabels = jBody.getFields();
		
		Iterator itF = fieldLabels.iterator();
		while (itF.hasNext()){
			JimpleField field = new JimpleField(itF.next().toString());
			field.parseField();
			field.findImageType();
			if (getFields() == null){
				setFields(new ArrayList());
			}
			getFields().add(field);
			file.addChild(new JimpleOutlineObject(field.getLabel(), field.getImageType(), field.findDecorators() ));
		}
		
		ArrayList methodLabels =jBody.getMethods();
		
		Iterator it = methodLabels.iterator();
		while (it.hasNext()){
			JimpleMethod method = new JimpleMethod(it.next().toString());
			method.parseMethod();
			method.findImageType();
			if (getMethods() == null){
				setMethods(new ArrayList());
			}
			getMethods().add(method);
			file.addChild(new JimpleOutlineObject(method.getLabel(), method.getImageType(), method.findDecorators()));
		}
		
		
		/*HashMap outline = new HashMap();
		outline.put(className, methods);*/
		return outline;
	}
	
	public String getSearch(String val){
		Iterator it;
		String search = val;
		if (getFields() != null) {
			it = getFields().iterator();
			while (it.hasNext()) {
				JimpleField next = (JimpleField)it.next();
				if (val.equals(next.getLabel())){
					search = next.getVal();
				}
			}
		}
		if (getMethods() != null) {
			it = getMethods().iterator();
			while (it.hasNext()) {
				JimpleMethod next = (JimpleMethod)it.next();
				if (val.equals(next.getLabel())){
					search = next.getVal();
				}
			}
		}
		return search;
	}
	
	public int getStartOfSelected(String val){
		
		Iterator it;
		String search = getSearch(val);
				
		it = getArr().iterator();
		int count = 0;
		while (it.hasNext()){
			String temp = (String)it.next();
			//System.out.println("temp: "+temp);
			//System.out.println("val: "+val);
			if (temp.indexOf(search.trim()) != -1){
				//System.out.println("val index: "+	temp.indexOf(val.trim()));
				count = count + temp.indexOf(search.trim());
				//System.out.println("count: "+count);
				return count;
			}
			count = count + temp.length() + 1;
		}
		return count;
	}
	
	public int getLength(String val){
		String search = getSearch(val);
		search = search.trim();
		return search.length();
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

	/**
	 * @return
	 */
	public ArrayList getArr() {
		return arr;
	}

	/**
	 * @param list
	 */
	public void setArr(ArrayList list) {
		arr = list;
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
	public ArrayList getModifiers() {
		return modifiers;
	}

	/**
	 * @param list
	 */
	public void setModifiers(ArrayList list) {
		modifiers = list;
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
