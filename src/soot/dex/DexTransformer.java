package soot.dex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public abstract class DexTransformer extends BodyTransformer {

  /**
   * Collect definitions of l in body including the definitions of aliases of l.
   *
   * In this context an alias is a local that propagates its value to l.
   *
   * @param l the local whose definitions are to collect
   * @param localDefs the LocalDefs object
   * @param body the body that contains the local
   */
  protected List<Unit> collectDefinitionsWithAliases(Local l, LocalDefs localDefs, LocalUses localUses, Body body) {
      Set<Local> seenLocals = new HashSet<Local>();
      Stack<Local> newLocals = new Stack<Local>();
      List<Unit> defs = new LinkedList<Unit>();
      newLocals.push(l);

      while (!newLocals.empty()) {
          Local local = newLocals.pop();
          System.out.println("[null local] "+ local);
          if (seenLocals.contains(local))
              continue;
          for (Unit u : collectDefinitions(local, localDefs, body)) {
              if (u instanceof AssignStmt) {
                  Value r = ((AssignStmt) u).getRightOp();
                  if (r instanceof Local && ! seenLocals.contains((Local) r))
                      newLocals.push((Local) r);
              }
              defs.add(u);
              //
              for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                Unit unit = pair.getUnit();
                if (unit instanceof AssignStmt) {
                  Value right = ((AssignStmt) unit).getRightOp();
                  Value left = ((AssignStmt) unit).getLeftOp();
                  if (right == local  && left instanceof Local && ! seenLocals.contains((Local) left))
                      newLocals.push((Local) left);
                }
              }
              //
          }
          seenLocals.add(local);
      }
      return defs;
  }

  /**
   * Convenience method that collects all definitions of l.
   *
   * @param l the local whose definitions are to collect
   * @param localDefs the LocalDefs object
   * @param body the body that contains the local
   */
  private List<Unit> collectDefinitions(Local l, LocalDefs localDefs, Body body) {
      List <Unit> defs = new LinkedList<Unit>();
      for (Unit u : body.getUnits()) {
          List<Unit> defsOf = localDefs.getDefsOfAt(l, u);
          if (defsOf != null)
              defs.addAll(defsOf);
      }
      for (Unit u: defs) {
        System.out.println("[add def] "+ u);
      }
      return defs;
  }
}
