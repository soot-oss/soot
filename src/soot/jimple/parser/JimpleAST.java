package soot.jimple.parser;


import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import java.io.*;
import java.util.*;

import soot.util.*;
import soot.*;



/** 
    This class encapsulates a JimpleAst instance and provides methods
    to act on it.      
*/


public class JimpleAST
{
    private static final String EXT = ".jimple";
    private static boolean debug = false;
    private static boolean verbose = false;

    private Start mTree = null;
    private InputStream mInputStream = null;
    boolean hasInputStream = false;
    
    private static final String USAGE = "usage: java JimpleAST [options] " +
        "jimple_file [jimple_file ...]";


    /** 
     * @param aInputStream The JimpleInputStream
     */

    public JimpleAST(JimpleInputStream aJIS)
    {
	mInputStream = aJIS;
	generateParseTree(mInputStream);
    }


    public SootClass getSkeleton(SootClass sc, SootResolver resolver)
    {
	Walker w = new SkeletonExtractorWalker(resolver, sc);	
	mTree.apply(w);  	
	return w.getSootClass();	
    }



    /*
      Parses a jimple input stream.
      If you just want to get the method bodies for a SootClass, pass as the second
      argument the SootClass you want method bodies for.
      If you want to create a SootClass for the inputStream set the 2nd arg to null.
    */

    public SootClass getMethodsForClass(SootClass sc) 
    {  	
	Walker w;

	if(sc == null)
	    w = new Walker();
	else {
	    w = new BodyExtractorWalker(sc);
	} 

        boolean oldPhantomValue = Scene.v().getPhantomRefs();

        Scene.v().setPhantomRefs(true);
	mTree.apply(w);  	
        Scene.v().setPhantomRefs(oldPhantomValue);
       	
	return w.getSootClass();	
    }



    public Set getCstPool() 
    {  
		
	CstPoolExtractorWalker w = new CstPoolExtractorWalker(); 
	
	mTree.apply(w);  	
		
	return w.getCstPool();	
    }




    

    private void generateParseTree(InputStream istream)
    {	
	Parser p =
	    new Parser(new Lexer(
		    new PushbackReader(new BufferedReader(
                    new InputStreamReader(istream)), 1024)));
	try {
	    mTree = p.parse();
	} catch(ParserException e) {
	    throw new RuntimeException("Parser exception occurred: " + e);
	} catch(LexerException e) {
	    throw new RuntimeException("Lexer exception occurred: " + e);
	} catch(IOException e) {
	    throw new RuntimeException("IOException occurred: " + e);
	}
	
	mInputStream = null; // allow garbage collection of the inputstream
    }

    

    /*
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
		
		setInputStream(inFile);

		Walker walker = new Walker();
		mTree.apply(walker);
		
            }
        }
    } // main

    */


} // Parse





