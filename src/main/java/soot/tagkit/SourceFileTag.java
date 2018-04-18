/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Archie L. Cobbs
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

import java.io.UnsupportedEncodingException;

public class SourceFileTag implements Tag
{
    private String sourceFile;
    private String absolutePath;
    
    public SourceFileTag(String sourceFile) {
    	this(sourceFile,null);
    }

    public SourceFileTag(String sourceFile, String path)
    {
	    this.sourceFile = sourceFile.intern();
        this.absolutePath = path;
    }

    public SourceFileTag(){
    }

    public String getName()
    {
	return "SourceFileTag";
    }

    public byte[] getValue()
    {
	try {
		return sourceFile.getBytes("UTF8");
	} catch (UnsupportedEncodingException e) {
		return new byte[0];
	}
    }

    public void setSourceFile(String srcFile){
        sourceFile = srcFile.intern();
    }
    public String getSourceFile()
    {
	return sourceFile;
    }

    public void setAbsolutePath(String path){
        absolutePath = path;
    }

    public String getAbsolutePath(){
        return absolutePath;
    }

    public String toString()
    {
	return sourceFile;
    }
}

