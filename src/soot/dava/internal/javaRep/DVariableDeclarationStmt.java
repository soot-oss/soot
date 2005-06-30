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


package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.dava.*;

public class DVariableDeclarationStmt extends AbstractUnit implements Stmt {

    Type declarationType=null;

    /*
      Declarations can either be identifiers (locals) eg int i;
      Or they can be DefinitionStmts  eg int i=0;
    */
    List declarations=null;


    public DVariableDeclarationStmt(Type decType){
	if(declarationType!=null)
	    throw new RuntimeException("creating a VariableDeclaration which has already been created");
	else{
	    declarationType=decType;
	    declarations = new ArrayList();
	}
    }

    public List getDeclarations(){
	return declarations;
    }

    public void addLocal(Local add){
	declarations.add(add);
    }

    public void removeLocal(Local remove){
	for(int i=0;i<declarations.size();i++){
	    Object temp = declarations.get(i);
	    if(temp instanceof Local){
		if(((Local)temp).getName().compareTo(remove.getName())==0){
		    //this is the local to be removed
		    //System.out.println("REMOVED"+temp);
		    declarations.remove(i);
		    return;
		}
	    }
	}
    }

    public void addDefinitionLocal(DefinitionStmt stmt){
	declarations.add(stmt);
    }


    public Type getType(){
	return declarationType;
    }

    public boolean isOfType(Type type){
	if(type.toString().compareTo(declarationType.toString())==0)
	    return true;
	else
	    return false;
    }




    public Object clone(){
	DVariableDeclarationStmt temp =  new DVariableDeclarationStmt(declarationType);
	Iterator it = declarations.iterator();
	while(it.hasNext()){
	    Object obj = it.next();
	    if(obj instanceof Local){
		Value temp1 = Grimp.cloneIfNecessary((Local)obj);
		if(temp1 instanceof Local)
		    temp.addLocal((Local)temp1);
	    }
	    else if (obj instanceof DefinitionStmt){
		temp.addDefinitionLocal((DefinitionStmt)((DefinitionStmt)obj).clone());
	    }
	}
	return temp;
    }

    public String toString(){
	StringBuffer b = new StringBuffer();

	if(declarations.size()==0)
	    return b.toString();

	String type = declarationType.toString();

	if (type.equals("null_type"))
	    b.append("Object");
	else
	    b.append(type);
	b.append(" ");
	    
	Iterator decIt = declarations.iterator();
	while(decIt.hasNext()){
	    Object tempDec = decIt.next();
	    if(tempDec instanceof Local)
		b.append(((Local)tempDec).getName());
	    else if (tempDec instanceof DefinitionStmt){
		b.append( ((Unit)tempDec).toString());
	    }
	    if (decIt.hasNext())
		b.append(", ");
	}
	return b.toString();
    }




    public void toString(UnitPrinter up){
	if(declarations.size()==0)
	    return;

	if(!(up instanceof DavaUnitPrinter))
	    throw new RuntimeException("DavaBody should always be printed using the DavaUnitPrinter");	    
	else{
	    DavaUnitPrinter dup = (DavaUnitPrinter)up;

	    String type = declarationType.toString();

	    if (type.equals("null_type"))
		dup.printString("Object");
	    else
		dup.printString(type);
	    dup.printString(" ");
	    
	    Iterator decIt = declarations.iterator();
	    while(decIt.hasNext()){
		Object tempDec = decIt.next();
		if(tempDec instanceof Local)
		    dup.printString(((Local)tempDec).getName());
		else if (tempDec instanceof DefinitionStmt){
		    Unit u = (Unit)tempDec;
		    u.toString( up );
		}
		if (decIt.hasNext())
		    dup.printString(", ");
	    }
	}
    }



    /*
      Methods needed to satisfy all obligations due to extension from AbstractUnit
      and implementing Stmt

    */



    public boolean fallsThrough() { return true;}        
    public boolean branches() { return false;}

    public boolean containsInvokeExpr(){
        return false;
    }

    public InvokeExpr getInvokeExpr(){
        throw new RuntimeException("getInvokeExpr() called with no invokeExpr present!");
    }

    public ValueBox getInvokeExprBox(){
        throw new RuntimeException("getInvokeExprBox() called with no invokeExpr present!");
    }

    public boolean containsArrayRef(){
	return false;
    }

    public ArrayRef getArrayRef(){
	throw new RuntimeException("getArrayRef() called with no ArrayRef present!");
    }

    public ValueBox getArrayRefBox(){
	throw new RuntimeException("getArrayRefBox() called with no ArrayRef present!");
    }

    public boolean containsFieldRef(){
	return false;
    }

    public FieldRef getFieldRef(){
	throw new RuntimeException("getFieldRef() called with no FieldRef present!");
    }

    public ValueBox getFieldRefBox(){
	throw new RuntimeException("getFieldRefBox() called with no FieldRef present!");
    }



    

}










