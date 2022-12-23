package soot.toolkits.scalar;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.IdentityUnit;
import soot.Local;
import soot.Timers;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalGraph;
import soot.toolkits.graph.ExceptionalGraph.ExceptionDest;
import soot.toolkits.graph.UnitGraph;

/**
 * Analysis that provides an implementation of the LocalDefs interface.
 */
public class SimpleLocalDefs implements LocalDefs {

  public static class StaticSingleAssignment implements LocalDefs {
    final Map<Local, List<Unit>> result;

    public StaticSingleAssignment(Local[] locals, List<Unit>[] unitList) {
      final int N = locals.length;
      assert (N == unitList.length);

      this.result = new HashMap<Local, List<Unit>>((N * 3) / 2 + 7);
      for (int i = 0; i < N; i++) {
        List<Unit> curr = unitList[i];
        if (!curr.isEmpty()) {
          assert (curr.size() == 1);
          result.put(locals[i], curr);
        }
      }
    }

    @Override
    public List<Unit> getDefsOfAt(Local l, Unit s) {
      List<Unit> lst = result.get(l);
      // singleton-lists are immutable
      return lst != null ? lst : Collections.emptyList();
    }

    @Override
    public List<Unit> getDefsOf(Local l) {
      return getDefsOfAt(l, null);
    }
  } // end inner class StaticSingleAssignment

  public class FlowAssignment extends ForwardFlowAnalysis<Unit, FlowAssignment.FlowBitSet> implements LocalDefs {

    class FlowBitSet extends BitSet {
      private static final long serialVersionUID = -8348696077189400377L;

      FlowBitSet() {
        super(universe.length);
      }

      List<Unit> asList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < fromIndex || universe.length < toIndex) {
          throw new IndexOutOfBoundsException();
        }
        if (fromIndex == toIndex) {
          return Collections.emptyList();
        }

        if (fromIndex == toIndex - 1) {
          if (this.get(fromIndex)) {
            return Collections.singletonList(universe[fromIndex]);
          } else {
            return Collections.emptyList();
          }
        }

        int i = this.nextSetBit(fromIndex);
        if (i < 0 || i >= toIndex) {
          return Collections.emptyList();
        }
        if (i == toIndex - 1) {
          return Collections.singletonList(universe[i]);
        }

