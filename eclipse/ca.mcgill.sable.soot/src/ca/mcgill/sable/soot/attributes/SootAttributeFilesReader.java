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

import ca.mcgill.sable.soot.util.*;
//import org.eclipse.core.resources.*;
import org.w3c.dom.Document;



public class SootAttributeFilesReader {

	/**
	 * @see java.lang.Object#Object()
	 */
	public SootAttributeFilesReader() {
	}

	public AttributeDomProcessor readFile(String full_filename) {
		AttributeFileReader afr = new AttributeFileReader(full_filename);
		String file = afr.readFile();
		if ((file == null) || (file.length() == 0)) return null;
		
		file = file.replaceAll("\"", "\\\"");
		
		StringToDom domMaker = new StringToDom();
		domMaker.getDocFromString(file);
		Document domDoc = domMaker.getDomDoc();
		
		AttributeDomProcessor adp = new AttributeDomProcessor(domDoc);
		adp.processAttributesDom();
		return adp;
					
	}
	
	
	public String fileToNoExt(String filename) {
		
		return filename.substring(0, filename.lastIndexOf('.'));
	}
}
