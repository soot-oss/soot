package soot.dava.toolkits.base.AST.interProcedural;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.HashMap;
import java.util.Iterator;

import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.toolkits.base.AST.transformations.CPApplication;
import soot.dava.toolkits.base.AST.transformations.EliminateConditions;
import soot.dava.toolkits.base.AST.transformations.LocalVariableCleaner;
import soot.dava.toolkits.base.AST.transformations.SimplifyConditions;
import soot.dava.toolkits.base.AST.transformations.SimplifyExpressions;
import soot.dava.toolkits.base.AST.transformations.UnreachableCodeEliminator;
import soot.dava.toolkits.base.AST.transformations.UselessLabelFinder;
import soot.dava.toolkits.base.AST.transformations.VoidReturnRemover;
import soot.dava.toolkits.base.renamer.Renamer;
import soot.dava.toolkits.base.renamer.infoGatheringAnalysis;
import soot.util.Chain;

public class InterProceduralAnalyses {

  public static boolean DEBUG = false;

  /*
   * Method is invoked by postProcessDava in PackManager if the transformations flag is true
   *
   * All interproceduralAnalyses should be applied in here
   */
  public static void applyInterProceduralAnalyses() {
    Chain<SootClass> classes = Scene.v().getApplicationClasses();

    if (DEBUG) {
      System.out.println("\n\nInvoking redundantFielduseEliminator");
    }
    ConstantFieldValueFinder finder = new ConstantFieldValueFinder(classes);

    HashMap<String, Object> constantValueFields = finder.getFieldsWithConstantValues();
    if (DEBUG) {
      finder.printConstantValueFields();
    }
    /*
     * The code above this gathers interprocedural information the code below this USES the interprocedural results
     */
    for (SootClass s : classes) {
      // go though all the methods
      for (Iterator<SootMethod> methodIt = s.methodIterator(); methodIt.hasNext();) {
        SootMethod m = methodIt.next();
        if (!m.hasActiveBody()) {
          continue;
        }

        DavaBody body = (DavaBody) m.getActiveBody();
        ASTNode AST = (ASTNode) body.getUnits().getFirst();
        if (!(AST instanceof ASTMethodNode)) {
          continue;
        }

        boolean deobfuscate = PhaseOptions.getBoolean(PhaseOptions.v().getPhaseOptions("db.deobfuscate"), "enabled");
        if (deobfuscate) {
          if (DEBUG) {
            System.out.println("\nSTART CP Class:" + s.getName() + " Method: " + m.getName());
          }
          AST.apply(
              new CPApplication((ASTMethodNode) AST, constantValueFields, finder.getClassNameFieldNameToSootFieldMapping()));

          if (DEBUG) {
            System.out.println("DONE CP for " + m.getName());
          }
        }

        // expression simplification
        // SimplifyExpressions.DEBUG=true;
        AST.apply(new SimplifyExpressions());

        // SimplifyConditions.DEBUG=true;
        AST.apply(new SimplifyConditions());

        // condition elimination
        // EliminateConditions.DEBUG=true;

        AST.apply(new EliminateConditions((ASTMethodNode) AST));
        // the above should ALWAYS be followed by an unreachable code eliminator
        AST.apply(new UnreachableCodeEliminator(AST));

        // local variable cleanup
        AST.apply(new LocalVariableCleaner(AST));

        // VERY EXPENSIVE STAGE of redoing all analyses!!!!
        if (deobfuscate) {
          if (DEBUG) {
            System.out.println("reinvoking analyzeAST");
          }
          UselessLabelFinder.DEBUG = false;
          body.analyzeAST();
        }

        // renaming should be applied as the last stage
        if (PhaseOptions.getBoolean(PhaseOptions.v().getPhaseOptions("db.renamer"), "enabled")) {
          applyRenamerAnalyses(AST, body);
        }

        // remove returns from void methods
        VoidReturnRemover.cleanClass(s);
      }
    }
  }

  /*
   * If there is any interprocedural information required it should be passed as argument to this method and then the renamer
   * can make use of it.
   */
  private static void applyRenamerAnalyses(ASTNode AST, DavaBody body) {
    // intra procedural heuristic gathering
    infoGatheringAnalysis info = new infoGatheringAnalysis(body);
    AST.apply(info);

    Renamer renamer = new Renamer(info.getHeuristicSet(), (ASTMethodNode) AST);
    renamer.rename();
  }

  private InterProceduralAnalyses() {
  }
}
