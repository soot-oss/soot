package soot.dava.toolkits.base.AST.traversals;

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

import java.util.ArrayList;
import java.util.List;

import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.DefinitionStmt;

/*
 * DefinitionStmts can occur in either ASTStatementSequenceNode or the for init and for update
 * These are needed for the newinitialFlow method of reachingDefs which needs a universal set of definitions
 */

public class AllDefinitionsFinder extends DepthFirstAdapter {
  ArrayList<DefinitionStmt> allDefs = new ArrayList<DefinitionStmt>();

  public AllDefinitionsFinder() {

  }

  public AllDefinitionsFinder(boolean verbose) {
    super(verbose);
  }

  public void inDefinitionStmt(DefinitionStmt s) {
    allDefs.add(s);
  }

  public List<DefinitionStmt> getAllDefs() {
    return allDefs;
  }

}
