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

import java.io.*;

public class EscapedWriter extends FilterWriter
{
    public final static String lineSeparator = System.getProperty("line.separator");
    private final static int cr = lineSeparator.charAt(0);
    private final static int lf = (lineSeparator.length() == 2) ? lineSeparator.charAt(1) : -1;

    public EscapedWriter(Writer fos)
    {
        super(fos);
    }

    private final static ThreadLocal miniTL = new ThreadLocal() 
    {
        protected Object initialValue() { return new StringBuffer(); }
    };

    public void print(int ch) throws IOException
    {
	write(ch);
	throw new RuntimeException();
    }
  
    public void write(String s, int off, int len) throws IOException
    {
        for(int i = off; i < off + len; i++)
            write(s.charAt(i));
    }
  
    public void write(int ch) throws IOException
    {
        if (ch >= 32 && ch <= 126 || ch == cr || ch == lf)
            { super.write(ch); return; }
	
        StringBuffer mini = (StringBuffer)miniTL.get();
        mini.setLength(0);
        mini.append(Integer.toHexString(ch));

        while (mini.length() < 4)
            mini.insert(0, "0");

        mini.insert(0, "\\u");
        for (int i = 0; i < mini.length(); i++)
            super.write(mini.charAt(i));
    }
}
