/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.util;

import java.util.Vector;
/**
 * @author Michael Batchelder 
 * 
 * Created on 30-Mar-2006 
 */
public class StringTrie {

  private char[] startChars = new char[0];
  private StringTrie[] tries = new StringTrie[0];
  
  public StringTrie() {
    super();
  }

  public void add(char[] chars, int index) {
    if (chars.length==index) return;
    if (startChars.length == 0) {
      startChars = new char[1];
      startChars[0] = chars[0];
      tries = new StringTrie[1];
      tries[0].add(chars,index++);
    } else {
      int i = findStart(chars[index], 0, startChars.length-1);
      if (i>=0) {
        tries[i].add(chars,index++);
      } else {
        i = addChar(chars[index]);
        tries[i].add(chars,index++);
      }
    }
  }
  
  private int addChar(char c) {
    int oldLength = startChars.length;
    
    int i = findSpot(c, 0, oldLength - 1);
    
    char tmp[] = (char[])startChars.clone();
    StringTrie t[] = (StringTrie[])tries.clone();
    
    startChars = new char[oldLength + 1];
    tries = new StringTrie[oldLength + 1];
    
    if (i > 0) {
      System.arraycopy(tmp,0,startChars,0,i);
      System.arraycopy(t,0,tries,0,i);
    }
    if (i < oldLength) {
      System.arraycopy(tmp,i,startChars,i+1,oldLength - i);
      System.arraycopy(t,i,tries,i+1,oldLength - i);
    }
    
    startChars[i] = c;
    tries[i] = new StringTrie();
    
    return i;
  }
  
  private int findSpot(char c, int first, int last) {
    int diff = last - first;
    if (diff == 1) return c < startChars[first] ? first : c < startChars[last] ? last : last + 1;
    
    diff /= 2;
    if (startChars[first+diff] < c)
      return findSpot(c, first+diff, last);
    else
      return findSpot(c, first, last - diff);
  }
  
  public boolean contains(char[] chars, int index) {
    if (chars.length==index) return true;
    else if (startChars.length == 0) return false;
    
    int i = findStart(chars[index], 0, startChars.length-1);
    if (i>=0) {
      return tries[i].contains(chars,index++);
    }
    return false;
  }
  
  private int findStart(char c, int first, int last) {
    int diff = last - first;
    if (diff <= 1) return c == startChars[first] ? first : c == startChars[last] ? last : -1;
    
    diff /= 2;
    if (startChars[first+diff] <= c)
      return findStart(c, first+diff, last);
    else
      return findStart(c, first, last - diff);
  }
}
