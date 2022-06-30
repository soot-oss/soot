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

import java.util.ArrayList;

import polyglot.util.IdentityKey;

public class PrivateAccessUses extends polyglot.visit.NodeVisitor {

  private final ArrayList<IdentityKey> list;
  private ArrayList avail;

  public ArrayList<IdentityKey> getList() {
    return list;
  }

  public void avail(ArrayList list) {
    avail = list;
  }

  public PrivateAccessUses() {
    list = new ArrayList<IdentityKey>();
  }

  public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {

    if (n instanceof polyglot.ast.Field) {

      polyglot.types.FieldInstance fi = ((polyglot.ast.Field) n).fieldInstance();

      if (avail.contains(new polyglot.util.IdentityKey(fi))) {
        list.add(new polyglot.util.IdentityKey(fi));
      }
    }
    if (n instanceof polyglot.ast.Call) {

      polyglot.types.ProcedureInstance pi = ((polyglot.ast.Call) n).methodInstance();

      if (avail.contains(new polyglot.util.IdentityKey(pi))) {
        list.add(new polyglot.util.IdentityKey(pi));
      }
    }
    if (n instanceof polyglot.ast.New) {

      polyglot.types.ProcedureInstance pi = ((polyglot.ast.New) n).constructorInstance();

      if (avail.contains(new polyglot.util.IdentityKey(pi))) {
        list.add(new polyglot.util.IdentityKey(pi));
      }
    }
    if (n instanceof polyglot.ast.ConstructorCall) {

      polyglot.types.ProcedureInstance pi = ((polyglot.ast.ConstructorCall) n).constructorInstance();

      if (avail.contains(new polyglot.util.IdentityKey(pi))) {
        list.add(new polyglot.util.IdentityKey(pi));
      }
    }
    return n;
  }
}
