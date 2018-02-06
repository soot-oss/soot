/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
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


package soot.jimple.parser;

import soot.jimple.JimpleBody;
import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import java.io.*;
import soot.util.*;
import java.util.*;

import soot.*;

/** Provides a test-driver for the Jimple parser. */
@Deprecated
public class Parse 
{
    private static final String EXT = ".jimple";
    
    private static final String USAGE = "usage: java Parse [options] " +
        "jimple_file [jimple_file ...]";


    /*
      Parses a jimple input stream.
      If you just want to get the method bodies for a SootClass, pass as the second
      argument the SootClass you want fill it's method bodies.
      If you want to create a SootClass for the inputStream set the 2nd arg to null.
    */
    static public SootClass parse(InputStream istream, SootClass sc) 
    {  
        Start tree = null;
        
        Parser p = 
                new Parser(new Lexer(
                      new PushbackReader(new EscapedReader(new BufferedReader(
                              new InputStreamReader(istream))), 1024)));        

        try {
            tree = p.parse();
        } catch(ParserException e) {
            throw new RuntimeException("Parser exception occurred: " + e);
        } catch(LexerException e) {
            throw new RuntimeException("Lexer exception occurred: " + e);
        } catch(IOException e) {
            throw new RuntimeException("IOException occurred: " + e);
        }
        
        Walker w;
        if(sc == null)
            w = new Walker(null);
        else {
            w = new BodyExtractorWalker(sc, null, new HashMap<SootMethod, JimpleBody>());
        }
        
        tree.apply(w);          
        return w.getSootClass();        
    }


    public static void main(String args[])  
        throws java.lang.Exception
              

    {
    boolean verbose = false;
        InputStream inFile;
        
        // check arguments
        if (args.length < 1) {
            G.v().out.println(USAGE);
            System.exit(0);
        }


        Scene.v().setPhantomRefs(true);

        for (String arg : args) {
            if (arg.startsWith("-")) {
                arg = arg.substring(1);
                if (arg.equals("d")) {
				} else if (arg.equals("v"))
                    verbose = true;
            }
            else {

               
                try {
                    if (verbose)
                        G.v().out.println(" ... looking for " + arg);
                    inFile = new FileInputStream(arg);
                } catch (FileNotFoundException e) {
                    if (arg.endsWith(EXT)) {
                        G.v().out.println(" *** can't find " + arg);
                        continue;
                    }
                    arg = arg + EXT;
                    try {
                        if (verbose)
                            G.v().out.println(" ... looking for " + arg);
                        inFile = new BufferedInputStream(new FileInputStream(arg));
                    } catch (FileNotFoundException ee) {
                        G.v().out.println(" *** can't find " + arg);
                        continue;
                    }
                }
               
                Parser p =
                    new Parser(
                               new Lexer(
                                         new PushbackReader(
                                                            new InputStreamReader(inFile), 1024)));

                Start tree = p.parse();
                    
                tree.apply(new Walker(null));               
            }
        }
    } // main
} // Parse





