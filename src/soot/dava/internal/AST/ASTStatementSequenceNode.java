/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTStatementSequenceNode extends ASTNode
{
    private List statementSequence;

    public ASTStatementSequenceNode( List statementSequence)
    {
	super();

	this.statementSequence = statementSequence;
    }

    public Object clone()
    {
	return new ASTStatementSequenceNode( statementSequence);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	if (a.getAnalysisDepth() > ASTAnalysis.ANALYSE_AST) {

	    Iterator it = statementSequence.iterator();
	    while (it.hasNext())
		ASTWalker.v().walk_stmt( a, ((AugmentedStmt) it.next()).get_Stmt());
	}

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}
    }

    public void toString( UnitPrinter up ) {
    	Iterator it = statementSequence.iterator();
    	while (it.hasNext()) {
                AugmentedStmt as = (AugmentedStmt) it.next();
    	    //System.out.println("Stmt is:"+as.get_Stmt());
                Unit u = as.get_Stmt();
                up.startUnit( u );
                u.toString( up );
                up.literal(";");
                up.endUnit( u );
                up.newline();
            }
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	Iterator it = statementSequence.iterator();
	while (it.hasNext()) {
	    b.append( ((Unit) ((AugmentedStmt) it.next()).get_Stmt()).toString());
	    b.append( ";");
	    b.append( NEWLINE);
	}

	return b.toString();
    }


    /*
      Nomair A. Naeem, 7-FEB-05
      Part of Visitor Design Implementation for AST
      See: soot.dava.toolkits.base.AST.analysis For details
    */
    public List getStatements(){
	return statementSequence;
    }

    public void apply(Analysis a){
	a.caseASTStatementSequenceNode(this);
    }




    /*
      Nomair A. Naeem added 3-MAY-05
    */
    public void setStatements(List statementSequence){
	this.statementSequence=statementSequence;
    }
}
