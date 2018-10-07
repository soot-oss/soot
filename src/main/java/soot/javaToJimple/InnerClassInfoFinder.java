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

import polyglot.ast.Node;

public class InnerClassInfoFinder extends polyglot.visit.NodeVisitor {

  private final ArrayList<Node> localClassDeclList;
  private final ArrayList<Node> anonBodyList;
  private final ArrayList<Node> memberList;
  // private ArrayList declaredInstList;
  // private ArrayList usedInstList;

  public ArrayList<Node> memberList() {
    return memberList;
  }

  /*
   * public ArrayList declaredInstList(){ return declaredInstList; }
   *
   * public ArrayList usedInstList(){ return usedInstList; }
   */

  public ArrayList<Node> localClassDeclList() {
    return localClassDeclList;
  }

  public ArrayList<Node> anonBodyList() {
    return anonBodyList;
  }

  public InnerClassInfoFinder() {
    // declFound = null;
    localClassDeclList = new ArrayList<Node>();
    anonBodyList = new ArrayList<Node>();
    memberList = new ArrayList<Node>();
    // declaredInstList = new ArrayList();
    // usedInstList = new ArrayList();
  }

  public polyglot.visit.NodeVisitor enter(polyglot.ast.Node parent, polyglot.ast.Node n) {

    if (n instanceof polyglot.ast.LocalClassDecl) {
      localClassDeclList.add(n);
    }
    if (n instanceof polyglot.ast.New) {
      if (((polyglot.ast.New) n).anonType() != null) {
        anonBodyList.add(n);
      }
      /*
       * polyglot.types.ProcedureInstance pi = ((polyglot.ast.New)n).constructorInstance(); if (pi.isPrivate()){
       * usedInstList.add(new polyglot.util.IdentityKey(pi)); }
       */
    }

    if (n instanceof polyglot.ast.ProcedureDecl) {
      memberList.add(n);
      /*
       * polyglot.types.ProcedureInstance pi = ((polyglot.ast.ProcedureDecl)n).procedureInstance(); if
       * (pi.flags().isPrivate()){ declaredInstList.add(new polyglot.util.IdentityKey(pi)); }
       */
    }
    if (n instanceof polyglot.ast.FieldDecl) {
      memberList.add(n);
      /*
       * polyglot.types.FieldInstance fi = ((polyglot.ast.FieldDecl)n).fieldInstance(); if (fi.flags().isPrivate()){
       * declaredInstList.add(new polyglot.util.IdentityKey(fi)); }
       */
    }
    if (n instanceof polyglot.ast.Initializer) {
      memberList.add(n);
    }

    /*
     * if (n instanceof polyglot.ast.Field) { polyglot.types.FieldInstance fi = ((polyglot.ast.Field)n).fieldInstance(); if
     * (fi.isPrivate()){ usedInstList.add(new polyglot.util.IdentityKey(fi)); } } if (n instanceof polyglot.ast.Call){
     * polyglot.types.ProcedureInst pi = ((polyglot.ast.Call)n).methodInstance(); if (pi.isPrivate()){ usedInstList.add(new
     * polyglot.util.IdentityKey(pi)); } } if (n instanceof polyglot.ast.ConstructorCall){ polyglot.types.ProcedureInstance
     * pi = ((polyglot.ast.ConstructorCall)n).constructorInstance(); if (pi.isPrivate()){ usedInstList.add(new
     * polyglot.util.IdentityKey(pi)); } }
     */

    return enter(n);
  }
}
