package soot.jbco.jimpleTransformations;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.DoubleType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PatchingChain;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.Rand;
import soot.jimple.AssignStmt;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.Expr;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.NumericConstant;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 *         <p>
 *         Created on 6-Mar-2006
 */

// when shifting, add multiple of 32 or 64 to the shift value, since it will
// have no effect
// shift negatively to confuse things further?
// look into calculating operational cost and limiting to those transforms that
// will
// not hurt the speed of the program. Empirically: 4 adds/shifts == 1 mult?
public class ArithmeticTransformer extends BodyTransformer implements IJbcoTransform {

  private static int mulPerformed = 0;
  private static int divPerformed = 0;
  private static int total = 0;

  public static String dependancies[] = new String[] { "jtp.jbco_cae2bo" };
  public static String name = "jtp.jbco_cae2bo";

  public String[] getDependencies() {
    return dependancies;
  }

  public String getName() {
    return name;
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) {
      return;
    }

    PatchingChain<Unit> units = b.getUnits();

    int localCount = 0;
    Chain<Local> locals = b.getLocals();
    if (output) {
      out.println("*** Performing Arithmetic Transformation on " + b.getMethod().getSignature());
    }

    Iterator<Unit> it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = it.next();
      if (u instanceof AssignStmt) {
        AssignStmt as = (AssignStmt) u;
        Value v = as.getRightOp();
        if (v instanceof MulExpr) {
          total++;

          MulExpr me = (MulExpr) v;
          Value op1 = me.getOp1();
          Value op = null, op2 = me.getOp2();
          NumericConstant nc = null;
          if (op1 instanceof NumericConstant) {
            nc = (NumericConstant) op1;
            op = op2;
          } else if (op2 instanceof NumericConstant) {
            nc = (NumericConstant) op2;
            op = op1;
          }

          if (nc != null) {
            if (output) {
              out.println("Considering: " + as + "\r");
            }

            Type opType = op.getType();
            int max = opType instanceof IntType ? 32 : opType instanceof LongType ? 64 : 0;

            if (max != 0) {
              Object shft_rem[] = checkNumericValue(nc);
              if (shft_rem[0] != null && (Integer) shft_rem[0] < max && Rand.getInt(10) <= weight) {
                List<Unit> unitsBuilt = new ArrayList<>();
                int rand = Rand.getInt(16);
                int shift = (Integer) shft_rem[0];
                boolean neg = (Boolean) shft_rem[2];
                if (rand % 2 == 0) {
                  shift += rand * max;
                } else {
                  shift -= rand * max;
                }

                Expr e;
                if (shft_rem[1] != null) { // if there is an additive floating component
                  Local tmp2 = null, tmp1 = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, opType);
                  locals.add(tmp1);

                  // shift the integral portion
                  Unit newU = Jimple.v().newAssignStmt(tmp1, Jimple.v().newShlExpr(op, IntConstant.v(shift)));
                  unitsBuilt.add(newU);
                  units.insertBefore(newU, u);

                  // grab remainder (that not part of the 2^x)
                  double rem = (Double) shft_rem[1];
                  if (rem != 1) {
                    if (rem == ((int) rem) && opType instanceof IntType) {
                      nc = IntConstant.v((int) rem);
                    } else if (rem == ((long) rem) && opType instanceof LongType) {
                      nc = LongConstant.v((long) rem);
                    } else {
                      nc = DoubleConstant.v(rem);
                    }

                    if (nc instanceof DoubleConstant) {
                      tmp2 = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, DoubleType.v());
                      locals.add(tmp2);

                      newU = Jimple.v().newAssignStmt(tmp2, Jimple.v().newCastExpr(op, DoubleType.v()));
                      unitsBuilt.add(newU);
                      units.insertBefore(newU, u);

                      newU = Jimple.v().newAssignStmt(tmp2, Jimple.v().newMulExpr(tmp2, nc));
                    } else {
                      tmp2 = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, nc.getType());
                      locals.add(tmp2);
                      newU = Jimple.v().newAssignStmt(tmp2, Jimple.v().newMulExpr(op, nc));
                    }
                    unitsBuilt.add(newU);
                    units.insertBefore(newU, u);
                  }
                  if (tmp2 == null) {
                    e = Jimple.v().newAddExpr(tmp1, op);
                  } else if (tmp2.getType().getClass() != tmp1.getType().getClass()) {
                    Local tmp3 = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, tmp2.getType());
                    locals.add(tmp3);

                    newU = Jimple.v().newAssignStmt(tmp3, Jimple.v().newCastExpr(tmp1, tmp2.getType()));
                    unitsBuilt.add(newU);
                    units.insertBefore(newU, u);

                    e = Jimple.v().newAddExpr(tmp3, tmp2);
                  } else {
                    e = Jimple.v().newAddExpr(tmp1, tmp2);
                  }
                } else {
                  e = Jimple.v().newShlExpr(op, IntConstant.v(shift));
                }

                if (e.getType().getClass() != as.getLeftOp().getType().getClass()) {
                  Local tmp = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, e.getType());
                  locals.add(tmp);
                  Unit newU = Jimple.v().newAssignStmt(tmp, e);
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);

