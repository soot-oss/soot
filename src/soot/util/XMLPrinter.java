/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot.util;


/** XML printing routines all XML output comes through here */
public class XMLPrinter
{	
	// version
	public static final String XML_PRINTER_VERSION = "1.0";
	
	// xml constants
	private static final String indent = "  ";
	private static final int bufferCapacity = 1024;
	
	// xml tree
	public XMLRoot root = new XMLRoot();

	// output buffer
	private StringBuffer buffer = new StringBuffer( bufferCapacity );

	// returns the buffer - this is the XML output
	public String toString()
	{
		if( root != null )
			return root.toString();
		else
			return "XML Error!";
	}
		
	// printer init
	public XMLPrinter newPrinter()
	{
		buffer = new StringBuffer( bufferCapacity );
		//buffer.append( xmlHeader );
		return this;
	}
	
	// clear
	public XMLPrinter clear()
	{
		buffer = new StringBuffer( bufferCapacity );
		return this;
	}
		
	// add single element <...>...</...>
	public XMLNode addElement( String name ) 
		{ return addElement( name, "", "", "" ); }
	public XMLNode addElement( String name, String value ) 
		{ return addElement( name, value, "", "" ); }
	public XMLNode addElement( String name, String value, String[] attributes ) 
		{ return addElement( name, value, attributes, null ); }
	public XMLNode addElement( String name, String value, String attribute, String attributeValue )
		{ return addElement( name, value, new String[] { attribute }, new String[] { attributeValue } ); }
	public XMLNode addElement( String name, String value, String[] attributes, String[] values )
	{	
		return root.addElement( name, value, attributes, values );
	}                                    		
}

