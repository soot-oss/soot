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
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;

/*
 * ALWAYS REMEMBER THAT THE FIRST NODE IN THE BODY OF A METHODNODE HAS TO BE A STATEMENT
 * SEQUENCE NODE WITH DECLARATIONS!!!!
 */
public class ASTMethodNode extends ASTNode {
	private List<Object> body;

	private DavaBody davaBody;

	private ASTStatementSequenceNode declarations;

	/*
	 * Variables that are used in shortcu statements are kept in the
	 * declarations since other analyses need quick access to all the declared
	 * locals in the method
	 * 
	 * Any local in the dontPrintLocals list is not printed in the top declarations
	 */
	private List<Local> dontPrintLocals = new ArrayList<Local>();
	
	public ASTStatementSequenceNode getDeclarations() {
		return declarations;
	}

	public void setDeclarations(ASTStatementSequenceNode decl) {
		declarations = decl;
	}

	public void setDavaBody(DavaBody bod) {
		this.davaBody = bod;
	}

	public DavaBody getDavaBody() {
		return davaBody;
	}

	public void storeLocals(Body OrigBody) {
		if ((OrigBody instanceof DavaBody) == false)
			throw new RuntimeException(
					"Only DavaBodies should invoke this method");

		davaBody = (DavaBody) OrigBody;
		Map<Type, List<Local>> typeToLocals = new DeterministicHashMap(
				OrigBody.getLocalCount() * 2 + 1, 0.7f);

		HashSet params = new HashSet();
		params.addAll(davaBody.get_ParamMap().values());
		params.addAll(davaBody.get_CaughtRefs());
		HashSet<Object> thisLocals = davaBody.get_ThisLocals();

		//populating the typeToLocals Map
		Iterator localIt = OrigBody.getLocals().iterator();
		while (localIt.hasNext()) {
			Local local = (Local) localIt.next();

			if (params.contains(local) || thisLocals.contains(local))
				continue;

			List<Local> localList;

			String typeName;
			Type t = local.getType();

			typeName = t.toString();

			if (typeToLocals.containsKey(t))
				localList = typeToLocals.get(t);
			else {
				localList = new ArrayList<Local>();
				typeToLocals.put(t, localList);
			}

			localList.add(local);
		}

		//create a StatementSequenceNode with all the declarations

		List<AugmentedStmt> statementSequence = new ArrayList<AugmentedStmt>();

		Iterator<Type> typeIt = typeToLocals.keySet().iterator();

		while (typeIt.hasNext()) {
			Type typeObject = typeIt.next();
			String type = typeObject.toString();

			
			DVariableDeclarationStmt varStmt = null;
			varStmt = new DVariableDeclarationStmt(typeObject,davaBody);

			List<Local> localList = typeToLocals.get(typeObject);
			for (Local element : localList) {
				varStmt.addLocal(element);
			}
			AugmentedStmt as = new AugmentedStmt(varStmt);
			statementSequence.add(as);
		}

		declarations = new ASTStatementSequenceNode(statementSequence);

		body.add(0, declarations);
		subBodies = new ArrayList<Object>();
		subBodies.add(body);
	}

	public ASTMethodNode(List<Object> body) {
		super();
		this.body = body;
		subBodies.add(body);	
	}

