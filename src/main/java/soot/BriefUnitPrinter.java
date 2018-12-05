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
  public BriefUnitPrinter(Body body) {
    super(body);
  }

  private boolean baf;

  public void startUnit(Unit u) {
    super.startUnit(u);
    if (u instanceof Stmt) {
      baf = false;
    } else {
      baf = true;
    }
  }

  public void methodRef(SootMethodRef m) {
    handleIndent();
    if (!baf && m.resolve().isStatic()) {
      output.append(m.declaringClass().getName());
      literal(".");
    }
    output.append(m.name());
  }

  public void fieldRef(SootFieldRef f) {
    handleIndent();
    if (baf || f.resolve().isStatic()) {
      output.append(f.declaringClass().getName());
      literal(".");
    }
    output.append(f.name());
  }

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

  public void literal(String s) {
    handleIndent();
    if (eatSpace && s.equals(" ")) {
      eatSpace = false;
      return;
    }
    eatSpace = false;
    if (!baf) {
      if (false || s.equals(Jimple.STATICINVOKE) || s.equals(Jimple.VIRTUALINVOKE) || s.equals(Jimple.INTERFACEINVOKE)) {
        eatSpace = true;
        return;
      }
    }
    output.append(s);
  }

  public void type(Type t) {
    handleIndent();
    output.append(t.toString());
  }
}
