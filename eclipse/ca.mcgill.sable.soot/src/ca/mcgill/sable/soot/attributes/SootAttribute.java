package ca.mcgill.sable.soot.attributes;

/**
 * @author jlhotak
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
public class SootAttribute {

	private int java_ln;
	private int jimple_ln;
	private int jimple_offset_start;
	private int jimple_offset_end;
	private int colorKey;
	private String text;
	
	private String filename;
	
	
	/*public SootAttribute(int java_ln, int jimple_ln, 
		String text, String filename) {
		setJava_ln(java_ln);
		setJimple_ln(jimple_ln);
		setText(text);
		setFilename(filename);
	}*/
	
	public boolean attrForJimpleLn(int jimple_ln) {
		if (getJimple_ln() == jimple_ln) return true;
		else return false;
	}
	
	public boolean attrForJavaLn(int java_ln) {
		if (getJava_ln() == java_ln) return true;
		else return false;
	}
	
	public SootAttribute() {
	}
	
	/**
	 * Returns the filename.
	 * @return String
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Returns the java_ln.
	 * @return int
	 */
	public int getJava_ln() {
		return java_ln;
	}

	/**
	 * Returns the jimple_ln.
	 * @return int
	 */
	public int getJimple_ln() {
		return jimple_ln;
	}

	/**
	 * Returns the pkg_name.
	 * @return String
	 */
	/*public String getPkg_name() {
		return pkg_name;
	}

	/**
	 * Returns the project_name.
	 * @return String
	 */
	/*public String getProject_name() {
		return project_name;
	}

	/**
	 * Returns the root_filename.
	 * @return String
	 */
	/*public String getRoot_filename() {
		return root_filename;
	}

	/**
	 * Returns the text.
	 * @return String
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the filename.
	 * @param filename The filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Sets the java_ln.
	 * @param java_ln The java_ln to set
	 */
	public void setJava_ln(int java_ln) {
		this.java_ln = java_ln;
	}

	/**
	 * Sets the jimple_ln.
	 * @param jimple_ln The jimple_ln to set
	 */
	public void setJimple_ln(int jimple_ln) {
		this.jimple_ln = jimple_ln;
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
	public int getColorKey() {
		return colorKey;
	}

	/**
	 * @return
	 */
	public int getJimple_offset_end() {
		return jimple_offset_end;
	}

	/**
	 * @return
	 */
	public int getJimple_offset_start() {
		return jimple_offset_start;
	}

	/**
	 * @param i
	 */
	public void setColorKey(int i) {
		colorKey = i;
	}

	/**
	 * @param i
	 */
	public void setJimple_offset_end(int i) {
		jimple_offset_end = i;
	}

	/**
	 * @param i
	 */
	public void setJimple_offset_start(int i) {
		jimple_offset_start = i;
	}

}
