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


package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.options.Options;

import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.io.*;

/**
 * This class resolves the type of local variables.
 * 
 * <b>NOTE:</b> This class has been superseded by {@link soot.jimple.toolkits.typing.fast.TypeResolver}.
 **/
public class TypeResolver
{
  /** Reference to the class hierarchy **/
  private final ClassHierarchy hierarchy;

  /** All type variable instances **/
  private final List<TypeVariable> typeVariableList = new ArrayList<TypeVariable>();

  /** Hashtable: [TypeNode or Local] -> TypeVariable **/
  private final Map<Object, TypeVariable> typeVariableMap = new HashMap<Object, TypeVariable>();

  private final JimpleBody stmtBody;

  final TypeNode NULL;
  private final TypeNode OBJECT;

  private static final boolean DEBUG = false;

  // categories for type variables (solved = hard, unsolved = soft)
  private List<TypeVariable> unsolved;
  private List<TypeVariable> solved;

  // parent categories for unsolved type variables
  private List<TypeVariable> single_soft_parent;
  private List<TypeVariable> single_hard_parent;
  private List<TypeVariable> multiple_parents;

  // child categories for unsolved type variables
  private List<TypeVariable> single_child_not_null;
  private List<TypeVariable> single_null_child;
  private List<TypeVariable> multiple_children;

  public ClassHierarchy hierarchy()
  {
    return hierarchy;
  }

  public TypeNode typeNode(Type type)
  {
    return hierarchy.typeNode(type);
  }

