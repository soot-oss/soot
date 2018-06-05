package soot.jimple.toolkits.typing;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.  All rights reserved.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.scalar.LocalDefs;
import soot.util.BitSetIterator;
import soot.util.BitVector;

/**
 * This class resolves the type of local variables.
 *
 * @deprecated use {@link soot.jimple.toolkits.typing.fast.TypeResolver} instead
 **/
@Deprecated
public class TypeResolverBV {
  private static final Logger logger = LoggerFactory.getLogger(TypeResolverBV.class);
  /** Reference to the class hierarchy **/
  private final ClassHierarchy hierarchy;

  /** All type variable instances **/
  private final List<TypeVariableBV> typeVariableList = new ArrayList<TypeVariableBV>();
  private final BitVector invalidIds = new BitVector();

  /** Hashtable: [TypeNode or Local] -> TypeVariableBV **/
  private final Map<Object, TypeVariableBV> typeVariableMap = new HashMap<Object, TypeVariableBV>();

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

  public ClassHierarchy hierarchy() {
    return hierarchy;
  }

  public TypeNode typeNode(Type type) {
    return hierarchy.typeNode(type);
  }

  /** Get type variable for the given local. **/
  TypeVariableBV typeVariable(Local local) {
    TypeVariableBV result = typeVariableMap.get(local);

    if (result == null) {
      int id = typeVariableList.size();
      typeVariableList.add(null);

      result = new TypeVariableBV(id, this);

      typeVariableList.set(id, result);
      typeVariableMap.put(local, result);

      if (DEBUG) {
        logger.debug("[LOCAL VARIABLE \"" + local + "\" -> " + id + "]");
      }
    }

    return result;
  }

  /** Get type variable for the given type node. **/
  public TypeVariableBV typeVariable(TypeNode typeNode) {
    TypeVariableBV result = typeVariableMap.get(typeNode);

    if (result == null) {
      int id = typeVariableList.size();
      typeVariableList.add(null);

      result = new TypeVariableBV(id, this, typeNode);

      typeVariableList.set(id, result);
      typeVariableMap.put(typeNode, result);
    }

    return result;
  }

  /** Get type variable for the given soot class. **/
  public TypeVariableBV typeVariable(SootClass sootClass) {
    return typeVariable(hierarchy.typeNode(sootClass.getType()));
  }

  /** Get type variable for the given type. **/
  public TypeVariableBV typeVariable(Type type) {
    return typeVariable(hierarchy.typeNode(type));
  }

  /** Get new type variable **/
  public TypeVariableBV typeVariable() {
    int id = typeVariableList.size();
    typeVariableList.add(null);

    TypeVariableBV result = new TypeVariableBV(id, this);

    typeVariableList.set(id, result);

    return result;
  }

  private TypeResolverBV(JimpleBody stmtBody, Scene scene) {
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
      logger.debug("" + stmtBody.getMethod());
    }

