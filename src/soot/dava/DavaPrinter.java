package soot.dava;

import soot.dava.internal.AST.*;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.tagkit.*;
import soot.toolkits.graph.*;
import java.io.*;
import java.util.*;

import soot.util.*;
import soot.xml.*;
import soot.dava.*;
import soot.tagkit.*;


public class DavaPrinter
{
    public DavaPrinter( Singletons.Global g ) {}
    public static DavaPrinter v() { return G.v().DavaPrinter(); }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printLocalsInBody(Body body, java.io.PrintWriter out, boolean isPrecise)
    {
	if ((body instanceof DavaBody) == false)
	    throw new RuntimeException( "Only DavaBodies should use the DavaLocalPrinter");

	DavaBody davaBody = (DavaBody) body;
	
        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);

	    HashSet params = new HashSet();
	    params.addAll( davaBody.get_ParamMap().values());
	    params.addAll( davaBody.get_CaughtRefs());
	    HashSet thisLocals = davaBody.get_ThisLocals();
	    

            // Collect locals
            {
                Iterator localIt = body.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

		    if (params.contains( local) || thisLocals.contains( local))
			continue;

                    List localList;
 
                    String typeName;
                    Type t = local.getType();

                    typeName = (isPrecise) ?  t.toString() :  t.toBriefString();

                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

	    InstanceInvokeExpr constructorExpr = davaBody.get_ConstructorExpr(); 
	    if (constructorExpr != null) {

		if (davaBody.getMethod().getDeclaringClass().getName().equals( constructorExpr.getMethod().getDeclaringClass().toString()))
		    out.print("        this(");
		else
		    out.print("        super(");

		Iterator ait = constructorExpr.getArgs().iterator();
		while (ait.hasNext()) {
		    out.print( ait.next().toString());
		    
		    if (ait.hasNext())
			out.print( ", ");
		}
		
		out.print( ");\n\n");
	    }


            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        ");
		    if (type.equals( "null_type"))
			out.print( "Object");
		    else 
			out.print(type);
		    out.print( " ");
                    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }
    }

    public void printStatementsInBody(Body body, java.io.PrintWriter out, boolean isPrecise, boolean isNumbered)
    {
	Chain units = ((DavaBody) body).getUnits();

	if (units.size() != 1)
	    throw new RuntimeException( "DavaBody AST doesn't have single root.");
	
	out.print( ((ASTNode) units.getFirst()).toString( null, "        "));
    }

    public void printDebugStatementsInBody(Body b, java.io.PrintWriter out, boolean isPrecise)
    {
    }

    final private static char fileSeparator = System.getProperty("file.separator").charAt(0);
    
	public static final int USE_ABBREVIATIONS = 0x0001,
							DEBUG_MODE        = 0x0002,
							NUMBERED          = 0x0004,
				XML_OUTPUT	      = 0x0008,
				ADD_JIMPLE_LN     = 0x0010;	

	public static boolean useAbbreviations(int m)
	{
		return (m & USE_ABBREVIATIONS) != 0;
	}

	public static boolean numbered(int m)
	{
		return (m & NUMBERED) != 0;
	}
    
	public static boolean debugMode(int m)
	{
		return (m & DEBUG_MODE) != 0;
	}

	public static boolean xmlOutput(int m)
	{
	return (m & XML_OUTPUT) != 0;
	}

	public static boolean addJimpleLn(int m)
	{
	return (m & ADD_JIMPLE_LN) != 0;
	}
     

    boolean addJimpleLn;	// if true jimple line number tags are 
    				// added to each statement
				
    int jimpleLnNum = 0;	// actual line number
    
    
    /**
        Returns true if cl class is being managed by a Scene. 
        A class may be unmanaged while it is being constructed.
    */

    // these five methods are for accessing vars associated with adding
    // jimple line number tags to stmts
    public boolean isAddJimpleLn() {
    	return addJimpleLn;
    }
    private void setAddJimpleLn(boolean val) {
    	addJimpleLn = val;
    }
    public int getJimpleLnNum() {
    	return jimpleLnNum;
    }
    public void setJimpleLnNum(int newVal) {
        jimpleLnNum = newVal;
    }
    public void incJimpleLnNum() {
    	jimpleLnNum++;
    }
    
    
    /** Prints cl SootClass to the given PrintWriter, including active bodies of methods. */
    public void printTo(SootClass cl, PrintWriter out)
    {
        printTo(cl, out, 0);
    }
	
    public void printJimpleStyleTo(SootClass cl, PrintWriter out, int printBodyOptions)
    {
	// add jimple line number tags
	setAddJimpleLn(addJimpleLn(printBodyOptions));
	if (isAddJimpleLn()) {
		incJimpleLnNum();
	}
	
       // Print class name + modifiers
        {
            StringTokenizer st = new StringTokenizer(Modifier.toString(cl.getModifiers()));
            while(st.hasMoreTokens())
                out.print(st.nextToken() + " ");

            String classPrefix = "";

            if(!cl.isInterface())
             {
                 classPrefix = classPrefix + " class";
                 classPrefix = classPrefix.trim();
             }

            out.print(classPrefix + " " + Scene.v().quotedNameOf(cl.getName()) + "");
        }

        // Print extension
        {
            if(cl.hasSuperclass())
                out.print(" extends " + Scene.v().quotedNameOf(cl.getSuperclass().getName()) + "");
        }

        // Print interfaces
        {
            Iterator interfaceIt = cl.getInterfaces().iterator();
            
            if(interfaceIt.hasNext())
            {
                out.print(" implements ");
                    
                out.print("" + Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()) + "");
                
                while(interfaceIt.hasNext())
                {
                    out.print(",");
                    out.print(" " + Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()) + "");
                }
            }
        }
        
        out.println();
	
	if (isAddJimpleLn()) {
		incJimpleLnNum();
	}
        out.println("{");
	if (isAddJimpleLn()) {
		incJimpleLnNum();
	}
        
        // Print fields
        {
            Iterator fieldIt = cl.getFields().iterator();
            
            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                    
                    out.println("    " + f.getDeclaration() + ";");
		    if (isAddJimpleLn()) {
			    incJimpleLnNum();
	 	    }
                }
            }
        }
        
        // Print methods
        {
            Iterator methodIt = cl.methodIterator();

            if(methodIt.hasNext())
            {
                if(cl.getMethodCount() != 0) {
                    out.println();
		    if (isAddJimpleLn()) {
			    incJimpleLnNum();  	
	            }
		}
                
                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();

                    if(method.isPhantom())
                        continue;
                    
                    if(!Modifier.isAbstract(method.getModifiers()) &&
                       !Modifier.isNative(method.getModifiers()))
                    {
                        if(!method.hasActiveBody())
                            throw new RuntimeException("method " + method.getName() + " has no active body!");
                        else
                            printTo(method.getActiveBody(), out, printBodyOptions);

                        if(methodIt.hasNext()){
                            out.println();
			    if (isAddJimpleLn()) {
				    incJimpleLnNum();
		            }		    
			}
                    }
                    else 
                    {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");
                        if (isAddJimpleLn()) {
				incJimpleLnNum();
			}
                        if(methodIt.hasNext()) {
                            out.println();
			    if (isAddJimpleLn()) {
				    incJimpleLnNum();
			    }
			}
                    }
                }
            }
        }
        out.println("}");
	if (isAddJimpleLn()) {
		incJimpleLnNum();
	}	
    }
    
    public void printTo( SootClass cl, PrintWriter out, int printBodyOptions)
    {
	// Optionally print the package info for Dava files.
	if (Main.v().getJavaStyle()) {

	    String curPackage = cl.getJavaPackageName();

	    if (curPackage.equals( "") == false) {
		out.println( "package " + curPackage + ";");
		out.println();
	    }

	    IterableSet packagesUsed = new IterableSet();

	    if (cl.hasSuperclass()) {
		SootClass superClass = cl.getSuperclass();
		packagesUsed.add( superClass.getJavaPackageName());
	    }
	    
	    Iterator interfaceIt = cl.getInterfaces().iterator();
	    while (interfaceIt.hasNext()) {
		String interfacePackage = ((SootClass) interfaceIt.next()).getJavaPackageName();
		if (packagesUsed.contains( interfacePackage) == false)
		    packagesUsed.add( interfacePackage);
	    }

	    Iterator methodIt = cl.methodIterator();
	    while (methodIt.hasNext()) {
		SootMethod dm = (SootMethod) methodIt.next();
		
		if (dm.hasActiveBody())
		    packagesUsed = packagesUsed.union( ((DavaBody) dm.getActiveBody()).get_PackagesUsed());
		    
		Iterator eit = dm.getExceptions().iterator();
		while (eit.hasNext()) {
		    String thrownPackage = ((SootClass) eit.next()).getJavaPackageName();
		    if (packagesUsed.contains( thrownPackage) == false)
			packagesUsed.add( thrownPackage);
		}

		Iterator pit = dm.getParameterTypes().iterator();
		while (pit.hasNext()) {
		    Type t = (Type) pit.next();

		    if (t instanceof RefType) {
			String paramPackage = ((RefType) t).getSootClass().getJavaPackageName();
			if (packagesUsed.contains( paramPackage) == false)
			    packagesUsed.add( paramPackage);
		    }
		}

		Type t = dm.getReturnType();
		if (t instanceof RefType) {
		    String returnPackage = ((RefType) t).getSootClass().getJavaPackageName();
		    if (packagesUsed.contains( returnPackage) == false)
			packagesUsed.add( returnPackage);
		}
	    }
	    
	    Iterator fieldIt = cl.getFields().iterator();
	    while (fieldIt.hasNext()) {
		SootField f = (SootField) fieldIt.next();

		if (f.isPhantom())
		    continue;

		Type t = f.getType();

		if (t instanceof RefType) {
		    String fieldPackage = ((RefType) t).getSootClass().getJavaPackageName();
		    if (packagesUsed.contains( fieldPackage) == false)
			packagesUsed.add( fieldPackage);
		}
	    }


	    if (packagesUsed.contains( curPackage))
		packagesUsed.remove( curPackage);

	    if (packagesUsed.contains( "java.lang"))
		packagesUsed.remove( "java.lang");

	    Iterator pit = packagesUsed.iterator();
	    while (pit.hasNext())
		out.println( "import " + (String) pit.next() + ".*;");

	    if (packagesUsed.isEmpty() == false)
		out.println();

	    packagesUsed.add( "java.lang");
	    packagesUsed.add( curPackage);

	    Dava.v().set_CurrentPackageContext( packagesUsed);
	    Dava.v().set_CurrentPackage( curPackage);
	}


        // Print class name + modifiers
        {
            String classPrefix = "";
            
            classPrefix = classPrefix + " " + Modifier.toString(cl.getModifiers());
            classPrefix = classPrefix.trim();

            if(!cl.isInterface())
            {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

	    if (Main.v().getJavaStyle())
		out.print(classPrefix + " " + cl.getShortJavaStyleName());
	    else 
		out.print(classPrefix + " " + cl.getName());
        }

        // Print extension
	if ((cl.hasSuperclass()) && 
	    ((Main.v().getJavaStyle() == false) || (cl.getSuperclass().getFullName().equals( "java.lang.Object") == false)))
	    out.print(" extends " + cl.getSuperclass().getName() + "");

        // Print interfaces
        {
            Iterator interfaceIt = cl.getInterfaces().iterator();
            
            if(interfaceIt.hasNext())
            {
                out.print(" implements ");
                
                out.print("" + ((SootClass) interfaceIt.next()).getName() + "");
                
                while(interfaceIt.hasNext())
                    out.print(", " + ((SootClass) interfaceIt.next()).getName() + "");
            }
        }
        
        out.println();
        out.println("{");
        
        // Print fields
        {
            Iterator fieldIt = cl.getFields().iterator();
            
            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                        
                    out.println("    " + f.getDeclaration() + ";");
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = cl.methodIterator();
            
            if(methodIt.hasNext())
            {
                if(cl.getMethodCount() != 0)
                    out.println();
                
                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();
                    
                    if(method.isPhantom())
                        continue;
                    
                    if(!Modifier.isAbstract(method.getModifiers()) &&
                       !Modifier.isNative(method.getModifiers()))
                    {
                        if(!method.hasActiveBody())
                            throw new RuntimeException("method " + method.getName() + " has no active body!");
                        else
                            printTo(method.getActiveBody(), out, printBodyOptions);
                            
                        if(methodIt.hasNext())
                            out.println();
                    }
                    else 
                    {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");
                        
                        if(methodIt.hasNext())
                            out.println();
                    }
                }
            }
        }
        out.println("}");
    }

    /**
        Writes the class out to a file.
     */
    public void write(SootClass cl)
    {
        write(cl, "");
    }

    /**
        Writes the class out to a file.
     */
    public void write(SootClass cl, String outputDir)
    {
        String outputDirWithSep = "";
            
        if(!outputDir.equals(""))
            outputDirWithSep = outputDir + fileSeparator;
            
        try {
            File tempFile = new File(outputDirWithSep + cl.getName() + ".jasmin");
 
            FileOutputStream streamOut = new FileOutputStream(tempFile);

            PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));

            if(cl.containsBafBody())
                new soot.baf.JasminClass(cl).print(writerOut);
            else
                new soot.jimple.JasminClass(cl).print(writerOut);

            writerOut.close();

            if(soot.Main.v().opts.time())
                Timers.v().assembleJasminTimer.start(); 

            // Invoke jasmin
            {
                String[] args;
                
                if(outputDir.equals(""))
                {
                    args = new String[1];
                    
                    args[0] = cl.getName() + ".jasmin";
                }
                else
                {
                    args = new String[3];
                    
                    args[0] = "-d";
                    args[1] = outputDir;
                    args[2] = outputDirWithSep + cl.getName() + ".jasmin";
                }
                
                jasmin.Main.main(args);
            }
            
            tempFile.delete();
            
            if(soot.Main.v().opts.time())
                Timers.v().assembleJasminTimer.end(); 
            
        } catch(IOException e)
        {
            throw new RuntimeException("Could not produce new classfile! (" + e + ")");
        }        
    }




	/**
	 *   Prints out the method corresponding to b Body, (declaration and body),
	 *   in the textual format corresponding to the IR used to encode b body. Default
	 *   printBodyOptions are used.
	 *
	 *   @param out a PrintWriter instance to print to. 
	 *
	 */
	public void printTo(Body b, java.io.PrintWriter out)
	{
		printTo(b, out, 0);
	}
    

	/**
	 *   Prints out the method corresponding to b Body, (declaration and body),
	 *   in the textual format corresponding to the IR used to encode b body.
	 *
	 *   @param out a PrintWriter instance to print to.
	 *   @param printBodyOptions options for printing.
	 *
	 *   @see PrintJimpleBodyOption
	 */   
	public void printTo(Body b, PrintWriter out, int printBodyOptions)
	{
		printToImpl(b, out, printBodyOptions, false);
	}

    
	/**
	 *   Prints out the method corresponding to b Body, (declaration and body),
	 *   in the textual format corresponding to the IR used to encode b body. Includes
	 *   extra debugging information.
	 *
	 *   @param out a PrintWriter instance to print to.
	 *   @param printBodyOptions options for printing.
	 *
	 *   @see PrintJimpleBodyOption
	 */
	public void printDebugTo(Body b, PrintWriter out, int printBodyOptions)
	{
		printToImpl(b, out, printBodyOptions, true);
	}        

    

	private void printToImpl(Body b, PrintWriter out, int printBodyOptions, boolean debug)
	{
		b.validate();

		boolean isPrecise = !useAbbreviations(printBodyOptions);
		boolean isNumbered = numbered(printBodyOptions);
	boolean xmlOutput = xmlOutput(printBodyOptions);
	
		Map stmtToName = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);
		String decl = b.getMethod().getDeclaration();
	int currentJimpleLnNum;

		if(!xmlOutput)
	{
		out.println("    " + decl);        
		if (isAddJimpleLn()) {
			incJimpleLnNum();
		}
			for( Iterator tIt = b.getMethod().getTags().iterator(); tIt.hasNext(); ) {        
				final Tag t = (Tag) tIt.next();
				out.println(t);
		if (isAddJimpleLn()) {
			incJimpleLnNum();
		}
				    
			}
		out.println("    {");
		if (isAddJimpleLn()) {
			incJimpleLnNum();
		}
			    
	
		printLocalsInBody( b, out, isPrecise);
	}

		// Print out statements
		// Use an external class so that it can be overridden.
		if(debug) {
			printDebugStatementsInBody(b, out, isPrecise);
		} else {
			printStatementsInBody(b, out, isPrecise, isNumbered);
		}
        
		if(!xmlOutput) {
		out.println("    }");
		if (isAddJimpleLn()) {
			incJimpleLnNum();
		}
			    
	}
	}
    
}

