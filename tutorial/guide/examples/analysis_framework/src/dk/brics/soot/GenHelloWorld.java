package dk.brics.soot;
import soot.*;
import soot.jimple.*;
import soot.options.*;
import soot.util.*;
import soot.dava.*;
import soot.grimp.*;

import java.util.Arrays;
import java.io.*;

public class GenHelloWorld
{
	public static void main(String[] args) {
		System.out.println("Generating class...");
		SootClass sClass = generateClass();
		
		System.out.println("Writing class file...");
		write(sClass, Options.output_format_class);
		
		System.out.println("Writing jimple file...");
		write(sClass, Options.output_format_jimple);
		
		System.out.println("Writing java file...");
		// Need to convert the jimple body to grimp before I can convert to dava
		GrimpBody gBody = Grimp.v().newBody(sClass.getMethodByName("main").getActiveBody(), "gb");
		DavaBody davaBody = Dava.v().newBody(gBody);
		sClass.getMethodByName("main").setActiveBody(davaBody);		
		write(sClass, Options.output_format_dava);
		
		System.out.println("Done");
	}

	public static SootClass generateClass() {
		// Load dependencies
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		Scene.v().loadNecessaryClasses();
		
		// Create the class HelloWorld as a public class that extends Object
		SootClass sClass = new SootClass("HelloWorld", Modifier.PUBLIC);
		sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
		Scene.v().addClass(sClass);
		
		// Create: public static void main(String[])
		SootMethod mainMethod = new SootMethod("main",
				Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}),
				VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
		sClass.addMethod(mainMethod);
		
		// Generate dava body from the jimple body
		JimpleBody jimpleBody = createJimpleBody(mainMethod);
		
		// Set the jimple body as the active one
		mainMethod.setActiveBody(jimpleBody);
		
		return sClass;
	}

	private static JimpleBody createJimpleBody(SootMethod method) {
		// Create a body for the main method and set it as the active body
		JimpleBody body = Jimple.v().newBody(method);		
		
		// Create a local to hold the main method argument
		// Note: In general for any use of objects or basic-types, must generate a local to
		// hold that in the method body
		Local frm1 = Jimple.v().newLocal("frm1", ArrayType.v(RefType.v("java.lang.String"), 1));
		body.getLocals().add(frm1);
		
		// Create a local to hold the PrintStream System.out
		Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		
		// Create a unit (or statement) that assigns main's formal param into the local arg
		PatchingChain<Unit> units = body.getUnits();
		units.add(Jimple.v().newIdentityStmt(frm1,
				Jimple.v().newParameterRef(ArrayType.v
						(RefType.v("java.lang.String"), 1), 0)));
		
		// Create a unit that assigns System.out to the local tmpRef
		units.add(Jimple.v().newAssignStmt(tmpRef,
				Jimple.v().newStaticFieldRef(Scene.v().getField
						("<java.lang.System: java.io.PrintStream out>").makeRef())));
		
		// Create the call to tmpRef.println("Hello world!")
		SootMethod toCall = Scene.v().getMethod
			("<java.io.PrintStream: void println(java.lang.String)>");
		units.add(Jimple.v().newInvokeStmt
				(Jimple.v().newVirtualInvokeExpr
						(tmpRef, toCall.makeRef(), StringConstant.v("Hello world!"))));
		
		// Add an empty return statement
		units.add(Jimple.v().newReturnVoidStmt());
		return body;
	}

	private static void write(SootClass sClass, int output_format) {
		OutputStream streamOut = null;
		try {
			String filename = SourceLocator.v().getFileNameFor(sClass, output_format);
			if (output_format == Options.output_format_class)
				streamOut = new JasminOutputStream(new FileOutputStream(filename));
			else
				streamOut = new FileOutputStream(filename);
			PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
			if (output_format == Options.output_format_class) {
				JasminClass jasClass = new JasminClass(sClass);
				jasClass.print(writerOut);
			} else if (output_format == Options.output_format_jimple)
				Printer.v().printTo(sClass, writerOut);
			else if (output_format == Options.output_format_dava)
				DavaPrinter.v().printTo(sClass, writerOut);
			writerOut.flush();
			writerOut.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (streamOut != null)
				try {
					streamOut.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
		}
	}
}
