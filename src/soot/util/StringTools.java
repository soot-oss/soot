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

import java.text.*;


public class StringTools
{
    static StringBuffer whole = new StringBuffer();
    static StringBuffer mini = new StringBuffer();


 /* This is used by sootClass to generate output. */
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

   public final static String lineSeparator = System.getProperty("line.separator");;

    /* Used by StringConstant.toString() */
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
	    {
	      if (ch == '\\')
                { toStringBuffer.append("\\\\"); continue;}
	      if (ch == '\'')
		{ toStringBuffer.append("\\\'"); continue; }
	      if (ch == '\"')
                { toStringBuffer.append("\\\""); continue; }
	      if (ch == '\n')
                { toStringBuffer.append("\\n"); continue; }
	      else if((int) ch >= 32 && (int) ch <= 126)
		{toStringBuffer.append(ch); continue;}
	    }
	    
	    toStringBuffer.append(getUnicodeStringFromChar(ch));
        }

        toStringBuffer.append("\"");
        return toStringBuffer.toString();
    }


  public static String  getUnicodeStringFromChar(char ch)
  {
    String s = Integer.toHexString( (int) ch);
    String padding = null;

    switch(s.length()) {
    case 1:
      padding = "000";
      break;
    case 2:
      padding = "00";
      break;
    case 3:
      padding = "0";
      break;
    case 4:
      padding = "";
      break;
    }   
    
    return "\\u" + padding + s;
  }
  
  public static String getUnEscapedStringOf(String str) 
  {
    StringBuffer buf = new StringBuffer();
    CharacterIterator iter = new StringCharacterIterator(str);

    
    for(char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {

    if (ch != '\\') {
      buf.append(ch);
    } else {  // enter escaped mode
      
      ch = iter.next();
      char format;

      if(ch == '\\') {
	buf.append(ch);
      } else if ( (format = getCFormatChar(ch)) != '\0') {
	buf.append(format);
      } else if(ch == 'u') {  //enter unicode mode

	StringBuffer mini = new StringBuffer(4);
	for(int i = 0; i <4; i++)
	  mini.append(iter.next());
	
	ch =  (char) Integer.parseInt(mini.toString(), 16);
	buf.append(ch); 
	
      } else {
	throw new RuntimeException("Unexpected char: " + ch);
      }      
    }

    }
    return buf.toString();
  }


 	
  public static char getCFormatChar(char c)
  {
    char res;

    switch(c) {
    case 'n':
      res = '\n';
      break;
    case 't':
      res = '\t';
      break;
    case 'r':
      res = '\r';
      break;
    case 'b':
      res = '\b';
      break;
    case '\"':
      res = '\"';
      break;
    case '\'':
      res = '\'';
      break;
      
      
    default:
      res = '\0';
      break;
    } 
    return res;
  }
}




















