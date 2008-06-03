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

package ca.mcgill.sable.soot.attributes;

import java.io.*;


public class AttributeFileReader {

	private String filename;
	
	/**
	 * Method AttributeFileReader.
	 * @param filename
	 */
	public AttributeFileReader(String filename) {
		setFilename(filename);
	}
	
	/**
	 * Method readFile.
	 * @return String
	 * reads given file trimming white space
	 */
	public String readFile() {
		StringBuffer file = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader( new
					FileReader(getFilename()));
			while (true) {
				String temp = br.readLine();
				if (temp == null) break;
				temp = temp.trim();
				file.append(temp);
							
			}
			
		}
		catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
		return file.toString();
			
	}
	
	/**
	 * Returns the filename.
	 * @return String
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the filename.
	 * @param filename The filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

}
