/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.jimpleTransformations;

import java.util.*;

import soot.*;
import soot.jbco.*;
import soot.util.*;
import soot.jimple.*;
import soot.BodyTransformer;
import soot.jbco.util.*;


/**
 * @author Michael Batchelder
 * 
 * Created on 6-Mar-2006
 */

// when shifting, add multiple of 32 or 64 to the shift value, since it will
// have no effect
// shift negatively to confuse things further?
// look into calculating operational cost and limiting to those transforms that
// will
// not hurt the speed of the program. Empirically: 4 adds/shifts == 1 mult?
public class ArithmeticTransformer extends BodyTransformer implements
    IJbcoTransform {

  private static int mulPerformed = 0;

  private static int divPerformed = 0;

  private static int total = 0;

  public static String dependancies[] = new String[] {"jtp.jbco_cae2bo"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "jtp.jbco_cae2bo";
  
  public String getName() {
    return name;
  }

  protected void internalTransform(Body b, String phaseName, Map options) 
  {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    PatchingChain units = b.getUnits();

    int localCount = 0;
    Chain locals = b.getLocals();
    if (output)
      out.println("*** Performing Arithmetic Transformation on "
          + b.getMethod().getSignature());

    Iterator it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = (Unit) it.next();
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
            if (output)
              out.println("Considering: " + as + "\r");

            Type opType = op.getType();
            int max = opType instanceof IntType ? 32
                : opType instanceof LongType ? 64 : 0;

            if (max != 0) {
              Object shft_rem[] = checkNumericValue(nc);
              if (shft_rem[0] != null
                  && ((Integer) shft_rem[0]).intValue() < max
                  && Rand.getInt(10) <= weight) {
                List<Unit> unitsBuilt = new ArrayList<Unit>();
                int rand = Rand.getInt(16);
                int shift = ((Integer) shft_rem[0]).intValue();
                boolean neg = ((Boolean) shft_rem[2]).booleanValue();
                if (rand % 2 == 0) {
                  shift += rand * max;
                } else {
                  shift -= rand * max;
                }

                Expr e = null;
                if (shft_rem[1] != null) { // if there is an additive floating
                                            // component
                  Local tmp2 = null, tmp1 = Jimple.v().newLocal(
                      "__tmp_shft_lcl" + localCount++, opType);
                  locals.add(tmp1);

                  // shift the integral portion
                  Unit newU = Jimple.v().newAssignStmt(tmp1,
                      Jimple.v().newShlExpr(op, IntConstant.v(shift)));
                  unitsBuilt.add(newU);
                  units.insertBefore(newU, u);

                  // grab remainder (that not part of the 2^x)
                  double rem = ((Double) shft_rem[1]).doubleValue();
                  if (rem != 1) {
                    if (rem == ((int) rem) && opType instanceof IntType)
                      nc = IntConstant.v((int) rem);
                    else if (rem == ((long) rem) && opType instanceof LongType)
                      nc = LongConstant.v((long) rem);
                    else
                      nc = DoubleConstant.v(rem);

                    if (nc instanceof DoubleConstant
                        && !(opType instanceof DoubleType)) {
                      tmp2 = Jimple.v().newLocal(
                          "__tmp_shft_lcl" + localCount++, DoubleType.v());
                      locals.add(tmp2);

                      newU = Jimple.v().newAssignStmt(tmp2,
                          Jimple.v().newCastExpr(op, DoubleType.v()));
                      unitsBuilt.add(newU);
                      units.insertBefore(newU, u);

                      newU = Jimple.v().newAssignStmt(tmp2,
                          Jimple.v().newMulExpr(tmp2, nc));
                    } else {
                      tmp2 = Jimple.v().newLocal(
                          "__tmp_shft_lcl" + localCount++, nc.getType());
                      locals.add(tmp2);
                      newU = Jimple.v().newAssignStmt(tmp2,
                          Jimple.v().newMulExpr(op, nc));
                    }
                    unitsBuilt.add(newU);
                    units.insertBefore(newU, u);
                  }
                  if (tmp2 == null) {
                    e = Jimple.v().newAddExpr(tmp1, op);
                  } else if (tmp2.getType().getClass() != tmp1.getType()
                      .getClass()) {
                    Local tmp3 = Jimple.v().newLocal(
                        "__tmp_shft_lcl" + localCount++, tmp2.getType());
                    locals.add(tmp3);

                    newU = Jimple.v().newAssignStmt(tmp3,
                        Jimple.v().newCastExpr(tmp1, tmp2.getType()));
                    unitsBuilt.add(newU);
                    units.insertBefore(newU, u);

                    e = Jimple.v().newAddExpr(tmp3, tmp2);
                  } else {
                    e = Jimple.v().newAddExpr(tmp1, tmp2);
                  }
                } else {
                  e = Jimple.v().newShlExpr(op, IntConstant.v(shift));
                }

                if (e.getType().getClass() != as.getLeftOp().getType()
                    .getClass()) {
                  Local tmp = Jimple.v().newLocal(
                      "__tmp_shft_lcl" + localCount++, e.getType());
                  locals.add(tmp);
                  Unit newU = Jimple.v().newAssignStmt(tmp, e);
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);

                  e = Jimple.v().newCastExpr(tmp, as.getLeftOp().getType());
                }

                as.setRightOp(e);
                unitsBuilt.add(as);
                if (neg) {
                  Unit newU = Jimple.v().newAssignStmt(as.getLeftOp(),
                      Jimple.v().newNegExpr(as.getLeftOp()));
                  unitsBuilt.add(newU);
                  units.insertAfter(newU, u);
                }

                mulPerformed++;

                if (output) {
                  System.out.println(" after as: ");
                  Iterator<Unit> ait = unitsBuilt.iterator();
                  while (ait.hasNext()) {
                    Unit uu = ait.next();
                    System.out.println("\t"
                        + uu
                        + "\ttype : "
                        + (uu instanceof AssignStmt ? ((AssignStmt) uu)
                            .getLeftOp().getType().toString() : ""));
                  }
                }
              }
            }
          }
        } else if (v instanceof DivExpr) {
          total++;
          DivExpr de = (DivExpr) v;
          Value op2 = de.getOp2();
          NumericConstant nc = null;
          if (op2 instanceof NumericConstant) {
            nc = (NumericConstant) op2;

            if (nc != null) {
              Type opType = de.getOp1().getType();
              int max = opType instanceof IntType ? 32
                  : opType instanceof LongType ? 64 : 0;

              if (max != 0) {
                Object shft_rem[] = checkNumericValue(nc);
                if (shft_rem[0] != null
                    && ((Integer) shft_rem[0]).intValue() < max
                    && Rand.getInt(10) <= weight) {
                  List<Unit> unitsBuilt = new ArrayList<Unit>();
                  int rand = Rand.getInt(16);
                  int shift = ((Integer) shft_rem[0]).intValue();
                  boolean neg = ((Boolean) shft_rem[2]).booleanValue();
                  if (Rand.getInt() % 2 == 0) {
                    shift += rand * max;
                  } else {
                    shift -= rand * max;
                  }

                  Expr e = Jimple.v().newShrExpr(de.getOp1(),
                      IntConstant.v(shift));

                  if (e.getType().getClass() != as.getLeftOp().getType()
                      .getClass()) {
                    Local tmp = Jimple.v().newLocal(
                        "__tmp_shft_lcl" + localCount++, e.getType());
                    locals.add(tmp);
                    Unit newU = Jimple.v().newAssignStmt(tmp, e);
                    unitsBuilt.add(newU);
                    units.insertAfter(newU, u);

                    e = Jimple.v().newCastExpr(tmp, as.getLeftOp().getType());
                  }

                  as.setRightOp(e);
                  unitsBuilt.add(as);
                  if (neg) {
                    Unit newU = Jimple.v().newAssignStmt(as.getLeftOp(),
                        Jimple.v().newNegExpr(as.getLeftOp()));
                    unitsBuilt.add(newU);
                    units.insertAfter(newU, u);
                  }

                  divPerformed++;

                  if (output) {
                    System.out.println(" after as: ");
                    Iterator<Unit> ait = unitsBuilt.iterator();
                    while (ait.hasNext()) {
                      Unit uu = ait.next();
                      System.out.println("\t"
                          + uu
                          + "\ttype : "
                          + (uu instanceof AssignStmt ? ((AssignStmt) uu)
                              .getLeftOp().getType().toString() : ""));
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public void outputSummary() {
    out.println("Replaced mul/div expressions: "
        + (divPerformed + mulPerformed));
    out.println("Total mul/div expressions: " + total);
  }

  private Object[] checkNumericValue(NumericConstant nc) {
    Double d = null;
    Object shift[] = new Object[3];
    if (nc instanceof IntConstant) {
      d = new Double(((IntConstant) nc).value);
    } else if (nc instanceof DoubleConstant) {
      d = new Double(((DoubleConstant) nc).value);
    } else if (nc instanceof FloatConstant) {
      d = new Double(((FloatConstant) nc).value);
    } else if (nc instanceof LongConstant) {
      d = new Double(((LongConstant) nc).value);
    }

    if (d != null) {
      shift[2] = new Boolean(d.doubleValue() < 0);
      double tmp[] = checkShiftValue(d.doubleValue());
      if (tmp[0] != 0) {
        shift[0] = new Integer((int) tmp[0]);
        if (tmp[1] != 0)
          shift[1] = new Double(tmp[1]);
        else
          shift[1] = null;
      } else
        d = null;
    }

    if (d == null) {
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
        if (Math.pow(2, shift_int) > val)
          shift_int--;
        shift[1] = val - Math.pow(2, shift_int);
      }
      shift[0] = shift_int;
    }

    return shift;
  }
}