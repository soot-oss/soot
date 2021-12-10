package soot.jimple.toolkits.reflection;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Eric Bodden
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.CompilationDeathException;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;
import soot.rtlib.tamiflex.DefaultHandler;
import soot.rtlib.tamiflex.IUnexpectedReflectiveCallHandler;
import soot.rtlib.tamiflex.OpaquePredicate;
import soot.rtlib.tamiflex.ReflectiveCalls;
import soot.rtlib.tamiflex.SootSig;
import soot.rtlib.tamiflex.UnexpectedReflectiveCall;

public class ReflInliner {
  private static final Logger logger = LoggerFactory.getLogger(ReflInliner.class);

  public static void main(String[] args) {
    PackManager.v().getPack("wjpp").add(new Transform("wjpp.inlineReflCalls", new ReflectiveCallsInliner()));
    final Scene scene = Scene.v();
    scene.addBasicClass(Object.class.getName());
    scene.addBasicClass(SootSig.class.getName(), SootClass.BODIES);
    scene.addBasicClass(UnexpectedReflectiveCall.class.getName(), SootClass.BODIES);
    scene.addBasicClass(IUnexpectedReflectiveCallHandler.class.getName(), SootClass.BODIES);
    scene.addBasicClass(DefaultHandler.class.getName(), SootClass.BODIES);
    scene.addBasicClass(OpaquePredicate.class.getName(), SootClass.BODIES);
    scene.addBasicClass(ReflectiveCalls.class.getName(), SootClass.BODIES);

    ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
    argList.add("-w");
    argList.add("-p");
    argList.add("cg");
    argList.add("enabled:false");
    argList.add("-app");

    Options.v().set_keep_line_number(true);

    logger.debug("TamiFlex Booster Version " + ReflInliner.class.getPackage().getImplementationVersion());
    try {
      soot.Main.main(argList.toArray(new String[0]));
    } catch (CompilationDeathException e) {
      logger.debug("\nERROR: " + e.getMessage() + "\n");
      logger.debug(
          "The command-line options are described at:\n" + "http://www.sable.mcgill.ca/soot/tutorial/usage/index.html");
      if (Options.v().verbose()) {
        throw e;
      } else {
        logger.debug("Use -verbose to see stack trace.");
      }

      usage();
    }
  }

  private static void usage() {
    logger.debug(Options.v().getUsage());
  }
}
