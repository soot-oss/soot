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

public class EscapedReader extends FilterReader
{
    public EscapedReader(Reader fos)
    {
        super(fos);
    }

    private StringBuffer mini = new StringBuffer();

    boolean nextF;
    int nextch = 0;

    public int read() throws IOException
    {
        if (nextF)
        {
            nextF = false;
            return nextch;
        }

        int ch = super.read();

        if (ch != '\\')
            return ch;

        mini.setLength(0);

        ch = super.read();
        if (ch != 'u')
            {
                nextF = true; nextch = ch;
                return '\\';
            }

        while (mini.length() < 4)
        {
            ch = super.read();
            mini.append((char)ch);
        }

        ch = Integer.parseInt(mini.toString(), 16);

        return ch;
    }
}
