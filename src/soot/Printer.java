/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with cl library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;

import java.io.*;
import java.util.*;

import soot.util.*;
import soot.xml.*;
import soot.dava.*;
import soot.tagkit.*;


/**
* Prints out a class and all its methods.
*/
public class Printer
{
	public Printer( Singletons.Global g ) {}
	public static Printer v() { return G.v().Printer(); }

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
	
    public void printXMLTo(SootClass cl, PrintWriter out)
    {
        XMLPrinter xmlOut = new XMLPrinter();
	XMLNode xmlRootNode = null;
	XMLNode xmlHistoryNode = null;
        XMLNode xmlClassNode = null;
        XMLNode xmlTempNode = null;

        // Print XML class output
        {
	    // add header nodes
	    xmlRootNode = xmlOut.root.addElement("jil");

	    // add history node
	    // TODO: grab the software version and command line
	    String cmdlineStr = "";
	    for( int i = 0; i < Main.v().cmdLineArgs.length; i++ )
	    {
		cmdlineStr += Main.v().cmdLineArgs[ i ] + " ";
	    }
	    String dateStr = new Date().toString();
	    xmlHistoryNode = xmlRootNode.addChild("history");
	    xmlHistoryNode.addAttribute("created", dateStr );
	    xmlHistoryNode.addChild("soot",new String[] {"version", "command", "timestamp"},new String[] {Main.v().versionString, cmdlineStr.trim(), dateStr});
            
	    // add class root node
            xmlClassNode = xmlRootNode.addChild("class",new String[] {"name"},new String[] {Scene.v().quotedNameOf(cl.getName()).toString()});
            if(cl.getPackageName().length()>0)
                xmlClassNode.addAttribute("package",cl.getPackageName());
            if(cl.hasSuperclass())
                xmlClassNode.addAttribute("extends",Scene.v().quotedNameOf(cl.getSuperclass().getName()).toString());

            // add modifiers subnode
            xmlTempNode = xmlClassNode.addChild("modifiers");
            StringTokenizer st = new StringTokenizer(Modifier.toString(cl.getModifiers()));
            while(st.hasMoreTokens())
		xmlTempNode.addChild("modifier",new String[] {"name"},new String[] {st.nextToken()+""});
            xmlTempNode.addAttribute("count",xmlTempNode.getNumberOfChildren()+"");		
        } 

        // Print interfaces
        {
            xmlTempNode = xmlClassNode.addChild("interfaces","",new String[] {"count"},new String[] {cl.getInterfaceCount()+""});

	    Iterator interfaceIt = cl.getInterfaces().iterator();
            if(interfaceIt.hasNext())
            {
                while(interfaceIt.hasNext())
		    xmlTempNode.addChild("implements","",new String[] {"class"},new String[] {Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()).toString()});
            }
        }
        
        // Print fields
        {   
	    xmlTempNode = xmlClassNode.addChild( "fields", "", new String[] { "count" }, new String[] { cl.getFieldCount()+"" } );
                
	    Iterator fieldIt = cl.getFields().iterator();
            if(fieldIt.hasNext())
            {
                int i = 0;
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                    
                    String type = f.getType().toString();
                    String name = f.getName().toString();
                    String decl = f.getDeclaration();
							
                    // add the field node
                    XMLNode xmlFieldNode = xmlTempNode.addChild( "field", "", new String[] { "id", "name", "type" }, new String[] { (i++)+"", name, type } );
                    XMLNode xmlModifiersNode = xmlFieldNode.addChild( "modifiers" );
							
                    StringTokenizer st = new StringTokenizer(Modifier.toString(f.getModifiers()));
                    while(st.hasMoreTokens())
                        xmlModifiersNode.addChild( "modifier", new String[] {"name"},new String[]{st.nextToken()+""} );
			
                    xmlModifiersNode.addAttribute( "count", xmlModifiersNode.getNumberOfChildren()+"" );
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = cl.methodIterator();

            if( Scene.v().getJimpleStmtPrinter() instanceof XMLStmtPrinter )
            {
                XMLStmtPrinter xmlStmtPrinter = ( XMLStmtPrinter )Scene.v().getJimpleStmtPrinter();
                xmlStmtPrinter.setXMLNode( xmlClassNode.addChild( "methods", new String[] { "count" }, new String[] { cl.getMethodCount()+"" } ) );
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
                        printTo(method.getActiveBody(), out, XML_OUTPUT);
                }
            }
        }
        out.println(xmlOut.toString());
    }
  

    public void printJimpleStyleTo(SootClass cl, PrintWriter out, int printBodyOptions)
    {
        // write cl class as XML
        boolean xmlOutput = xmlOutput(printBodyOptions);
        if(xmlOutput)
        {
            printXMLTo(cl, out);
            return;
        }

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
			    
	
		Scene.v().getLocalPrinter().printLocalsInBody( b, out, isPrecise);
	}

		// Print out statements
		// Use an external class so that it can be overridden.
		if(debug) {
			Scene.v().getJimpleStmtPrinter().printDebugStatementsInBody(b, out, isPrecise);
		} else {
			Scene.v().getJimpleStmtPrinter().printStatementsInBody(b, out, isPrecise, isNumbered);
		}
        
		if(!xmlOutput) {
		out.println("    }");
		if (isAddJimpleLn()) {
			incJimpleLnNum();
		}
			    
	}
	}
    
}

