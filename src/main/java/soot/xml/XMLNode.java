package soot.xml;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 David Eng
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/** XML helper */
public class XMLNode extends XMLRoot {
  // constants
  public static final int TAG_STRING_BUFFER = 4096;

  // node pointers
  public XMLNode next = null; // -> to next node
  public XMLNode prev = null; // -> to previous node
  public XMLNode parent = null; // -> to parent node
  public XMLNode child = null; // -> to child node
  public XMLRoot root = null; // -> to root node

  public XMLNode(String in_name, String in_value, String[] in_attributes, String[] in_values) {
    name = in_name;
    value = in_value;
    attributes = in_attributes;
    values = in_values;
  }

  public XMLNode(XMLNode node) {
    if (node != null) {
      name = node.name;
      value = node.value;
      attributes = node.attributes;
      values = node.values;

      if (node.child != null) {
        this.child = (XMLNode) node.child.clone();
      }
      if (node.next != null) {
        this.next = (XMLNode) node.next.clone();
      }
    }
  }

  public Object clone() {
    return new XMLNode(this);
  }

  public String toPostString() {
    return toPostString("");
  }

  public String toPostString(String indent) {
    if (next != null) {
      return this.toString(indent) + next.toPostString(indent);
    } else {
      return this.toString(indent);
    }
  }

  // returns the number of children
  public int getNumberOfChildren() {
    int count = 0;
    if (child != null) {
      XMLNode current = child;
      while (current != null) {
        current = current.next;
        count++;
      }
    }
    return count;
  }

  // adds an attribute to an element
  public XMLNode addAttribute(String attribute, String value) {
    // check if this attribute already exists
    String[] tempAttributes = this.attributes;
    String[] tempValues = this.values;
    this.attributes = new String[tempAttributes.length + 1];
    this.values = new String[tempValues.length + 1];
    for (int i = 0; i < tempAttributes.length; i++) {
      this.attributes[i] = tempAttributes[i];
      if (tempValues.length > i) {
        this.values[i] = tempValues[i];
      }
    }
    this.attributes[tempAttributes.length] = attribute.trim();
    this.values[tempValues.length] = value.trim();
    return this;
  }

  // XML Printing and formatting
  //
  public String toString() {
    return toString("");
  }

  public String toString(String indent) {
    // <tag
    StringBuffer beginTag = new StringBuffer(TAG_STRING_BUFFER);
    StringBuffer endTag = new StringBuffer(TAG_STRING_BUFFER);
    String xmlName = eliminateSpaces(name);

    beginTag.append("<" + xmlName);

    if (attributes != null) {
      for (int i = 0; i < attributes.length; i++) {
        if (attributes[i].length() > 0) {
          // <tag attr="
          String attributeName = eliminateSpaces(attributes[i].toString().trim());
          // TODO: attribute name should be one word! squish it?
          beginTag.append(" " + attributeName + "=\"");

          // <tag attr="val"
          // if there is no value associated with this attribute,
          // consider it a <hr NOSHADE> style attribute;
          // use the default: <hr NOSHADE="NOSHADE">
          if (values != null) {
            if (i < values.length) {
              beginTag.append(values[i].toString().trim() + "\"");
            } else {
              beginTag.append(attributeName.trim() + "\"");
            }
          }
        }
      }
    }

    // <tag attr="val"...> or <tag attr="val".../>
    // if there is no value in this element AND this element has no children, it can be a single tag <.../>
    if (value.length() < 1 && child == null) {
      beginTag.append(" />\n");
      endTag.setLength(0);
    } else {
      beginTag.append(">");
      endTag.append("</" + xmlName + ">\n");
    }

    // return ( prev.toString() + beginTag.toString() + value.toString() + child.toString() + endTag.toString() +
    // next.toString() );
    String returnStr = indent + beginTag.toString();
    if (value.length() > 0) {
      returnStr += value.toString();
    }
    if (child != null) {
      returnStr += "\n" + child.toPostString(indent + "  ");
    }
    if (child != null) {
      returnStr += indent;
    }
    if (endTag.length() > 0) {
      returnStr += endTag.toString();
    }
    return (returnStr);
  }

