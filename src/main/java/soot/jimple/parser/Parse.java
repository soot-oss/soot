package soot.jimple.parser;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.jimple.parser.lexer.Lexer;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.node.Start;
import soot.jimple.parser.parser.Parser;
import soot.jimple.parser.parser.ParserException;
import soot.util.EscapedReader;

/** Provides a test-driver for the Jimple parser. */
@Deprecated
public class Parse {
  private static final Logger logger = LoggerFactory.getLogger(Parse.class);
  private static final String EXT = ".jimple";

  private static final String USAGE = "usage: java Parse [options] " + "jimple_file [jimple_file ...]";

  /*
   * Parses a jimple input stream. If you just want to get the method bodies for a SootClass, pass as the second argument the
   * SootClass you want fill it's method bodies. If you want to create a SootClass for the inputStream set the 2nd arg to
   * null.
   */
  static public SootClass parse(InputStream istream, SootClass sc) {
    Start tree = null;

    Parser p = new Parser(
        new Lexer(new PushbackReader(new EscapedReader(new BufferedReader(new InputStreamReader(istream))), 1024)));

    try {
      tree = p.parse();
    } catch (ParserException e) {
      throw new RuntimeException("Parser exception occurred: " + e);
    } catch (LexerException e) {
      throw new RuntimeException("Lexer exception occurred: " + e);
    } catch (IOException e) {
      throw new RuntimeException("IOException occurred: " + e);
    }

    Walker w;
    if (sc == null) {
      w = new Walker(null);
    } else {
      w = new BodyExtractorWalker(sc, null, new HashMap<SootMethod, JimpleBody>());
    }

    tree.apply(w);
    return w.getSootClass();
  }

  public static void main(String args[]) throws java.lang.Exception

  {
    boolean verbose = false;
    InputStream inFile;

    // check arguments
    if (args.length < 1) {
      logger.debug("" + USAGE);
      System.exit(0);
    }

    Scene.v().setPhantomRefs(true);

    for (String arg : args) {
      if (arg.startsWith("-")) {
        arg = arg.substring(1);
        if (arg.equals("d")) {
        } else if (arg.equals("v")) {
          verbose = true;
        }
      } else {

        try {
          if (verbose) {
            logger.debug(" ... looking for " + arg);
          }
          inFile = new FileInputStream(arg);
        } catch (FileNotFoundException e) {
          if (arg.endsWith(EXT)) {
            logger.debug(" *** can't find " + arg);
            continue;
          }
          arg = arg + EXT;
          try {
            if (verbose) {
              logger.debug(" ... looking for " + arg);
            }
            inFile = new BufferedInputStream(new FileInputStream(arg));
          } catch (FileNotFoundException ee) {
            logger.debug(" *** can't find " + arg);
            continue;
          }
        }

        Parser p = new Parser(new Lexer(new PushbackReader(new InputStreamReader(inFile), 1024)));

        Start tree = p.parse();

        tree.apply(new Walker(null));
      }
    }
  } // main
} // Parse
