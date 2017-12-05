/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.tagkit;


/** Represents a tag that just has a string to be printed with the code.
 */

public class StringTag implements  Tag
{
    String s;
    private String analysisType = "Unknown";

    public StringTag(String s, String type){
        this(s);
        analysisType = type;
    }
    
    public StringTag( String s ) {
        this.s = s;
    }

    public String toString() {
        return s;
    }

    public String getAnalysisType(){
        return analysisType;
    }

    /** Returns the tag name. */
    public String getName() {
        return "StringTag";
    }

    public String getInfo(){
        return s;
    }
    
    /** Returns the tag raw data. */
    public byte[] getValue() {
        throw new RuntimeException( "StringTag has no value for bytecode" );
    }
}

