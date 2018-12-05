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

public class LocalClassDeclFinder extends polyglot.visit.NodeVisitor {

  private polyglot.types.ClassType typeToFind;
  private polyglot.ast.LocalClassDecl declFound;

  public void typeToFind(polyglot.types.ClassType type) {
    typeToFind = type;
  }

  public polyglot.ast.LocalClassDecl declFound() {
    return declFound;
  }

  public LocalClassDeclFinder() {
    declFound = null;
  }

  public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {

    if (n instanceof polyglot.ast.LocalClassDecl) {
      if (((polyglot.ast.LocalClassDecl) n).decl().type().equals(typeToFind)) {
        declFound = (polyglot.ast.LocalClassDecl) n;
      }
    }
    return enter(n);
  }
}
