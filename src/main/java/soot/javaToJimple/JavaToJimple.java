package soot.javaToJimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.SourceJob;
import polyglot.frontend.SourceLoader;
import polyglot.frontend.VisitorPass;

public class JavaToJimple {

  public static final polyglot.frontend.Pass.ID CAST_INSERTION = new polyglot.frontend.Pass.ID("cast-insertion");
  public static final polyglot.frontend.Pass.ID STRICTFP_PROP = new polyglot.frontend.Pass.ID("strictfp-prop");
  public static final polyglot.frontend.Pass.ID ANON_CONSTR_FINDER = new polyglot.frontend.Pass.ID("anon-constr-finder");
  public static final polyglot.frontend.Pass.ID SAVE_AST = new polyglot.frontend.Pass.ID("save-ast");

  /**
   * sets up the info needed to invoke polyglot
   */
  public polyglot.frontend.ExtensionInfo initExtInfo(String fileName, List<String> sourceLocations) {

    Set<String> source = new HashSet<String>();
    ExtensionInfo extInfo = new soot.javaToJimple.jj.ExtensionInfo() {
      public List passes(Job job) {
        List passes = super.passes(job);
        // beforePass(passes, Pass.EXIT_CHECK, new VisitorPass(polyglot.frontend.Pass.FOLD, job, new
        // polyglot.visit.ConstantFolder(ts, nf)));
        beforePass(passes, Pass.EXIT_CHECK, new VisitorPass(CAST_INSERTION, job, new CastInsertionVisitor(job, ts, nf)));
        beforePass(passes, Pass.EXIT_CHECK, new VisitorPass(STRICTFP_PROP, job, new StrictFPPropagator(false)));
        beforePass(passes, Pass.EXIT_CHECK,
            new VisitorPass(ANON_CONSTR_FINDER, job, new AnonConstructorFinder(job, ts, nf)));
        afterPass(passes, Pass.PRE_OUTPUT_ALL, new SaveASTVisitor(SAVE_AST, job, this));
        removePass(passes, Pass.OUTPUT);
        return passes;
      }

    };
    polyglot.main.Options options = extInfo.getOptions();

    options.assertions = true;
    options.source_path = new LinkedList<File>();
    Iterator<String> it = sourceLocations.iterator();
    while (it.hasNext()) {
      Object next = it.next();
      // System.out.println("adding src loc: "+next.toString());
      options.source_path.add(new File(next.toString()));
    }

    options.source_ext = new String[] { "java" };
    options.serialize_type_info = false;

    source.add(fileName);

    options.source_path.add(new File(fileName).getParentFile());

    polyglot.main.Options.global = options;

    return extInfo;
  }

  /**
   * uses polyglot to compile source and build AST
   */
  public polyglot.ast.Node compile(polyglot.frontend.Compiler compiler, String fileName,
      polyglot.frontend.ExtensionInfo extInfo) {
    SourceLoader source_loader = compiler.sourceExtension().sourceLoader();

    try {
      FileSource source = new FileSource(new File(fileName));
      // This hack is to stop the catch block at the bottom causing an error
      // with versions of Polyglot where the constructor above can't throw IOException
      // It should be removed as soon as Polyglot 1.3 is no longer supported.
      if (false) {
        throw new IOException("Bogus exception");
      }

      SourceJob job = null;

      if (compiler.sourceExtension() instanceof soot.javaToJimple.jj.ExtensionInfo) {
        soot.javaToJimple.jj.ExtensionInfo jjInfo = (soot.javaToJimple.jj.ExtensionInfo) compiler.sourceExtension();
        if (jjInfo.sourceJobMap() != null) {
          job = (SourceJob) jjInfo.sourceJobMap().get(source);
        }
      }
      if (job == null) {
        job = compiler.sourceExtension().addJob(source);
      }

      boolean result = false;
      result = compiler.sourceExtension().runToCompletion();

      if (!result) {

        throw new soot.CompilationDeathException(0, "Could not compile");
      }

      polyglot.ast.Node node = job.ast();

      return node;

    } catch (IOException e) {
      return null;
    }

  }

}
