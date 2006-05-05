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

  double nameComplexity = 0;
  double nameCount =0;
  
  
  
  int dictionarySize = 0;
  ArrayList dictionary;
  
  //cache of names so no recomputation
  HashMap names;
  /**
   * @param astNode
   * 
   * This metric will take a measure of the "complexity" of each identifier used
   * within the program. An identifier's complexity is computed as follows:
   * 
   * First the alpha tokens are parsed by splitting on non-alphas and capitals:
   * 
   * 	example identifier: getASTNode		alpha tokens: get, AST, Node
   * 	example identifier: ___Junk$$name	alpha tokens: Junk, name)
   * 
   * The alpha tokens are then counted and a 'token complexity' is formed by the ratio
   * of total tokens to the number of tokens found in the dictionary:
   * 
   * 	example identifier: getASTNode		Total: 3, Found: 2, Complexity: 1.5
   * 
   * Then the 'character complexity' is computed, which is a ratio of total number of
   * characters to the number of non-complex characters. Non-complex characters are 
   * those which are NOT part of a multiple string of non-alphas.
   * 
   * 	example identifier: ___Junk$$name	complex char strings: '___', '$$'
   * 		number of non-complex (Junk + name): 8, total: 13, Complexity: 1.625
   * 
   * Finally, the total identifier complexity is the sum of the token and character
   * complexities multipled by the 'importance' of an identifier:
   * 
   * Multipliers are as follows:
   * 	     
   * Class multiplier = 3;
   * Method multiplier = 4;
   * Field multiplier = 2;
   * Formal multiplier = 1.5;
   * Local multiplier = 1;
   * 
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
    nameCount=0;
  }

  /* (non-Javadoc)
   * @see soot.toolkits.astmetrics.ASTMetric#addMetrics(soot.toolkits.astmetrics.ClassData)
   */
  public void addMetrics(ClassData data) {
	    data.addMetric(new MetricData("NameComplexity",new Double(nameComplexity)));
	    data.addMetric(new MetricData("NameCount",new Double(nameCount)));
  }
  
  public NodeVisitor enter(Node parent, Node n){
    double multiplier = 1;
    String name = null;
    if(n instanceof ClassDecl){
      name = ((ClassDecl)n).name();
      multiplier = 3;
      nameCount++;
    } else if (n instanceof MethodDecl) {
      name = ((MethodDecl)n).name();
      multiplier = 4;
      nameCount++;
    } else if (n instanceof FieldDecl) {
      name = ((FieldDecl)n).name();
      multiplier = 2;
      nameCount++;
    } else if (n instanceof Formal) { 		// this is locals and formals
      name = ((Formal)n).name();
      multiplier = 1.5;
      nameCount++;
    } else if (n instanceof LocalDecl) { 		// this is locals and formals
      name = ((LocalDecl)n).name();
      nameCount++;
    }
    
    if (name!=null)
    {
      nameComplexity += (double) (multiplier * computeNameComplexity(name));
    }
	return enter(n);
  }

  private double computeNameComplexity(String name) {
    if (names.containsKey(name))
      return ((Double)names.get(name)).doubleValue();
    
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
    
    double words = 0;
    double complexity = 0;
    for (int i = 0; i < tokens.size(); i++)
      if (dictionary.contains(tokens.get(i)))
        words++;
      
    if (words>0)
      complexity = ((double)tokens.size()) / words;
    
    names.put(name,new Double(complexity + computeCharComplexity(name)));
    
    return complexity;
  }
  
  private double computeCharComplexity(String name) {
    int count = 0, index = 0, last = 0, lng = name.length();
    while (index < lng) {
      char c = name.charAt(index);
      if ((c < 65 || c > 90) && (c < 97 || c > 122)) { 
        last++;
      } else {
        if (last>1)
          count += last;
        last = 0;
      }
      index++;
    }
    
    double complexity = lng - count;
    
    if (complexity > 0)
      return (((double)lng) / complexity);
    else return (double)lng;
  }
  
  
  /*
   * @author Michael Batchelder 
   * 
   * Created on 6-Mar-2006
   * 
   * @param	name	string to parse
   * @return		number of leading non-alpha chars
   */
  private int countNonAlphas(String name) {
    int chars = 0;
    while (chars < name.length()) {
      char c = name.charAt(chars);
      if ((c < 65 || c > 90) && (c < 97 || c > 122)) 
        chars++;
      else 
        break;
    }
    
    return chars;
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