  /** Get type variable for the given local. **/
  TypeVariable typeVariable(Local local)
  {
    TypeVariable result = typeVariableMap.get(local);

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
    TypeVariable result = typeVariableMap.get(typeNode);

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

  /** Get type variable for the given soot class. **/
  public TypeVariable typeVariable(SootClass sootClass)
  {
    return typeVariable(hierarchy.typeNode(sootClass.getType()));
  }

  /** Get type variable for the given type. **/
  public TypeVariable typeVariable(Type type)
  {
    return typeVariable(hierarchy.typeNode(type));
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

  private TypeResolver(JimpleBody stmtBody, Scene scene)
  {
    this.stmtBody = stmtBody;
    hierarchy = ClassHierarchy.classHierarchy(scene);

    OBJECT = hierarchy.OBJECT;
    NULL = hierarchy.NULL;
    typeVariable(OBJECT);
    typeVariable(NULL);
    
    // hack for J2ME library, reported by Stephen Cheng 
    if (!Options.v().j2me()) {
      typeVariable(hierarchy.CLONEABLE);
      typeVariable(hierarchy.SERIALIZABLE);
    }
  }

  public static void resolve(JimpleBody stmtBody, Scene scene) {
		if (DEBUG) {
			G.v().out.println(stmtBody.getMethod());
		}

		try {
			TypeResolver resolver = new TypeResolver(stmtBody, scene);
			resolver.resolve_step_1();
		} catch (TypeException e1) {
			if (DEBUG) {
				e1.printStackTrace();
				G.v().out.println("Step 1 Exception-->" + e1.getMessage());
			}

			try {
				TypeResolver resolver = new TypeResolver(stmtBody, scene);
				resolver.resolve_step_2();
			} catch (TypeException e2) {
				if (DEBUG) {
					e2.printStackTrace();
					G.v().out.println("Step 2 Exception-->" + e2.getMessage());
				}

				try {
					TypeResolver resolver = new TypeResolver(stmtBody, scene);
					resolver.resolve_step_3();
				} catch (TypeException e3) {
					StringWriter st = new StringWriter();
					PrintWriter pw = new PrintWriter(st);
					e3.printStackTrace(pw);
					pw.close();
					throw new RuntimeException(st.toString());
				}
			}
		}
		soot.jimple.toolkits.typing.integer.TypeResolver.resolve(stmtBody);
	}
  
  private void debug_vars(String message)
  {
    if(DEBUG)
      {
	int count = 0;
	G.v().out.println("**** START:" + message);
	for (TypeVariable var : typeVariableList) {
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
    //    remove_spurious_locals();

    collect_constraints_1_2();
    debug_vars("constraints");

    compute_array_depth();
    propagate_array_constraints();
    debug_vars("arrays");

    merge_primitive_types();
    debug_vars("primitive");

    merge_connected_components();
    debug_vars("components");

    remove_transitive_constraints();
    debug_vars("transitive");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1_2();
    debug_vars("assign");

    check_constraints();
  }

  private void resolve_step_2() throws TypeException
  {
    debug_body();
    split_new();
    debug_body();

    collect_constraints_1_2();
    debug_vars("constraints");

    compute_array_depth();
    propagate_array_constraints();
    debug_vars("arrays");

    merge_primitive_types();
    debug_vars("primitive");

    merge_connected_components();
    debug_vars("components");

    remove_transitive_constraints();
    debug_vars("transitive");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1_2();
    debug_vars("assign");

    check_constraints();
  }

  private void resolve_step_3() throws TypeException
  {
    collect_constraints_3();
    compute_approximate_types();
    assign_types_3();
    check_and_fix_constraints();
  }

  private void collect_constraints_1_2()
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

  private void collect_constraints_3()
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

  private void compute_array_depth() throws TypeException
  {
    compute_approximate_types();

    TypeVariable[] vars = new TypeVariable[typeVariableList.size()];
    vars = typeVariableList.toArray(vars);

    for (TypeVariable element : vars) {
	element.fixDepth();
      }
  }

  private void propagate_array_constraints()
  {
    // find max depth
    int max = 0;
    for (TypeVariable var : typeVariableList) {
        int depth = var.depth();

	if(depth > max)
	  {
	    max = depth;
	  }
      }

    if(max > 1) {
      // hack for J2ME library, reported by Stephen Cheng 
      if (!Options.v().j2me()) {
	typeVariable(ArrayType.v(RefType.v("java.lang.Cloneable"), max - 1));
	typeVariable(ArrayType.v(RefType.v("java.io.Serializable"), max - 1));
      }
    }

    // create lists for each array depth
    LinkedList[] lists = new LinkedList[max + 1];
    for(int i = 0; i <= max; i++)
      {
	lists[i] = new LinkedList();
      }

    for (TypeVariable var : typeVariableList) {
        int depth = var.depth();
	
	lists[depth].add(var);
      }

    // propagate constraints, starting with highest depth
    for(int i = max; i >= 0; i--)
      {
	for (TypeVariable var : typeVariableList) {
	    var.propagate();
	  }
      }
  }

  private void merge_primitive_types() throws TypeException
  {
    // merge primitive types with all parents/children
    compute_solved();

    Iterator<TypeVariable> varIt = solved.iterator();
    while( varIt.hasNext() ) {
        TypeVariable var = varIt.next();

	if(var.type().type() instanceof IntType ||
	   var.type().type() instanceof LongType ||
	   var.type().type() instanceof FloatType ||
	   var.type().type() instanceof DoubleType)
	  {
	    List<TypeVariable> parents;
	    List<TypeVariable> children;
	    boolean finished;

	    do
	      {
		finished = true;

		parents = var.parents();
		if(parents.size() != 0)
		  {
		    finished = false;
		    for (TypeVariable parent : parents) {
			if(DEBUG)
			  {
			    G.v().out.print(".");
			  }
	
			var = var.union(parent);
		      }
		  }
		
		children = var.children();
		if(children.size() != 0)
		  {
		    finished = false;
		    for (TypeVariable child : children) {
			if(DEBUG)
			  {
			    G.v().out.print(".");
			  }
	
			var = var.union(child);
		      }
		  }
	      }
	    while(!finished);
	  }
      }
  }

  private void merge_connected_components() throws TypeException
  {
    refresh_solved();
    List<TypeVariable> list = new LinkedList<TypeVariable>();
    list.addAll(solved);
    list.addAll(unsolved);
    
    StronglyConnectedComponents.merge(list);
  }
  
  private void remove_transitive_constraints() throws TypeException
  {
    refresh_solved();
    List<TypeVariable> list = new LinkedList<TypeVariable>();
    list.addAll(solved);
    list.addAll(unsolved);

    for (TypeVariable var : list) {

        var.removeIndirectRelations();
      }
  }

  private void merge_single_constraints() throws TypeException
  {
    boolean finished = false;
    boolean modified = false;
    while(true)
      {
	categorize();
	
	if(single_child_not_null.size() != 0)
	  {
	    finished = false;
	    modified = true;
	    
            Iterator<TypeVariable> i = single_child_not_null.iterator();
            while( i.hasNext() ) {
                TypeVariable var = i.next();

		if(single_child_not_null.contains(var))
		  {
		    TypeVariable child = var.children().get(0);
		
		    var = var.union(child);
		  }
	      }
	  }

	if(finished)
	  {
	    if(single_soft_parent.size() != 0)
	      {
		finished = false;
		modified = true;
		
                Iterator<TypeVariable> i = single_soft_parent.iterator();
                while( i.hasNext() ) {
                    TypeVariable var = i.next();
		    
		    if(single_soft_parent.contains(var))
		      {
			TypeVariable parent = var.parents().get(0);
			
			var = var.union(parent);
		      }
		  }
	      }
	    
	    if(single_hard_parent.size() != 0)
	      {
		finished = false;
		modified = true;
		
                Iterator<TypeVariable> i = single_hard_parent.iterator();
                while( i.hasNext() ) {
                    TypeVariable var = i.next();
		    
		    if(single_hard_parent.contains(var))
		      {
			TypeVariable parent = var.parents().get(0);
			
			debug_vars("union single parent\n " + var + "\n " + parent);
			var = var.union(parent);
		      }
		  }
	      }

	    if(single_null_child.size() != 0)
	      {
		finished = false;
		modified = true;
		
                Iterator<TypeVariable> i = single_null_child.iterator();
                while( i.hasNext() ) {
                    TypeVariable var = i.next();
		    
		    if(single_null_child.contains(var))
		      {
			TypeVariable child = var.children().get(0);
			
			var = var.union(child);
		      }
		  }
	      }
	    
	    if(finished)
	      {
		break;
	      }
	    
	    continue;
	  }
	
	if(modified)
	  {
	    modified = false;
	    continue;
	  }

	finished = true;
	
      multiple_children:
	for (TypeVariable var : multiple_children) {
	    TypeNode lca = null;
	    List<TypeVariable> children_to_remove = new LinkedList<TypeVariable>();
	    
	    var.fixChildren();
	    
	    for (TypeVariable child : var.children()) {
	    
	        TypeNode type = child.type();

		if(type != null && type.isNull())
		  {
		    var.removeChild(child);
		  }
		else if(type != null && type.isClass())
		  {
		    children_to_remove.add(child);
		    
		    if(lca == null)
		      {
			lca = type;
		      }
		    else
		      {
			lca = lca.lcaIfUnique(type);

			if(lca == null)
			  {
			    if(DEBUG)
			      {
				G.v().out.println
				  ("==++==" +
				   stmtBody.getMethod().getDeclaringClass().getName() + "." + 
				   stmtBody.getMethod().getName());
			      }
			    
			    continue multiple_children;
			  }
		      }
		  }
	      }
	    
	    if(lca != null)
	      {
		for (TypeVariable child : children_to_remove) {
		    var.removeChild(child);
		  }

		var.addChild(typeVariable(lca));
	      }
	  }
	
	for (TypeVariable var : multiple_parents) {
	
	    LinkedList<TypeVariable> hp = new LinkedList<TypeVariable>(); // hard parents
	    
	    var.fixParents();
	    
	    for (TypeVariable parent : var.parents()) {
	    
	        TypeNode type = parent.type();
		
		if(type != null)
		  {
                    Iterator<TypeVariable> k = hp.iterator();
                    while( k.hasNext() ) {
                        TypeVariable otherparent = k.next();
			TypeNode othertype = otherparent.type();
			
			if(type.hasDescendant(othertype))
			  {
			    var.removeParent(parent);
			    type = null;
			    break;
			  }
			
			if(type.hasAncestor(othertype))
			  {
			    var.removeParent(otherparent);
			    k.remove();
			  }
		      }
		    
		    if(type != null)
		      {
			hp.add(parent);
		      }
		  }
	      }
	  }
      }
  }

  private void assign_types_1_2() throws TypeException
  {
    for( Iterator localIt = stmtBody.getLocals().iterator(); localIt.hasNext(); ) {
        final Local local = (Local) localIt.next();
	TypeVariable var = typeVariable(local);
	
	if(var == null)
	  {
	    local.setType(RefType.v("java.lang.Object"));
	  }
	else if (var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		TypeVariable.error("Type Error(5):  Variable without type");
	      }
	    else
	      {
		local.setType(var.type().type());
	      }
	  }
	else
	  {
	    TypeVariable element = var.element();
	    
	    for(int j = 1; j < var.depth(); j++)
	      {
		element = element.element();
	      }

	    if(element.type() == null)
	      {
		TypeVariable.error("Type Error(6):  Array variable without base type");
	      }
	    else if(element.type().type() instanceof NullType)
	      {
		local.setType(NullType.v());
	      }
	    else
	      {
		Type t = element.type().type();
		if(t instanceof IntType)
		  {
		    local.setType(var.approx().type());
		  }
		else
		  {
		    local.setType(ArrayType.v(t, var.depth()));
		  }
	      }
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

  private void assign_types_3() throws TypeException
  {
    for( Iterator localIt = stmtBody.getLocals().iterator(); localIt.hasNext(); ) {
        final Local local = (Local) localIt.next();
	TypeVariable var = typeVariable(local);
	
	if(var == null ||
	   var.approx() == null ||
	   var.approx().type() == null)
	  {
	    local.setType(RefType.v("java.lang.Object"));
	  }
	else
	  {
	    local.setType(var.approx().type());
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

    for (Stmt stmt : stmts) {
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
    TreeSet<TypeVariable> workList = new TreeSet<TypeVariable>();

    for (TypeVariable var : typeVariableList) {

        if(var.type() != null)
	  {
	    workList.add(var);
	  }
      }

    TypeVariable.computeApprox(workList);

    for (TypeVariable var : typeVariableList) {

        if(var.approx() == NULL)
	  {
	    var.union(typeVariable(NULL));
	  }
	else if (var.approx() == null)
	  {
	    var.union(typeVariable(NULL));
	  }
      }
  }
  
  private void compute_solved()
  {
    Set<TypeVariable> unsolved_set = new TreeSet<TypeVariable>();
    Set<TypeVariable> solved_set = new TreeSet<TypeVariable>();
    
    for (TypeVariable var : typeVariableList) {
    
        if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved_set.add(var);
	      }
	    else
	      {
		solved_set.add(var);
	      }
	  }
      }

    solved = new LinkedList<TypeVariable>(solved_set);
    unsolved = new LinkedList<TypeVariable>(unsolved_set);
  }

  private void refresh_solved() throws TypeException
  {
    Set<TypeVariable> unsolved_set = new TreeSet<TypeVariable>();
    Set<TypeVariable> solved_set = new TreeSet<TypeVariable>(solved);
    
    for (TypeVariable var : unsolved) {
    
        if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved_set.add(var);
	      }
	    else
	      {
		solved_set.add(var);
	      }
	  }
      }

    solved = new LinkedList<TypeVariable>(solved_set);
    unsolved = new LinkedList<TypeVariable>(unsolved_set);
    
    // validate();
  }

  private void categorize() throws TypeException
  {
    refresh_solved();
   
    single_soft_parent = new LinkedList<TypeVariable>();
    single_hard_parent = new LinkedList<TypeVariable>();
    multiple_parents = new LinkedList<TypeVariable>();
    single_child_not_null = new LinkedList<TypeVariable>();
    single_null_child = new LinkedList<TypeVariable>();
    multiple_children = new LinkedList<TypeVariable>();
    
    for (TypeVariable var : unsolved) {
	// parent category
	{
	  List<TypeVariable> parents = var.parents();
	  int size = parents.size();
	  
	  if(size == 0)
	    {
	      var.addParent(typeVariable(OBJECT));
	      single_soft_parent.add(var);
	    }
	  else if(size == 1)
	    {
	      TypeVariable parent = parents.get(0);
	      
	      if(parent.type() == null)
		{
		  single_soft_parent.add(var);
		}
	      else
		{
		  single_hard_parent.add(var);
		}
	    }
	  else
	    {
	      multiple_parents.add(var);
	    }
	}
	
	// child category
	{
	  List<TypeVariable> children = var.children();
	  int size = children.size();
	  
	  if(size == 0)
	    {
	      var.addChild(typeVariable(NULL));
	      single_null_child.add(var);
	    }
	  else if(size == 1)
	    {
	      TypeVariable child = children.get(0);
	      
	      if(child.type() == NULL)
		{
		  single_null_child.add(var);
		}
	      else
		{
		  single_child_not_null.add(var);
		}
	    }
	  else
	    {
	      multiple_children.add(var);
	    }
	}
      }
  }

  private void split_new()
  {
    ExceptionalUnitGraph graph = new ExceptionalUnitGraph(stmtBody);
    SimpleLocalDefs defs = new SimpleLocalDefs(graph);
    // SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
    PatchingChain units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];

    units.toArray(stmts);
    
    for (Stmt stmt : stmts) {
	if(stmt instanceof InvokeStmt)
	  {
	    InvokeStmt invoke = (InvokeStmt) stmt;
	    
	    if(invoke.getInvokeExpr() instanceof SpecialInvokeExpr)
	      {
		SpecialInvokeExpr special = (SpecialInvokeExpr) invoke.getInvokeExpr();
		
		if(special.getMethodRef().name().equals("<init>"))
		  {
		    List<Unit> deflist = defs.getDefsOfAt((Local) special.getBase(), invoke);
		    
		    while(deflist.size() == 1)
		      {
			Stmt stmt2 = (Stmt) deflist.get(0);
			
			if(stmt2 instanceof AssignStmt)
			  {
			    AssignStmt assign = (AssignStmt) stmt2;
			    
			    if(assign.getRightOp() instanceof Local)
			      {
				deflist = defs.getDefsOfAt((Local) assign.getRightOp(), assign);
				continue;
			      }
			    else if(assign.getRightOp() instanceof NewExpr)
			      {			
				// We split the local.
				//G.v().out.println("split: [" + assign + "] and [" + stmt + "]");
				Local newlocal = Jimple.v().newLocal("tmp", null);
				stmtBody.getLocals().add(newlocal);
				
				special.setBase(newlocal);
				
				units.insertAfter(Jimple.v().newAssignStmt(assign.getLeftOp(), newlocal), assign);
				assign.setLeftOp(newlocal);
			      }
			  }
			break;
		      }
		  }
	      }
	  }
      }
  }
}
