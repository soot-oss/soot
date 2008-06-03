/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


import soot.*;
import soot.baf.*;
import soot.jimple.*;
import soot.options.Options;
import soot.tagkit.*;
import soot.util.*;
import java.io.*;
import java.util.*;

/** Example of using Soot to create a classfile from scratch.
 * The 'createclass' example creates a HelloWorld class file using Soot.
 * It proceeds as follows:
 *
 * - Create a SootClass <code>HelloWorld</code> extending java.lang.Object.
 *
 * - Create a 'main' method and add it to the class.
 *
 * - Create an empty JimpleBody and add it to the 'main' method.
 *
 * - Add locals and statements to JimpleBody.
 *
 * - Write the result out to a class file.
 */

public class Main
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        SootClass sClass;
        SootMethod method;
        
        // Resolve dependencies
           Scene.v().loadClassAndSupport("java.lang.Object");
           Scene.v().loadClassAndSupport("java.lang.System");
           
        // Declare 'public class HelloWorld'   
           sClass = new SootClass("HelloWorld", Modifier.PUBLIC);

        // 'extends Object'
           sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
           Scene.v().addClass(sClass);
           
        // create and add the class attribute
           GenericAttribute classAttr = new GenericAttribute("MyClassAttr", "foo".getBytes());
           sClass.addTag(classAttr);
           
        // Create the method, public static void main(String[])
           method = new SootMethod("main",
                Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        
           sClass.addMethod(method);

        // Create and add the method attribute
           GenericAttribute mAttr = new GenericAttribute("MyMethodAttr", "".getBytes());
           method.addTag(mAttr);
           
        // Create the method body
        {
            // create empty body
            JimpleBody body = Jimple.v().newBody(method);
            
            method.setActiveBody(body);
            Chain units = body.getUnits();
            Local arg, tmpRef;
            Unit tmpUnit;
            
            // Add some locals, java.lang.String l0
                arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
                body.getLocals().add(arg);
            
            // Add locals, java.io.printStream tmpRef
                tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
                body.getLocals().add(tmpRef);
                
            // add "l0 = @parameter0"
                tmpUnit = Jimple.v().newIdentityStmt(arg, 
                     Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0));
                tmpUnit.addTag(new MyTag(1));
                units.add(tmpUnit);
            
            // add "tmpRef = java.lang.System.out"
                units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));
            
            // insert "tmpRef.println("Hello world!")"
            {
                SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(java.lang.String)>");
                tmpUnit = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!")));
                tmpUnit.addTag(new MyTag(2));
                units.add(tmpUnit);
            }
            
            // insert "return"
                units.add(Jimple.v().newReturnVoidStmt());
                     
        }

        MyTagAggregator mta = new MyTagAggregator();
        // convert the body to Baf
        method.setActiveBody(
            Baf.v().newBody((JimpleBody) method.getActiveBody()));
        // aggregate the tags and produce a CodeAttribute
        mta.transform(method.getActiveBody());

        // write the class to a file
        String fileName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
        OutputStream streamOut = new JasminOutputStream(
                                    new FileOutputStream(fileName));
        PrintWriter writerOut = new PrintWriter(
                                    new OutputStreamWriter(streamOut));
        AbstractJasminClass jasminClass = new soot.baf.JasminClass(sClass);
        jasminClass.print(writerOut);
        writerOut.flush();
        streamOut.close();
    }
}

class MyTag implements Tag {
        
    int value;

    public MyTag(int value) {
        this.value = value;
    }

    public String getName() {
        return "MyTag";
    }

    // output the value as a 4-byte array
    public byte[] getValue() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(value);
            dos.flush();
        } catch(IOException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}


class MyTagAggregator extends TagAggregator {

    public String aggregatedName() {
        return "MyTag";
    }

    public boolean wantTag(Tag t) {
        return (t instanceof MyTag);
    }

    public void considerTag(Tag t, Unit u) {
        units.add(u);
        tags.add(t);
    }
}