    try {
      TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
      resolver.resolve_step_1();
    } catch (TypeException e1) {
      if (DEBUG) {
        logger.error(e1.getMessage(), e1);
        logger.debug("Step 1 Exception-->" + e1.getMessage());
      }

      try {
        TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
        resolver.resolve_step_2();
      } catch (TypeException e2) {
        if (DEBUG) {
          logger.error(e2.getMessage(), e2);
          logger.debug("Step 2 Exception-->" + e2.getMessage());
        }

        try {
          TypeResolverBV resolver = new TypeResolverBV(stmtBody, scene);
          resolver.resolve_step_3();
        } catch (TypeException e3) {
          StringWriter st = new StringWriter();
          PrintWriter pw = new PrintWriter(st);
          logger.error(e3.getMessage(), e3);
          pw.close();
          throw new RuntimeException(st.toString());
        }
      }
    }
    soot.jimple.toolkits.typing.integer.TypeResolver.resolve(stmtBody);
  }

  private void debug_vars(String message) {
    if (DEBUG) {
      int count = 0;
      logger.debug("**** START:" + message);
      for (TypeVariableBV var : typeVariableList) {
        logger.debug("" + count++ + " " + var);
      }
      logger.debug("**** END:" + message);
    }
  }

  private void debug_body() {
    if (DEBUG) {
      logger.debug("-- Body Start --");
      for (Iterator<Unit> stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext();) {
        final Stmt stmt = (Stmt) stmtIt.next();
        logger.debug("" + stmt);
      }
      logger.debug("-- Body End --");
    }
  }

  private void resolve_step_1() throws TypeException {
    // remove_spurious_locals();

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

  private void resolve_step_2() throws TypeException {
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

  private void resolve_step_3() throws TypeException {
    collect_constraints_3();
    compute_approximate_types();
    assign_types_3();
    check_and_fix_constraints();
  }

  private void collect_constraints_1_2() {
    ConstraintCollectorBV collector = new ConstraintCollectorBV(this, true);

    for (Iterator<Unit> stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext();) {

      final Stmt stmt = (Stmt) stmtIt.next();
      if (DEBUG) {
        logger.debug("stmt: ");
      }
      collector.collect(stmt, stmtBody);
      if (DEBUG) {
        logger.debug("" + stmt);
      }
    }
  }

  private void collect_constraints_3() {
    ConstraintCollectorBV collector = new ConstraintCollectorBV(this, false);

    for (Iterator<Unit> stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext();) {

      final Stmt stmt = (Stmt) stmtIt.next();
      if (DEBUG) {
        logger.debug("stmt: ");
      }
      collector.collect(stmt, stmtBody);
      if (DEBUG) {
        logger.debug("" + stmt);
      }
    }
  }

  private void compute_array_depth() throws TypeException {
    compute_approximate_types();

    TypeVariableBV[] vars = new TypeVariableBV[typeVariableList.size()];
    vars = typeVariableList.toArray(vars);

    for (TypeVariableBV element : vars) {
      element.fixDepth();
    }
  }

  private void propagate_array_constraints() {
    // find max depth
    int max = 0;
    for (TypeVariableBV var : typeVariableList) {
      int depth = var.depth();

      if (depth > max) {
        max = depth;
      }
    }

    if (max > 1) {
      // hack for J2ME library, reported by Stephen Cheng
      if (!Options.v().j2me()) {
        typeVariable(ArrayType.v(RefType.v("java.lang.Cloneable"), max - 1));
        typeVariable(ArrayType.v(RefType.v("java.io.Serializable"), max - 1));
      }
    }

    // create lists for each array depth
    @SuppressWarnings("unchecked")
    LinkedList<TypeVariableBV>[] lists = new LinkedList[max + 1];
    for (int i = 0; i <= max; i++) {
      lists[i] = new LinkedList<TypeVariableBV>();
    }

    for (TypeVariableBV var : typeVariableList) {
      int depth = var.depth();

      lists[depth].add(var);
    }

    // propagate constraints, starting with highest depth
    for (int i = max; i >= 0; i--) {
      for (TypeVariableBV var : typeVariableList) {
        var.propagate();
      }
    }
  }

  private void merge_primitive_types() throws TypeException {
    // merge primitive types with all parents/children
    compute_solved();

    BitSetIterator varIt = solved.iterator();
    while (varIt.hasNext()) {
      TypeVariableBV var = typeVariableForId(varIt.next());

      if (var.type().type() instanceof IntType || var.type().type() instanceof LongType
          || var.type().type() instanceof FloatType || var.type().type() instanceof DoubleType) {
        BitVector parents;
        BitVector children;
        boolean finished;

        do {
          finished = true;

          parents = var.parents();
          if (parents.length() != 0) {
            finished = false;
            for (BitSetIterator j = parents.iterator(); j.hasNext();) {
              if (DEBUG) {
                logger.debug(".");
              }

              TypeVariableBV parent = typeVariableForId(j.next());

              var = var.union(parent);
            }
          }

          children = var.children();
          if (children.length() != 0) {
            finished = false;
            for (BitSetIterator j = children.iterator(); j.hasNext();) {
              if (DEBUG) {
                logger.debug(".");
              }

              TypeVariableBV child = typeVariableForId(j.next());

              var = var.union(child);
            }
          }
        } while (!finished);
      }
    }
  }

  private void merge_connected_components() throws TypeException {
    refresh_solved();
    BitVector list = new BitVector();
    list.or(solved);
    list.or(unsolved);

    new StronglyConnectedComponentsBV(list, this);
  }

  private void remove_transitive_constraints() throws TypeException {
    refresh_solved();
    BitVector list = new BitVector();
    list.or(solved);
    list.or(unsolved);

    for (BitSetIterator varIt = list.iterator(); varIt.hasNext();) {

      final TypeVariableBV var = typeVariableForId(varIt.next());

      var.removeIndirectRelations();
    }
  }

  private void merge_single_constraints() throws TypeException {
    boolean finished = false;
    boolean modified = false;
    while (true) {
      categorize();

      if (single_child_not_null.length() != 0) {
        finished = false;
        modified = true;

        BitSetIterator i = single_child_not_null.iterator();
        while (i.hasNext()) {
          TypeVariableBV var = typeVariableForId(i.next());

          if (single_child_not_null.get(var.id())) {
            // PA: Potential difference to old algorithm - using the smallest element
            // in the list rather than children().get(0);
            TypeVariableBV child = typeVariableForId(var.children().iterator().next());

            var = var.union(child);
          }
        }
      }

      if (finished) {
        if (single_soft_parent.length() != 0) {
          finished = false;
          modified = true;

          BitSetIterator i = single_soft_parent.iterator();
          while (i.hasNext()) {
            TypeVariableBV var = typeVariableForId(i.next());

            if (single_soft_parent.get(var.id())) {
              // PA: See above.
              TypeVariableBV parent = typeVariableForId(var.parents().iterator().next());

              var = var.union(parent);
            }
          }
        }

        if (single_hard_parent.length() != 0) {
          finished = false;
          modified = true;

          BitSetIterator i = single_hard_parent.iterator();
          while (i.hasNext()) {
            TypeVariableBV var = typeVariableForId(i.next());

            if (single_hard_parent.get(var.id())) {
              // PA: See above
              TypeVariableBV parent = typeVariableForId(var.parents().iterator().next());

              debug_vars("union single parent\n " + var + "\n " + parent);
              var = var.union(parent);
            }
          }
        }

        if (single_null_child.length() != 0) {
          finished = false;
          modified = true;

          BitSetIterator i = single_null_child.iterator();
          while (i.hasNext()) {
            TypeVariableBV var = typeVariableForId(i.next());

            if (single_null_child.get(var.id())) {
              // PA: See above
              TypeVariableBV child = typeVariableForId(var.children().iterator().next());

              var = var.union(child);
            }
          }
        }

        if (finished) {
          break;
        }

        continue;
      }

      if (modified) {
        modified = false;
        continue;
      }

      finished = true;

      multiple_children: for (BitSetIterator varIt = multiple_children.iterator(); varIt.hasNext();) {
        final TypeVariableBV var = typeVariableForId(varIt.next());
        TypeNode lca = null;
        BitVector children_to_remove = new BitVector();

        for (BitSetIterator childIt = var.children().iterator(); childIt.hasNext();) {

          final TypeVariableBV child = typeVariableForId(childIt.next());
          TypeNode type = child.type();

          if (type != null && type.isNull()) {
            var.removeChild(child);
          } else if (type != null && type.isClass()) {
            children_to_remove.set(child.id());

            if (lca == null) {
              lca = type;
            } else {
              lca = lca.lcaIfUnique(type);

              if (lca == null) {
                if (DEBUG) {
                  logger.debug(
                      "==++==" + stmtBody.getMethod().getDeclaringClass().getName() + "." + stmtBody.getMethod().getName());
                }

                continue multiple_children;
              }
            }
          }
        }

        if (lca != null) {
          for (BitSetIterator childIt = children_to_remove.iterator(); childIt.hasNext();) {
            final TypeVariableBV child = typeVariableForId(childIt.next());
            var.removeChild(child);
          }

          var.addChild(typeVariable(lca));
        }
      }

      for (BitSetIterator varIt = multiple_parents.iterator(); varIt.hasNext();) {

        final TypeVariableBV var = typeVariableForId(varIt.next());
        LinkedList<TypeVariableBV> hp = new LinkedList<TypeVariableBV>(); // hard parents

        for (BitSetIterator parentIt = var.parents().iterator(); parentIt.hasNext();) {

          final TypeVariableBV parent = typeVariableForId(parentIt.next());
          TypeNode type = parent.type();

          if (type != null) {
            Iterator<TypeVariableBV> k = hp.iterator();
            while (k.hasNext()) {
              TypeVariableBV otherparent = k.next();
              TypeNode othertype = otherparent.type();

              if (type.hasDescendant(othertype)) {
                var.removeParent(parent);
                type = null;
                break;
              }

              if (type.hasAncestor(othertype)) {
                var.removeParent(otherparent);
                k.remove();
              }
            }

            if (type != null) {
              hp.add(parent);
            }
          }
        }
      }
    }
  }

  private void assign_types_1_2() throws TypeException {
    for (Iterator<Local> localIt = stmtBody.getLocals().iterator(); localIt.hasNext();) {
      final Local local = localIt.next();
      TypeVariableBV var = typeVariable(local);

      if (var == null) {
        local.setType(RefType.v("java.lang.Object"));
      } else if (var.depth() == 0) {
        if (var.type() == null) {
          TypeVariableBV.error("Type Error(5):  Variable without type");
        } else {
          local.setType(var.type().type());
        }
      } else {
        TypeVariableBV element = var.element();

        for (int j = 1; j < var.depth(); j++) {
          element = element.element();
        }

        if (element.type() == null) {
          TypeVariableBV.error("Type Error(6):  Array variable without base type");
        } else if (element.type().type() instanceof NullType) {
          local.setType(NullType.v());
        } else {
          Type t = element.type().type();
          if (t instanceof IntType) {
            local.setType(var.approx().type());
          } else {
            local.setType(ArrayType.v(t, var.depth()));
          }
        }
      }

      if (DEBUG) {
        if ((var != null) && (var.approx() != null) && (var.approx().type() != null) && (local != null)
            && (local.getType() != null) && !local.getType().equals(var.approx().type())) {
          logger.debug("local: " + local + ", type: " + local.getType() + ", approx: " + var.approx().type());
        }
      }
    }
  }

  private void assign_types_3() throws TypeException {
    for (Iterator<Local> localIt = stmtBody.getLocals().iterator(); localIt.hasNext();) {
      final Local local = localIt.next();
      TypeVariableBV var = typeVariable(local);

      if (var == null || var.approx() == null || var.approx().type() == null) {
        local.setType(RefType.v("java.lang.Object"));
      } else {
        local.setType(var.approx().type());
      }
    }
  }

  private void check_constraints() throws TypeException {
    ConstraintCheckerBV checker = new ConstraintCheckerBV(this, false);
    StringBuffer s = null;

    if (DEBUG) {
      s = new StringBuffer("Checking:\n");
    }

    for (Iterator<Unit> stmtIt = stmtBody.getUnits().iterator(); stmtIt.hasNext();) {

      final Stmt stmt = (Stmt) stmtIt.next();
      if (DEBUG) {
        s.append(" " + stmt + "\n");
      }
      try {
        checker.check(stmt, stmtBody);
      } catch (TypeException e) {
        if (DEBUG) {
          logger.debug("" + s);
        }
        throw e;
      }
    }
  }

  private void check_and_fix_constraints() throws TypeException {
    ConstraintCheckerBV checker = new ConstraintCheckerBV(this, true);
    StringBuffer s = null;
    PatchingChain<Unit> units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];
    units.toArray(stmts);

    if (DEBUG) {
      s = new StringBuffer("Checking:\n");
    }

    for (Stmt stmt : stmts) {
      if (DEBUG) {
        s.append(" " + stmt + "\n");
      }
      try {
        checker.check(stmt, stmtBody);
      } catch (TypeException e) {
        if (DEBUG) {
          logger.debug("" + s);
        }
        throw e;
      }
    }
  }

  private void compute_approximate_types() throws TypeException {
    TreeSet<TypeVariableBV> workList = new TreeSet<TypeVariableBV>();

    for (TypeVariableBV var : typeVariableList) {

      if (var.type() != null) {
        workList.add(var);
      }
    }

    TypeVariableBV.computeApprox(workList);

    for (TypeVariableBV var : typeVariableList) {

      if (var.approx() == NULL) {
        var.union(typeVariable(NULL));
      } else if (var.approx() == null) {
        var.union(typeVariable(NULL));
      }
    }
  }

  private void compute_solved() {
    unsolved = new BitVector();
    solved = new BitVector();

    for (TypeVariableBV var : typeVariableList) {

      if (var.depth() == 0) {
        if (var.type() == null) {
          unsolved.set(var.id());
        } else {
          solved.set(var.id());
        }
      }
    }
  }

  private void refresh_solved() throws TypeException {
    unsolved = new BitVector();
    // solved stays the same

    for (BitSetIterator varIt = unsolved.iterator(); varIt.hasNext();) {

      final TypeVariableBV var = typeVariableForId(varIt.next());

      if (var.depth() == 0) {
        if (var.type() == null) {
          unsolved.set(var.id());
        } else {
          solved.set(var.id());
        }
      }
    }

    // validate();
  }

  private void categorize() throws TypeException {
    refresh_solved();

    single_soft_parent = new BitVector();
    single_hard_parent = new BitVector();
    multiple_parents = new BitVector();
    single_child_not_null = new BitVector();
    single_null_child = new BitVector();
    multiple_children = new BitVector();

    for (BitSetIterator i = unsolved.iterator(); i.hasNext();) {
      TypeVariableBV var = typeVariableForId(i.next());

      // parent category
      {
        BitVector parents = var.parents();
        int size = parents.length();

        if (size == 0) {
          var.addParent(typeVariable(OBJECT));
          single_soft_parent.set(var.id());
        } else if (size == 1) {
          TypeVariableBV parent = typeVariableForId(parents.iterator().next());

          if (parent.type() == null) {
            single_soft_parent.set(var.id());
          } else {
            single_hard_parent.set(var.id());
          }
        } else {
          multiple_parents.set(var.id());
        }
      }

      // child category
      {
        BitVector children = var.children();
        int size = children.size();

        if (size == 0) {
          var.addChild(typeVariable(NULL));
          single_null_child.set(var.id());
        } else if (size == 1) {
          TypeVariableBV child = typeVariableForId(children.iterator().next());

          if (child.type() == NULL) {
            single_null_child.set(var.id());
          } else {
            single_child_not_null.set(var.id());
          }
        } else {
          multiple_children.set(var.id());
        }
      }
    }
  }

  private void split_new() {
    LocalDefs defs = LocalDefs.Factory.newLocalDefs(stmtBody);
    PatchingChain<Unit> units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];

    units.toArray(stmts);

    for (Stmt stmt : stmts) {
      if (stmt instanceof InvokeStmt) {
        InvokeStmt invoke = (InvokeStmt) stmt;

        if (invoke.getInvokeExpr() instanceof SpecialInvokeExpr) {
          SpecialInvokeExpr special = (SpecialInvokeExpr) invoke.getInvokeExpr();

          if (special.getMethodRef().name().equals("<init>")) {
            List<Unit> deflist = defs.getDefsOfAt((Local) special.getBase(), invoke);

            while (deflist.size() == 1) {
              Stmt stmt2 = (Stmt) deflist.get(0);

              if (stmt2 instanceof AssignStmt) {
                AssignStmt assign = (AssignStmt) stmt2;

                if (assign.getRightOp() instanceof Local) {
                  deflist = defs.getDefsOfAt((Local) assign.getRightOp(), assign);
                  continue;
                } else if (assign.getRightOp() instanceof NewExpr) {
                  // We split the local.
                  // logger.debug("split: [" + assign + "] and [" + stmt + "]");
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
    return typeVariableList.get(idx);
  }

  public BitVector invalidIds() {
    return invalidIds;
  }

  public void invalidateId(int id) {
    invalidIds.set(id);
  }
}
