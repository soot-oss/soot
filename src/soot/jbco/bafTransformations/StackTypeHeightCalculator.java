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

package soot.jbco.bafTransformations;

import java.util.*;

import soot.*;
import soot.baf.*;
import soot.baf.internal.AbstractOpTypeInst;
import soot.toolkits.graph.*;
/**
 * @author Michael Batchelder 
 * 
 * Created on 3-May-2006 
 */
public class StackTypeHeightCalculator {

  protected class StackEffectSwitch implements InstSwitch {
    
    public boolean shouldThrow = true;
    public HashMap bafToJLocals = null; 
    public Type remove_types[] = null;
    public Type add_types[] = null;
    
    public void caseReturnInst(ReturnInst i){
      remove_types=new Type[]{i.getOpType()};
      add_types=null;
    }
    
    public void caseReturnVoidInst(ReturnVoidInst i){
      remove_types=null;
      add_types=null;
    }
    
    public void caseNopInst(NopInst i){
      remove_types=null;
      add_types=null;
    }
    
    public void caseGotoInst(GotoInst i){
      remove_types=null;
      add_types=null;  
    }
    
    public void caseJSRInst(JSRInst i){
      remove_types=null;
      //add_types=new Type[]{RefType.v()};
      add_types=new Type[]{StmtAddressType.v()};
    }
    
    public void casePushInst(PushInst i) {
      remove_types=null;
      add_types=new Type[]{i.getConstant().getType()};
    }
    
    public void casePopInst(PopInst i){
      remove_types=new Type[]{((soot.baf.internal.BPopInst)i).getType()};
      add_types=null;
    }
    
    public void caseIdentityInst(IdentityInst i){
      remove_types=null;
      add_types=null;
    }
    
    public void caseStoreInst(StoreInst i){
      remove_types=new Type[]{((AbstractOpTypeInst)i).getOpType()};
      add_types=null;
    }
    
    public void caseLoadInst(LoadInst i){
      remove_types = null;
      add_types = null;
      if (bafToJLocals!=null) {
        Local jl = (Local)bafToJLocals.get(i.getLocal());
        if (jl!=null)
          add_types = new Type[]{jl.getType()};
      }
      
      if (add_types == null)
        add_types = new Type[]{i.getOpType()};
    }
    
    public void caseArrayWriteInst(ArrayWriteInst i){
      // RefType replaces the arraytype      
      remove_types = new Type[]{RefType.v(),IntType.v(),i.getOpType()};
      add_types = null;
    }
    
