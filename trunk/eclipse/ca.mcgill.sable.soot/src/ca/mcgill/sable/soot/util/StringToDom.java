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

package ca.mcgill.sable.soot.util;

import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class StringToDom {

	private Document domDoc;

	public StringToDom() {
		setDomDoc(null);
	}

	public void getDocFromString(String to_convert) {


		try {
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(to_convert));
			setDomDoc(builder.parse(is));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		

	}
	/**
		* Returns the domDoc.
		* @return Document
		*/
	public Document getDomDoc() {
		return domDoc;
	}

	/**
	 * Sets the domDoc.
		* @param domDoc The domDoc to set
	 */
	public void setDomDoc(Document domDoc) {
		this.domDoc = domDoc;
	}

}
