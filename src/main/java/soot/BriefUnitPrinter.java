package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 - 2004 Ondrej Lhotak
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

import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityRef;
import soot.jimple.Jimple;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;

/**
 * UnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
 */
public class BriefUnitPrinter extends LabeledUnitPrinter {

  private boolean baf;

  public BriefUnitPrinter(Body body) {
    super(body);
  }

  @Override
  public void startUnit(Unit u) {
    super.startUnit(u);
    baf = !(u instanceof Stmt);
  }

  @Override
  public void methodRef(SootMethodRef m) {
    handleIndent();
    if (!baf && m.resolve().isStatic()) {
      output.append(m.getDeclaringClass().getName());
      literal(".");
    }
    output.append(m.name());
  }

  @Override
  public void fieldRef(SootFieldRef f) {
    handleIndent();
    if (baf || f.resolve().isStatic()) {
      output.append(f.declaringClass().getName());
      literal(".");
    }
    output.append(f.name());
  }

  @Override
  public void identityRef(IdentityRef r) {
    handleIndent();
    if (r instanceof ThisRef) {
      literal("@this");
    } else if (r instanceof ParameterRef) {
      ParameterRef pr = (ParameterRef) r;
      literal("@parameter" + pr.getIndex());
    } else if (r instanceof CaughtExceptionRef) {
      literal("@caughtexception");
    } else {
      throw new RuntimeException();
    }
  }

  private boolean eatSpace = false;

  @Override
  public void literal(String s) {
    handleIndent();
    if (eatSpace && " ".equals(s)) {
      eatSpace = false;
      return;
    }
    eatSpace = false;
    if (!baf) {
      switch (s) {
        case Jimple.STATICINVOKE:
        case Jimple.VIRTUALINVOKE:
        case Jimple.INTERFACEINVOKE:
          eatSpace = true;
          return;
      }
    }
    output.append(s);
  }

  @Override
  public void type(Type t) {
    handleIndent();
    output.append(t.toString());
  }
}
