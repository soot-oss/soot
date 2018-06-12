package soot.jimple.toolkits.typing.integer;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.BooleanType;
import soot.ByteType;
import soot.IntegerType;
import soot.Local;
import soot.PatchingChain;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;

/**
 * This class resolves the type of local variables.
 **/
public class TypeResolver {
  private static final Logger logger = LoggerFactory.getLogger(TypeResolver.class);
  /** All type variable instances **/
  private final List<TypeVariable> typeVariableList = new ArrayList<TypeVariable>();

  /** Hashtable: [TypeNode or Local] -> TypeVariable **/
  private final Map<Object, TypeVariable> typeVariableMap = new HashMap<Object, TypeVariable>();

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
  private static final boolean IMPERFORMANT_TYPE_CHECK = false;

  // categories for type variables (solved = hard, unsolved = soft)
  private Collection<TypeVariable> unsolved;
  private Collection<TypeVariable> solved;

  /** Get type variable for the given local. **/
  TypeVariable typeVariable(Local local) {
    TypeVariable result = typeVariableMap.get(local);

    if (result == null) {
      int id = typeVariableList.size();
      typeVariableList.add(null);

      result = new TypeVariable(id, this);

      typeVariableList.set(id, result);
      typeVariableMap.put(local, result);

      if (DEBUG) {
        logger.debug("[LOCAL VARIABLE \"" + local + "\" -> " + id + "]");
      }
    }

    return result;
  }

  /** Get type variable for the given type node. **/
  public TypeVariable typeVariable(TypeNode typeNode) {
    TypeVariable result = typeVariableMap.get(typeNode);

    if (result == null) {
      int id = typeVariableList.size();
      typeVariableList.add(null);

      result = new TypeVariable(id, this, typeNode);

      typeVariableList.set(id, result);
      typeVariableMap.put(typeNode, result);
    }

    return result;
  }

  /** Get type variable for the given type. **/
  public TypeVariable typeVariable(Type type) {
    return typeVariable(ClassHierarchy.v().typeNode(type));
  }

  /** Get new type variable **/
  public TypeVariable typeVariable() {
    int id = typeVariableList.size();
    typeVariableList.add(null);

    TypeVariable result = new TypeVariable(id, this);

    typeVariableList.set(id, result);

    return result;
  }

  private TypeResolver(JimpleBody stmtBody) {
    this.stmtBody = stmtBody;
  }

  public static void resolve(JimpleBody stmtBody) {
    if (DEBUG) {
      logger.debug("" + stmtBody.getMethod());
    }

    try {
      TypeResolver resolver = new TypeResolver(stmtBody);
      resolver.resolve_step_1();
    } catch (TypeException e1) {
      if (DEBUG) {
        logger.debug("[integer] Step 1 Exception-->" + e1.getMessage());
      }

      try {
        TypeResolver resolver = new TypeResolver(stmtBody);
        resolver.resolve_step_2();
      } catch (TypeException e2) {
        StringWriter st = new StringWriter();
        PrintWriter pw = new PrintWriter(st);
        logger.error(e2.getMessage(), e2);
        pw.close();
        throw new RuntimeException(st.toString());
      }
    }
  }

  private void debug_vars(String message) {
    if (DEBUG) {
      int count = 0;
      logger.debug("**** START:" + message);
      for (TypeVariable var : typeVariableList) {
        logger.debug("" + count++ + " " + var);
      }
      logger.debug("**** END:" + message);
    }
  }

  private void resolve_step_1() throws TypeException {
    collect_constraints_1();
    debug_vars("constraints");

    compute_approximate_types();
    merge_connected_components();
    debug_vars("components");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1();
    debug_vars("assign");

    check_and_fix_constraints();
  }

  private void resolve_step_2() throws TypeException {
    collect_constraints_2();
    compute_approximate_types();
    assign_types_2();
    check_and_fix_constraints();
  }

