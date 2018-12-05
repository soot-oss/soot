package soot.jimple.spark.pag;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Context;
import soot.PhaseOptions;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.options.CGOptions;

/**
 * Represents an allocation site node (Blue) in the pointer assignment graph.
 * 
 * @author Ondrej Lhotak
 */
public class AllocNode extends Node implements Context {
  /** Returns the new expression of this allocation site. */
  public Object getNewExpr() {
    return newExpr;
  }

  /** Returns all field ref nodes having this node as their base. */
  public Collection<AllocDotField> getAllFieldRefs() {
    if (fields == null) {
      return Collections.emptySet();
    }
    return fields.values();
  }

  /**
   * Returns the field ref node having this node as its base, and field as its field; null if nonexistent.
   */
  public AllocDotField dot(SparkField field) {
    return fields == null ? null : fields.get(field);
  }

  public String toString() {
    return "AllocNode " + getNumber() + " " + newExpr + " in method " + method;
  }

  /* End of public methods. */

  AllocNode(PAG pag, Object newExpr, Type t, SootMethod m) {
    super(pag, t);
    this.method = m;
    if (t instanceof RefType) {
      RefType rt = (RefType) t;
      if (rt.getSootClass().isAbstract()) {
        boolean usesReflectionLog = new CGOptions(PhaseOptions.v().getPhaseOptions("cg")).reflection_log() != null;
        if (!usesReflectionLog) {
          throw new RuntimeException("Attempt to create allocnode with abstract type " + t);
        }
      }
    }
    this.newExpr = newExpr;
    if (newExpr instanceof ContextVarNode) {
      throw new RuntimeException();
    }
    pag.getAllocNodeNumberer().add(this);
  }

  /** Registers a AllocDotField as having this node as its base. */
  void addField(AllocDotField adf, SparkField field) {
    if (fields == null) {
      fields = new HashMap<SparkField, AllocDotField>();
    }
    fields.put(field, adf);
  }

  public Set<AllocDotField> getFields() {
    if (fields == null) {
      return Collections.emptySet();
    }
    return new HashSet<AllocDotField>(fields.values());
  }

  /* End of package methods. */

  protected Object newExpr;
  protected Map<SparkField, AllocDotField> fields;

  private SootMethod method;

  public SootMethod getMethod() {
    return method;
  }
}
