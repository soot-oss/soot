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

public class PrivateInstancesAvailable extends polyglot.visit.NodeVisitor {

  private final ArrayList<IdentityKey> list;

  public ArrayList<IdentityKey> getList() {
    return list;
  }

  public PrivateInstancesAvailable() {
    list = new ArrayList<IdentityKey>();
  }

  public polyglot.ast.Node leave(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor visitor) {

    if (n instanceof polyglot.ast.FieldDecl) {

      polyglot.types.FieldInstance fi = ((polyglot.ast.FieldDecl) n).fieldInstance();

      if (fi.flags().isPrivate()) {
        list.add(new polyglot.util.IdentityKey(fi));
      }
    }
    if (n instanceof polyglot.ast.ProcedureDecl) {

      polyglot.types.ProcedureInstance pi = ((polyglot.ast.ProcedureDecl) n).procedureInstance();

      if (pi.flags().isPrivate()) {
        list.add(new polyglot.util.IdentityKey(pi));
      }
    }
    return n;
  }
}