    public void caseArrayReadInst(ArrayReadInst i){
      remove_types = new Type[]{RefType.v(),IntType.v()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseIfNullInst(IfNullInst i){
      remove_types = new Type[]{RefType.v("java.lang.Object")};
      add_types = null;
    }
    
    public void caseIfNonNullInst(IfNonNullInst i){
      remove_types = new Type[]{RefType.v("java.lang.Object")};
      add_types = null;
    }
    
    public void caseIfEqInst(IfEqInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;  
    }
    
    public void caseIfNeInst(IfNeInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;    
    }
    
    public void caseIfGtInst(IfGtInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;    
    }
    
    public void caseIfGeInst(IfGeInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;    
    }
    
    public void caseIfLtInst(IfLtInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;  
    }
    
    public void caseIfLeInst(IfLeInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;    
    }
    
    public void caseIfCmpEqInst(IfCmpEqInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;  
    }
    
    public void caseIfCmpNeInst(IfCmpNeInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;    
    }
    public void caseIfCmpGtInst(IfCmpGtInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;    
    }
    
    public void caseIfCmpGeInst(IfCmpGeInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;    
    }
    
    public void caseIfCmpLtInst(IfCmpLtInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;    
    }
    
    public void caseIfCmpLeInst(IfCmpLeInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = null;    
    }
    
    public void caseStaticGetInst(StaticGetInst i){
      remove_types = null;
      add_types = new Type[]{i.getField().getType()};    
    }
    
    public void caseStaticPutInst(StaticPutInst i){
      remove_types = new Type[]{i.getField().getType()};
      add_types = null;
    }
    
    public void caseFieldGetInst(FieldGetInst i){
      remove_types = new Type[]{i.getField().getDeclaringClass().getType()};
      add_types = new Type[]{i.getField().getType()};
    }
    
    public void caseFieldPutInst(FieldPutInst i){
      remove_types = new Type[]{i.getField().getDeclaringClass().getType(),i.getField().getType()};
      add_types = null;
    }
    
    public void caseInstanceCastInst(InstanceCastInst i){
    	remove_types = new Type[]{RefType.v("java.lang.Object")};
    	add_types = new Type[]{i.getCastType()};
    }
    
    public void caseInstanceOfInst(InstanceOfInst i){
      remove_types = new Type[]{RefType.v("java.lang.Object")}; 
  	  add_types = new Type[]{IntType.v()};  
    }
    
    public void casePrimitiveCastInst(PrimitiveCastInst i){
      remove_types = new Type[]{i.getFromType()};
      add_types = new Type[]{i.getToType()};
    }
    
    public void caseStaticInvokeInst(StaticInvokeInst i){
      SootMethod m = i.getMethod();
      Object args[] = m.getParameterTypes().toArray();
      remove_types = new Type[args.length];
      for (int ii = 0; ii < args.length; ii++)
      	remove_types[ii] = (Type)args[ii];
      
      if (m.getReturnType() instanceof VoidType)
        add_types = null;
      else
        add_types = new Type[]{m.getReturnType()};
    }
    
    private void instanceinvoke(MethodArgInst i) {
      SootMethod m = i.getMethod();

      int length = m.getParameterCount();
      remove_types = new Type[length+1];
      remove_types[0] = RefType.v();
      System.arraycopy(m.getParameterTypes().toArray(),0,remove_types,1,length);

      if (m.getReturnType() instanceof VoidType)
        add_types = null;
      else
        add_types = new Type[]{m.getReturnType()};
    }
    
    public void caseVirtualInvokeInst(VirtualInvokeInst i){
      instanceinvoke((MethodArgInst)i);   
    }
    
    public void caseInterfaceInvokeInst(InterfaceInvokeInst i){
      instanceinvoke((MethodArgInst)i);
    }
    
    public void caseSpecialInvokeInst(SpecialInvokeInst i){
      instanceinvoke((MethodArgInst)i);
    }
    
    public void caseThrowInst(ThrowInst i){
      remove_types = new Type[]{RefType.v("java.lang.Throwable")};
      add_types = null;
    }
    
    public void caseAddInst(AddInst i){
    	remove_types = new Type[]{i.getOpType(),i.getOpType()};
    	add_types = new Type[]{i.getOpType()};
    }
    
    private void bitOps(OpTypeArgInst i) {
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
	  add_types = new Type[]{i.getOpType()};
    }
    public void caseAndInst(AndInst i){
      bitOps(i);
    }
    
    public void caseOrInst(OrInst i){
      bitOps(i);  
    }
    
    public void caseXorInst(XorInst i){
      bitOps(i);  
    }
    
    public void caseArrayLengthInst(ArrayLengthInst i){
      remove_types = new Type[]{RefType.v()};
      add_types = new Type[]{IntType.v()};
    }
    
    public void caseCmpInst(CmpInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{IntType.v()};
    }
    
    public void caseCmpgInst(CmpgInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{IntType.v()};  
    }
    
    public void caseCmplInst(CmplInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{IntType.v()};
    }

    public void caseDivInst(DivInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseIncInst(IncInst i){
      remove_types = null;
      add_types = null;
    }
    
    public void caseMulInst(MulInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};  
    }
    
    public void caseRemInst(RemInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseSubInst(SubInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseShlInst(ShlInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseShrInst(ShrInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseUshrInst(UshrInst i){
      remove_types = new Type[]{i.getOpType(),i.getOpType()};
      add_types = new Type[]{i.getOpType()};
    }
    
    public void caseNewInst(NewInst i){
      remove_types = null;
      add_types = new Type[]{i.getBaseType()};
    }
    
    public void caseNegInst(NegInst i){
      remove_types = null;
      add_types = null;
    }
    
    public void caseSwapInst(SwapInst i){
      remove_types = new Type[]{i.getFromType(),i.getToType()};
      add_types = new Type[]{i.getToType(),i.getFromType()};
    }
    
    public void caseDup1Inst(Dup1Inst i){
      remove_types = new Type[]{i.getOp1Type()};
      add_types = new Type[]{i.getOp1Type(),i.getOp1Type()};
    }
    
    public void caseDup2Inst(Dup2Inst i){
      if (!(i.getOp1Type() instanceof DoubleType 
             || i.getOp1Type() instanceof LongType)) {
        add_types = new Type[]{i.getOp2Type(),i.getOp1Type()};
        remove_types = null;
      } else {        
        add_types = new Type[]{i.getOp1Type()};
        remove_types = null;
      }
    }
    
    public void caseDup1_x1Inst(Dup1_x1Inst i){
      remove_types = new Type[]{i.getUnder1Type(),i.getOp1Type()};
      add_types = new Type[]{i.getOp1Type(),i.getUnder1Type(),i.getOp1Type()};
    }
    
    public void caseDup1_x2Inst(Dup1_x2Inst i){
      Type u1 = i.getUnder1Type();
      if (u1 instanceof DoubleType || u1 instanceof LongType) {
        remove_types = new Type[]{u1,i.getOp1Type()};
        add_types = new Type[]{i.getOp1Type(),u1,i.getOp1Type()};
      } else {
        remove_types = new Type[]{i.getUnder2Type(),u1,i.getOp1Type()};
        add_types = new Type[]{i.getOp1Type(),i.getUnder2Type(),u1,i.getOp1Type()};
      }
    }
    
    public void caseDup2_x1Inst(Dup2_x1Inst i){
      Type ot = i.getOp1Type();
      if (ot instanceof DoubleType || ot instanceof LongType) {
        remove_types = new Type[]{i.getUnder1Type(), ot};
        add_types = new Type[]{ot,i.getUnder1Type(),ot};
      } else {
        remove_types = new Type[]{i.getUnder1Type(),i.getOp2Type(),ot};
        add_types = new Type[]{i.getOp2Type(),ot,i.getUnder1Type(),i.getOp2Type(),ot};
      }
    }
    
    public void caseDup2_x2Inst(Dup2_x2Inst i){
      Type u1 = i.getUnder1Type();
      Type o1 = i.getOp1Type();
      if (u1 instanceof DoubleType || u1 instanceof LongType) {
        if (o1 instanceof DoubleType || o1 instanceof LongType) {
          remove_types = new Type[]{u1,o1};
          add_types = new Type[]{o1,u1,o1};
        } else {
          remove_types = new Type[]{u1,i.getOp2Type(),o1};
          add_types = new Type[]{i.getOp2Type(),o1,u1,i.getOp2Type(),o1};
        }
      } else if (o1 instanceof DoubleType || o1 instanceof LongType) {
        remove_types = new Type[]{i.getUnder2Type(),u1,o1};
        add_types = new Type[]{o1,i.getUnder2Type(),u1,o1};
      } else {
        remove_types = new Type[]{i.getUnder2Type(),u1,i.getOp2Type(),o1};
        add_types = new Type[]{i.getOp2Type(),o1,i.getUnder2Type(),u1,i.getOp2Type(),o1};
      }
    } 
    
    public void caseNewArrayInst(NewArrayInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = new Type[]{RefType.v()};
    }
    
    public void caseNewMultiArrayInst(NewMultiArrayInst i){
      remove_types = new Type[i.getDimensionCount()];
      for (int ii = 0; ii < remove_types.length; ii++)
        remove_types[ii] = IntType.v();
      add_types = new Type[]{RefType.v()};
    }
    
    public void caseLookupSwitchInst(LookupSwitchInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;
    }
    
    public void caseTableSwitchInst(TableSwitchInst i){
      remove_types = new Type[]{IntType.v()};
      add_types = null;
    }
    
    public void caseEnterMonitorInst(EnterMonitorInst i){
      remove_types = new Type[]{RefType.v("java.lang.Object")};
      add_types = null;
    }
    
    public void caseExitMonitorInst(ExitMonitorInst i){
      remove_types = new Type[]{RefType.v("java.lang.Object")};
      add_types = null;
    }    
  }
  
  public static StackEffectSwitch sw = new StackTypeHeightCalculator().new StackEffectSwitch();
  public static BriefUnitGraph bug = null;
  
  public static HashMap calculateStackHeights(Body b, HashMap b2JLocs) {
    sw.bafToJLocals = b2JLocs;
    return calculateStackHeights(b,true);
  }
  
  public static HashMap calculateStackHeights(Body b) {
    sw.bafToJLocals = null;
    return calculateStackHeights(b,false);
  }
  
  public static HashMap calculateStackHeights(Body b, boolean jimpleLocals) {
    if (!(b instanceof BafBody)) 
      throw new java.lang.RuntimeException("Expecting Baf Body");
    //System.out.println("\n"+b.getMethod().getName());
    
    HashMap results = new HashMap();
    bug = new BriefUnitGraph(b);
    List heads = bug.getHeads();
    for (int i = 0; i < heads.size(); i++)
    {
      Unit h = (Unit)heads.get(i);
      RefType handlerExc = isHandlerUnit(b.getTraps(),h);
      Stack stack = (Stack)results.get(h);
      if (stack != null) {
        if (stack.size() != (handlerExc!=null ? 1 : 0))
          throw new java.lang.RuntimeException("Problem with stack height - head expects ZERO or one if handler");
        continue;
      }
        
      ArrayList worklist = new ArrayList();
      stack = new Stack();
      if (handlerExc!=null)
        stack.push(handlerExc);
      results.put(h,stack);
      worklist.add(h);
      while (!worklist.isEmpty()) {
        Inst inst = (Inst)worklist.remove(0);
        
        inst.apply(sw);

        try {
          stack = updateStack(sw,(Stack)results.get(inst));
        } catch (RuntimeException rexc) {
          printStack(b.getUnits(),results,false);
          System.exit(1);
        }
        Iterator lit = bug.getSuccsOf(inst).iterator();
        while (lit.hasNext()) {
          Unit next = (Unit)lit.next();
          Stack nxtStck = (Stack)results.get(next);
          if (nxtStck != null) {
            if (nxtStck.size() != stack.size()) {
              printStack(b.getUnits(),results,false);
              throw new java.lang.RuntimeException("Problem with stack height at: "+next + "\n\rHas Stack "+nxtStck+" but is expecting "+stack);
            }
            continue;
          }
          
          results.put(next,stack);
          worklist.add(next);
        }
      }
    }
    
    return results;
  }

  public static Stack updateStack(Unit u, Stack st) {
    u.apply(sw);
    return updateStack(sw,st);
  }
  
  public static Stack updateStack(StackEffectSwitch sw, Stack st) {
    Stack clone = (Stack)st.clone();
    
    if (sw.remove_types != null) {
	    if (sw.remove_types.length > clone.size()) {
          String exc = "Expecting values on stack: ";
          for (int i = 0; i < sw.remove_types.length; i++) {
            String type = sw.remove_types[i].toString();
            if (type.trim().length() == 0) type = sw.remove_types[i] instanceof RefLikeType ? "L" : "U";
            
            exc += type + "  ";
          }
          exc += "\n\tbut only found: "; 
          for (int i = 0; i < clone.size(); i++) {
            String type = clone.get(i).toString();
            if (type.trim().length() == 0) type = clone.get(i) instanceof RefLikeType ? "L" : "U";
          
            exc += type + "  ";
          }
          
          if (sw.shouldThrow)
            throw new RuntimeException(exc);
          else
            G.v().out.println(exc);
        }
	    for (int i = sw.remove_types.length - 1; i >= 0; i--) {
	      try {
            Type t = (Type)clone.pop();
         
            if (!checkTypes(t,sw.remove_types[i])) {
              //System.out.println("Incompatible types: " + t + "  :  "+sw.remove_types[i]);
            }
          } catch (Exception exc) {
            return null;
          }
	    }
    }
    
    if (sw.add_types != null)
	  for (int i = 0; i < sw.add_types.length; i++)
	    clone.push(sw.add_types[i]);
    
    return clone;
  }
  
  private static boolean checkTypes(Type t1, Type t2) {
    if (t1 == t2) 
      return true;
    
    if (t1 instanceof RefLikeType && t2 instanceof RefLikeType)
      return true;
    
    if (t1 instanceof IntegerType && t2 instanceof IntegerType) 
      return true;
    
    if (t1 instanceof LongType && t2 instanceof LongType)
      return true;
    
    if (t1 instanceof DoubleType && t2 instanceof DoubleType)
      return true;
    
    if (t1 instanceof FloatType && t2 instanceof FloatType)
      return true;
    
    return false;
  }
  
  public static void printStack(PatchingChain units, HashMap stacks, boolean before) {
    
    int count = 0;
    sw.shouldThrow = false;
    HashMap indexes = new HashMap();
    Iterator it = units.snapshotIterator();
    while (it.hasNext())
      indexes.put(it.next(),new Integer(count++));
    it = units.snapshotIterator();
    while (it.hasNext()) {
      String s = "";
      Object o = it.next();
      if (o instanceof TargetArgInst) {
        Object t = ((TargetArgInst)o).getTarget();
        s = indexes.get(t).toString();
      } else if (o instanceof TableSwitchInst) {
        TableSwitchInst tswi = (TableSwitchInst)o;
        s+= "\r\tdefault: " + tswi.getDefaultTarget() + "  "+indexes.get(tswi.getDefaultTarget());
        int index = 0;
        for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++)
          s+= "\r\t "+x+": " + tswi.getTarget(index) + "  "+indexes.get(tswi.getTarget(index++));
      }
      try {
        s = indexes.get(o) + " " + o + "  " + s + "   [";
      } catch (Exception e) {
        G.v().out.println("Error in StackTypeHeightCalculator trying to find index of unit");
      }
      Stack stack = (Stack)stacks.get(o);
      if (stack != null) {
        if (!before) {
          ((Unit)o).apply(sw);
          stack = updateStack(sw,stack);
          if (stack == null) {
            soot.jbco.util.Debugger.printUnits(units, " StackTypeHeightCalc failed");
            sw.shouldThrow = true;
            return;
          }
        }
        for (int i = 0; i < stack.size(); i++)
          s += printType((Type)stack.get(i));
      } else s+="***missing***";
      System.out.println(s+"]");  
    }
    sw.shouldThrow = true;
  }
  
  private static String printType(Type t) {
    if (t instanceof IntegerType) {
      return "I";
    } else if (t instanceof FloatType) { 
      return "F";
    } else if (t instanceof DoubleType) {
      return "D";
    } else if (t instanceof LongType) {
      return "J";
    } else if (t instanceof RefLikeType) {
      
      //if (t instanceof RefType && ((RefType)t).getSootClass() != null)
      //  return "L(" + ((RefType)t).getSootClass().getName()+")";
      //else
        return "L"+t.toString();
    } else {
      return "U("+t.getClass().toString()+")";
    }
  }
  
  private static RefType isHandlerUnit(soot.util.Chain traps, Unit h) {
    Iterator it = traps.iterator();
    while (it.hasNext()) {
      Trap t = (Trap)it.next();
      if (t.getHandlerUnit() == h)
        return t.getException().getType();
    }
    return null;
  }
  
  public static Stack getAfterStack(Body b, Unit u) {
    Stack stack = (Stack)calculateStackHeights(b).get(u);
    u.apply(sw);
    return updateStack(sw,stack);
  }
  
  public static Stack getAfterStack(Stack beforeStack, Unit u) {
    u.apply(sw);
    return updateStack(sw,beforeStack);
  }
}
