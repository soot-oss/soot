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
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.io.*;

/**
 * This class resolves the type of local variables.
 **/
public class TypeResolverBV
{
  /** Reference to the class hierarchy **/
  private ClassHierarchy hierarchy;

  /** All type variable instances **/
  private final List typeVariableList = new ArrayList();
  private BitVector invalidIds = new BitVector();

  /** Hashtable: [TypeNode or Local] -> TypeVariableBV **/
  private final Map typeVariableMap = new HashMap();

  private final JimpleBody stmtBody;

  final TypeNode NULL;
  private final TypeNode OBJECT;

  private static final boolean DEBUG = false;

  // categories for type variables (solved = hard, unsolved = soft)
  private BitVector unsolved;
  private BitVector solved;

  // parent categories for unsolved type variables
  private BitVector single_soft_parent;
  private BitVector single_hard_parent;
  private BitVector multiple_parents;

  // child categories for unsolved type variables
  private BitVector single_child_not_null;
  private BitVector single_null_child;
  private BitVector multiple_children;

  public ClassHierarchy hierarchy()
  {
    return hierarchy;
  }

  public TypeNode typeNode(Type type)
  {
    return hierarchy.typeNode(type);
  }

  /** Get type variable for the given local. **/
  TypeVariableBV typeVariable(Local local)
  {
    TypeVariableBV result = (TypeVariableBV) typeVariableMap.get(local);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariableBV(id, this);

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
  public TypeVariableBV typeVariable(TypeNode typeNode)
  {
    TypeVariableBV result = (TypeVariableBV) typeVariableMap.get(typeNode);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariableBV(id, this, typeNode);

	typeVariableList.set(id, result);
	typeVariableMap.put(typeNode, result);
      }

    return result;
  }

  /** Get type variable for the given soot class. **/
  public TypeVariableBV typeVariable(SootClass sootClass)
  {
    return typeVariable(hierarchy.typeNode(sootClass.getType()));
  }

  /** Get type variable for the given type. **/
  public TypeVariableBV typeVariable(Type type)
  {
    return typeVariable(hierarchy.typeNode(type));
  }

  /** Get new type variable **/
  public TypeVariableBV typeVariable()
  {
    int id = typeVariableList.size();
    typeVariableList.add(null);
    
    TypeVariableBV result = new TypeVariableBV(id, this);
    
    typeVariableList.set(id, result);
    
    return result;
  }

  private TypeResolverBV(JimpleBody stmtBody, Scene scene)
  {
    this.stmtBody = stmtBody;
    hierarchy = ClassHierarchy.classHierarchy(scene);

    OBJECT = hierarchy.OBJECT;
    NULL = hierarchy.NULL;
    typeVariable(OBJECT);
    typeVariable(NULL);
    
    // hack for J2ME library, reported by Stephen Cheng 
    if (!G.v().isJ2ME) {
      typeVariable(hierarchy.CLONEABLE);
      typeVariable(hierarchy.SERIALIZABLE);
    }
  }

