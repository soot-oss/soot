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

import java.util.*;
//import javax.xml.parsers.*;

import org.w3c.dom.*;
//import org.xml.sax.*;

public class AttributeDomProcessor {

	Document domDoc;
	ArrayList attributes;
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
				
	}
	

	private void processNode(Node node) {

		//System.out.println("Start Processing: "+System.currentTimeMillis());
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				setAttributes(new ArrayList());
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}			
			}
			else {
				//System.out.println("children are null"); 
			}
		}
		else if (node.getNodeType() == Node.ELEMENT_NODE) {
			if ( node.getNodeName().equals("attribute")) { 
				
				current = new SootAttribute();
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processAttributeNode(current, children.item(i));
				}
				getAttributes().add(current);
			}
			else {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
			}
			
		}
		else if (node.getNodeType() == Node.TEXT_NODE) {	
			
		}	
		else {
		
		}
		//System.out.println("Stop Processing: "+System.currentTimeMillis());
		
	}
	
	private void processAttributeNode(SootAttribute current, Node node) {

		//System.out.println("node type: "+node.getNodeType());
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			/*if (node.getNodeName().equals("value_box_attribute")){
				NodeList children = node.getChildNodes();
				PosColAttribute vbAttr = new PosColAttribute();
				for (int i = 0; i < children.getLength(); i++) {
					processVBNode(vbAttr, children.item(i));
				}
				current.addValueAttr(vbAttr);
			}*/
			if (node.getNodeName().equals("link")){
				NamedNodeMap map = node.getAttributes();
				//NodeList children = node.getChildNodes();
				LinkAttribute la = new LinkAttribute();
				
				la.setLabel(map.getNamedItem("label").getNodeValue());
				la.setJavaLink((new Integer(map.getNamedItem("srcLink").getNodeValue()).intValue()));
				la.setJimpleLink((new Integer(map.getNamedItem("jmpLink").getNodeValue()).intValue()));
				la.setClassName(map.getNamedItem("clssNm").getNodeValue());
				
				
				/*for (int i = 0; i < children.getLength(); i++){
					processLinkNode(la,children.item(i));	
				}*/
				current.addLinkAttr(la);
			}
			else if (node.getNodeName().equals("color")){
				NamedNodeMap map = node.getAttributes();
				int r = (new Integer(map.getNamedItem("r").getNodeValue())).intValue();
				int g = (new Integer(map.getNamedItem("g").getNodeValue())).intValue();
				int b = (new Integer(map.getNamedItem("b").getNodeValue())).intValue();
				int fgInt = (new Integer(map.getNamedItem("fg").getNodeValue())).intValue();
				boolean fg = false;
				if (fgInt == 1){
					fg = true;
				}
				ColorAttribute ca = new ColorAttribute(r, g, b, fg);
				current.setColor(ca);
			}
			else if (node.getNodeName().equals("srcPos")){
				NamedNodeMap map = node.getAttributes();
				int sline = (new Integer(map.getNamedItem("sline").getNodeValue())).intValue();
				int eline = (new Integer(map.getNamedItem("eline").getNodeValue())).intValue();
				int spos = (new Integer(map.getNamedItem("spos").getNodeValue())).intValue();
				int epos = (new Integer(map.getNamedItem("epos").getNodeValue())).intValue();
				
				current.setJavaStartLn(sline);
				current.setJavaEndLn(eline);
				current.setJavaStartPos(spos);
				current.setJavaEndPos(epos);
			}
			else if (node.getNodeName().equals("jmpPos")){
				NamedNodeMap map = node.getAttributes();
				int sline = (new Integer(map.getNamedItem("sline").getNodeValue())).intValue();
				int eline = (new Integer(map.getNamedItem("eline").getNodeValue())).intValue();
				int spos = (new Integer(map.getNamedItem("spos").getNodeValue())).intValue();
				int epos = (new Integer(map.getNamedItem("epos").getNodeValue())).intValue();
	
				current.setJimpleStartLn(sline);
				current.setJimpleEndLn(eline);
				current.setJimpleStartPos(spos);
				current.setJimpleEndPos(epos);
			}
			else {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processAttributeNode(current, children.item(i));
				}
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE){
			String type = node.getParentNode().getNodeName();
			

			if (type.equals("text")) {
				//System.out.println("reading text node");
				current.addTextAttr(node.getNodeValue());
			}
		}
	}
	
	/*private void processVBNode(PosColAttribute vbAttr, Node node){
		if (node.getNodeType() == Node.ELEMENT_NODE){
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				processVBNode(vbAttr, children.item(i));
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE){
			String type = node.getParentNode().getNodeName();
				
			if (type.equals("jimpleStartPos")){
				vbAttr.setStartOffset((new Integer(node.getNodeValue()).intValue()));
			}
			else if (type.equals("jimpleEndPos")){ 
				vbAttr.setEndOffset((new Integer(node.getNodeValue()).intValue()));
			}
            else if (type.equals("javaStartPos")){
                vbAttr.setSourceStartOffset((new Integer(node.getNodeValue()).intValue()));
                //System.out.println("java start offset: "+vbAttr.getSourceStartOffset());
                           
            }
            else if (type.equals("javaEndPos")){ 
                vbAttr.setSourceEndOffset((new Integer(node.getNodeValue()).intValue()));
                //System.out.println("java end offset: "+vbAttr.getSourceEndOffset());
                 
            }
			else if (type.equals("red")) {
				vbAttr.setRed((new Integer(node.getNodeValue()).intValue()));
			}
			else if (type.equals("green")) {
				vbAttr.setGreen((new Integer(node.getNodeValue()).intValue()));
			}
			else if (type.equals("blue")){
				vbAttr.setBlue((new Integer(node.getNodeValue()).intValue()));
			}
			else if (type.equals("fg")){
				vbAttr.setFg((new Integer(node.getNodeValue()).intValue()));
			}
		
		}
	}
	
	private void processLinkNode(LinkAttribute la, Node node){
		
		if (node.getNodeType() == Node.ELEMENT_NODE){
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				processLinkNode(la, children.item(i));
			}
		}
		else if (node.getNodeType() == Node.TEXT_NODE){
			String type = node.getParentNode().getNodeName();
			if (type.equals("jimple_link")){
				la.setJimpleLink((new Integer(node.getNodeValue())).intValue());	
			}
			else if (type.equals("java_link")){
				la.setJavaLink((new Integer(node.getNodeValue())).intValue());	
			}
			else if (type.equals("link_label")){
				la.setLabel(node.getNodeValue());
			}
			else if (type.equals("className")){
				la.setClassName(node.getNodeValue());
			}
		}
	}*/
	
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
	public ArrayList getAttributes() {
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
	public void setAttributes(ArrayList attributes) {
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