        List<Unit> elements = new ArrayList<Unit>(toIndex - i);
        for (;;) {
          int endOfRun = Math.min(toIndex, this.nextClearBit(i + 1));
          do {
            elements.add(universe[i++]);
          } while (i < endOfRun);
          if (i >= toIndex) {
            break;
          }
          i = this.nextSetBit(i + 1);
          if (i < 0 || i >= toIndex) {
            break;
          }
        }
        return elements;
      }
    }

    final Map<Local, Integer> locals;
    final List<Unit>[] unitList;
    final int[] localRange;
    final Unit[] universe;

    private Map<Unit, Integer> indexOfUnit;

    public FlowAssignment(DirectedGraph<Unit> graph, Local[] locals, List<Unit>[] unitList, int units, boolean omitSSA) {
      super(graph);
      this.unitList = unitList;
      this.universe = new Unit[units];
      this.indexOfUnit = new HashMap<Unit, Integer>(units);
      final int N = locals.length;
      this.locals = new HashMap<Local, Integer>((N * 3) / 2 + 7);
      this.localRange = new int[N + 1];

      for (int j = 0, i = 0; i < N; this.localRange[++i] = j) {
        List<Unit> currUnitList = unitList[i];
        if (currUnitList == null || currUnitList.isEmpty()) {
          continue;
        }

        localRange[i + 1] = j;
        this.locals.put(locals[i], i);

        if (currUnitList.size() >= 2) {
          for (Unit u : currUnitList) {
            this.indexOfUnit.put(u, j);
            this.universe[j++] = u;
          }
        } else if (omitSSA) {
          this.universe[j++] = currUnitList.get(0);
        }
      }
      assert (localRange[N] == units);

      doAnalysis();

      this.indexOfUnit = null;// release memory
    }

    @Override
    protected boolean omissible(Unit u) {
      final List<ValueBox> defs = u.getDefBoxes();
      if (!defs.isEmpty()) { // avoid temporary creation of iterators (more like micro-tuning)
        for (ValueBox vb : defs) {
          Value v = vb.getValue();
          if (v instanceof Local) {
            Local l = (Local) v;
            int lno = getLocalNumber(l);
            return (localRange[lno] == localRange[lno + 1]);
          }
        }
      }
      return true;
    }

    @Override
    protected Flow getFlow(Unit from, Unit to) {
      // QND
      if (to instanceof IdentityUnit && graph instanceof ExceptionalGraph) {
        ExceptionalGraph<Unit> g = (ExceptionalGraph<Unit>) graph;
        if (!g.getExceptionalPredsOf(to).isEmpty()) {
          // look if there is a real exception edge
          for (ExceptionDest<Unit> exd : g.getExceptionDests(from)) {
            Trap trap = exd.getTrap();
            if (trap != null && trap.getHandlerUnit() == to) {
              return Flow.IN;
            }
          }
        }
      }
      return Flow.OUT;
    }

    @Override
    protected void flowThrough(FlowBitSet in, Unit unit, FlowBitSet out) {
      copy(in, out);

      // reassign all definitions
      for (ValueBox vb : unit.getDefBoxes()) {
        Value v = vb.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          int lno = getLocalNumber(l);
          int from = localRange[lno];
          int to = localRange[1 + lno];

          if (from == to) {
            continue;
          }

          assert (from <= to);

          if (to - from == 1) {
            // special case: this local has only one def point
            out.set(from);
          } else {
            out.clear(from, to);
            out.set(indexOfUnit.get(unit));
          }
        }
      }
    }

    @Override
    protected void copy(FlowBitSet source, FlowBitSet dest) {
      if (dest != source) {
        dest.clear();
        dest.or(source);
      }
    }

    protected FlowBitSet newInitialFlow() {
      return new FlowBitSet();
    }

    @Override
    protected void mergeInto(Unit succNode, FlowBitSet inout, FlowBitSet in) {
      inout.or(in);
    }

    @Override
    protected void merge(FlowBitSet in1, FlowBitSet in2, FlowBitSet out) {
      throw new UnsupportedOperationException("should never be called");
    }

    @Override
    public List<Unit> getDefsOfAt(Local l, Unit s) {
      Integer lno = locals.get(l);
      if (lno == null) {
        return Collections.emptyList();
      }

      int from = localRange[lno];
      int to = localRange[lno + 1];
      assert (from <= to);

      if (from == to) {
        assert (unitList[lno].size() == 1);
        // both singletonList is immutable
        return unitList[lno];
      } else {
        return getFlowBefore(s).asList(from, to);
      }
    }

    @Override
    public List<Unit> getDefsOf(Local l) {
      List<Unit> defs = new ArrayList<Unit>();
      for (Unit u : graph) {
        List<Unit> defsOf = getDefsOfAt(l, u);
        if (defsOf != null) {
          defs.addAll(defsOf);
        }
      }
      return defs;
    }
  } // end inner class FlowAssignment

  /**
   * The different modes in which the flow analysis can run
   */
  public enum FlowAnalysisMode {
    /**
     * Automatically detect the mode to use
     */
    Automatic,
    /**
     * Never use the SSA form, even if the unit graph would allow for a flow-insensitive analysis without losing precision
     */
    OmitSSA,
    /**
     * Always conduct a flow-insensitive analysis
     */
    FlowInsensitive
  }

  private final LocalDefs def;

  /**
   * 
   * @param graph
   */
  public SimpleLocalDefs(UnitGraph graph) {
    this(graph, FlowAnalysisMode.Automatic);
  }

  public SimpleLocalDefs(UnitGraph graph, FlowAnalysisMode mode) {
    this(graph, graph.getBody().getLocals(), mode);
  }

  SimpleLocalDefs(DirectedGraph<Unit> graph, Collection<Local> locals, FlowAnalysisMode mode) {
    this(graph, locals.toArray(new Local[locals.size()]), mode);
  }

  SimpleLocalDefs(DirectedGraph<Unit> graph, Local[] locals, boolean omitSSA) {
    this(graph, locals, omitSSA ? FlowAnalysisMode.OmitSSA : FlowAnalysisMode.Automatic);
  }

  SimpleLocalDefs(DirectedGraph<Unit> graph, Local[] locals, FlowAnalysisMode mode) {
    final boolean time = Options.v().time();
    if (time) {
      Timers.v().defsTimer.start();
    }

    // reassign local numbers
    int[] oldNumbers = assignNumbers(locals);

    this.def = init(graph, locals, mode);

    // restore local numbering
    restoreNumbers(locals, oldNumbers);

    if (time) {
      Timers.v().defsTimer.end();
    }
  }

  protected void restoreNumbers(Local[] locals, int[] oldNumbers) {
    for (int i = 0; i < oldNumbers.length; i++) {
      locals[i].setNumber(oldNumbers[i]);
    }
  }

  protected int[] assignNumbers(Local[] locals) {
    final int N = locals.length;
    int[] oldNumbers = new int[N];
    for (int i = 0; i < N; i++) {
      oldNumbers[i] = locals[i].getNumber();
      locals[i].setNumber(i);
    }
    return oldNumbers;
  }

  protected LocalDefs init(DirectedGraph<Unit> graph, Local[] locals, FlowAnalysisMode mode) {
    @SuppressWarnings("unchecked")
    List<Unit>[] unitList = new List[locals.length];
    Arrays.fill(unitList, Collections.emptyList());

    final boolean omitSSA = (mode == FlowAnalysisMode.OmitSSA);
    boolean doFlowAnalsis = omitSSA;

    int units = 0;

    // collect all def points
    for (Unit unit : graph) {
      for (ValueBox box : unit.getDefBoxes()) {
        Value v = box.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          int lno = getLocalNumber(l);

          switch (unitList[lno].size()) {
            case 0:
              unitList[lno] = Collections.singletonList(unit);
              if (omitSSA) {
                units++;
              }
              break;
            case 1:
              if (!omitSSA) {
                units++;
              }
              unitList[lno] = new ArrayList<Unit>(unitList[lno]);
              doFlowAnalsis = true;
              // fallthrough
            default:
              unitList[lno].add(unit);
              units++;
              break;
          }
        }
      }
    }

    if (doFlowAnalsis && mode != FlowAnalysisMode.FlowInsensitive) {
      return new FlowAssignment(graph, locals, unitList, units, omitSSA);
    } else {
      return new StaticSingleAssignment(locals, unitList);
    }
  }

  // Is protected so that in case we have a smarter implementation we can use it.
  protected int getLocalNumber(Local l) {
    return l.getNumber();
  }

  @Override
  public List<Unit> getDefsOfAt(Local l, Unit s) {
    return def.getDefsOfAt(l, s);
  }

  @Override
  public List<Unit> getDefsOf(Local l) {
    return def.getDefsOf(l);
  }
}