  // CONSTRUCTION ROUTINES
  //
  //

  // insert element before the node here
  public XMLNode insertElement(String name) {
    return insertElement(name, "", "", "");
  }

  public XMLNode insertElement(String name, String value) {
    return insertElement(name, value, "", "");
  }

  public XMLNode insertElement(String name, String value, String[] attributes) {
    return insertElement(name, value, attributes, null);
  }

  public XMLNode insertElement(String name, String[] attributes, String[] values) {
    return insertElement(name, "", attributes, values);
  }

  public XMLNode insertElement(String name, String value, String attribute, String attributeValue) {
    return insertElement(name, value, new String[] { attribute }, new String[] { attributeValue });
  }

  public XMLNode insertElement(String name, String value, String[] attributes, String[] values) {
    XMLNode newnode = new XMLNode(name, value, attributes, values);

    // check if this node is the first of a chain
    if (this.parent != null) {
      if (this.parent.child.equals(this)) {
        this.parent.child = newnode;
      }
    }
    // if it has no parent it might be a root's child
    else {
      if (this.prev == null) {
        this.root.child = newnode;
      }
    }

    newnode.child = null;
    newnode.parent = this.parent;
    newnode.prev = this.prev;
    if (newnode.prev != null) {
      newnode.prev.next = newnode;
    }
    this.prev = newnode;
    newnode.next = this;
    return newnode;
  }

  // add element to end of tree
  public XMLNode addElement(String name) {
    return addElement(name, "", "", "");
  }

  public XMLNode addElement(String name, String value) {
    return addElement(name, value, "", "");
  }

  public XMLNode addElement(String name, String value, String[] attributes) {
    return addElement(name, value, attributes, null);
  }

  public XMLNode addElement(String name, String[] attributes, String[] values) {
    return addElement(name, "", attributes, values);
  }

  public XMLNode addElement(String name, String value, String attribute, String attributeValue) {
    return addElement(name, value, new String[] { attribute }, new String[] { attributeValue });
  }

  public XMLNode addElement(String name, String value, String[] attributes, String[] values) {
    XMLNode newnode = new XMLNode(name, value, attributes, values);
    return addElement(newnode);
  }

  public XMLNode addElement(XMLNode node) {
    XMLNode current = this;
    while (current.next != null) {
      current = current.next;
    }
    current.next = node;
    node.prev = current;
    return node;
  }

  // add one level of children
  public XMLNode addChildren(XMLNode children) {
    XMLNode current = children;
    while (current != null) {
      current.parent = this;
      current = current.next;
    }

    if (this.child == null) {
      this.child = children;
    } else {
      current = this.child;
      while (current.next != null) {
        current = current.next;
      }
      current.next = children;
    }
    return this;
  }

  // add element to end of tree
  public XMLNode addChild(String name) {
    return addChild(name, "", "", "");
  }

  public XMLNode addChild(String name, String value) {
    return addChild(name, value, "", "");
  }

  public XMLNode addChild(String name, String value, String[] attributes) {
    return addChild(name, value, attributes, null);
  }

  public XMLNode addChild(String name, String[] attributes, String[] values) {
    return addChild(name, "", attributes, values);
  }

  public XMLNode addChild(String name, String value, String attribute, String attributeValue) {
    return addChild(name, value, new String[] { attribute }, new String[] { attributeValue });
  }

  public XMLNode addChild(String name, String value, String[] attributes, String[] values) {
    XMLNode newnode = new XMLNode(name, value, attributes, values);
    return addChild(newnode);
  }

  public XMLNode addChild(XMLNode node) {
    if (this.child == null) {
      this.child = node;
      node.parent = this;
    } else {
      XMLNode current = this.child;
      while (current.next != null) {
        current = current.next;
      }
      current.next = node;
      node.prev = current;
      node.parent = this;
    }
    return node;
  }

  private String eliminateSpaces(String str) {
    return str.trim().replace(' ', '_');
  }
}
