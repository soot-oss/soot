package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.Singletons;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.util.LocalBitSetPacker;

/**
 * A BodyTransformer that attempts to identify and separate uses of a local variable that are independent of each other.
 * Conceptually the inverse transform with respect to the LocalPacker transform. For example the code:
 * {@code for(int i; i < k; i++); for(int i; i < k; i++);} would be transformed into:
 * {@code for(int i; i < k; i++); for(int j; j < k; j++);}
 *
 * @see BodyTransformer
 * @see LocalPacker
 * @see Body
 */
public class LocalSplitter extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(LocalSplitter.class);

  protected ThrowAnalysis throwAnalysis;
  protected boolean omitExceptingUnitEdges;

  public LocalSplitter(Singletons.Global g) {
  }

  public LocalSplitter(ThrowAnalysis ta) {
    this(ta, false);
  }

  public LocalSplitter(ThrowAnalysis ta, boolean omitExceptingUnitEdges) {
    this.throwAnalysis = ta;
    this.omitExceptingUnitEdges = omitExceptingUnitEdges;
  }

  public static LocalSplitter v() {
    return G.v().soot_toolkits_scalar_LocalSplitter();
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    Options o = Options.v();
    if (o.verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Splitting locals...");
    }

    if (o.time()) {
      Timers timers = Timers.v();
      timers.splitTimer.start();
      timers.splitPhase1Timer.start();
    }

    if (throwAnalysis == null) {
      throwAnalysis = Scene.v().getDefaultThrowAnalysis();
      if (throwAnalysis instanceof UnitThrowAnalysis
          && o.throw_analysis() == Options.throw_analysis_auto_select) {
        /*
         * Sadly, the JVM is not smart
         See https://github.com/soot-oss/soot/issues/1951
         
         With the standard UnitThrowAnalysis, soot knows that the definition at (1) is only used at (2), since
         the event handler at (3) cannot be reached before overwriting a at (2).
         Thus, the local splitter splits the variable and 
         uses the split variable at (1) and at the right side of (2), but not at (3).
         
         Input: 
    public static void m1(int)
    {
        unknown a, $stack2, $stack3, $stack4, A, $stack5, a#1;

        a := @parameter0: int; (1)

     label1:
        a = a * 2; (2)
        ...
      
     label2:
        $stack4 := @caughtexception;

        virtualinvoke $stack5.<java.io.PrintStream: void println(int)>(a); (3)

        return;

        catch java.io.IOException from label1 to label2 with label2;
    }
    
    
    Output with UnitThrowAnalysis:
        
    public static void m1(int)
    {
        unknown a, $stack2, $stack3, $stack4, A, $stack5, a#1;

        a#1 := @parameter0: int;

     label1:
        a = a#1 * 2;
        ...
      
     label2:
        $stack4 := @caughtexception;

        virtualinvoke $stack5.<java.io.PrintStream: void println(int)>(a);

        return;

        catch java.io.IOException from label1 to label2 with label2;
    }
    
    Now, the JVM assumes conservatively that the exception handler could be reached
    prior to the definition of a. Thus, we use the pedantic throw analysis which is conservative. 
         */
        
        throwAnalysis = PedanticThrowAnalysis.v();
      }
    }

    if (!omitExceptingUnitEdges) {
      omitExceptingUnitEdges = o.omit_excepting_unit_edges();
    }

    // Pack the locals for efficiency
    final LocalBitSetPacker localPacker = new LocalBitSetPacker(body);
    localPacker.pack();

    // Go through the definitions, building the webs
    ExceptionalUnitGraph graph
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);

    // run in panic mode on first split (maybe change this depending on the input source)
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);
    final LocalUses uses = LocalUses.Factory.newLocalUses(graph, defs);

    if (o.time()) {
      Timers timers = Timers.v();
      timers.splitPhase1Timer.end();
      timers.splitPhase2Timer.start();
    }

    // Collect the set of locals that we need to split
    final BitSet localsToSplit;
    {
      int localCount = localPacker.getLocalCount();
      BitSet localsVisited = new BitSet(localCount);
      localsToSplit = new BitSet(localCount);
      for (Unit s : body.getUnits()) {
        Iterator<ValueBox> defsInUnitItr = s.getDefBoxes().iterator();
        if (defsInUnitItr.hasNext()) {
          Value value = defsInUnitItr.next().getValue();
          if (value instanceof Local) {
            // If we see a local the second time, we know that we must split it
            int localNumber = ((Local) value).getNumber();
            if (localsVisited.get(localNumber)) {
              localsToSplit.set(localNumber);
            } else {
              localsVisited.set(localNumber);
            }
          }
        }
      }
    }

    {
      int w = 0;
      Set<Unit> visited = new HashSet<Unit>();
      for (Unit s : body.getUnits()) {
        Iterator<ValueBox> defsInUnitItr = s.getDefBoxes().iterator();
        if (!defsInUnitItr.hasNext()) {
          continue;
        }
        Value singleDef = defsInUnitItr.next().getValue();
        if (defsInUnitItr.hasNext()) {
          throw new RuntimeException("stmt with more than 1 defbox!");
        }
        // we don't want to visit a node twice
        if (!(singleDef instanceof Local) || visited.remove(s)) {
          continue;
        }

        // always reassign locals to avoid "use before definition" bugs!
        // unfortunately this creates a lot of new locals, so it's important
        // to remove them afterwards
        Local oldLocal = (Local) singleDef;
        if (!localsToSplit.get(oldLocal.getNumber())) {
          continue;
        }

        Local newLocal = (Local) oldLocal.clone();
        String name = newLocal.getName();
        if (name != null) {
          newLocal.setName(name + '#' + (++w)); // renaming should not be done here
        }
        body.getLocals().add(newLocal);

        Deque<Unit> queue = new ArrayDeque<Unit>();
        queue.addFirst(s);
        do {
          final Unit head = queue.removeFirst();
          if (visited.add(head)) {
            for (UnitValueBoxPair use : uses.getUsesOf(head)) {
              ValueBox vb = use.valueBox;
              Value v = vb.getValue();
              if (v == newLocal) {
                continue;
              }
              // should always be true - but who knows ...
              if (v instanceof Local) {
                queue.addAll(defs.getDefsOfAt((Local) v, use.unit));
                vb.setValue(newLocal);
              }
            }

            for (ValueBox vb : head.getDefBoxes()) {
              Value v = vb.getValue();
              if (v instanceof Local) {
                vb.setValue(newLocal);
              }
            }
          }
        } while (!queue.isEmpty());

        // keep the set small
        visited.remove(s);
      }
    }

    // Restore the original local numbering
    localPacker.unpack();

    if (o.time()) {
      Timers timers = Timers.v();
      timers.splitPhase2Timer.end();
      timers.splitTimer.end();
    }
  }
}
