package soot.dava;

import soot.*;
import java.io.*;
import java.util.*;
import soot.jimple.*;


public class Dava
{
    private static final String LOG_TO_FILE = null;
    private static final PrintStream LOG_TO_SCREEN = null;

    private static final Dava instance = new Dava();
    private Writer iOut;
    
    private Dava() 
    {
	iOut = null;
    }

    public static Dava v() 
    {
        return instance;
    }

    public DavaBody newBody(SootMethod m)
    {
        return new DavaBody( m);
    }

    /** Returns a DavaBody constructed from the given body b. */
    public DavaBody newBody(Body b, String phase)
    {
        Map options = Scene.v().computePhaseOptions(phase, "");
        return new DavaBody(b, options);
    }
    
    /** Returns a DavaBody constructed from the given body b. */
    public DavaBody newBody(Body b, String phase, String optionsString)
    {
        Map options = Scene.v().computePhaseOptions(phase, optionsString);
        return new DavaBody(b, options);
    }

    public Local newLocal(String name, Type t)
    {
        return Jimple.v().newLocal(name, t);
    }

    public void log( String s)
    {
	if (LOG_TO_SCREEN != null) {
	    LOG_TO_SCREEN.println( s);
	    LOG_TO_SCREEN.flush();
	}

	if (LOG_TO_FILE != null) {
	    if (iOut == null) 
		try {
		    iOut = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( LOG_TO_FILE), "US-ASCII"));
		}
		catch (FileNotFoundException fnfe) {
		    System.err.println( "Unable to open " + LOG_TO_FILE);
		    fnfe.printStackTrace();
		    System.exit(0);
		}
		catch (UnsupportedEncodingException uee) {
		    System.err.println( "This system doesn't support US-ASCII encoding!!");
		    uee.printStackTrace();
		    System.exit(0);
		}

	    try {
		iOut.write( s);
		iOut.write( "\n");
		iOut.flush();
	    }
	    catch (IOException ioe) {
		System.err.println( "Unable to write to " + LOG_TO_FILE);
		ioe.printStackTrace();
		System.exit(0);
	    }
	}
    }
}






