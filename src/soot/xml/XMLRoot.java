/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 David Eng
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.xml;


/** XML helper */
public class XMLRoot
{
	public String name = "";				// <NAME attr1="val1" attr2="val2"...>val</NAME>
	public String value = "";				// <name attr1="val1" attr2="val2"...>VAL</name>
	public String[] attributes = { "" };	// <name ATTR1="val1" ATTR2="val2"...>val</name>
	public String[] values = { "" };		// <name attr1="VAL1" attr2="VAL2"...>val</name>

	protected XMLNode child = null;			// -> to child node

	XMLRoot()
	{
	}

	public String toString()
	{
		return XMLPrinter.xmlHeader + XMLPrinter.dtdHeader + this.child.toPostString();
	}

	// add element to end of tree
	public XMLNode addElement( String name ) 
	{
		return addElement( name, "", "", "" );
	}
	public XMLNode addElement( String name, String value ) 
	{
		return addElement( name, value, "", "" );
	}
	public XMLNode addElement( String name, String value, String[] attributes ) 
	{
		return addElement( name, value, attributes, null );
	}
	public XMLNode addElement( String name, String[] attributes, String[] values )
	{
		return addElement( name, "", attributes, values );
	}
	public XMLNode addElement( String name, String value, String attribute, String attributeValue )
	{
		return addElement( name, value, new String[] { attribute}, new String[] { attributeValue} );
	}
	public XMLNode addElement( String name, String value, String[] attributes, String[] values )
	{
		XMLNode current= null;
		XMLNode newnode = new XMLNode( name, value, attributes, values );
		newnode.root = this;

		if( this.child == null )
		{
			this.child = newnode;
			newnode.parent = null; // root's children have NO PARENTS :(
		}
		else
		{
			current = this.child;
			while( current.next != null )
			{
				current = current.next;
			}           
			current.next = newnode;     
			newnode.prev = current;
		}
		return newnode;
	}   
}
