/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.typing.integer;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.io.*;

/**
 * This class resolves the type of local variables.
 **/
public class TypeResolver
{
  /** All type variable instances **/
  private final List typeVariableList = new ArrayList();

  /** Hashtable: [TypeNode or Local] -> TypeVariable **/
  private final Map typeVariableMap = new HashMap();

  private final JimpleBody stmtBody;

  final TypeVariable BOOLEAN = typeVariable(ClassHierarchy.v().BOOLEAN);
  final TypeVariable BYTE = typeVariable(ClassHierarchy.v().BYTE);
  final TypeVariable SHORT = typeVariable(ClassHierarchy.v().SHORT);
  final TypeVariable CHAR = typeVariable(ClassHierarchy.v().CHAR);
  final TypeVariable INT = typeVariable(ClassHierarchy.v().INT);
  final TypeVariable TOP = typeVariable(ClassHierarchy.v().TOP);
  final TypeVariable R0_1 = typeVariable(ClassHierarchy.v().R0_1);
  final TypeVariable R0_127 = typeVariable(ClassHierarchy.v().R0_127);
  final TypeVariable R0_32767 = typeVariable(ClassHierarchy.v().R0_32767);

  private static final boolean DEBUG = false;

  // categories for type variables (solved = hard, unsolved = soft)
  private List unsolved;
  private List solved;

  /** Get type variable for the given local. **/
  TypeVariable typeVariable(Local local)
  {
    TypeVariable result = (TypeVariable) typeVariableMap.get(local);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariable(id, this);

	typeVariableList.set(id, result);
	typeVariableMap.put(local, result);
	
	if(DEBUG)
	  {
	    G.v().out.println("[LOCAL VARIABLE \"" + local + "\" -> " + id + "]");
	  }
      }
    
