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

public class StringTools
{
    public static String lineSeparator = System.getProperty("line.separator");;
    static StringBuffer whole = new StringBuffer();
    static StringBuffer mini = new StringBuffer();

    public static java.lang.String getEscapedStringOf(String fromString)
    {
        char[] fromStringArray;
        int cr, lf, ch;

        whole.setLength(0);
        mini.setLength(0);

        fromStringArray = fromString.toCharArray();

        cr = lineSeparator.charAt(0);
        lf = -1;

        if (lineSeparator.length() == 2)
            lf = lineSeparator.charAt(1);

        for (int i = 0; i < fromStringArray.length; i++)
        {
            ch = (int) fromStringArray[i];
            if (ch >= 32 && ch <= 126 || ch == cr || ch == lf)
            {
                whole.append((char) ch);
 
                continue;
            }
            
            mini.setLength(0);
            mini.append(Integer.toHexString(ch));

            while (mini.length() < 4)
                mini.insert(0, "0");

            mini.insert(0, "\\u");
            whole.append(mini.toString());
        }

        return whole.toString();
    }

    public static java.lang.String getQuotedStringOf(String fromString)
    {
        StringBuffer toStringBuffer;
        char[] fromStringArray;

        toStringBuffer = new java.lang.StringBuffer();
        fromStringArray = fromString.toCharArray();

        toStringBuffer.append("\"");

        for (int i = 0; i < fromStringArray.length; i++)
        {
            char ch = fromStringArray[i];
            if (ch == '\\')
                { toStringBuffer.append("\\\\"); continue; }

            if (ch == '\'')
                { toStringBuffer.append("\\\'"); continue; }

            if (ch == '\"')
                { toStringBuffer.append("\\\""); continue; }

            toStringBuffer.append(ch);
        }

        toStringBuffer.append("\"");
        return toStringBuffer.toString();
    }
}
