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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import ca.mcgill.sable.soot.editors.JimpleOutlineObject;

@SuppressWarnings("rawtypes")
public class JimpleFile {

	private String file;
	private IFile input;
	
	private ArrayList arr;
	private ArrayList fields;
	private ArrayList methods;
	private ArrayList modifiers;
	private int imageType;
	
	public static final String LEFT_BRACE = "{"; 
	public static final String RIGHT_BRACE = "}";
		
	public JimpleFile(IFile file){
		setInput(file);
		
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(getInput().getContents()));
			
			StringBuffer sb = new StringBuffer();
			String line;
			
			try {
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			setFile(sb.toString());	
			
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
	
	public boolean isJimpleFile() throws IOException, CoreException{
		
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
	
	public JimpleOutlineObject getOutline() {
		
		
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
	
	public int getStartOfSelected(String val) {
		
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(getInput().getContents()));
		} catch (CoreException e1) {
			e1.printStackTrace();
			return 0;
		}
		String search = getSearch(val).trim();

		int count = 0;
		int cur;
		String line = "";
		
		try {
			while ((cur = in.read()) != -1) {
				char c = (char) cur;
				
				if (c == '\n' || ("" + c).equals(System.getProperty("line.separator"))) {
					int index = line.indexOf(search);
					if (index != -1) {
						return count - line.length() + index;
					}
					
					line = "";
				}

				line += c;
				count += 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
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
	public IFile getInput() {
		return input;
	}
	
	public ArrayList getArr() {
		if (arr == null)
			initArr();
		
		return arr;
	}
	
	private void initArr() {
		ArrayList text = new ArrayList();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getInput().getContents()));
			
			while (true) {
				String nextLine = null;
				try {
					nextLine = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (nextLine == null) break;// || (nextLine.length() == 0)) break;
				text.add(nextLine);
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		arr = text;
	}

	/**
	 * @param list
	 */
	public void setInput(IFile file) {
		input = file;
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
