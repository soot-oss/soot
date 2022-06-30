package soot.jimple.toolkits.typing;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.util.BitSetIterator;
import soot.util.BitVector;

/**
 * @deprecated use {@link soot.jimple.toolkits.typing.fast.TypeResolver} instead
 */
@Deprecated
class StronglyConnectedComponentsBV {
  private static final Logger logger = LoggerFactory.getLogger(StronglyConnectedComponentsBV.class);
  BitVector variables;
  Set<TypeVariableBV> black;
  LinkedList<TypeVariableBV> finished;

  TypeResolverBV resolver;

  LinkedList<LinkedList<TypeVariableBV>> forest = new LinkedList<>();
  LinkedList<TypeVariableBV> current_tree;

  private static final boolean DEBUG = false;

  public StronglyConnectedComponentsBV(BitVector typeVariableList, TypeResolverBV resolver) throws TypeException {
    this.resolver = resolver;
    variables = typeVariableList;

    black = new TreeSet<>();
    finished = new LinkedList<>();

    for (BitSetIterator i = variables.iterator(); i.hasNext();) {
      TypeVariableBV var = resolver.typeVariableForId(i.next());

      if (!black.contains(var)) {
        black.add(var);
        dfsg_visit(var);
      }
    }

    black = new TreeSet<>();

    for (TypeVariableBV var : finished) {
      if (!black.contains(var)) {
        current_tree = new LinkedList<>();
        forest.add(current_tree);
        black.add(var);
        dfsgt_visit(var);
      }
    }

    for (LinkedList<TypeVariableBV> list : forest) {
      TypeVariableBV previous = null;
      StringBuffer s = null;
      if (DEBUG) {
        s = new StringBuffer("scc:\n");
      }

      for (TypeVariableBV current : list) {
        if (DEBUG) {
          s.append(" " + current + "\n");
        }

        if (previous == null) {
          previous = current;
        } else {
          try {
            previous = previous.union(current);
          } catch (TypeException e) {
            if (DEBUG) {
              logger.debug("" + s);
            }
            throw e;
          }
        }
      }
    }
  }

  private void dfsg_visit(TypeVariableBV var) {
    BitVector parents = var.parents();

    for (BitSetIterator i = parents.iterator(); i.hasNext();) {
      TypeVariableBV parent = resolver.typeVariableForId(i.next());

      if (!black.contains(parent)) {
        black.add(parent);
        dfsg_visit(parent);
      }
    }

    finished.add(0, var);
  }

  private void dfsgt_visit(TypeVariableBV var) {
    current_tree.add(var);

    BitVector children = var.children();

    for (BitSetIterator i = children.iterator(); i.hasNext();) {
      TypeVariableBV child = resolver.typeVariableForId(i.next());

      if (!black.contains(child)) {
        black.add(child);
        dfsgt_visit(child);
      }
    }
  }
}
