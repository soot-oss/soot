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
