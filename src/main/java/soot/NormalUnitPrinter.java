package soot;

import java.util.List;

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
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

/**
 * UnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
 */
public class NormalUnitPrinter extends LabeledUnitPrinter {
  private final Scene scene = Scene.v();

  public NormalUnitPrinter(Body body) {
    super(body);
  }

  @Override
  public void type(Type t) {
    handleIndent();
    output.append(t == null ? "<null>" : scene.quotedTypeNameOf(t));
  }

  @Override
  public void methodRef(SootMethodRef m) {
    handleIndent();
    // we need to quote the signature
    output.append('<').append(scene.quotedNameOf(m.declaringClass().getName())).append(": ");
    output.append(scene.quotedTypeNameOf(m.getReturnType()));
    output.append(' ').append(scene.quotedNameOf(m.name())).append('(');
    final List<Type> pt = m.getParameterTypes();
    for (int i = 0; i < pt.size(); i++) {
      if (i != 0) {
        output.append(',');
      }
      output.append(scene.quotedTypeNameOf(pt.get(i)));
    }
    output.append(")>");
  }

  @Override
  public void fieldRef(SootFieldRef f) {
    handleIndent();
    // we need to quote the signature
    output.append('<').append(scene.quotedNameOf(f.declaringClass().getName())).append(": ");
    output.append(scene.quotedTypeNameOf(f.type()));
    output.append(' ').append(scene.quotedNameOf(f.name()));
    output.append('>');
  }

  @Override
  public void identityRef(IdentityRef r) {
    handleIndent();
    if (r instanceof ThisRef) {
      literal("@this: ");
      type(r.getType());
    } else if (r instanceof ParameterRef) {
      ParameterRef pr = (ParameterRef) r;
      literal("@parameter" + pr.getIndex() + ": ");
      type(r.getType());
    } else if (r instanceof CaughtExceptionRef) {
      literal("@caughtexception");
    } else {
      throw new RuntimeException();
    }
  }

  @Override
  public void literal(String s) {
    handleIndent();
    output.append(s);
  }
}
