package soot.toolkits.astmetrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import polyglot.ast.ClassDecl;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

import soot.options.Options;

/**
 * @author Michael Batchelder
 *
 *         Created on 5-Mar-2006
 */
public class IdentifiersMetric extends ASTMetric {
  private static final Logger logger = LoggerFactory.getLogger(IdentifiersMetric.class);

  double nameComplexity = 0;
  double nameCount = 0;

  int dictionarySize = 0;
  ArrayList<String> dictionary;

  // cache of names so no recomputation
  HashMap<String, Double> names;

  /**
   * @param astNode
   *
   *          This metric will take a measure of the "complexity" of each identifier used within the program. An identifier's
   *          complexity is computed as follows:
   *
   *          First the alpha tokens are parsed by splitting on non-alphas and capitals:
   *
   *          example identifier: getASTNode alpha tokens: get, AST, Node example identifier: ___Junk$$name alpha tokens:
   *          Junk, name)
   *
   *          The alpha tokens are then counted and a 'token complexity' is formed by the ratio of total tokens to the number
   *          of tokens found in the dictionary:
   *
   *          example identifier: getASTNode Total: 3, Found: 2, Complexity: 1.5
   *
   *          Then the 'character complexity' is computed, which is a ratio of total number of characters to the number of
   *          non-complex characters. Non-complex characters are those which are NOT part of a multiple string of non-alphas.
   *
   *          example identifier: ___Junk$$name complex char strings: '___', '$$' number of non-complex (Junk + name): 8,
   *          total: 13, Complexity: 1.625
   *
   *          Finally, the total identifier complexity is the sum of the token and character complexities multipled by the
   *          'importance' of an identifier:
   *
   *          Multipliers are as follows:
   *
   *          Class multiplier = 3; Method multiplier = 4; Field multiplier = 2; Formal multiplier = 1.5; Local multiplier =
   *          1;
   *
   */
  public IdentifiersMetric(Node astNode) {
    super(astNode);

    initializeDictionary();
  }

  private void initializeDictionary() {
    String line;
    BufferedReader br = null;
    dictionary = new ArrayList<String>();
    names = new HashMap<String, Double>();

    InputStream is = ClassLoader.getSystemResourceAsStream("mydict.txt");
    if (is != null) {
      br = new BufferedReader(new InputStreamReader(is));

      try {
        while ((line = br.readLine()) != null) {
          addWord(line);
        }
      } catch (IOException ioexc) {
        logger.debug("" + ioexc.getMessage());
      }
    }

    is = ClassLoader.getSystemResourceAsStream("soot/toolkits/astmetrics/dict.txt");
    if (is != null) {
      br = new BufferedReader(new InputStreamReader(is));

      try {
        while ((line = br.readLine()) != null) {
          addWord(line.trim().toLowerCase());
        }
      } catch (IOException ioexc) {
        logger.debug("" + ioexc.getMessage());
      }
    }

    if ((dictionarySize = dictionary.size()) == 0) {
      logger.debug("Error reading in dictionary file(s)");
    } else if (Options.v().verbose()) {
      logger.debug("Read " + dictionarySize + " words in from dictionary file(s)");
    }

    try {
      is.close();
    } catch (IOException e) {
      logger.debug("" + e.getMessage());
    }
    try {
      if (br != null) {
        br.close();
      }
    } catch (IOException e) {
      logger.debug("" + e.getMessage());
    }

  }

  private void addWord(String word) {
    if (dictionarySize == 0 || word.compareTo(dictionary.get(dictionarySize - 1)) > 0) {
      dictionary.add(word);
    } else {
      int i = 0;
      while (i < dictionarySize && word.compareTo(dictionary.get(i)) > 0) {
        i++;
      }

      if (word.compareTo(dictionary.get(i)) == 0) {
        return;
      }

      dictionary.add(i, word);
    }

    dictionarySize++;
  }

