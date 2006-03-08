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

package soot.toolkits.astmetrics;

import soot.G;
import soot.options.*;
import polyglot.ast.*;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

import java.util.*;
import java.io.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 5-Mar-2006 
 */
public class IdentifiersMetric extends ASTMetric {

  int nameComplexity = 0;
  int charComplexity = 0;
  
  int dictionarySize = 0;
  ArrayList dictionary;
  HashMap names;
  /**
   * @param astNode
   */
  public IdentifiersMetric(Node astNode) {
    super(astNode);
    
    initializeDictionary();
  }
  
  private void initializeDictionary() {
    String line;
    BufferedReader br;
    dictionary = new ArrayList();
    names = new HashMap();
    
    InputStream is = ClassLoader.getSystemResourceAsStream("mydict.txt");
    if (is != null)
    {
      br = new BufferedReader(new InputStreamReader(is));
      
      try {
        while ((line = br.readLine()) != null)
          addWord(line);
      } catch (IOException ioexc) {}
    }
    
    is = ClassLoader.getSystemResourceAsStream("soot/toolkits/astmetrics/dict.txt");
    if (is != null)
    {
      br = new BufferedReader(new InputStreamReader(is));
    
      try {
        while ((line = br.readLine()) != null)
          addWord(line.trim().toLowerCase());
      } catch (IOException ioexc) {}
    }
      
    if ((dictionarySize = dictionary.size()) == 0)
      G.v().out.println("Error reading in dictionary file(s)");  
    else if (Options.v().verbose())
      G.v().out.println("Read "+dictionarySize+" words in from dictionary file(s)");
  }
  
  private void addWord(String word) {
    if (dictionarySize == 0 || word.compareTo((String)dictionary.get(dictionarySize - 1)) > 0) {
      dictionary.add(word);
    } else {
      int i = 0;
	  while (i < dictionarySize && word.compareTo((String)dictionary.get(i)) > 0)
	    i++;
	  
	  if (word.compareTo((String)dictionary.get(i)) == 0) 
	    return;
	  
	  dictionary.add(i,word);
    }
    
    dictionarySize++;
  }

  /* (non-Javadoc)
   * @see soot.toolkits.astmetrics.ASTMetric#reset()
   */
  public void reset() {
    nameComplexity = 0;
    charComplexity = 0;
  }

  /* (non-Javadoc)
   * @see soot.toolkits.astmetrics.ASTMetric#addMetrics(soot.toolkits.astmetrics.ClassData)
   */
  public void addMetrics(ClassData data) {
    data.addMetric(new MetricData("NameComplexity",new Integer(nameComplexity)));
    data.addMetric(new MetricData("CharComplexity",new Integer(charComplexity)));
  }
  
  public NodeVisitor enter(Node parent, Node n){
    double multiplier = 1;
    String name = null;
    if(n instanceof ClassDecl){
      name = ((ClassDecl)n).name();
      multiplier = 3;
    } else if (n instanceof MethodDecl) {
      name = ((MethodDecl)n).name();
      multiplier = 4;
    } else if (n instanceof FieldDecl) {
      name = ((FieldDecl)n).name();
      multiplier = 2;
    } else if (n instanceof Formal) { 		// this is locals and formals
      name = ((Formal)n).name();
      multiplier = 1.5;
    } else if (n instanceof Local) { 		// this is locals and formals
      name = ((Local)n).name();
    }
    
    if (name!=null)
    {
      nameComplexity += (int) (multiplier * computeNameComplexity(name));
      charComplexity += (int) (multiplier * computeCharComplexity(name));
    }
	return enter(n);
  }

  private int computeNameComplexity(String name) {
    if (names.containsKey(name))
      return ((Integer)names.get(name)).intValue();
    
    int index = 0;
    ArrayList strings = new ArrayList();
    
    // throw out non-alpha characters
    String tmp = "";
    for (int i = 0; i < name.length(); i++)
    {
      char c = name.charAt(i);
      if ((c > 64 && c < 91) || (c > 96 && c < 123)) {
        tmp+=c;
      } else if (tmp.length() > 0) {
        strings.add(tmp);
        tmp = "";
      }
    }
    if (tmp.length()>0)
      strings.add(tmp);
    
    ArrayList tokens = new ArrayList();
    for (int i = 0; i < strings.size(); i++)
    {
      tmp = (String)strings.get(i);
      while (tmp.length() > 0) {
        int caps = countCaps(tmp);
        if (caps == 0)
        {
          int idx = findCap(tmp);
          if (idx > 0) {
            tokens.add(tmp.substring(0,idx));
            tmp = tmp.substring(idx,tmp.length());
          } else {
            tokens.add(tmp.substring(0,tmp.length()));
            break;
          }
        } else if (caps == 1){
          int idx = findCap(tmp.substring(1)) + 1;
          if (idx > 0) {
            tokens.add(tmp.substring(0,idx));
            tmp = tmp.substring(idx,tmp.length());
          } else {
            tokens.add(tmp.substring(0,tmp.length()));
            break;
          }
        } else {
          if (caps < tmp.length()) {
            // count seq of capitals as one token
            tokens.add(tmp.substring(0, caps - 1).toLowerCase());
            tmp = tmp.substring(caps);
          } else {
            tokens.add(tmp.substring(0, caps).toLowerCase());
            break;
          }
        }
      }
    }
    
    int complexity = 0;
    for (int i = 0; i < tokens.size(); i++)
      if (!dictionary.contains(tokens.get(i)))
        complexity++;
        
    names.put(name,new Integer(complexity));
    
    return complexity;
  }
  
  private int computeCharComplexity(String name) {
    int complexity = 0;
    
    //[^[a-zA-Z]]
    
    return complexity;
  }
  /*
   * @author Michael Batchelder 
   * 
   * Created on 6-Mar-2006
   * 
   * @param	name	string to parse
   * @return		number of leading capital letters
   */
  private int countCaps(String name) {
    int caps = 0;
    while (caps < name.length()) {
      char c = name.charAt(caps);
      if (c > 64 && c < 91) 
        caps++;
      else 
        break;
    }
    
    return caps;
  }
  
  /*
   * @author Michael Batchelder 
   * 
   * Created on 6-Mar-2006
   * 
   * @param	name	string to parse
   * @return		index of first capital letter
   */
  private int findCap(String name) {
    int idx = 0;
    while (idx < name.length()) {
      char c = name.charAt(idx);
      if (c > 64 && c < 91) 
        return idx;
      else 
        idx++;
    }
    
    return -1;
  }
}
