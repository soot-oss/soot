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
        return G.v().Printer();
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
    }

    public void printJimpleStyleTo(SootClass cl, PrintWriter out) {
        // add jimple line number tags
        incJimpleLnNum();

        // Print class name + modifiers
        {
            StringTokenizer st =
                new StringTokenizer(Modifier.toString(cl.getModifiers()));
            while (st.hasMoreTokens())
                out.print(st.nextToken() + " ");

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
            if (cl.hasSuperclass() && !cl.isInterface())
                out.print(
                    " extends "
                        + Scene.v().quotedNameOf(cl.getSuperclass().getName())
                        + "");
        }

        // Print interfaces
        {
            Iterator interfaceIt = cl.getInterfaces().iterator();

            if (interfaceIt.hasNext()) {
                out.print(cl.isInterface() ? " extends " : " implements ");

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
        out.println("{");
        incJimpleLnNum();

        // Print fields
        {
            Iterator fieldIt = cl.getFields().iterator();

            if (fieldIt.hasNext()) {
                while (fieldIt.hasNext()) {
                    SootField f = (SootField) fieldIt.next();

                    if (f.isPhantom())
                        continue;

                    out.println("    " + f.getDeclaration() + ";");
                    incJimpleLnNum();
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
                            printTo(method.getActiveBody(), out);

                        if (methodIt.hasNext()) {
                            out.println();
                            incJimpleLnNum();
                        }
                    } else {
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

    public void printTo(SootClass cl, PrintWriter out) {
        // Print class name + modifiers
        {
            String classPrefix = "";

            classPrefix =
                classPrefix + " " + Modifier.toString(cl.getModifiers());
            classPrefix = classPrefix.trim();

            if (!cl.isInterface()) {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

            out.print(classPrefix + " " + cl.getName());
        }

        // Print extension
        if (cl.hasSuperclass())
            out.print(" extends " + cl.getSuperclass().getName() + "");

        // Print interfaces
        {
            Iterator interfaceIt = cl.getInterfaces().iterator();

            if (interfaceIt.hasNext()) {
                out.print(" implements ");

                out.print("" + ((SootClass) interfaceIt.next()).getName() + "");

                while (interfaceIt.hasNext())
                    out.print(
                        ", " + ((SootClass) interfaceIt.next()).getName() + "");
            }
        }

        out.println();
        out.println("{");

        // Print fields
        {
            Iterator fieldIt = cl.getFields().iterator();

            if (fieldIt.hasNext()) {
                while (fieldIt.hasNext()) {
                    SootField f = (SootField) fieldIt.next();

                    if (f.isPhantom())
                        continue;

                    out.println("    " + f.getDeclaration() + ";");
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = cl.methodIterator();

            if (methodIt.hasNext()) {
                if (cl.getMethodCount() != 0)
                    out.println();

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
                            printTo(method.getActiveBody(), out);

                        if (methodIt.hasNext())
                            out.println();
                    } else {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");

                        if (methodIt.hasNext())
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
    public void write(SootClass cl, String outputDir) {
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

            if (soot.Main.v().opts.time())
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

            if (soot.Main.v().opts.time())
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
    private void printTo(Body b, PrintWriter out) {
        b.validate();

        boolean isPrecise = !useAbbreviations();

        String decl = b.getMethod().getDeclaration();

        {
            out.println("    " + decl);
            incJimpleLnNum();
            for( Iterator tIt = b.getMethod().getTags().iterator(); tIt.hasNext(); ) {
                final Tag t = (Tag) tIt.next();
                out.println(t);
                incJimpleLnNum();

            }
            out.println("    {");
            incJimpleLnNum();

            printLocalsInBody(b, out, isPrecise);
        }

        printStatementsInBody(b, out);

        out.println("    }");
        incJimpleLnNum();

    }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    private void printStatementsInBody(Body body, java.io.PrintWriter out) {
        boolean isPrecise = !useAbbreviations();

        Chain units = body.getUnits();

        Map stmtToName = new HashMap(units.size() * 2 + 1, 0.7f);
        UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph(body);

        // Create statement name table
        {
            Iterator boxIt = body.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                while (boxIt.hasNext()) {
                    UnitBox box = (UnitBox) boxIt.next();
                    Unit stmt = (Unit) box.getUnit();

                    labelStmts.add(stmt);
                }

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = units.iterator();

                while (stmtIt.hasNext()) {
                    Unit s = (Unit) stmtIt.next();

                    if (labelStmts.contains(s)) {
                        stmtToName.put(s, "label" + (labelCount++));
                    }
                }
            }
        }

        Iterator unitIt = units.iterator();
        Unit currentStmt = null, previousStmt;
        String indent = "        ";

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
                        || stmtToName.containsKey(currentStmt)) {
                        out.println();
                        incJimpleLnNum();
                    } else {
                        // Or if the previous node does not have body statement as a successor.

                        List succs = unitGraph.getSuccsOf(previousStmt);

                        if (succs.get(0) != currentStmt) {
                            out.println();
                            incJimpleLnNum();

                        }
                    }
                }

                if (stmtToName.containsKey(currentStmt)) {
                    out.println("     " + stmtToName.get(currentStmt) + ":");
                    incJimpleLnNum();

                }

            }

            if (isPrecise)
                out.print(currentStmt.toString(stmtToName, indent));
            else
                out.print(currentStmt.toBriefString(stmtToName, indent));

            out.print(";");
            out.println();
            if (addJimpleLn()) {
                setJimpleLnNum(addJimpleLnTags(getJimpleLnNum(), currentStmt));
            }

            // only print them if not generating attributes files 
            // because they mess up line number
            if (!addJimpleLn()) {
                Iterator tagIterator = currentStmt.getTags().iterator();
                while (tagIterator.hasNext()) {
                    Tag t = (Tag) tagIterator.next();
                    out.println(t);
                }
            }
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
                        + stmtToName.get(trap.getBeginUnit())
                        + " to "
                        + stmtToName.get(trap.getEndUnit())
                        + " with "
                        + stmtToName.get(trap.getHandlerUnit())
                        + ";");

                incJimpleLnNum();

            }
        }

    }

    private int addJimpleLnTags(int lnNum, Unit stmt) {
        stmt.addTag(new JimpleLineNumberTag(lnNum));
        lnNum++;
        return lnNum;
    }

    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    private void printLocalsInBody(
        Body body,
        java.io.PrintWriter out,
        boolean isPrecise) {
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

                    String typeName;
                    Type t = local.getType();

                    typeName = (isPrecise) ? t.toString() : t.toBriefString();

                    if (typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while (typeIt.hasNext()) {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        " + type + " ");

                    for (int k = 0; k < locals.length; k++) {
                        if (k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                    incJimpleLnNum();
                }
            }

            if (!typeToLocals.isEmpty()) {
                out.println();
                incJimpleLnNum();
            }
        }
    }
}
