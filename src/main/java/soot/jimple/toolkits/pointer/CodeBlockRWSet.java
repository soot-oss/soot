package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import soot.PointsToSet;
import soot.Scene;
import soot.SootField;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.HashPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;

public class CodeBlockRWSet extends MethodRWSet {

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    boolean empty = true;
    if (fields != null) {
      for (Map.Entry<Object, PointsToSet> e : fields.entrySet()) {
        ret.append("[Field: ").append(e.getKey()).append(' ');
        PointsToSet baseObj = e.getValue();
        if (baseObj instanceof PointsToSetInternal) {
          /*
           * PointsToSetInternal base = (PointsToSetInternal) fields.get(field); base.forall( new P2SetVisitor() { public
           * void visit( Node n ) { ret.append(n.getNumber() + " "); } } );
           */
          int baseSize = ((PointsToSetInternal) baseObj).size();
          ret.append(baseSize).append(baseSize == 1 ? " Node]\n" : " Nodes]\n");
        } else {
          ret.append(baseObj).append("]\n");
        }
        empty = false;
      }
    }
    if (globals != null) {
      for (SootField global : globals) {
        ret.append("[Global: ").append(global).append("]\n");
        empty = false;
      }
    }
    if (empty) {
      ret.append("[emptyset]\n");
    }
    return ret.toString();
  }

  /** Adds the RWSet other into this set. */
  @Override
  public boolean union(RWSet other) {
    if (other == null || isFull) {
      return false;
    }
    boolean ret = false;
    if (other instanceof MethodRWSet) {
      MethodRWSet o = (MethodRWSet) other;
      if (o.getCallsNative()) {
        ret = !getCallsNative() | ret;
        setCallsNative();
      }
      if (o.isFull) {
        ret = !isFull | ret;
        isFull = true;
        if (true) {
          throw new RuntimeException("attempt to add full set " + o + " into " + this);
        }
        globals = null;
        fields = null;
        return ret;
      }
      if (o.globals != null) {
        if (globals == null) {
          globals = new HashSet<SootField>();
        }
        ret = globals.addAll(o.globals) | ret;
        if (globals.size() > MAX_SIZE) {
          globals = null;
          isFull = true;
          throw new RuntimeException("attempt to add full set " + o + " into " + this);
        }
      }
      if (o.fields != null) {
        for (Object field : o.fields.keySet()) {
          ret = addFieldRef(o.getBaseForField(field), field) | ret;
        }
      }
    } else if (other instanceof StmtRWSet) {
      StmtRWSet oth = (StmtRWSet) other;
      if (oth.base != null) {
        ret = addFieldRef(oth.base, oth.field) | ret;
      } else if (oth.field != null) {
        ret = addGlobal((SootField) oth.field) | ret;
      }
    } else if (other instanceof SiteRWSet) {
      SiteRWSet oth = (SiteRWSet) other;
      for (RWSet set : oth.sets) {
        this.union(set);
      }
    }
    if (!getCallsNative() && other.getCallsNative()) {
      setCallsNative();
      return true;
    }
    return ret;
  }

  public boolean containsField(Object field) {
    return fields != null && fields.containsKey(field);
  }

  public CodeBlockRWSet intersection(MethodRWSet other) {
    // May run slowly... O(n^2)
    CodeBlockRWSet ret = new CodeBlockRWSet();

    if (isFull) {
      return ret;
    }

    if (globals != null && other.globals != null && !globals.isEmpty() && !other.globals.isEmpty()) {
      for (SootField sg : other.globals) {
        if (globals.contains(sg)) {
          ret.addGlobal(sg);
        }
      }
    }

    if (fields != null && other.fields != null && !fields.isEmpty() && !other.fields.isEmpty()) {
      for (Object field : other.fields.keySet()) {
        if (fields.containsKey(field)) {
          PointsToSet pts1 = getBaseForField(field);
          PointsToSet pts2 = other.getBaseForField(field);
          if (pts1 instanceof FullObjectSet) {
            ret.addFieldRef(pts2, field);
          } else if (pts2 instanceof FullObjectSet) {
            ret.addFieldRef(pts1, field);
          } else if (pts1.hasNonEmptyIntersection(pts2)) {
            if ((pts1 instanceof PointsToSetInternal) && (pts2 instanceof PointsToSetInternal)) {
              final PointsToSetInternal pti1 = (PointsToSetInternal) pts1;
              final PointsToSetInternal pti2 = (PointsToSetInternal) pts2;
              final PointsToSetInternal newpti = new HashPointsToSet(pti1.getType(), (PAG) Scene.v().getPointsToAnalysis());

              pti1.forall(new P2SetVisitor() {
                @Override
                public void visit(Node n) {
                  if (pti2.contains(n)) {
                    newpti.add(n);
                  }
                }
              });

              ret.addFieldRef(newpti, field);
            }
          }
        }
      }
    }
    return ret;
  }

  @Override
  public boolean addFieldRef(PointsToSet otherBase, Object field) {
    boolean ret = false;
    if (fields == null) {
      fields = new HashMap<Object, PointsToSet>();
    }

    // Get our points-to set, merge with other
    PointsToSet base = getBaseForField(field);
    if (base instanceof FullObjectSet) {
      return false;
    }
    if (otherBase instanceof FullObjectSet) {
      fields.put(field, otherBase);
      return true;
    }
    if (otherBase.equals(base)) {
      return false;
    }
    if (base == null) {
      // NOTE: this line makes unsafe assumptions about the PTA
      base = new HashPointsToSet(((PointsToSetInternal) otherBase).getType(), (PAG) Scene.v().getPointsToAnalysis());
      fields.put(field, base);
    }

    return ((PointsToSetInternal) base).addAll((PointsToSetInternal) otherBase, null) | ret;
  }
}
