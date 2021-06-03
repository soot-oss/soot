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
    this.name = in_name;
    this.value = in_value;
    this.attributes = in_attributes;
    this.values = in_values;
  }

  public XMLNode(XMLNode node) {
    if (node != null) {
      this.name = node.name;
      this.value = node.value;
      this.attributes = node.attributes;
      this.values = node.values;
      if (node.child != null) {
        this.child = (XMLNode) node.child.clone();
      }
      if (node.next != null) {
        this.next = (XMLNode) node.next.clone();
      }
    }
  }

  @Override
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
    for (XMLNode current = child; current != null; current = current.next) {
      count++;
    }
    return count;
  }

  // adds an attribute to an element
  public XMLNode addAttribute(String attribute, String value) {
    {
      String[] oldAttrs = this.attributes;
      int oldAttrsLen = oldAttrs.length;
      String[] newAttrs = new String[oldAttrsLen + 1];
      System.arraycopy(oldAttrs, 0, newAttrs, 0, oldAttrsLen);
      newAttrs[oldAttrsLen] = attribute.trim();
      this.attributes = newAttrs;
    }
    {
      String[] oldValues = this.values;
      final int oldValuesLen = oldValues.length;
      String[] newValues = new String[oldValuesLen + 1];
      System.arraycopy(oldValues, 0, newValues, 0, oldValuesLen);
      newValues[oldValuesLen] = value.trim();
      this.values = newValues;
    }
    return this;
  }

  // XML Printing and formatting
  //
  @Override
  public String toString() {
    return toString("");
  }

  public String toString(String indent) {
    final String xmlName = eliminateSpaces(name);

    // <tag
    StringBuilder beginTag = new StringBuilder(TAG_STRING_BUFFER);
    beginTag.append(indent);
    beginTag.append('<').append(xmlName);

    if (attributes != null) {
      for (int i = 0; i < attributes.length; i++) {
        if (attributes[i].length() > 0) {
          // <tag attr="
          String attributeName = eliminateSpaces(attributes[i].trim());
          // TODO: attribute name should be one word! squish it?
          beginTag.append(' ').append(attributeName).append("=\"");

          // <tag attr="val"
          // if there is no value associated with this attribute,
          // consider it a <hr NOSHADE> style attribute;
          // use the default: <hr NOSHADE="NOSHADE">
          if (values != null) {
            if (i < values.length) {
              beginTag.append(values[i].trim()).append('"');
            } else {
              beginTag.append(attributeName.trim()).append('"');
            }
          }
        }
      }
    }

    // <tag attr="val"...> or <tag attr="val".../>
    // if there is no value in this element AND this element has no children, it can be a single tag <.../>
    final String endTag;
    if (child == null && value.isEmpty()) {
      beginTag.append(" />\n");
      endTag = null;
    } else {
      beginTag.append('>');
      // endTag = new StringBuilder(TAG_STRING_BUFFER);
      endTag = "</" + xmlName + ">\n";
    }

    if (!value.isEmpty()) {
      beginTag.append(value);
    }
    if (child != null) {
      beginTag.append('\n');
      beginTag.append(child.toPostString(indent + "  "));
      beginTag.append(indent);
    }
    if (endTag != null) {
      beginTag.append(endTag);
    }
    return beginTag.toString();
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

    if (this.parent != null) {
      // check if this node is the first of a chain
      if (this.parent.child.equals(this)) {
        this.parent.child = newnode;
      }
    } else {
      // if it has no parent it might be a root's child
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
  @Override
  public XMLNode addElement(String name) {
    return addElement(name, "", "", "");
  }

  @Override
  public XMLNode addElement(String name, String value) {
    return addElement(name, value, "", "");
  }

  @Override
  public XMLNode addElement(String name, String value, String[] attributes) {
    return addElement(name, value, attributes, null);
  }

  @Override
  public XMLNode addElement(String name, String[] attributes, String[] values) {
    return addElement(name, "", attributes, values);
  }

  @Override
  public XMLNode addElement(String name, String value, String attribute, String attributeValue) {
    return addElement(name, value, new String[] { attribute }, new String[] { attributeValue });
  }

  @Override
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
