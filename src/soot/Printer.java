/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot;
import soot.options.*;
import java.io.*;
import soot.tagkit.*;
import java.util.*;
import soot.util.*;
import soot.toolkits.graph.*;

/**
* Prints out a class and all its methods.
*/
public class Printer {
    public Printer(Singletons.Global g) {
    }
    public static Printer v() {
        return G.v().soot_Printer();
    }

    final private static char fileSeparator =
        System.getProperty("file.separator").charAt(0);

    public static final int USE_ABBREVIATIONS = 0x0001, ADD_JIMPLE_LN = 0x0010;

    public boolean useAbbreviations() {
        return (options & USE_ABBREVIATIONS) != 0;
    }

    public boolean addJimpleLn() {
        return (options & ADD_JIMPLE_LN) != 0;
    }

    int options = 0;
    public void setOption(int opt) {
        options |= opt;
    }
    public void clearOption(int opt) {
        options &= ~opt;
    }

    int jimpleLnNum = 0; // actual line number

    public int getJimpleLnNum() {
        return jimpleLnNum;
    }
    public void setJimpleLnNum(int newVal) {
        jimpleLnNum = newVal;
    }
    public void incJimpleLnNum() {
        jimpleLnNum++;
	//G.v().out.println("jimple Ln Num: "+jimpleLnNum);
    }

