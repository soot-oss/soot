/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Maintained by: Nomair A. Naeem
 */

/*
 * CHANGE LOG:   24th November   Created Class since the newinitialFlow of reachingDefs need a universal set of defs
 *              
 *              
 *              
 */


package soot.dava.toolkits.base.AST.traversals;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.toolkits.base.AST.structuredAnalysis.*;


/*
 * DefinitionStmts can occur in either ASTStatementSequenceNode or the for init and for update
 * These are needed for the newinitialFlow method of reachingDefs which needs a universal set of definitions
 */


public class AllDefinitionsFinder extends DepthFirstAdapter{
    ArrayList allDefs = new ArrayList();

    public AllDefinitionsFinder(){

    }

    public AllDefinitionsFinder(boolean verbose){
	super(verbose);
    }

    public void inDefinitionStmt(DefinitionStmt s){
	allDefs.add(s);
    }


    public List getAllDefs(){
	return allDefs;
    }

}



