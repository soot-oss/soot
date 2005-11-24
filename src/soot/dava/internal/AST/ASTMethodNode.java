/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004-2005 Nomair A. Naeem

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


/**
 * Maintained by Nomair A. Naeem
 */


/*
 * CHANGE LOG:  23rd november 2005, Adding method getDeclaredLocals to return all locals
 *              declared in the declarations ASTStatementSequenceNode
 */

package soot.dava.internal.AST;

import soot.*;
import java.util.*;
import soot.dava.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.asg.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

public class ASTMethodNode extends ASTNode
{
    private List body;
    private DavaBody davaBody;

    private ASTStatementSequenceNode declarations;


    /*
      typeToLocals stores the type of the local and a list of all locals with that type
    */
    private Map typeToLocals;

    public ASTStatementSequenceNode getDeclarations(){
	return declarations;
    }

    public void storeLocals(Body OrigBody){
	if ((OrigBody instanceof DavaBody) == false)
            throw new RuntimeException("Only DavaBodies should invoke this method");

        davaBody = (DavaBody) OrigBody;
	typeToLocals =
	    new DeterministicHashMap(OrigBody.getLocalCount() * 2 + 1, 0.7f);

	HashSet params = new HashSet();
	params.addAll(davaBody.get_ParamMap().values());
	params.addAll(davaBody.get_CaughtRefs());
	HashSet thisLocals = davaBody.get_ThisLocals();
	

	//populating the typeToLocals Map
	Iterator localIt = OrigBody.getLocals().iterator();
	while (localIt.hasNext()) {
	    Local local = (Local) localIt.next();

	    if (params.contains(local) || thisLocals.contains(local))
		continue;

	    List localList;

	    String typeName;
	    Type t = local.getType();

	    typeName = t.toString();

	    if (typeToLocals.containsKey(t))
		localList = (List) typeToLocals.get(t);
	    else {
		localList = new ArrayList();
		typeToLocals.put(t, localList);
	    }
	    
	    localList.add(local);
	}


	//create a StatementSequenceNode with all the declarations
	
	List statementSequence = new ArrayList();
	
	Iterator typeIt = typeToLocals.keySet().iterator();

	while (typeIt.hasNext()) {
	    Type typeObject = (Type) typeIt.next();
	    String type = typeObject.toString();

	    List localList = (List) typeToLocals.get(typeObject);
	    Object[] locals = localList.toArray();

	    DVariableDeclarationStmt varStmt = null;
	    varStmt = new DVariableDeclarationStmt(typeObject);
	    
	    for (int k = 0; k < locals.length; k++) {
		varStmt.addLocal((Local)locals[k]);
	    }
	    AugmentedStmt as = new AugmentedStmt(varStmt);
	    statementSequence.add(as);
	}

	declarations = new ASTStatementSequenceNode(statementSequence);

	body.add(0,declarations);
	subBodies = new ArrayList();
	subBodies.add( body);
    }





    public ASTMethodNode( List body)
    {
	super();
	this.body = body;

	subBodies.add( body);
    }

