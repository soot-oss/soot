package soot.jimple.parser;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import java.io.*;
import soot.util.*;

import soot.*;


public class Parse 
{
    private static final String EXT = ".jimple";
    private static boolean debug = false;
    private static boolean verbose = false;

    
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
		      new PushbackReader(new EscapedReader(
			      new InputStreamReader(istream)), 1024)));
	
	
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
	    w = new Walker();
	else {
	    w = new BodyExtractorWalker(sc);
	}
	
	tree.apply(w);  	
	return w.getSootClass();	
    }


    public static void main(String args[])  
	throws java.lang.Exception
	      

    {
        InputStream inFile;
        
        // check arguments
        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(0);
        }


	Scene.v().setPhantomRefs(true);

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                arg = arg.substring(1);
                if (arg.equals("d"))
                    debug = true;
                else if (arg.equals("v"))
                    verbose = true;
            }
            else {

	       
                try {
                    if (verbose)
                        System.out.println(" ... looking for " + arg);
                    inFile = new FileInputStream(arg);
                } catch (FileNotFoundException e) {
                    if (arg.endsWith(EXT)) {
                        System.out.println(" *** can't find " + arg);
                        continue;
                    }
                    arg = arg + EXT;
                    try {
                        if (verbose)
                            System.out.println(" ... looking for " + arg);
                        inFile = new BufferedInputStream(new FileInputStream(arg));
                    } catch (FileNotFoundException ee) {
                        System.out.println(" *** can't find " + arg);
                        continue;
                    }
                }
               
		Parser p =
		    new Parser(
			       new Lexer(
					 new PushbackReader(
							    new InputStreamReader(inFile), 1024)));

		Start tree = p.parse();
                    
		tree.apply(new Walker());               
            }
        }
    } // main
} // Parse