  /*
   * (non-Javadoc)
   *
   * @see soot.toolkits.astmetrics.ASTMetric#reset()
   */
  public void reset() {
    nameComplexity = 0;
    nameCount = 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see soot.toolkits.astmetrics.ASTMetric#addMetrics(soot.toolkits.astmetrics.ClassData)
   */
  public void addMetrics(ClassData data) {
    data.addMetric(new MetricData("NameComplexity", new Double(nameComplexity)));
    data.addMetric(new MetricData("NameCount", new Double(nameCount)));
  }

  public NodeVisitor enter(Node parent, Node n) {
    double multiplier = 1;
    String name = null;
    if (n instanceof ClassDecl) {
      name = ((ClassDecl) n).name();
      multiplier = 3;
      nameCount++;
    } else if (n instanceof MethodDecl) {
      name = ((MethodDecl) n).name();
      multiplier = 4;
      nameCount++;
    } else if (n instanceof FieldDecl) {
      name = ((FieldDecl) n).name();
      multiplier = 2;
      nameCount++;
    } else if (n instanceof Formal) { // this is locals and formals
      name = ((Formal) n).name();
      multiplier = 1.5;
      nameCount++;
    } else if (n instanceof LocalDecl) { // this is locals and formals
      name = ((LocalDecl) n).name();
      nameCount++;
    }

    if (name != null) {
      nameComplexity += (multiplier * computeNameComplexity(name));
    }
    return enter(n);
  }

  private double computeNameComplexity(String name) {
    if (names.containsKey(name)) {
      return names.get(name).doubleValue();
    }

    ArrayList<String> strings = new ArrayList<String>();

    // throw out non-alpha characters
    String tmp = "";
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if ((c > 64 && c < 91) || (c > 96 && c < 123)) {
        tmp += c;
      } else if (tmp.length() > 0) {
        strings.add(tmp);
        tmp = "";
      }
    }
    if (tmp.length() > 0) {
      strings.add(tmp);
    }

    ArrayList<String> tokens = new ArrayList<String>();
    for (int i = 0; i < strings.size(); i++) {
      tmp = strings.get(i);
      while (tmp.length() > 0) {
        int caps = countCaps(tmp);
        if (caps == 0) {
          int idx = findCap(tmp);
          if (idx > 0) {
            tokens.add(tmp.substring(0, idx));
            tmp = tmp.substring(idx, tmp.length());
          } else {
            tokens.add(tmp.substring(0, tmp.length()));
            break;
          }
        } else if (caps == 1) {
          int idx = findCap(tmp.substring(1)) + 1;
          if (idx > 0) {
            tokens.add(tmp.substring(0, idx));
            tmp = tmp.substring(idx, tmp.length());
          } else {
            tokens.add(tmp.substring(0, tmp.length()));
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
    for (int i = 0; i < tokens.size(); i++) {
      if (dictionary.contains(tokens.get(i))) {
        words++;
      }
    }

    if (words > 0) {
      complexity = (tokens.size()) / words;
    }

    names.put(name, new Double(complexity + computeCharComplexity(name)));

    return complexity;
  }

  private double computeCharComplexity(String name) {
    int count = 0, index = 0, last = 0, lng = name.length();
    while (index < lng) {
      char c = name.charAt(index);
      if ((c < 65 || c > 90) && (c < 97 || c > 122)) {
        last++;
      } else {
        if (last > 1) {
          count += last;
        }
        last = 0;
      }
      index++;
    }

    double complexity = lng - count;

    if (complexity > 0) {
      return ((lng) / complexity);
    } else {
      return lng;
    }
  }

  /*
   * @author Michael Batchelder
   *
   * Created on 6-Mar-2006
   *
   * @param name string to parse
   *
   * @return number of leading capital letters
   */
  private int countCaps(String name) {
    int caps = 0;
    while (caps < name.length()) {
      char c = name.charAt(caps);
      if (c > 64 && c < 91) {
        caps++;
      } else {
        break;
      }
    }

    return caps;
  }

  /*
   * @author Michael Batchelder
   *
   * Created on 6-Mar-2006
   *
   * @param name string to parse
   *
   * @return index of first capital letter
   */
  private int findCap(String name) {
    int idx = 0;
    while (idx < name.length()) {
      char c = name.charAt(idx);
      if (c > 64 && c < 91) {
        return idx;
      } else {
        idx++;
      }
    }

    return -1;
  }
}
