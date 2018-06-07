package soot.jimple.toolkits.ide.exampleproblems;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Eric Bodden and others
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

import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Gen;
import heros.flowfunc.Identity;
import heros.flowfunc.Kill;
import heros.flowfunc.KillAll;
import heros.flowfunc.Transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;

public class IFDSLocalInfoFlow extends DefaultJimpleIFDSTabulationProblem<Local, InterproceduralCFG<Unit, SootMethod>> {

  public IFDSLocalInfoFlow(InterproceduralCFG<Unit, SootMethod> icfg) {
    super(icfg);
  }

  public FlowFunctions<Unit, Local, SootMethod> createFlowFunctionsFactory() {
    return new FlowFunctions<Unit, Local, SootMethod>() {

      @Override
      public FlowFunction<Local> getNormalFlowFunction(Unit src, Unit dest) {
        if (src instanceof IdentityStmt && interproceduralCFG().getMethodOf(src) == Scene.v().getMainMethod()) {
          IdentityStmt is = (IdentityStmt) src;
          Local leftLocal = (Local) is.getLeftOp();
          Value right = is.getRightOp();
          if (right instanceof ParameterRef) {
            return new Gen<Local>(leftLocal, zeroValue());
          }
        }

        if (src instanceof AssignStmt) {
          AssignStmt assignStmt = (AssignStmt) src;
          Value right = assignStmt.getRightOp();
          if (assignStmt.getLeftOp() instanceof Local) {
            final Local leftLocal = (Local) assignStmt.getLeftOp();
            if (right instanceof Local) {
              final Local rightLocal = (Local) right;
              return new Transfer<Local>(leftLocal, rightLocal);
            } else {
              return new Kill<Local>(leftLocal);
            }
          }
        }
        return Identity.v();
      }

      @Override
      public FlowFunction<Local> getCallFlowFunction(Unit src, final SootMethod dest) {
        Stmt s = (Stmt) src;
        InvokeExpr ie = s.getInvokeExpr();
        final List<Value> callArgs = ie.getArgs();
        final List<Local> paramLocals = new ArrayList<Local>();
        for (int i = 0; i < dest.getParameterCount(); i++) {
          paramLocals.add(dest.getActiveBody().getParameterLocal(i));
        }
        return new FlowFunction<Local>() {
          public Set<Local> computeTargets(Local source) {
            // ignore implicit calls to static initializers
            if (dest.getName().equals(SootMethod.staticInitializerName) && dest.getParameterCount() == 0) {
              return Collections.emptySet();
            }

            Set<Local> taintsInCaller = new HashSet<Local>();
            for (int i = 0; i < callArgs.size(); i++) {
              if (callArgs.get(i).equivTo(source)) {
                taintsInCaller.add(paramLocals.get(i));
              }
            }
            return taintsInCaller;
          }
        };
      }

      @Override
      public FlowFunction<Local> getReturnFlowFunction(Unit callSite, SootMethod callee, Unit exitStmt, Unit retSite) {
        if (exitStmt instanceof ReturnStmt) {
          ReturnStmt returnStmt = (ReturnStmt) exitStmt;
          Value op = returnStmt.getOp();
          if (op instanceof Local) {
            if (callSite instanceof DefinitionStmt) {
              DefinitionStmt defnStmt = (DefinitionStmt) callSite;
              Value leftOp = defnStmt.getLeftOp();
              if (leftOp instanceof Local) {
                final Local tgtLocal = (Local) leftOp;
                final Local retLocal = (Local) op;
                return new FlowFunction<Local>() {

                  public Set<Local> computeTargets(Local source) {
                    if (source == retLocal) {
                      return Collections.singleton(tgtLocal);
                    }
                    return Collections.emptySet();
                  }

                };
              }
            }
          }
        }
        return KillAll.v();
      }

      @Override
      public FlowFunction<Local> getCallToReturnFlowFunction(Unit call, Unit returnSite) {
        return Identity.v();
      }
    };
  }

  @Override
  public Local createZeroValue() {
    return new JimpleLocal("zero", NullType.v());
  }

  @Override
  public Map<Unit, Set<Local>> initialSeeds() {
    return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()),
        zeroValue());
  }
}
