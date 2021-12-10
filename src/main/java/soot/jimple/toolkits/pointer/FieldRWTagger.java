package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class FieldRWTagger extends BodyTransformer {

  public FieldRWTagger(Singletons.Global g) {
  }

  public static FieldRWTagger v() {
    return G.v().soot_jimple_toolkits_pointer_FieldRWTagger();
  }

  public int numRWs = 0;
  public int numWRs = 0;
  public int numRRs = 0;
  public int numWWs = 0;
  public int numNatives = 0;
  public Date startTime = null;
  boolean optionDontTag = false;
  boolean optionNaive = false;
  private CallGraph cg;

  protected class UniqueRWSets implements Iterable<RWSet> {
    protected final ArrayList<RWSet> l = new ArrayList<RWSet>();

    RWSet getUnique(RWSet s) {
      if (s != null) {
        for (RWSet ret : l) {
          if (ret.isEquivTo(s)) {
            return ret;
          }
        }
        l.add(s);
      }
      return s;
    }

    @Override
    public Iterator<RWSet> iterator() {
      return l.iterator();
    }

    short indexOf(RWSet s) {
      short i = 0;
      for (RWSet ret : l) {
        if (ret.isEquivTo(s)) {
          return i;
        }
        i++;
      }
      return -1;
    }
  }

  protected void initializationStuff(String phaseName) {
    if (G.v().Union_factory == null) {
      G.v().Union_factory = new UnionFactory() {
        @Override
        public Union newUnion() {
          return FullObjectSet.v();
        }
      };
    }
    if (startTime == null) {
      startTime = new Date();
    }
    cg = Scene.v().getCallGraph();
  }

  protected Object keyFor(Stmt s) {
    if (s.containsInvokeExpr()) {
      if (optionNaive) {
        throw new RuntimeException("shouldn't get here");
      }
      Iterator<Edge> it = cg.edgesOutOf(s);
      if (!it.hasNext()) {
        return Collections.emptyList();
      }
      ArrayList<Edge> ret = new ArrayList<Edge>();
      while (it.hasNext()) {
        ret.add(it.next());
      }
      return ret;
    } else {
      return s;
    }
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    initializationStuff(phaseName);
    SideEffectAnalysis sea = new SideEffectAnalysis(DumbPointerAnalysis.v(), Scene.v().getCallGraph());
    sea.findNTRWSets(body.getMethod());
    HashMap<Object, RWSet> stmtToReadSet = new HashMap<Object, RWSet>();
    HashMap<Object, RWSet> stmtToWriteSet = new HashMap<Object, RWSet>();
    UniqueRWSets sets = new UniqueRWSets();
    optionDontTag = PhaseOptions.getBoolean(options, "dont-tag");
    final boolean justDoTotallyConservativeThing = "<clinit>".equals(body.getMethod().getName());
    for (Unit u : body.getUnits()) {
      final Stmt stmt = (Stmt) u;
      if (!stmt.containsInvokeExpr()) {
        continue;
      }
      if (justDoTotallyConservativeThing) {
        stmtToReadSet.put(stmt, sets.getUnique(new FullRWSet()));
        stmtToWriteSet.put(stmt, sets.getUnique(new FullRWSet()));
        continue;
      }
      Object key = keyFor(stmt);
      if (!stmtToReadSet.containsKey(key)) {
        stmtToReadSet.put(key, sets.getUnique(sea.readSet(body.getMethod(), stmt)));
        stmtToWriteSet.put(key, sets.getUnique(sea.writeSet(body.getMethod(), stmt)));
      }
    }
    /*
     * DependenceGraph graph = new DependenceGraph(); for( Iterator outerIt = sets.iterator(); outerIt.hasNext(); ) { final
     * RWSet outer = (RWSet) outerIt.next();
     *
     * for( Iterator innerIt = sets.iterator(); innerIt.hasNext(); ) {
     *
     * final RWSet inner = (RWSet) innerIt.next(); if( inner == outer ) break; if( outer.hasNonEmptyIntersection( inner ) ) {
     * graph.addEdge( sets.indexOf( outer ), sets.indexOf( inner ) ); } } } if( !optionDontTag ) { body.getMethod().addTag(
     * graph ); } for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) { final Stmt stmt = (Stmt)
     * stmtIt.next(); Object key; if( optionNaive && stmt.containsInvokeExpr() ) { key = stmt; } else { key = keyFor( stmt );
     * } RWSet read = (RWSet) stmtToReadSet.get( key ); RWSet write = (RWSet) stmtToWriteSet.get( key ); if( read != null ||
     * write != null ) { DependenceTag tag = new DependenceTag(); if( read != null && read.getCallsNative() ) {
     * tag.setCallsNative(); numNatives++; } else if( write != null && write.getCallsNative() ) { tag.setCallsNative();
     * numNatives++; } tag.setRead( sets.indexOf( read ) ); tag.setWrite( sets.indexOf( write ) ); if( !optionDontTag )
     * stmt.addTag( tag );
     *
     * // The loop below is just fro calculating stats. if( !justDoTotallyConservativeThing ) { for( Iterator innerIt =
     * body.getUnits().iterator(); innerIt.hasNext(); ) { final Stmt inner = (Stmt) innerIt.next(); Object ikey; if(
     * optionNaive && inner.containsInvokeExpr() ) { ikey = inner; } else { ikey = keyFor( inner ); } RWSet innerRead =
     * (RWSet) stmtToReadSet.get( ikey ); RWSet innerWrite = (RWSet) stmtToWriteSet.get( ikey ); if( graph.areAdjacent(
     * sets.indexOf( read ), sets.indexOf( innerWrite ) ) ) numRWs++; if( graph.areAdjacent( sets.indexOf( write ),
     * sets.indexOf( innerRead ) ) ) numWRs++; if( inner == stmt ) continue; if( graph.areAdjacent( sets.indexOf( write ),
     * sets.indexOf( innerWrite ) ) ) numWWs++; if( graph.areAdjacent( sets.indexOf( read ), sets.indexOf( innerRead ) ) )
     * numRRs++; } } } }
     */
  }
}
