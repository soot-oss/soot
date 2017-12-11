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

package soot.dava.internal.AST;

import soot.*;
import soot.dava.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTAndCondition extends ASTAggregatedCondition{

    public ASTAndCondition(ASTCondition left, ASTCondition right){
    	super(left,right);
    }

    public void apply(Analysis a){
    	a.caseASTAndCondition(this);
    }

    public String toString(){
	if(left instanceof ASTUnaryBinaryCondition){
	   if(right instanceof ASTUnaryBinaryCondition){
	       if(not)
		   return "!("+ left.toString() + " && "+ right.toString()+")";
	       else
		   return left.toString() + " && "+ right.toString();
	   }
	   else{ //right is ASTAggregatedCondition
	       if(not)
		   return "!("+left.toString() + " && ("+right.toString() +" ))";
	       else
		   return left.toString() + " && ("+right.toString() +" )";
           }
        }
	else{ //left is ASTAggregatedCondition
	   if(right instanceof ASTUnaryBinaryCondition){
	       if(not)
		   return "!(( "+left.toString() + ") && "+ right.toString()+")";
	       else
		   return "( "+left.toString() + ") && "+ right.toString();
	   }
	   else{ //right is ASTAggregatedCondition also
	       if(not)
		   return "!(( "+left.toString() + ") && ("+right.toString() +" ))";
	       else
		   return "( "+left.toString() + ") && ("+right.toString() +" )";
           }
	}
    }

    public void toString(UnitPrinter up){
	if(up instanceof DavaUnitPrinter){
	    
	    if(not){
		//print !
		((DavaUnitPrinter)up).addNot();
		//print left paren
		((DavaUnitPrinter)up).addLeftParen();
	    }

	    if(left instanceof ASTUnaryBinaryCondition){
		if(right instanceof ASTUnaryBinaryCondition){
		    
		    left.toString(up);
		    
		    ((DavaUnitPrinter)up).addAggregatedAnd();
		    
		    right.toString(up);
		}
		else{ //right is ASTAggregatedCondition
		    
		    left.toString(up); 
		    
		    ((DavaUnitPrinter)up).addAggregatedAnd(); 
		    
		    ((DavaUnitPrinter)up).addLeftParen();
		    right.toString(up); 
		    ((DavaUnitPrinter)up).addRightParen();
		}
	    }
	    else{ //left is ASTAggregatedCondition
		if(right instanceof ASTUnaryBinaryCondition){
		    
		    ((DavaUnitPrinter)up).addLeftParen();
		    left.toString(up); 
		    ((DavaUnitPrinter)up).addRightParen();
		    
		    ((DavaUnitPrinter)up).addAggregatedAnd(); 
		    
		    right.toString(up); 
		}
		else{ //right is ASTAggregatedCondition also
		    
		    ((DavaUnitPrinter)up).addLeftParen();
		    left.toString(up); 
		    ((DavaUnitPrinter)up).addRightParen();
		    
		    ((DavaUnitPrinter)up).addAggregatedAnd(); 
		    
		    ((DavaUnitPrinter)up).addLeftParen();
		    right.toString(up); 
		    ((DavaUnitPrinter)up).addRightParen();
		}
	    }

	    if(not){
		//print right paren
		((DavaUnitPrinter)up).addRightParen();
	    }
	}
	else
	    throw new RuntimeException();
    }
    
}
