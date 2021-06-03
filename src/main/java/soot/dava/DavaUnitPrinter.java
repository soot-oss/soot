package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
 * Copyright (C) 2004 - 2005 Nomair A. Naeem
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

import soot.AbstractUnitPrinter;
import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.IdentityRef;
import soot.jimple.Jimple;
import soot.jimple.ThisRef;

/**
 * UnitPrinter implementation for Dava.
 */
public class DavaUnitPrinter extends AbstractUnitPrinter {

  private boolean eatSpace = false;
  DavaBody body;

  /*
   * 30th March 2006, Nomair A Naeem Adding constructor so that the current methods DabaBody can be stored
   */
  public DavaUnitPrinter(DavaBody body) {
    this.body = body;
  }

  @Override
  public void methodRef(SootMethodRef m) {
    handleIndent();
    output.append(m.getName());
  }

  @Override
  public void fieldRef(SootFieldRef f) {
    handleIndent();
    output.append(f.name());
  }

  @Override
  public void identityRef(IdentityRef r) {
    handleIndent();
    if (r instanceof ThisRef) {
      literal("this");
    } else {
      throw new RuntimeException();
    }
  }

  @Override
  public void literal(String s) {
    handleIndent();
    if (eatSpace && " ".equals(s)) {
      eatSpace = false;
      return;
    }
    eatSpace = false;
    switch (s) {
      case Jimple.STATICINVOKE:
      case Jimple.VIRTUALINVOKE:
      case Jimple.INTERFACEINVOKE:
        eatSpace = true;
        return;
    }
    output.append(s);
  }

  @Override
  public void type(Type t) {
    handleIndent();
    if (t instanceof RefType) {
      SootClass sootClass = ((RefType) t).getSootClass();
      String name = sootClass.getJavaStyleName();
      /*
       * March 30th 2006, Nomair Adding check to check that the fully qualified name can actually be removed
       */
      if (!name.equals(sootClass.toString())) {
        // means javaStyle name is probably shorter check that there is no class clash in imports for this

        // System.out.println(">>>>Type is"+t.toString());
        // System.out.println(">>>>Name is"+name);
        name = RemoveFullyQualifiedName.getReducedName(body.getImportList(), sootClass.toString(), t);

      }
      output.append(name);
    } else if (t instanceof ArrayType) {
      ((ArrayType) t).toString(this);
    } else {
      output.append(t.toString());
    }
  }

  @Override
  public void unitRef(Unit u, boolean branchTarget) {
    throw new RuntimeException("Dava doesn't have unit references!");
  }

  @Override
  public void constant(Constant c) {
    if (c instanceof ClassConstant) {
      handleIndent();
      output.append(((ClassConstant) c).value.replace('/', '.')).append(".class");
    } else {
      super.constant(c);
    }
  }

  public void addNot() {
    output.append(" !");
  }

  public void addAggregatedOr() {
    output.append(" || ");
  }

  public void addAggregatedAnd() {
    output.append(" && ");
  }

  public void addLeftParen() {
    output.append(" (");
  }

  public void addRightParen() {
    output.append(") ");
  }

  public void printString(String s) {
    output.append(s);
  }
}
