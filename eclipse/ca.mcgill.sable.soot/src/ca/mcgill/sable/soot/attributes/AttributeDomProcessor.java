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
	private ValueBoxAttribute vbAttr;
	
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
			System.out.println("New Attribute"); //$NON-NLS-1$
			System.out.println("Java Line: "+sa.getJava_ln()); //$NON-NLS-1$
			System.out.println("Jimple Line: "+sa.getJimple_ln()); //$NON-NLS-1$
			System.out.println("Jimple Offset Start: "+sa.getJimple_offset_start()); //$NON-NLS-1$
			System.out.println("Jimple Offset End: "+sa.getJimple_offset_end()); //$NON-NLS-1$
			System.out.println("Jimple Color Key: "+sa.getColorKey()); //$NON-NLS-1$
			System.out.println("Text: "+sa.getText()); //$NON-NLS-1$
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
		//System.out.println(node.getNodeName()+node.getNodeType());
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				setAttributes(new Vector());
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}			
			}
			else {
				System.out.println("children are null"); //$NON-NLS-1$
			}
		}
		else if (node.getNodeType() == Node.ELEMENT_NODE) {
			if ( node.getNodeName().compareTo(Messages.getString("AttributeDomProcessor.attribute")) == 0) { //$NON-NLS-1$
				if (current != null) {
					//getAttributes().add(current);
				}
				current = new SootAttribute();
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
				getAttributes().add(current);
			}
			else if (node.getNodeName().equals("value_box_attribute")){
				NodeList children = node.getChildNodes();
				vbAttr = new ValueBoxAttribute();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
				current.addValueAttr(vbAttr);
			}
			else {//if (node.getNodeName().compareTo("attributes") == 0) {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE) {
			System.out.println(node.getParentNode().getNodeName()+" "+node.getNodeValue()); //$NON-NLS-1$
			if (node.getParentNode().getNodeName().equals("java_ln")){			//System.out.println(node.getNodeValue());
				current.setJava_ln((new Integer(node.getNodeValue())).intValue());
			}
			else if (node.getParentNode().getNodeName().equals("jimple_ln")) {
				//System.out.println(node.getNodeValue());
				current.setJimple_ln((new Integer(node.getNodeValue())).intValue());
			}
			else if (node.getParentNode().getNodeName().equals("startOffset")){
				System.out.println(node.getParentNode().getNodeName()+" "+node.getNodeValue()); //$NON-NLS-1$
				//current.setJimple_offset_start((new Integer(node.getNodeValue()).intValue()));
				vbAttr.setStartOffset((new Integer(node.getNodeValue()).intValue()));
			}
			else if (node.getParentNode().getNodeName().equals("endOffset")){ //$NON-NLS-1$
				System.out.println(node.getNodeValue());
				//current.setJimple_offset_end((new Integer(node.getNodeValue()).intValue()));
				vbAttr.setEndOffset((new Integer(node.getNodeValue()).intValue()));
			}
			else if (node.getParentNode().getNodeName().equals("red")){
				vbAttr.setRed((new Integer(node.getNodeValue()).intValue()));
			}
			else if (node.getParentNode().getNodeName().equals("green")){
				vbAttr.setGreen((new Integer(node.getNodeValue()).intValue()));
			}
			else if (node.getParentNode().getNodeName().equals("blue")){
				vbAttr.setBlue((new Integer(node.getNodeValue()).intValue()));
			}
			else if (node.getParentNode().getNodeName().equals("text")) {
			//System.out.println(node.getNodeValue());
				current.addTextAttr(node.getNodeValue());
				//System.out.println("just before add "+current.java_ln+" "+current.jimple_ln+" "+current.text);
				//getAttributes().add(current);
				//System.out.println("in attrList "+getAttributes().capacity()+"size: "+getAttributes().size()); //$NON-NLS-1$ //$NON-NLS-2$
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