  public static void resolve(JimpleBody stmtBody, Scene scene)
  {
    if(DEBUG)
      {
	G.v().out.println(stmtBody.getMethod());
      }

    try
    {
      TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
      resolver.resolve_step_1();
    }
  catch(TypeException e1)
    {
      if(DEBUG)
        {
          e1.printStackTrace();
          G.v().out.println("Step 1 Exception-->" + e1.getMessage());
        }
    
      try
        {
          TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
          resolver.resolve_step_2();
        }
      catch(TypeException e2)
        {
          if(DEBUG)
            {
          e2.printStackTrace();
          G.v().out.println("Step 2 Exception-->" + e2.getMessage());
            }
          
          try
            {
              TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
              resolver.resolve_step_3();
            }
          catch(TypeException e3)
            {
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
	for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
	    final TypeVariableBV var = (TypeVariableBV) varIt.next();
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
    ConstraintCollectorBV collector = new ConstraintCollectorBV(this, true);

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
    ConstraintCollectorBV collector = new ConstraintCollectorBV(this, false);

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

    TypeVariableBV[] vars = new TypeVariableBV[typeVariableList.size()];
    vars = (TypeVariableBV[]) typeVariableList.toArray(vars);

    for(int i = 0; i < vars.length; i++)
      {
	vars[i].fixDepth();
      }
  }

  private void propagate_array_constraints()
  {
    // find max depth
    int max = 0;
    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
        final TypeVariableBV var = (TypeVariableBV) varIt.next();
	int depth = var.depth();

	if(depth > max)
	  {
	    max = depth;
	  }
      }

    if(max > 1) {
      // hack for J2ME library, reported by Stephen Cheng 
      if (!G.v().isJ2ME) {
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

    // initialize lists
    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
        final TypeVariableBV var = (TypeVariableBV) varIt.next();
	int depth = var.depth();
	
	lists[depth].add(var);
      }

    // propagate constraints, starting with highest depth
    for(int i = max; i >= 0; i--)
      {
	for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
	    final TypeVariableBV var = (TypeVariableBV) varIt.next();

	    var.propagate();
	  }
      }
  }

  private void merge_primitive_types() throws TypeException
  {
    // merge primitive types with all parents/children
    compute_solved();

    BitSetIterator varIt = solved.iterator();
    while( varIt.hasNext() ) {
        TypeVariableBV var = typeVariableForId(varIt.next());

	if(var.type().type() instanceof IntType ||
	   var.type().type() instanceof LongType ||
	   var.type().type() instanceof FloatType ||
	   var.type().type() instanceof DoubleType)
	  {
	    BitVector parents;
	    BitVector children;
	    boolean finished;

	    do
	      {
		finished = true;

		parents = var.parents();
		if(parents.length() != 0)
		  {
		    finished = false;
		    for(BitSetIterator j = parents.iterator(); j.hasNext(); )
		      {
			if(DEBUG)
			  {
			    G.v().out.print(".");
			  }
	
			TypeVariableBV parent = typeVariableForId(j.next());

			var = var.union(parent);
		      }
		  }
		
		children = var.children();
		if(children.length() != 0)
		  {
		    finished = false;
		    for(BitSetIterator j = children.iterator(); j.hasNext(); )
		      {
			if(DEBUG)
			  {
			    G.v().out.print(".");
			  }
	
			TypeVariableBV child = typeVariableForId(j.next());

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
    BitVector list = new BitVector();
    list.or(solved);
    list.or(unsolved);
    
    new StronglyConnectedComponentsBV(list, this);
  }
  
  private void remove_transitive_constraints() throws TypeException
  {
    refresh_solved();
    BitVector list = new BitVector();
    list.or(solved);
    list.or(unsolved);

    for( BitSetIterator varIt = list.iterator(); varIt.hasNext(); ) {

        final TypeVariableBV var = typeVariableForId(varIt.next());
	
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
	
	if(single_child_not_null.length() != 0)
	  {
	    finished = false;
	    modified = true;
	    
            BitSetIterator i = single_child_not_null.iterator();
            while( i.hasNext() ) 
              {
                TypeVariableBV var = typeVariableForId(i.next());

                if(single_child_not_null.get(var.id()))
		  {
		    // PA: Potential difference to old algorithm - using the smallest element
                    // in the list rather than children().get(0);
                    TypeVariableBV child = typeVariableForId(var.children().iterator().next());
		
                    var = var.union(child);
		  }
              }
	  }

	if(finished)
	  {
	    if(single_soft_parent.length() != 0)
	      {
		finished = false;
		modified = true;
		
                BitSetIterator i = single_soft_parent.iterator();
                while( i.hasNext() ) 
                  {
                    TypeVariableBV var = typeVariableForId(i.next());
		    
		    if(single_soft_parent.get(var.id()))
		      {
		        // PA: See above.
		        TypeVariableBV parent = typeVariableForId(var.parents().iterator().next());
			
		        var = var.union(parent);
		      }
                  }
	      }
	    
	    if(single_hard_parent.length() != 0)
	      {
		finished = false;
		modified = true;
		
                BitSetIterator i = single_hard_parent.iterator();
                while( i.hasNext() ) 
                  {
                    TypeVariableBV var = typeVariableForId(i.next());
		    
		    if(single_hard_parent.get(var.id()))
		      {
		        // PA: See above
			TypeVariableBV parent = typeVariableForId(var.parents().iterator().next());
			
			debug_vars("union single parent\n " + var + "\n " + parent);
			var = var.union(parent);
		      }
		  }
	      }

	    if(single_null_child.length() != 0)
	      {
		finished = false;
		modified = true;
		
                BitSetIterator i = single_null_child.iterator();
                while( i.hasNext() ) 
                  {
                    TypeVariableBV var = typeVariableForId(i.next());
		    
		    if(single_null_child.get(var.id()))
		      {
		        // PA: See above
			TypeVariableBV child = typeVariableForId(var.children().iterator().next());
			
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
	for( BitSetIterator varIt = multiple_children.iterator(); varIt.hasNext(); ) 
          {
	    final TypeVariableBV var = typeVariableForId(varIt.next());
	    TypeNode lca = null;
	    BitVector children_to_remove = new BitVector();
	    
	    for( BitSetIterator childIt = var.children().iterator(); childIt.hasNext(); ) 
              {
	    
	        final TypeVariableBV child = typeVariableForId(childIt.next());
		TypeNode type = child.type();

		if(type != null && type.isNull())
		  {
		    var.removeChild(child);
		  }
		else if(type != null && type.isClass())
		  {
		    children_to_remove.set(child.id());
		    
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
		for( BitSetIterator childIt = children_to_remove.iterator(); childIt.hasNext(); ) {
		    final TypeVariableBV child = typeVariableForId(childIt.next());
		    var.removeChild(child);
		  }

		var.addChild(typeVariable(lca));
	      }
	  }
	
	for( BitSetIterator varIt = multiple_parents.iterator(); varIt.hasNext(); ) {
	
	    final TypeVariableBV var = typeVariableForId(varIt.next());
	    LinkedList hp = new LinkedList(); // hard parents
	    
	    for( BitSetIterator parentIt = var.parents().iterator(); parentIt.hasNext(); ) {
	    
	        final TypeVariableBV parent = typeVariableForId(parentIt.next());
		TypeNode type = parent.type();
		
		if(type != null)
		  {
                    Iterator k = hp.iterator();
                    while( k.hasNext() ) {
                        TypeVariableBV otherparent = (TypeVariableBV) k.next();
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
	TypeVariableBV var = typeVariable(local);
	
	if(var == null)
	  {
	    local.setType(RefType.v("java.lang.Object"));
	  }
	else if (var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		TypeVariableBV.error("Type Error(5):  Variable without type");
	      }
	    else
	      {
		local.setType(var.type().type());
	      }
	  }
	else
	  {
	    TypeVariableBV element = var.element();
	    
	    for(int j = 1; j < var.depth(); j++)
	      {
		element = element.element();
	      }

	    if(element.type() == null)
	      {
		TypeVariableBV.error("Type Error(6):  Array variable without base type");
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
	TypeVariableBV var = typeVariable(local);
	
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
    ConstraintCheckerBV checker = new ConstraintCheckerBV(this, false);
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
    ConstraintCheckerBV checker = new ConstraintCheckerBV(this, true);
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

        final TypeVariableBV var = (TypeVariableBV) varIt.next();

	if(var.type() != null)
	  {
	    workList.add(var);
	  }
      }

    TypeVariableBV.computeApprox(workList);

    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {

        final TypeVariableBV var = (TypeVariableBV) varIt.next();

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
    unsolved = new BitVector();
    solved = new BitVector();
    
    for( Iterator varIt = typeVariableList.iterator(); varIt.hasNext(); ) {
    
        final TypeVariableBV var = (TypeVariableBV) varIt.next();
	
	if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved.set(var.id());
	      }
	    else
	      {
		solved.set(var.id());
	      }
	  }
      }
  }

  private void refresh_solved() throws TypeException
  {
    unsolved = new BitVector();
    // solved stays the same
    
    for( BitSetIterator varIt = unsolved.iterator(); varIt.hasNext(); ) {
    
        final TypeVariableBV var = typeVariableForId(varIt.next());
	
	if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved.set(var.id());
	      }
	    else
	      {
		solved.set(var.id());
	      }
	  }
      }

    // validate();
  }

  private void categorize() throws TypeException
  {
    refresh_solved();
   
    single_soft_parent = new BitVector();
    single_hard_parent = new BitVector();
    multiple_parents = new BitVector();
    single_child_not_null = new BitVector();
    single_null_child = new BitVector();
    multiple_children = new BitVector();
    
    for(BitSetIterator i = unsolved.iterator(); i .hasNext(); )
      {
	TypeVariableBV var = typeVariableForId(i.next());
	
	// parent category
	{
	  BitVector parents = var.parents();
	  int size = parents.length();
	  
	  if(size == 0)
	    {
	      var.addParent(typeVariable(OBJECT));
	      single_soft_parent.set(var.id());
	    }
	  else if(size == 1)
	    {
	      TypeVariableBV parent = typeVariableForId(parents.iterator().next());
	      
	      if(parent.type() == null)
		{
		  single_soft_parent.set(var.id());
		}
	      else
		{
		  single_hard_parent.set(var.id());
		}
	    }
	  else
	    {
	      multiple_parents.set(var.id());
	    }
	}
	
	// child category
	{
	  BitVector children = var.children();
	  int size = children.size();
	  
	  if(size == 0)
	    {
	      var.addChild(typeVariable(NULL));
	      single_null_child.set(var.id());
	    }
	  else if(size == 1)
	    {
	      TypeVariableBV child = typeVariableForId(children.iterator().next());
	      
	      if(child.type() == NULL)
		{
		  single_null_child.set(var.id());
		}
	      else
		{
		  single_child_not_null.set(var.id());
		}
	    }
	  else
	    {
	      multiple_children.set(var.id());
	    }
	}
      }
  }

  private void validate() throws TypeException
  {
    for( BitSetIterator varIt = solved.iterator(); varIt.hasNext(); ) {
        final TypeVariableBV var = typeVariableForId(varIt.next());
	
	try
	  {
	    var.validate();
	  }
	catch(TypeException e)
	  {
	    debug_vars("Error while validating");
	    throw(e);
	  }
      }
  }

  /*
  private void remove_spurious_locals()
  {
    boolean repeat;

    do
      {
	ExceptionalUnitGraph graph = new ExceptionalUnitGraph(stmtBody);
	SimpleLocalDefs defs = new SimpleLocalDefs(graph);
	SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
	PatchingChain units = stmtBody.getUnits();
	Stmt[] stmts = new Stmt[units.size()];
	HashSet deleted = new HashSet();

	repeat = false;
	units.toArray(stmts);
	
	for(int i = 0; i < stmts.length; i++)
	  {
	    Stmt stmt = stmts[i];
	    
	    if(stmt instanceof AssignStmt)
	      {
		AssignStmt assign1 = (AssignStmt) stmt;
		
		if(assign1.getLeftOp() instanceof Local)
		  {
		    List uselist = uses.getUsesOf(assign1);
		    
		    if(uselist.size() == 1)
		      {
			UnitValueBoxPair pair = (UnitValueBoxPair) uselist.get(0);
			
			List deflist = defs.getDefsOfAt((Local) pair.getValueBox().getValue(), pair.getUnit());
			
			if(deflist.size() == 1)
			  {
			    if(pair.getValueBox().canContainValue(assign1.getRightOp()))
			      {
				// This is definitely a spurious local!

				// Hmm.. use is in a deleted statement.  Must wait till next iteration.
				if(deleted.contains(pair.getUnit()))
				  {
				    repeat = true;
				    continue;
				  }
				
				pair.getValueBox().setValue(assign1.getRightOp());
				deleted.add(assign1);
				units.remove(assign1);
				stmtBody.getLocals().remove(assign1.getLeftOp());
				
			      }
			  }
		      }
		  }
	      }
	  }
      }
    while(repeat);
  }
  */

  private void split_new()
  {
    ExceptionalUnitGraph graph = new ExceptionalUnitGraph(stmtBody);
    LocalDefs defs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
    PatchingChain units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];

    units.toArray(stmts);
    
    for(int i = 0; i < stmts.length; i++)
      {
	Stmt stmt = stmts[i];

	if(stmt instanceof InvokeStmt)
	  {
	    InvokeStmt invoke = (InvokeStmt) stmt;
	    
	    if(invoke.getInvokeExpr() instanceof SpecialInvokeExpr)
	      {
		SpecialInvokeExpr special = (SpecialInvokeExpr) invoke.getInvokeExpr();
		
		if(special.getMethodRef().name().equals("<init>"))
		  {
		    List deflist = defs.getDefsOfAt((Local) special.getBase(), invoke);
		    
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
  
  public TypeVariableBV typeVariableForId(int idx) {
      return (TypeVariableBV)typeVariableList.get(idx);
  }
  
  public BitVector invalidIds() {
      return invalidIds;
  }
  
  public void invalidateId(int id) {
      invalidIds.set(id);
  }
}