    return result;
  }

  /** Get type variable for the given type node. **/
  public TypeVariable typeVariable(TypeNode typeNode)
  {
    TypeVariable result = (TypeVariable) typeVariableMap.get(typeNode);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariable(id, this, typeNode);

	typeVariableList.set(id, result);
	typeVariableMap.put(typeNode, result);
      }

    return result;
  }

  /** Get type variable for the given type. **/
  public TypeVariable typeVariable(Type type)
  {
    return typeVariable(ClassHierarchy.v().typeNode(type));
  }

  /** Get new type variable **/
  public TypeVariable typeVariable()
  {
    int id = typeVariableList.size();
    typeVariableList.add(null);
    
    TypeVariable result = new TypeVariable(id, this);
    
    typeVariableList.set(id, result);
    
    return result;
  }

  private TypeResolver(JimpleBody stmtBody)
  {
    this.stmtBody = stmtBody;
  }

  public static void resolve(JimpleBody stmtBody)
  {
    if(DEBUG)
      {
	G.v().out.println(stmtBody.getMethod());
      }

    try
      {
	TypeResolver resolver = new TypeResolver(stmtBody);
	resolver.resolve_step_1();
      }
    catch(TypeException e1)
      {
	if(DEBUG)
	  {
	    G.v().out.println("[integer] Step 1 Exception-->" + e1.getMessage());
	  }
	
	try
	  {
	    TypeResolver resolver = new TypeResolver(stmtBody);
	    resolver.resolve_step_2();
	  }
	catch(TypeException e2)
	  {
              StringWriter st = new StringWriter();
	      PrintWriter pw = new PrintWriter(st);
	      e2.printStackTrace(pw);
	      pw.close();
              throw new RuntimeException(st.toString());
	  }
      }
  }
  
  private void debug_vars(String message)
  {
    if(DEBUG)
      {
	int count = 0;
	G.v().out.println("**** START:" + message);
	for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
	    final TypeVariable var = (TypeVariable) varIt.next();
	    G.v().out.println(count++ + " " + var);
	  }
	G.v().out.println("**** END:" + message);
      }
  }

  private void debug_body()
  {
    if(DEBUG)
      {
	G.v().out.println("-- Body Start --");
	for( Iterator stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    G.v().out.println(stmt);
	  }
	G.v().out.println("-- Body End --");
      }
  }

  private void resolve_step_1() throws TypeException
  {
    collect_constraints_1();
    debug_vars("constraints");

    compute_approximate_types();
    merge_connected_components();
    debug_vars("components");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1();
    debug_vars("assign");

    try
      {
	check_constraints();
      }
    catch(TypeException e)
      {
	if(DEBUG)
	  {
	    G.v().out.println("[integer] Step 1(check) Exception [" + stmtBody.getMethod() + "]-->" + e.getMessage());
	  }
	
	check_and_fix_constraints();
      }
  }

  private void resolve_step_2() throws TypeException
  {
    collect_constraints_2();
    compute_approximate_types();
    assign_types_2();
    check_and_fix_constraints();
  }

  private void collect_constraints_1()
  {
    ConstraintCollector collector = new ConstraintCollector(this, true);

    for( Iterator stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext(); ) {

        final Stmt stmt = (Stmt) stmtIt.next();
	if(DEBUG)
	  {
	    G.v().out.print("stmt: ");
	  }
	collector.collect(stmt, stmtBody);
	if(DEBUG)
	  {
	    G.v().out.println(stmt);
	  }
      }
  }

  private void collect_constraints_2()
  {
    ConstraintCollector collector = new ConstraintCollector(this, false);

    for( Iterator stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext(); ) {

        final Stmt stmt = (Stmt) stmtIt.next();
	if(DEBUG)
	  {
	    G.v().out.print("stmt: ");
	  }
	collector.collect(stmt, stmtBody);
	if(DEBUG)
	  {
	    G.v().out.println(stmt);
	  }
      }
  }

  private void merge_connected_components() throws TypeException
  {
    compute_solved();
    List list = new LinkedList();
    list.addAll(solved);
    list.addAll(unsolved);
    
    StronglyConnectedComponents.merge(list);
  }
  
  private void merge_single_constraints() throws TypeException
  {
    boolean modified = true;
    
    while(modified)
      {
	modified = false;
	refresh_solved();
	
	for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	
	    final TypeVariable var = (TypeVariable) varIt.next();
	    List children_to_remove = new LinkedList();
	    TypeNode lca = null;
	    
	    var.fixChildren();
	    
	    for( Iterator childIt = var.children().iterator(); childIt.hasNext(); ) {
	    
	        final TypeVariable child = (TypeVariable) childIt.next();
		TypeNode type = child.type();
		
		if(type != null)
		  {
		    children_to_remove.add(child);
		    
		    if(lca == null)
		      {
			lca = type;
		      }
		    else
		      {
			lca = lca.lca_1(type);
		      }
		  }
	      }
	    
	    if(lca != null)
	      {
		if(DEBUG)
		  {
		    if(lca == ClassHierarchy.v().TOP)
		      {
			G.v().out.println("*** TOP *** " + var);
			for(Iterator j = children_to_remove.iterator(); j.hasNext();)
			  {
			    G.v().out.println("-- " + j.next());
			  }
		      }
		  }
		
		for( Iterator childIt = children_to_remove.iterator(); childIt.hasNext(); ) {
		
		    final TypeVariable child = (TypeVariable) childIt.next();
		    var.removeChild(child);
		  }
		
		var.addChild(typeVariable(lca));
	      }

	    if(var.children().size() == 1)
	      {
		TypeVariable child = (TypeVariable) var.children().get(0);
		TypeNode type = child.type();
		
		if(type == null || type.type() != null)
		  {
		    var.union(child);
		    modified = true;
		  }
	      }
	  }
      
	if(!modified)
	  {
	    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		List parents_to_remove = new LinkedList();
		TypeNode gcd = null;
		
		var.fixParents();
		
		for( Iterator parentIt = var.parents().iterator(); parentIt.hasNext(); ) {
		
		    final TypeVariable parent = (TypeVariable) parentIt.next();
		    TypeNode type = parent.type();
		    
		    if(type != null)
		      {
			parents_to_remove.add(parent);
			
			if(gcd == null)
			  {
			    gcd = type;
			  }
			else
			  {
			    gcd = gcd.gcd_1(type);
			  }
		      }
		  }
		
		if(gcd != null)
		  {
		    for( Iterator parentIt = parents_to_remove.iterator(); parentIt.hasNext(); ) {
		        final TypeVariable parent = (TypeVariable) parentIt.next();
			var.removeParent(parent);
		      }
		    
		    var.addParent(typeVariable(gcd));
		  }
		
		if(var.parents().size() == 1)
		  {
		    TypeVariable parent = (TypeVariable) var.parents().get(0);
		    TypeNode type = parent.type();
		    
		    if(type == null || type.type() != null)
		      {
			var.union(parent);
			modified = true;
		      }
		  }
	      }
	  }

	if(!modified)
	  {
	    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		
		if(var.type() == null && var.inv_approx() != null && var.inv_approx().type() != null)
		  {
		    if(DEBUG)
		      {
			G.v().out.println("*** I->" + var.inv_approx().type() + " *** " + var);
		      }
		    
		    var.union(typeVariable(var.inv_approx()));
		    modified = true;
		  }
	      }
	  }

	if(!modified)
	  {
	    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		
		if(var.type() == null && var.approx() != null && var.approx().type() != null)
		  {
		    if(DEBUG)
		      {
			G.v().out.println("*** A->" + var.approx().type() + " *** " + var);
		      }
		    
		    var.union(typeVariable(var.approx()));
		    modified = true;
		  }
	      }
	  }

	if(!modified)
	  {
	    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		
		if(var.type() == null && var.approx() == ClassHierarchy.v().R0_32767)
		  {
		    if(DEBUG)
		      {
			G.v().out.println("*** R->SHORT *** " + var);
		      }
		    
		    var.union(SHORT);
		    modified = true;
		  }
	      }
	  }

	if(!modified)
	  {
	    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		
		if(var.type() == null && var.approx() == ClassHierarchy.v().R0_127)
		  {
		    if(DEBUG)
		      {
			G.v().out.println("*** R->BYTE *** " + var);
		      }
		    
		    var.union(BYTE);
		    modified = true;
		  }
	      }
	  }

	if(!modified)
	  {
	    for( Iterator varIt = R0_1.parents().iterator(); varIt.hasNext(); ) {
	        final TypeVariable var = (TypeVariable) varIt.next();
		
		if(var.type() == null && var.approx() == ClassHierarchy.v().R0_1)
		  {
		    if(DEBUG)
		      {
			G.v().out.println("*** R->BOOLEAN *** " + var);
		      }
		    var.union(BOOLEAN);
		    modified = true;
		  }
	      }
	  }
      }
  }

  private void assign_types_1() throws TypeException
  {
    for( Iterator localIt = stmtBody.getLocals().iterator(); localIt.hasNext(); ) {
        final Local local = (Local) localIt.next();

	if(local.getType() instanceof IntegerType)
	  {
	    TypeVariable var = typeVariable(local);
	    
	    if(var.type() == null || var.type().type() == null)
	      {
		TypeVariable.error("Type Error(21):  Variable without type");
	      }
	    else
	      {
		local.setType(var.type().type());
	      }
	    
	    if(DEBUG)
	      {
		if((var != null) &&
		   (var.approx() != null) &&
		   (var.approx().type() != null) &&
		   (local != null) &&
		   (local.getType() != null) &&
		   !local.getType().equals(var.approx().type()))
		  {
		    G.v().out.println("local: " + local + ", type: " + local.getType() + ", approx: " + var.approx().type());
		  }
	      }
	  }
      }
  }
  
  private void assign_types_2() throws TypeException
  {
    for( Iterator localIt = stmtBody.getLocals().iterator(); localIt.hasNext(); ) {
        final Local local = (Local) localIt.next();

	if(local.getType() instanceof IntegerType)
	  {
	    TypeVariable var = typeVariable(local);
	    
	    if(var.inv_approx() != null && var.inv_approx().type() != null)
	      {
		local.setType(var.inv_approx().type());
	      }
	    else if(var.approx().type() != null)
	      {
		local.setType(var.approx().type());
	      }
	    else if(var.approx() == ClassHierarchy.v().R0_1)
	      {
		local.setType(BooleanType.v());
	      }
	    else if(var.approx() == ClassHierarchy.v().R0_127)
	      {
		local.setType(ByteType.v());
	      }
	    else
	      {
		local.setType(ShortType.v());
	      }
	  }
      }
  }

  private void check_constraints() throws TypeException
  {
    ConstraintChecker checker = new ConstraintChecker(this, false);
    StringBuffer s = null;

    if(DEBUG)
      {
	s = new StringBuffer("Checking:\n");
      }

    for( Iterator stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext(); ) {

        final Stmt stmt = (Stmt) stmtIt.next();
	if(DEBUG)
	  {
	    s.append(" " + stmt + "\n");
	  }
	try
	  {
	    checker.check(stmt, stmtBody);
	  }
	catch(TypeException e)
	  {
	    if(DEBUG)
	      {
		G.v().out.println(s);
	      }
	    throw e;
	  }
      }
  }

  private void check_and_fix_constraints() throws TypeException
  {
    ConstraintChecker checker = new ConstraintChecker(this, true);
    StringBuffer s = null;
    PatchingChain units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];
    units.toArray(stmts);

    if(DEBUG)
      {
	s = new StringBuffer("Checking:\n");
      }

    for(int i = 0; i < stmts.length; i++)
      {
	Stmt stmt = stmts[i];

	if(DEBUG)
	  {
	    s.append(" " + stmt + "\n");
	  }
	try
	  {
	    checker.check(stmt, stmtBody);
	  }
	catch(TypeException e)
	  {
	    if(DEBUG)
	      {
		G.v().out.println(s);
	      }
	    throw e;
	  }
      }
  }

  private void compute_approximate_types() throws TypeException
  {
    TreeSet workList = new TreeSet();

    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {

        final TypeVariable var = (TypeVariable) varIt.next();

	if(var.type() != null)
	  {
	    workList.add(var);
	  }
      }

    TypeVariable.computeApprox(workList);

    workList = new TreeSet();

    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {

        final TypeVariable var = (TypeVariable) varIt.next();
	
	if(var.type() != null)
	  {
	    workList.add(var);
	  }
      }

    TypeVariable.computeInvApprox(workList);

    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {

        final TypeVariable var = (TypeVariable) varIt.next();

	if (var.approx() == null)
	  {
	    var.union(INT);
	  }
      }
  }
  
  private void compute_solved()
  {
    Set unsolved_set = new TreeSet();
    Set solved_set = new TreeSet();
    
    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
    
        final TypeVariable var = (TypeVariable) varIt.next();
	
	if(var.type() == null)
	  {
	    unsolved_set.add(var);
	  }
	else
	  {
	    solved_set.add(var);
	  }
      }
    
    solved = new LinkedList(solved_set);
    unsolved = new LinkedList(unsolved_set);
  }

  private void refresh_solved() throws TypeException
  {
    Set unsolved_set = new TreeSet();
    Set solved_set = new TreeSet(solved);
    
    for( Iterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
    
        final TypeVariable var = (TypeVariable) varIt.next();
	
	if(var.type() == null)
	  {
	    unsolved_set.add(var);
	  }
	else
	  {
	    solved_set.add(var);
	  }
      }
    
    solved = new LinkedList(solved_set);
    unsolved = new LinkedList(unsolved_set);
  }
}
