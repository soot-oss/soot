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
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTUnaryCondition extends ASTUnaryBinaryCondition{
      Value value;
	
      public ASTUnaryCondition(Value value){
    	  this.value=value;
      }
    
      public void apply(Analysis a){
    	  a.caseASTUnaryCondition(this);
      }

      public Value getValue(){
    	  return value;
      }

      
      public void setValue(Value value){
    	  this.value=value;
      }


      public String toString(){
    	  return value.toString();
      }

      public void toString(UnitPrinter up){
    	  value.toString(up);
      }

    
      public void flip(){
    	  /*
    	   Since its a unarycondition we know this is a flag
    	   See if its a DNotExpr if yes set this.value to the op inside DNotExpr
    	   If it is NOT a DNotExpr make one
    	   */
    	  if(value instanceof DNotExpr){
    		  this.value=((DNotExpr)value).getOp();
    	  }
    	  else{
    		  this.value=new DNotExpr(value);
    	  }
      }
      
      public boolean isNotted(){
    	  return (value instanceof DNotExpr);   		  
      }
}
