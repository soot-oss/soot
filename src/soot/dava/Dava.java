package soot.dava;

import soot.*;
import java.io.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;


public class Dava
{
    public Dava( Singletons.Global g ) {}
    public static Dava v() { return G.v().Dava(); }
    private static final String LOG_TO_FILE = null;
    private static final PrintStream LOG_TO_SCREEN = null;

    private Writer iOut = null;
    private IterableSet currentPackageContext = null;
    private String currentPackage;
    
    public void set_CurrentPackage( String cp)
    {
	currentPackage = cp;
    }

    public String get_CurrentPackage()
    {
	return currentPackage;
    }

    public void set_CurrentPackageContext( IterableSet cpc)
    {
	currentPackageContext = cpc;
    }

    public IterableSet get_CurrentPackageContext()
    {
	return currentPackageContext;
    }

    public DavaBody newBody(SootMethod m)
    {
        return new DavaBody( m);
    }

    /** Returns a DavaBody constructed from the given body b. */
    public DavaBody newBody(Body b)
    {
        return new DavaBody(b);
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
		    G.v().out.println( "Unable to open " + LOG_TO_FILE);
		    fnfe.printStackTrace();
                    throw new CompilationDeathException(Main.COMPILATION_ABORTED);
		}
		catch (UnsupportedEncodingException uee) {
		    G.v().out.println( "This system doesn't support US-ASCII encoding!!");
		    uee.printStackTrace();
                    throw new CompilationDeathException(Main.COMPILATION_ABORTED);
		}

	    try {
		iOut.write( s);
		iOut.write( "\n");
		iOut.flush();
	    }
	    catch (IOException ioe) {
		G.v().out.println( "Unable to write to " + LOG_TO_FILE);
		ioe.printStackTrace();
                throw new CompilationDeathException(Main.COMPILATION_ABORTED);
	    }
	}
    }
}






