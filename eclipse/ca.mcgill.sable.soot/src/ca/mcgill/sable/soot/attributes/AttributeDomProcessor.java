package ca.mcgill.sable.soot.attributes;

import java.util.*;
//import javax.xml.parsers.*;

import org.w3c.dom.*;
//import org.xml.sax.*;

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
public class AttributeDomProcessor {

	Document domDoc;
	Vector attributes;
	private SootAttribute current;
	
	/**
	 * Method AttributeDomProcessor.
	 * @param domDoc
	 */
	public AttributeDomProcessor(Document domDoc) {
		setDomDoc(domDoc);
	}
	
	/**
	 * Method processAttributesDom.
	 */
	public void processAttributesDom() {
		processNode(getDomDoc());
		Iterator it = getAttributes().iterator();
		while (it.hasNext()) {
			SootAttribute sa = (SootAttribute)it.next();
			System.out.println("New Attribute");
			System.out.println("Java Line: "+sa.getJava_ln());
			System.out.println("Jimple Line: "+sa.getJimple_ln());
			System.out.println("Text: "+sa.getText());
			System.out.println();
		}
	}
	
	/*
	 * processes nodes expecting the following layout:
	 * <attributes>
	 * <attribute>
	 * <java_ln>INT</java_ln>
	 * <jimple_ln>INT</jimple_ln>
	 * <text>STRING</text>
	 * </attribute>
	 * ...
	 * </attributes>
	 */
	private void processNode(Node node) {
		System.out.println(node.getNodeName()+node.getNodeType());
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				setAttributes(new Vector());
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}			
			}
			else {
				System.out.println("children are null");
			}
		}
		else if (node.getNodeType() == Node.ELEMENT_NODE) {
			if ( node.getNodeName().compareTo("attribute") == 0) {
				if (current != null) {
					//getAttributes().add(current);
				}
				current = new SootAttribute();
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
			}
			else {//if (node.getNodeName().compareTo("attributes") == 0) {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE) {
			System.out.println(node.getParentNode().getNodeName()+" "+node.getNodeValue());
			if (node.getParentNode().getNodeName().compareTo("java_ln") == 0 ) {
			System.out.println(node.getNodeValue());
				current.setJava_ln((new Integer(node.getNodeValue())).intValue());
			}
			else if (node.getParentNode().getNodeName().compareTo("jimple_ln") == 0) {
			System.out.println(node.getNodeValue());
				current.setJimple_ln((new Integer(node.getNodeValue())).intValue());
			}
			else if (node.getParentNode().getNodeName().compareTo("text") == 0) {
			System.out.println(node.getNodeValue());
				current.setText(node.getNodeValue());
				System.out.println("just before add "+current.java_ln+" "+current.jimple_ln+" "+current.text);
				getAttributes().add(current);
				System.out.println("in attrList "+getAttributes().capacity()+"size: "+getAttributes().size());
			}
		}	
		else {
			//System.out.println(node.getNodeName()+node.getNodeType());
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

	/**
	 * Returns the attributes.
	 * @return Vector
	 */
	public Vector getAttributes() {
		return attributes;
	}

	/**
	 * Returns the current.
	 * @return SootAttribute
	 */
	public SootAttribute getCurrent() {
		return current;
	}

	/**
	 * Sets the attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(Vector attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets the current.
	 * @param current The current to set
	 */
	public void setCurrent(SootAttribute current) {
		this.current = current;
	}

}
