package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.Body;
import soot.G;
import soot.Modifier;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.util.Chain;

/*
 * Coded to remove the static "final" bug from Dava. Usually occurs in AspectJ code
 * **********The staticBlockInlining Method is invoked by PackManager**************
 *
 * In the bug this is what used to happen:
 *
 *        public static final ClassName myField;
 *        static{
               CLASSNAME.postClinit();

          }

 *        postClinit(){ myField = new ClassName(); }
 *
 *  Now this causes a problem since final fields can not be defined using a method call
 * So the solution was to inline just this method. to get something like
 *        static{
             myField = new ClassName();
	  }
 * At the same time the code in the method postClinit is removed and an exception is thrown if this method is invoked
 */

public class DavaStaticBlockCleaner {
  SootClass sootClass;

  public DavaStaticBlockCleaner(Singletons.Global g) {
  }

  public static DavaStaticBlockCleaner v() {
    return G.v().soot_dava_DavaStaticBlockCleaner();
  }

  // invoked by the PackManager
  public void staticBlockInlining(SootClass sootClass) {
    this.sootClass = sootClass;
    // retrieve the clinit method if any for sootClass
    // the clinit method gets converted into the static block which could initialize the final variable
    if (!sootClass.declaresMethod("void <clinit>()")) {
      // System.out.println("no clinit");
      return;
    }

    SootMethod clinit = sootClass.getMethod("void <clinit>()");
    // System.out.println(clinit);

    // retireve the active body
    if (!clinit.hasActiveBody()) {
      throw new RuntimeException("method " + clinit.getName() + " has no active body!");
    }

    Body clinitBody = clinit.getActiveBody();
    Chain units = ((DavaBody) clinitBody).getUnits();

    if (units.size() != 1) {
      throw new RuntimeException("DavaBody AST doesn't have single root.");
    }

    ASTNode AST = (ASTNode) units.getFirst();
    if (!(AST instanceof ASTMethodNode)) {
      throw new RuntimeException("Starting node of DavaBody AST is not an ASTMethodNode");
    }

    // running methodCallFinder on the Clinit method
    AST.apply(new MethodCallFinder(this));
  }

  /*
   * Method called with a sootMethod to decide whether this method should be inlined or not returns null if it shouldnt be
   * inlined
   *
   * A method can be inlined if it belongs to the same class and also if its static....(why???)
   */
  public ASTMethodNode inline(SootMethod maybeInline) {
    // check if this method should be inlined

    if (sootClass != null) {
      // 1, method should belong to the same class as the clinit method
      if (sootClass.declaresMethod(maybeInline.getSubSignature())) {
        // System.out.println("The method invoked is from the same class");
        // 2, method should be static

        if (Modifier.isStatic(maybeInline.getModifiers())) {
          // decided to inline
          // send the ASTMethod node of the TO BE INLINED METHOD

          // retireve the active body
          if (!maybeInline.hasActiveBody()) {
            throw new RuntimeException("method " + maybeInline.getName() + " has no active body!");
          }

          Body bod = maybeInline.getActiveBody();

          Chain units = ((DavaBody) bod).getUnits();

          if (units.size() != 1) {
            throw new RuntimeException("DavaBody AST doesn't have single root.");
          }

          ASTNode ASTtemp = (ASTNode) units.getFirst();
          if (!(ASTtemp instanceof ASTMethodNode)) {
            throw new RuntimeException("Starting node of DavaBody AST is not an ASTMethodNode");
          }

          // restricting to methods which do not have any variables declared
          ASTMethodNode toReturn = (ASTMethodNode) ASTtemp;

          ASTStatementSequenceNode declarations = toReturn.getDeclarations();
          if (declarations.getStatements().size() == 0) {
            // inline only if there are no declarations in the method inlined
            // System.out.println("No declarations in the method. we can inline this method");
            return toReturn;
          }
        }
      }
    }
    return null;// meaning dont inline
  }

}