  private void collect_constraints_1() {
    ConstraintCollector collector = new ConstraintCollector(this, true);

    for (Unit u : stmtBody.getUnits()) {
      final Stmt stmt = (Stmt) u;
      if (DEBUG) {
        logger.debug("stmt: ");
      }
      collector.collect(stmt, stmtBody);
      if (DEBUG) {
        logger.debug("" + stmt);
      }
    }
  }

  private void collect_constraints_2() {
    ConstraintCollector collector = new ConstraintCollector(this, false);

    for (Unit u : stmtBody.getUnits()) {
      final Stmt stmt = (Stmt) u;
      if (DEBUG) {
        logger.debug("stmt: ");
      }
      collector.collect(stmt, stmtBody);
      if (DEBUG) {
        logger.debug("" + stmt);
      }
    }
  }

  private void merge_connected_components() throws TypeException {
    compute_solved();
    if (IMPERFORMANT_TYPE_CHECK) {
      List<TypeVariable> list = new ArrayList<TypeVariable>(solved.size() + unsolved.size());
      list.addAll(solved);
      list.addAll(unsolved);
      // MMI: This method does not perform any changing effect
      // on the list, just a bit error checking, if
      // I see this correctly.
      StronglyConnectedComponents.merge(list);
    }
  }

  private void merge_single_constraints() throws TypeException {
    boolean modified = true;

    while (modified) {
      modified = false;
      refresh_solved();

      for (TypeVariable var : unsolved) {

        List<TypeVariable> children_to_remove = new LinkedList<TypeVariable>();
        TypeNode lca = null;

        var.fixChildren();

        for (TypeVariable child : var.children()) {

          TypeNode type = child.type();

          if (type != null) {
            children_to_remove.add(child);

            if (lca == null) {
              lca = type;
            } else {
              lca = lca.lca_1(type);
            }
          }
        }

        if (lca != null) {
          if (DEBUG) {
            if (lca == ClassHierarchy.v().TOP) {
              logger.debug("*** TOP *** " + var);
              for (TypeVariable typeVariable : children_to_remove) {
                logger.debug("-- " + typeVariable);
              }
            }
          }

          for (TypeVariable child : children_to_remove) {

            var.removeChild(child);
          }

          var.addChild(typeVariable(lca));
        }

        if (var.children().size() == 1) {
          TypeVariable child = var.children().get(0);
          TypeNode type = child.type();

          if (type == null || type.type() != null) {
            var.union(child);
            modified = true;
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : unsolved) {
          List<TypeVariable> parents_to_remove = new LinkedList<TypeVariable>();
          TypeNode gcd = null;

          var.fixParents();

          for (TypeVariable parent : var.parents()) {

            TypeNode type = parent.type();

            if (type != null) {
              parents_to_remove.add(parent);

              if (gcd == null) {
                gcd = type;
              } else {
                gcd = gcd.gcd_1(type);
              }
            }
          }

          if (gcd != null) {
            for (TypeVariable parent : parents_to_remove) {
              var.removeParent(parent);
            }

            var.addParent(typeVariable(gcd));
          }

          if (var.parents().size() == 1) {
            TypeVariable parent = var.parents().get(0);
            TypeNode type = parent.type();

            if (type == null || type.type() != null) {
              var.union(parent);
              modified = true;
            }
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : unsolved) {
          if (var.type() == null && var.inv_approx() != null && var.inv_approx().type() != null) {
            if (DEBUG) {
              logger.debug("*** I->" + var.inv_approx().type() + " *** " + var);
            }

            var.union(typeVariable(var.inv_approx()));
            modified = true;
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : unsolved) {
          if (var.type() == null && var.approx() != null && var.approx().type() != null) {
            if (DEBUG) {
              logger.debug("*** A->" + var.approx().type() + " *** " + var);
            }

            var.union(typeVariable(var.approx()));
            modified = true;
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : unsolved) {
          if (var.type() == null && var.approx() == ClassHierarchy.v().R0_32767) {
            if (DEBUG) {
              logger.debug("*** R->SHORT *** " + var);
            }

            var.union(SHORT);
            modified = true;
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : unsolved) {
          if (var.type() == null && var.approx() == ClassHierarchy.v().R0_127) {
            if (DEBUG) {
              logger.debug("*** R->BYTE *** " + var);
            }

            var.union(BYTE);
            modified = true;
          }
        }
      }

      if (!modified) {
        for (TypeVariable var : R0_1.parents()) {
          if (var.type() == null && var.approx() == ClassHierarchy.v().R0_1) {
            if (DEBUG) {
              logger.debug("*** R->BOOLEAN *** " + var);
            }
            var.union(BOOLEAN);
            modified = true;
          }
        }
      }
    }
  }

  private void assign_types_1() throws TypeException {
    for (Iterator<Local> localIt = stmtBody.getLocals().iterator(); localIt.hasNext();) {
      final Local local = localIt.next();

      if (local.getType() instanceof IntegerType) {
        TypeVariable var = typeVariable(local);

        if (var.type() == null || var.type().type() == null) {
          TypeVariable.error("Type Error(21):  Variable without type");
        } else {
          local.setType(var.type().type());
        }

        if (DEBUG) {
          if ((var != null) && (var.approx() != null) && (var.approx().type() != null) && (local != null)
              && (local.getType() != null) && !local.getType().equals(var.approx().type())) {
            logger.debug("local: " + local + ", type: " + local.getType() + ", approx: " + var.approx().type());
          }
        }
      }
    }
  }

  private void assign_types_2() throws TypeException {
    for (Iterator<Local> localIt = stmtBody.getLocals().iterator(); localIt.hasNext();) {
      final Local local = localIt.next();

      if (local.getType() instanceof IntegerType) {
        TypeVariable var = typeVariable(local);

        if (var.inv_approx() != null && var.inv_approx().type() != null) {
          local.setType(var.inv_approx().type());
        } else if (var.approx().type() != null) {
          local.setType(var.approx().type());
        } else if (var.approx() == ClassHierarchy.v().R0_1) {
          local.setType(BooleanType.v());
        } else if (var.approx() == ClassHierarchy.v().R0_127) {
          local.setType(ByteType.v());
        } else {
          local.setType(ShortType.v());
        }
      }
    }
  }

  private void check_constraints() throws TypeException {
    ConstraintChecker checker = new ConstraintChecker(this, false);
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
    ConstraintChecker checker = new ConstraintChecker(this, true);
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
    TreeSet<TypeVariable> workList = new TreeSet<TypeVariable>();

    for (TypeVariable var : typeVariableList) {

      if (var.type() != null) {
        workList.add(var);
      }
    }

    TypeVariable.computeApprox(workList);

    workList = new TreeSet<TypeVariable>();

    for (TypeVariable var : typeVariableList) {

      if (var.type() != null) {
        workList.add(var);
      }
    }

    TypeVariable.computeInvApprox(workList);

    for (TypeVariable var : typeVariableList) {

      if (var.approx() == null) {
        var.union(INT);
      }
    }
  }

  private void compute_solved() {
    Set<TypeVariable> unsolved_set = new TreeSet<TypeVariable>();
    Set<TypeVariable> solved_set = new TreeSet<TypeVariable>();

    for (TypeVariable var : typeVariableList) {

      if (var.type() == null) {
        unsolved_set.add(var);
      } else {
        solved_set.add(var);
      }
    }

    solved = solved_set;
    unsolved = unsolved_set;
  }

  private void refresh_solved() throws TypeException {
    Set<TypeVariable> unsolved_set = new TreeSet<TypeVariable>();
    Set<TypeVariable> solved_set = new TreeSet<TypeVariable>(solved);

    for (TypeVariable var : unsolved) {

      if (var.type() == null) {
        unsolved_set.add(var);
      } else {
        solved_set.add(var);
      }
    }

    solved = solved_set;
    unsolved = unsolved_set;
  }
}
