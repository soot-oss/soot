/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Eric Bodden
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

package soot.jimple.toolkits.reflection;

import java.util.ArrayList;
import java.util.Arrays;

import soot.CompilationDeathException;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.jimple.toolkits.reflection.ReflectiveCallsInliner;
import soot.options.Options;
import soot.rtlib.tamiflex.DefaultHandler;
import soot.rtlib.tamiflex.IUnexpectedReflectiveCallHandler;
import soot.rtlib.tamiflex.OpaquePredicate;
import soot.rtlib.tamiflex.ReflectiveCalls;
import soot.rtlib.tamiflex.SootSig;
import soot.rtlib.tamiflex.UnexpectedReflectiveCall;


public class ReflInliner {
	
	public static void main(String[] args) {
		PackManager.v().getPack("wjpp").add(new Transform("wjpp.inlineReflCalls", new ReflectiveCallsInliner()));		
		Scene.v().addBasicClass(Object.class.getName());
		Scene.v().addBasicClass(SootSig.class.getName(),SootClass.BODIES);
		Scene.v().addBasicClass(UnexpectedReflectiveCall.class.getName(),SootClass.BODIES);
		Scene.v().addBasicClass(IUnexpectedReflectiveCallHandler.class.getName(),SootClass.BODIES);
		Scene.v().addBasicClass(DefaultHandler.class.getName(),SootClass.BODIES);
		Scene.v().addBasicClass(OpaquePredicate.class.getName(),SootClass.BODIES);
		Scene.v().addBasicClass(ReflectiveCalls.class.getName(),SootClass.BODIES);
		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		argList.add("-w");
		argList.add("-p");
		argList.add("cg");
		argList.add("enabled:false");
		argList.add("-app");
		
		Options.v().set_keep_line_number(true);
		
		G.v().out.println("TamiFlex Booster Version "+ReflInliner.class.getPackage().getImplementationVersion());
		try {
			soot.Main.main(argList.toArray(new String[0]));
		} catch(CompilationDeathException e) {
			G.v().out.println("\nERROR: "+e.getMessage()+"\n");
			G.v().out.println("The command-line options are described at:\n" +
					"http://www.sable.mcgill.ca/soot/tutorial/usage/index.html");
			if(Options.v().verbose()) {
				throw e;
			} else {
				G.v().out.println("Use -verbose to see stack trace.");
			}
			G.v().out.println();
			usage();
		}
	}

	private static void usage() {
		G.v().out.println(Options.v().getUsage());
	}

}
