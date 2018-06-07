package soot.jimple.parser;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
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
import java.util.Iterator;
import java.util.List;

import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.jimple.parser.node.AFile;
import soot.jimple.parser.node.AMethodMember;
import soot.jimple.parser.node.AThrowsClause;
import soot.jimple.parser.node.PModifier;

/*
   Walks a jimple AST and extracts the fields, and method signatures and produces
   a new squeleton SootClass instance.
*/
public class SkeletonExtractorWalker extends Walker {

  public SkeletonExtractorWalker(SootResolver aResolver, SootClass aSootClass) {
    super(aSootClass, aResolver);
  }

  public SkeletonExtractorWalker(SootResolver aResolver) {
    super(aResolver);
  }

  public void caseAFile(AFile node) {
    inAFile(node);
    {
      Object temp[] = node.getModifier().toArray();
      for (Object element : temp) {
        ((PModifier) element).apply(this);
      }
    }
    if (node.getFileType() != null) {
      node.getFileType().apply(this);
    }
    if (node.getClassName() != null) {
      node.getClassName().apply(this);
    }

    String className = (String) mProductions.removeLast();

    if (mSootClass == null) {
      mSootClass = new SootClass(className);
      mSootClass.setResolvingLevel(SootClass.SIGNATURES);
    } else {
      if (!className.equals(mSootClass.getName())) {
        throw new RuntimeException("expected:  " + className + ", but got: " + mSootClass.getName());
      }
    }

    if (node.getExtendsClause() != null) {
      node.getExtendsClause().apply(this);
    }
    if (node.getImplementsClause() != null) {
      node.getImplementsClause().apply(this);
    }
    if (node.getFileBody() != null) {
      node.getFileBody().apply(this);
    }
    outAFile(node);
  }

  public void outAFile(AFile node) {
    List implementsList = null;
    String superClass = null;

    String classType = null;

    if (node.getImplementsClause() != null) {
      implementsList = (List) mProductions.removeLast();
    }
    if (node.getExtendsClause() != null) {
      superClass = (String) mProductions.removeLast();

    }
    classType = (String) mProductions.removeLast();

    int modifierFlags = processModifiers(node.getModifier());

    if (classType.equals("interface")) {
      modifierFlags |= Modifier.INTERFACE;
    }

    mSootClass.setModifiers(modifierFlags);

    if (superClass != null) {
      mSootClass.setSuperclass(mResolver.makeClassRef(superClass));
    }

    if (implementsList != null) {
      Iterator implIt = implementsList.iterator();
      while (implIt.hasNext()) {
        SootClass interfaceClass = mResolver.makeClassRef((String) implIt.next());
        mSootClass.addInterface(interfaceClass);
      }
    }

    mProductions.addLast(mSootClass);
  }

  /*
   * member = {field} modifier* type name semicolon | {method} modifier* type name l_paren parameter_list? r_paren
   * throws_clause? method_body;
   */

  public void caseAMethodMember(AMethodMember node) {
    inAMethodMember(node);
    {
      Object temp[] = node.getModifier().toArray();
      for (Object element : temp) {
        ((PModifier) element).apply(this);
      }
    }
    if (node.getType() != null) {
      node.getType().apply(this);
    }
    if (node.getName() != null) {
      node.getName().apply(this);
    }
    if (node.getLParen() != null) {
      node.getLParen().apply(this);
    }
    if (node.getParameterList() != null) {
      node.getParameterList().apply(this);
    }
    if (node.getRParen() != null) {
      node.getRParen().apply(this);
    }
    if (node.getThrowsClause() != null) {
      node.getThrowsClause().apply(this);
    }
    /*
     * if(node.getMethodBody() != null) { node.getMethodBody().apply(this); }
     */
    outAMethodMember(node);
  }

  public void outAMethodMember(AMethodMember node) {
    int modifier = 0;
    Type type;
    String name;
    List parameterList = null;
    List<SootClass> throwsClause = null;
    if (node.getThrowsClause() != null) {
      throwsClause = (List<SootClass>) mProductions.removeLast();
    }

    if (node.getParameterList() != null) {
      parameterList = (List) mProductions.removeLast();
    } else {
      parameterList = new ArrayList();
    }

    Object o = mProductions.removeLast();

    name = (String) o;
    type = (Type) mProductions.removeLast();
    modifier = processModifiers(node.getModifier());

    SootMethod method;

    if (throwsClause != null) {
      method = Scene.v().makeSootMethod(name, parameterList, type, modifier, throwsClause);
    } else {
      method = Scene.v().makeSootMethod(name, parameterList, type, modifier);
    }

    mSootClass.addMethod(method);
  }

  /*
   * throws_clause = throws class_name_list;
   */
  public void outAThrowsClause(AThrowsClause node) {
    List l = (List) mProductions.removeLast();

    Iterator it = l.iterator();
    List<SootClass> exceptionClasses = new ArrayList<SootClass>(l.size());

    while (it.hasNext()) {
      String className = (String) it.next();

      exceptionClasses.add(mResolver.makeClassRef(className));
    }

    mProductions.addLast(exceptionClasses);
  }
}