                  e = Jimple.v().newCastExpr(tmp, as.getLeftOp().getType());
                }

                as.setRightOp(e);
                unitsBuilt.add(as);
                if (neg) {
                  Unit newU = Jimple.v().newAssignStmt(as.getLeftOp(), Jimple.v().newNegExpr(as.getLeftOp()));
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);
                }

                mulPerformed++;

                printOutput(unitsBuilt);
              }
            }
          }
        } else if (v instanceof DivExpr) {
          total++;
          DivExpr de = (DivExpr) v;
          Value op2 = de.getOp2();
          NumericConstant nc;
          if (op2 instanceof NumericConstant) {
            nc = (NumericConstant) op2;

            Type opType = de.getOp1().getType();
            int max = opType instanceof IntType ? 32 : opType instanceof LongType ? 64 : 0;

            if (max != 0) {
              Object shft_rem[] = checkNumericValue(nc);
              if (shft_rem[0] != null && (shft_rem[1] == null || (Double) shft_rem[1] == 0) && (Integer) shft_rem[0] < max
                  && Rand.getInt(10) <= weight) {
                List<Unit> unitsBuilt = new ArrayList<>();
                int rand = Rand.getInt(16);
                int shift = (Integer) shft_rem[0];
                boolean neg = (Boolean) shft_rem[2];
                if (Rand.getInt() % 2 == 0) {
                  shift += rand * max;
                } else {
                  shift -= rand * max;
                }

                Expr e = Jimple.v().newShrExpr(de.getOp1(), IntConstant.v(shift));

                if (e.getType().getClass() != as.getLeftOp().getType().getClass()) {
                  Local tmp = Jimple.v().newLocal("__tmp_shft_lcl" + localCount++, e.getType());
                  locals.add(tmp);
                  Unit newU = Jimple.v().newAssignStmt(tmp, e);
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);

                  e = Jimple.v().newCastExpr(tmp, as.getLeftOp().getType());
                }

                as.setRightOp(e);
                unitsBuilt.add(as);
                if (neg) {
                  Unit newU = Jimple.v().newAssignStmt(as.getLeftOp(), Jimple.v().newNegExpr(as.getLeftOp()));
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);
                }

                divPerformed++;

                printOutput(unitsBuilt);
              }
            }
          }
        }
      }
    }
  }

  private void printOutput(List<Unit> unitsBuilt) {
    if (!output) {
      return;
    }

    out.println(" after as: ");
    for (Unit uu : unitsBuilt) {
      out.println(
          "\t" + uu + "\ttype : " + (uu instanceof AssignStmt ? ((AssignStmt) uu).getLeftOp().getType().toString() : ""));
    }
  }

  public void outputSummary() {
    if (!output) {
      return;
    }

    out.println("Replaced mul/div expressions: " + (divPerformed + mulPerformed));
    out.println("Total mul/div expressions: " + total);
  }

  private Object[] checkNumericValue(NumericConstant nc) {
    Double dnc = null;
    if (nc instanceof IntConstant) {
      dnc = (double) ((IntConstant) nc).value;
    } else if (nc instanceof DoubleConstant) {
      dnc = ((DoubleConstant) nc).value;
    } else if (nc instanceof FloatConstant) {
      dnc = (double) ((FloatConstant) nc).value;
    } else if (nc instanceof LongConstant) {
      dnc = (double) ((LongConstant) nc).value;
    }

    Object shift[] = new Object[3];
    if (dnc != null) {
      shift[2] = dnc < 0;
      double tmp[] = checkShiftValue(dnc);
      if (tmp[0] != 0) {
        shift[0] = (int) tmp[0];
        if (tmp[1] != 0) {
          shift[1] = tmp[1];
        } else {
          shift[1] = null;
        }
      } else {
        dnc = null;
      }
    }

    if (dnc == null) {
      shift[0] = null;
      shift[1] = null;
    }

    return shift;
  }

  private double[] checkShiftValue(double val) {
    double shift[] = new double[2];
    if (val == 0 || val == 1 || val == -1) {
      shift[0] = 0;
      shift[1] = 0;
    } else {
      double shift_dbl = Math.log(val) / Math.log(2);
      double shift_int = Math.rint(shift_dbl);
      if (shift_dbl == shift_int) {
        shift[1] = 0;
      } else {
        if (Math.pow(2, shift_int) > val) {
          shift_int--;
        }
        shift[1] = val - Math.pow(2, shift_int);
      }
      shift[0] = shift_int;
    }

    return shift;
  }
}