    public void printTo(SootClass cl, PrintWriter out) {
        // add jimple line number tags
        setJimpleLnNum(1);

        // Print class name + modifiers
        {
            StringTokenizer st =
                new StringTokenizer(Modifier.toString(cl.getModifiers()));
            while (st.hasMoreTokens()) {
                String tok = (String) st.nextToken();
                if( cl.isInterface() && tok.equals("abstract") ) continue;
                out.print(tok + " ");
            }

            String classPrefix = "";

            if (!cl.isInterface()) {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

            out.print(
                classPrefix + " " + Scene.v().quotedNameOf(cl.getName()) + "");
        }

        // Print extension
        {
            if (cl.hasSuperclass())
                out.print(
                    " extends "
                        + Scene.v().quotedNameOf(cl.getSuperclass().getName())
                        + "");
        }

        // Print interfaces
        {
            Iterator interfaceIt = cl.getInterfaces().iterator();

            if (interfaceIt.hasNext()) {
                out.print(" implements ");

                out.print(
                    ""
                        + Scene.v().quotedNameOf(
                            ((SootClass) interfaceIt.next()).getName())
                        + "");

                while (interfaceIt.hasNext()) {
                    out.print(",");
                    out.print(
                        " "
                            + Scene.v().quotedNameOf(
                                ((SootClass) interfaceIt.next()).getName())
                            + "");
                }
            }
        }

        out.println();
        incJimpleLnNum();
/*        if (!addJimpleLn()) {
            Iterator clTagsIt = cl.getTags().iterator();
            while (clTagsIt.hasNext()) {
                final Tag t = (Tag)clTagsIt.next();
                out.println(t);
            }
        }*/
        out.println("{");
        incJimpleLnNum();
        if (Options.v().print_tags_in_output()){
            Iterator cTagIterator = cl.getTags().iterator();
            while (cTagIterator.hasNext()) {
                Tag t = (Tag) cTagIterator.next();
                out.print("/*");
                out.print(t.toString());
                out.println("*/");
            }
        }

        // Print fields
        {
            Iterator fieldIt = cl.getFields().iterator();

            if (fieldIt.hasNext()) {
                while (fieldIt.hasNext()) {
                    SootField f = (SootField) fieldIt.next();

                    if (f.isPhantom())
                        continue;

                    if (Options.v().print_tags_in_output()){
                        Iterator fTagIterator = f.getTags().iterator();
                        while (fTagIterator.hasNext()) {
                            Tag t = (Tag) fTagIterator.next();
                            out.print("/*");
                            out.print(t.toString());
                            out.println("*/");
                        }
                    }
                    out.println("    " + f.getDeclaration() + ";");
                    if (addJimpleLn()) {
                        setJimpleLnNum(addJimpleLnTags(getJimpleLnNum(), f));		
                    }

                    //incJimpleLnNum();
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = cl.methodIterator();

            if (methodIt.hasNext()) {
                if (cl.getMethodCount() != 0) {
                    out.println();
                    incJimpleLnNum();
                }

                while (methodIt.hasNext()) {
                    SootMethod method = (SootMethod) methodIt.next();

                    if (method.isPhantom())
                        continue;

                    if (!Modifier.isAbstract(method.getModifiers())
                        && !Modifier.isNative(method.getModifiers())) {
                        if (!method.hasActiveBody())
                            throw new RuntimeException(
                                "method "
                                    + method.getName()
                                    + " has no active body!");
                        else
                            if (Options.v().print_tags_in_output()){
                                Iterator mTagIterator = method.getTags().iterator();
                                while (mTagIterator.hasNext()) {
                                    Tag t = (Tag) mTagIterator.next();
                                    out.print("/*");
                                    out.print(t.toString());
                                    out.println("*/");
                                }
                            }
                            printTo(method.getActiveBody(), out);

                        if (methodIt.hasNext()) {
                            out.println();
                            incJimpleLnNum();
                        }
                    } else {
                           
                        if (Options.v().print_tags_in_output()){
                            Iterator mTagIterator = method.getTags().iterator();
                            while (mTagIterator.hasNext()) {
                                Tag t = (Tag) mTagIterator.next();
                                out.print("/*");
                                out.print(t.toString());
                                out.println("*/");
                            }
                        }
                        
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");
                        incJimpleLnNum();
                        if (methodIt.hasNext()) {
                            out.println();
                            incJimpleLnNum();
                        }
                    }
                }
            }
        }
        out.println("}");
        incJimpleLnNum();
    }

    /**
        Writes the class out to a file.
     */
    // This method is deprecated. Use soot.util.JasminOutputStream instead.
    public void writeXXXDeprecated(SootClass cl, String outputDir) {
        String outputDirWithSep = "";

        if (!outputDir.equals(""))
            outputDirWithSep = outputDir + fileSeparator;

        try {
            File tempFile =
                new File(outputDirWithSep + cl.getName() + ".jasmin");

            FileOutputStream streamOut = new FileOutputStream(tempFile);

            PrintWriter writerOut =
                new PrintWriter(
                    new EscapedWriter(new OutputStreamWriter(streamOut)));

            if (cl.containsBafBody())
                new soot.baf.JasminClass(cl).print(writerOut);
            else
                new soot.jimple.JasminClass(cl).print(writerOut);

            writerOut.close();

            if (Options.v().time())
                Timers.v().assembleJasminTimer.start();

            // Invoke jasmin
            {
                String[] args;

                if (outputDir.equals("")) {
                    args = new String[1];

                    args[0] = cl.getName() + ".jasmin";
                } else {
                    args = new String[3];

                    args[0] = "-d";
                    args[1] = outputDir;
                    args[2] = outputDirWithSep + cl.getName() + ".jasmin";
                }

                jasmin.Main.main(args);
            }

            tempFile.delete();

            if (Options.v().time())
                Timers.v().assembleJasminTimer.end();

        } catch (IOException e) {
            throw new RuntimeException(
                "Could not produce new classfile! (" + e + ")");
        }
    }

    /**
     *   Prints out the method corresponding to b Body, (declaration and body),
     *   in the textual format corresponding to the IR used to encode b body.
     *
     *   @param out a PrintWriter instance to print to.
     */
    public void printTo(Body b, PrintWriter out) {
        b.validate();

        boolean isPrecise = !useAbbreviations();

        String decl = b.getMethod().getDeclaration();

        out.println("    " + decl);
        //incJimpleLnNum();
    
        // only print tags if not printing attributes in a file 
        if (!addJimpleLn()) {
            /*for( Iterator tIt = b.getMethod().getTags().iterator(); tIt.hasNext(); ) {    final Tag t = (Tag) tIt.next();
                out.println(t);
                incJimpleLnNum();

            }*/
        }
       
        if (addJimpleLn()) {
            setJimpleLnNum(addJimpleLnTags(getJimpleLnNum(), b.getMethod()));		
            //G.v().out.println("added jimple ln tag for method: "+b.getMethod().toString()+" "+b.getMethod().getDeclaringClass().getName());
        }

        out.println("    {");
        incJimpleLnNum();
        
        UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph(b);

        LabeledUnitPrinter up;
        if( isPrecise ) up = new NormalUnitPrinter(b);
        else up = new BriefUnitPrinter(b);

        if (addJimpleLn()) {
            up.setPositionTagger( new AttributesUnitPrinter(getJimpleLnNum()) );
        }
	
        printLocalsInBody(b, up);

        printStatementsInBody(b, out, up, unitGraph);

        out.println("    }");
        incJimpleLnNum();

    }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    private void printStatementsInBody(Body body, java.io.PrintWriter out, LabeledUnitPrinter up, UnitGraph unitGraph ) {
    	Chain units = body.getUnits();
        Iterator unitIt = units.iterator();
        Unit currentStmt = null, previousStmt;

        while (unitIt.hasNext()) {

            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();

            // Print appropriate header.
            {
                // Put an empty line if the previous node was a branch node, the current node is a join node
                //   or the previous statement does not have body statement as a successor, or if
                //   body statement has a label on it

                if (currentStmt != units.getFirst()) {
                    if (unitGraph.getSuccsOf(previousStmt).size() != 1
                        || unitGraph.getPredsOf(currentStmt).size() != 1
                        || up.labels().containsKey(currentStmt)) {
                        up.newline();
                    } else {
                        // Or if the previous node does not have body statement as a successor.

                        List succs = unitGraph.getSuccsOf(previousStmt);

                        if (succs.get(0) != currentStmt) {
                            up.newline();
                        }
                    }
                }

                if (up.labels().containsKey(currentStmt)) {
                    up.unitRef( currentStmt, true );
                    up.literal(":");
                    up.newline();
                }

                if (up.references().containsKey(currentStmt)) {
                    up.unitRef( currentStmt, false );
                }
            }

            up.startUnit(currentStmt);
            currentStmt.toString(up);
            up.endUnit(currentStmt);

            up.literal(";");
            up.newline();

            // only print them if not generating attributes files 
            // because they mess up line number
            //if (!addJimpleLn()) {
            if (Options.v().print_tags_in_output()){
                Iterator tagIterator = currentStmt.getTags().iterator();
                while (tagIterator.hasNext()) {
                    Tag t = (Tag) tagIterator.next();
                    up.noIndent();
                    up.literal("/*");
                    up.literal(t.toString());
                    up.literal("*/");
                    up.newline();
                }
                /*Iterator udIt = currentStmt.getUseAndDefBoxes().iterator();
                while (udIt.hasNext()) {
                    ValueBox temp = (ValueBox)udIt.next();
                    Iterator vbtags = temp.getTags().iterator();
                    while (vbtags.hasNext()) {
                        Tag t = (Tag) vbtags.next();
                        up.noIndent();
                        up.literal("VB Tag: "+t.toString());
                        up.newline();
                    }
                }*/
            }
        }

        out.print(up.toString());
		if (addJimpleLn()){
			setJimpleLnNum(up.getPositionTagger().getEndLn());
		}


        // Print out exceptions
        {
            Iterator trapIt = body.getTraps().iterator();

            if (trapIt.hasNext()) {
                out.println();
                incJimpleLnNum();
            }

            while (trapIt.hasNext()) {
                Trap trap = (Trap) trapIt.next();

                out.println(
                    "        catch "
                        + Scene.v().quotedNameOf(trap.getException().getName())
                        + " from "
                        + up.labels().get(trap.getBeginUnit())
                        + " to "
                        + up.labels().get(trap.getEndUnit())
                        + " with "
                        + up.labels().get(trap.getHandlerUnit())
                        + ";");

                incJimpleLnNum();

            }
        }

    }

    private int addJimpleLnTags(int lnNum, Unit stmt, int endLn) {
		
		if (endLn-lnNum <= 1) {
        	stmt.addTag(new JimpleLineNumberTag(lnNum));
			//G.v().out.println(stmt.getClass().toString());
        	lnNum++;
        	return lnNum;
		}
		else {
			stmt.addTag(new JimpleLineNumberTag(lnNum, endLn));
			//G.v().out.println("multi-line: "+stmt.getClass().toString());
			endLn++;
			return endLn;
		}
    }

    private int addJimpleLnTags(int lnNum, SootMethod meth) {
    	meth.addTag(new JimpleLineNumberTag(lnNum));
	lnNum++;
	return lnNum;
    }
    
    private int addJimpleLnTags(int lnNum, SootField f) {
    	f.addTag(new JimpleLineNumberTag(lnNum));
	lnNum++;
	return lnNum;
    }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    private void printLocalsInBody(
        Body body,
        UnitPrinter up) {
        // Print out local variables
        {
            Map typeToLocals =
                new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = body.getLocals().iterator();

                while (localIt.hasNext()) {
                    Local local = (Local) localIt.next();

                    List localList;

                    Type t = local.getType();

                    if (typeToLocals.containsKey(t))
                        localList = (List) typeToLocals.get(t);
                    else {
                        localList = new ArrayList();
                        typeToLocals.put(t, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while (typeIt.hasNext()) {
                    Type type = (Type) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    up.type( type );
                    up.literal( " " );

                    for (int k = 0; k < locals.length; k++) {
                        if (k != 0)
                            up.literal( ", " );

                        up.local( (Local) locals[k] );
                    }

                    up.literal(";");
                    up.newline();
                }
            }

            if (!typeToLocals.isEmpty()) {
                up.newline();
            }
        }
    }
}