	/*
	 Nomair A. Naeem 23rd November 2005
	 Need to efficiently get all locals being declared in the declarations node
	 Dont really care what type they are.. Interesting thing is that they are all different names :)
	 */
	public List getDeclaredLocals() {
		List toReturn = new ArrayList();

		for (AugmentedStmt as : declarations.getStatements()) {//going through each stmt
			Stmt s = as.get_Stmt();

			if (!(s instanceof DVariableDeclarationStmt))
				continue;//shouldnt happen since this node only contains declarations

			DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt) s;

			//get the locals of this particular type
			List declarations = varStmt.getDeclarations();
			Iterator decIt = declarations.iterator();
			while (decIt.hasNext()) {
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
	public void removeDeclaredLocal(Local local) {
		Stmt s = null;
		for (AugmentedStmt as : declarations.getStatements()) {//going through each stmt
			s = as.get_Stmt();

			if (!(s instanceof DVariableDeclarationStmt))
				continue;//shouldnt happen since this node only contains declarations

			DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt) s;

			//get the locals declared in this stmt
			List declarations = varStmt.getDeclarations();
			Iterator decIt = declarations.iterator();

			boolean foundIt = false;//becomes true if the local was found in this stmt
			while (decIt.hasNext()) {
				//going through each local declared
				Local temp = (Local) decIt.next();
				if (temp.getName().compareTo(local.getName()) == 0) {
					//found it
					foundIt = true;
					break;
				}
			}

			if (foundIt) {
				varStmt.removeLocal(local);
				break; //breaks going through other stmts as we already did what we needed to do
			}
		}
		//the removal of a local might have made some declaration empty
		//remove such a declaraion

		List<AugmentedStmt> newSequence = new ArrayList<AugmentedStmt>();
		for (AugmentedStmt as : declarations.getStatements()) {
			s = as.get_Stmt();

			if (!(s instanceof DVariableDeclarationStmt))
				continue;

			DVariableDeclarationStmt varStmt = (DVariableDeclarationStmt) s;

			if (varStmt.getDeclarations().size() != 0)
				newSequence.add(as);

		}
		declarations.setStatements(newSequence);
	}

	/*
	 Nomair A Naeem 21-FEB-2005
	 Used by UselessLabeledBlockRemove to update a body
	 */
	public void replaceBody(List<Object> body) {
		this.body = body;
		subBodies = new ArrayList<Object>();
		subBodies.add(body);
	}

	public Object clone() {
		ASTMethodNode toReturn = new ASTMethodNode(body);
		toReturn.setDeclarations((ASTStatementSequenceNode) declarations.clone());
		toReturn.setDontPrintLocals(dontPrintLocals);
		return toReturn;
	}

	public void setDontPrintLocals(List<Local> list){
		dontPrintLocals=list;
	}
	
	public void addToDontPrintLocalsList(Local toAdd){
		dontPrintLocals.add(toAdd);
	}
	
	public void perform_Analysis(ASTAnalysis a) {
		perform_AnalysisOnSubBodies(a);
	}

	public void toString(UnitPrinter up) {
		if (!(up instanceof DavaUnitPrinter))
			throw new RuntimeException(
					"Only DavaUnitPrinter should be used to print DavaBody");

		DavaUnitPrinter dup = (DavaUnitPrinter) up;
		/*
		 Print out constructor first
		 */
		if (davaBody != null) {
			InstanceInvokeExpr constructorExpr = davaBody.get_ConstructorExpr();

			if (constructorExpr != null) {
				boolean printCloseBrace=true;
				if (davaBody.getMethod().getDeclaringClass().getName().equals(
						constructorExpr.getMethodRef().declaringClass().toString()))
					dup.printString("        this(");
				else{
					//only invoke super if its not the default call since the default is 
					//called automatically
					if(constructorExpr.getArgCount()>0)
						dup.printString("        super(");
					else
						printCloseBrace=false;

				}
				Iterator ait = constructorExpr.getArgs().iterator();
				while (ait.hasNext()) {
					/*
					 * January 12th, 2006
					 * found a problem here. If a super has a method
					 * call as one of the args then the toString prints the
					 * jimple representation and does not convert it into java
					 * syntax
					 */
					Object arg = ait.next();
					if (arg instanceof Value) {
						//dup.printString(((Value)arg).toString());
						//already in super no indentation required
						dup.noIndent();
						((Value) arg).toString(dup);
					} else {
						/**
						 * Staying with the old style
						 */
						dup.printString(arg.toString());
					}

					if (ait.hasNext())
						dup.printString(", ");
				}

				if(printCloseBrace)
					dup.printString(");\n");
			}

			// print out the remaining body
			up.newline();
		}//if //davaBody != null

		//notice that for an ASTMethod Node the first element of the body list is the
		//declared variables print it here so that we can control what gets printed
		printDeclarationsFollowedByBody(up,body);
	}

	/*
	 * This method has been written to bring into the printing of the method body the printing of the
	 * declared locals
	 * 
	 * This is required because the dontPrintLocals list contains a list of locals which are declared from within
	 * the body and hence we dont want to print them here at the top of the method. However at the same time we dont
	 * want to remove the local entry in the declarations node since this is used by analyses throughout as a quick and
	 * easy way to find out which locals are used by this method...... bad code design but hey what can i say :(
	 */
	public void printDeclarationsFollowedByBody(UnitPrinter up, List<Object> body){
		//System.out.println("printing body from within MEthodNode\n\n"+body.toString());
		for (AugmentedStmt as : declarations.getStatements()) {
			//System.out.println("Stmt is:"+as.get_Stmt());
			Unit u = as.get_Stmt();
			
			//stupid sanity check cos i am paranoid
			if(u instanceof DVariableDeclarationStmt){
				DVariableDeclarationStmt declStmt = (DVariableDeclarationStmt)u;
				List localDeclarations = declStmt.getDeclarations();
				/*
				 * Check that of the localDeclarations List atleast one is not present in the dontPrintLocals list
				 */
				boolean shouldContinue=false;
				Iterator declsIt = localDeclarations.iterator();
				while(declsIt.hasNext()){
					if(!dontPrintLocals.contains(declsIt.next())){
						shouldContinue=true;
						break;
					}
				}
				if(!shouldContinue){
					//shouldnt print this declaration stmt
					continue;
				}
				if (localDeclarations.size() == 0)
					continue;

				if (!(up instanceof DavaUnitPrinter))
					throw new RuntimeException("DavaBody should always be printed using the DavaUnitPrinter");
	
				DavaUnitPrinter dup = (DavaUnitPrinter) up;	
				dup.startUnit(u);
				String type = declStmt.getType().toString();

				if (type.equals("null_type"))
					dup.printString("Object");
				else{
					IterableSet importSet = davaBody.getImportList();
					if(!importSet.contains(type))
						davaBody.addToImportList(type);
							
					type = RemoveFullyQualifiedName.getReducedName(davaBody.getImportList(),type,declStmt.getType());
					
					dup.printString(type);
				}
				dup.printString(" ");

				int number=0;
				Iterator decIt = localDeclarations.iterator();
				while (decIt.hasNext()) {
					Local tempDec = (Local) decIt.next();
					if(dontPrintLocals.contains(tempDec))
						continue;

					if(number!=0)
						dup.printString(", ");
					number++;
					dup.printString(tempDec.getName());
				}
				
                up.literal(";");
                up.endUnit( u );
                up.newline();
			} //if DVariableDeclarationStmt
			else{
				up.startUnit( u );
				u.toString( up );
				up.literal(";");
				up.endUnit( u );
				up.newline();
			}
        }

		boolean printed = false;
		if(body.size()>0){
			ASTNode firstNode = (ASTNode)body.get(0);
			if(firstNode instanceof ASTStatementSequenceNode){
				List<AugmentedStmt> tempstmts = ((ASTStatementSequenceNode)firstNode).getStatements();
				if(tempstmts.size()!=0){
					AugmentedStmt tempas = tempstmts.get(0);
					Stmt temps = tempas.get_Stmt();
					if(temps instanceof DVariableDeclarationStmt){
						printed=true;
						body_toString(up, body.subList(1,body.size()));
					}
				}
			}
		}
		if(!printed){
			//System.out.println("Here for method"+this.getDavaBody().getMethod().toString());
			body_toString(up, body);
		}		
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		/*
		 Print out constructor first
		 */
		if (davaBody != null) {
			InstanceInvokeExpr constructorExpr = davaBody.get_ConstructorExpr();
			if (constructorExpr != null) {

				if (davaBody.getMethod().getDeclaringClass().getName().equals(
						constructorExpr.getMethodRef().declaringClass()
								.toString()))
					b.append("        this(");
				else
					b.append("        super(");

				boolean isFirst = true;
				for (Value val : constructorExpr.getArgs()) {
					if (!isFirst)
						b.append(", ");
					b.append(val.toString());
					isFirst = false;
				}

				b.append(");\n\n");
			}
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
	public void apply(Analysis a) {
		a.caseASTMethodNode(this);
	}
}