    /*
      Nomair A. Naeem 23rd November 2005
      Need to efficiently get all locals being declared in the declarations node
      Dont really care what type they are.. Interesting thing is that they are all different names :)
    */
    public List getDeclaredLocals(){
	List toReturn = new ArrayList();

	Iterator it = declarations.getStatements().iterator();

	while(it.hasNext()){//going through each stmt
	    Stmt s = ((AugmentedStmt)it.next()).get_Stmt();

	    if(! (s instanceof DVariableDeclarationStmt))
		continue;//shouldnt happen since this node only contains declarations

	    DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt)s;

	    //get the locals of this particular type
	    List declarations = varStmt.getDeclarations();
	    Iterator decIt = declarations.iterator();
	    while(decIt.hasNext()){
		//going through each local declared
		
		toReturn.add(decIt.next());
	    }//going through all locals of this type
	}//going through all stmts 
	return toReturn;
    }


    /*
     * Given a local first searches the declarations for the local
     * Once it is found the local is removed from its declaring stmt
     * If the declaring stmt does not declare any more locals the stmt itself is removed
     * IT WOULD BE NICE TO ALSO CHECK IF THIS WAS THE LAST STMT IN THE NODE IN WHICH CASE THE NODE SHOULD BE REMOVED
     * just afraid of its after effects on other analyses!!!!
     */
    public void removeDeclaredLocal(Local local){
	Stmt s=null;
	Iterator it = declarations.getStatements().iterator();
	while(it.hasNext()){//going through each stmt
	    s = ((AugmentedStmt)it.next()).get_Stmt();

	    if(! (s instanceof DVariableDeclarationStmt))
		continue;//shouldnt happen since this node only contains declarations
	    
	    DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt)s;

	    //get the locals declared in this stmt
	    List declarations = varStmt.getDeclarations();
	    Iterator decIt = declarations.iterator();

	    boolean foundIt=false;//becomes true if the local was found in this stmt
	    while(decIt.hasNext()){
		//going through each local declared
		Local temp = (Local)decIt.next();
		if(temp.getName().compareTo(local.getName())==0){
		    //found it
		    foundIt = true;
		    break;
		}
	    }

	    if(foundIt){
		varStmt.removeLocal(local);
		break; //breaks going through other stmts as we already did what we needed to do
	    }
	}
	//the removal of a local might have made some declaration empty
	//remove such a declaraion

	List newSequence = new ArrayList();
	it = declarations.getStatements().iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    s = as.get_Stmt();

	    if(! (s instanceof DVariableDeclarationStmt))
		continue;

	    DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt)s;

	    if(varStmt.getDeclarations().size()!=0)
		newSequence.add(as);
	    
	}
	declarations.setStatements(newSequence);

    }




    /*
      Nomair A Naeem 21-FEB-2005
      Used by UselessLabeledBlockRemove to update a body
    */
    public void replaceBody(List body){
	this.body=body;
	subBodies=new ArrayList();
	subBodies.add(body);
    }


    public Object clone()
    {
	return new ASTMethodNode( body);
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	perform_AnalysisOnSubBodies( a);
    }


    public void toString( UnitPrinter up ) {
	if(!(up instanceof DavaUnitPrinter))
            throw new RuntimeException("Only DavaUnitPrinter should be used to print DavaBody");	    
	
	DavaUnitPrinter dup = (DavaUnitPrinter)up;

	/*
	  Print out constructor first
	*/
	InstanceInvokeExpr constructorExpr = davaBody.get_ConstructorExpr();
	if (constructorExpr != null) {

	    if (davaBody.getMethod().getDeclaringClass().getName()
		.equals(constructorExpr.getMethodRef().declaringClass().toString()))
		dup.printString("        this(");
	    else
		dup.printString("        super(");

	    Iterator ait = constructorExpr.getArgs().iterator();
	    while (ait.hasNext()) {
		dup.printString(ait.next().toString());
		
		if (ait.hasNext())
		    dup.printString(", ");
	    }
	    
	    dup.printString(");\n\n");
	}
	
	// print out the remaining body
        body_toString( up, body );
    }


    public String toString(){
	StringBuffer b = new StringBuffer();
	/*
	  Print out constructor first
	*/
	InstanceInvokeExpr constructorExpr = davaBody.get_ConstructorExpr();
	if (constructorExpr != null) {

	    if (davaBody.getMethod().getDeclaringClass().getName()
		.equals(constructorExpr.getMethodRef().declaringClass().toString()))
		b.append("        this(");
	    else
		b.append("        super(");

	    Iterator ait = constructorExpr.getArgs().iterator();
	    while (ait.hasNext()) {
		b.append(ait.next().toString());
		
		if (ait.hasNext())
		    b.append(", ");
	    }
	    
	    b.append(");\n\n");
	}
	
	
	// print out the remaining body
	b.append(body_toString(body));
	return b.toString();
    }

    /*
      Nomair A. Naeem, 7-FEB-05
      Part of Visitor Design Implementation for AST
      See: soot.dava.toolkits.base.AST.analysis For details
    */
    public void apply(Analysis a){
	a.caseASTMethodNode(this);
    }
}
