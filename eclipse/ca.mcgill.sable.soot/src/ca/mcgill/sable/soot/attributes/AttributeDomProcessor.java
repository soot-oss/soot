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


public class AttributeDomProcessor {

	Document domDoc;
	ArrayList attributes;
	private ArrayList keys;
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

		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				setAttributes(new ArrayList());
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}			
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
			else if (node.getNodeName().equals("key")){
				if (keys == null){
					keys = new ArrayList();
				}
				NamedNodeMap map = node.getAttributes();
				AnalysisKey key = new AnalysisKey();
				key.setRed((new Integer(map.getNamedItem("red").getNodeValue())).intValue());
				key.setGreen((new Integer(map.getNamedItem("green").getNodeValue())).intValue());
				key.setBlue((new Integer(map.getNamedItem("blue").getNodeValue())).intValue());
				key.setKey(map.getNamedItem("key").getNodeValue());
				key.setType(map.getNamedItem("aType").getNodeValue());
				keys.add(key);
			}
			else {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					processNode(children.item(i));
				}
			}
			
		}
		
	}
	
	private void processAttributeNode(SootAttribute current, Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (node.getNodeName().equals("link")){
				NamedNodeMap map = node.getAttributes();
				LinkAttribute la = new LinkAttribute();
				
				la.setLabel(map.getNamedItem("label").getNodeValue());
				la.setJavaLink((new Integer(map.getNamedItem("srcLink").getNodeValue()).intValue()));
				la.setJimpleLink((new Integer(map.getNamedItem("jmpLink").getNodeValue()).intValue()));
				la.setClassName(map.getNamedItem("clssNm").getNodeValue());
				la.setType(map.getNamedItem("aType").getNodeValue());
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
				ca.type(map.getNamedItem("aType").getNodeValue());
				current.addColorAttr(ca);//.setColor(ca);
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
			else if (node.getNodeName().equals("text")){
				NamedNodeMap map = node.getAttributes();
				TextAttribute ta = new TextAttribute();
				ta.setInfo(map.getNamedItem("info").getNodeValue());
				ta.setType(map.getNamedItem("aType").getNodeValue());
				current.addTextAttr(ta);
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

	/**
	 * @return
	 */
	public ArrayList getKeys() {
		return keys;
	}

	/**
	 * @param list
	 */
	public void setKeys(ArrayList list) {
		keys = list;
	}

}
