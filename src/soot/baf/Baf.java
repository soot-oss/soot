/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot.baf;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.baf.internal.*;
import java.util.*;

public class Baf
{
    public Baf( Singletons.Global g ) {}
    public static Baf v() { return G.v().soot_baf_Baf(); }

    public static Type getDescriptorTypeOf(Type opType) 
    {        
        if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
            opType = RefType.v();
        
        return opType;
    }

    /**
        Constructs a Local with the given name and type.
    */

    public Local newLocal(String name, Type t)
    {
        return new BafLocal(name, t);
    }

    /**
        Constructs a new BTrap for the given exception on the given Unit range with the given Unit handler.
    */

    public Trap newTrap(SootClass exception, Unit beginUnit, Unit endUnit, Unit handlerUnit)
    {
        return new BTrap(exception, beginUnit, endUnit, handlerUnit);
    }

    /**
        Constructs a ExitMonitorInst() grammar chunk
     */

    public ExitMonitorInst newExitMonitorInst()
    {
        return new BExitMonitorInst();
    }


    /**
        Constructs a EnterMonitorInst() grammar chunk.
     */

    public EnterMonitorInst newEnterMonitorInst()
    {
        return new BEnterMonitorInst();
    }

    public ReturnVoidInst newReturnVoidInst()
    {
        return new BReturnVoidInst();
    }
    
    public NopInst newNopInst()
    {
        return new BNopInst();
    }

    public GotoInst newGotoInst(Unit unit)
    {
        return new BGotoInst(unit);
    }
    
    public JSRInst newJSRInst(Unit unit)
    {
      	return new BJSRInst(unit);
    }

    public PlaceholderInst newPlaceholderInst(Unit source)
    {
        return new PlaceholderInst(source);
    }
        
    public UnitBox newInstBox(Unit unit)
    {
        return new InstBox((Inst) unit);
    }
    
    public PushInst newPushInst(Constant c)
    {
        return new BPushInst(c);
    }
    
    public IdentityInst newIdentityInst(Value local, Value identityRef)
    {
        return new BIdentityInst(local, identityRef);
    }

    public ValueBox newLocalBox(Value value)
    {
        return new BafLocalBox(value);
    }
    
    public ValueBox newIdentityRefBox(Value value)
    {
        return new IdentityRefBox(value);
    }

    
    /**
        Constructs a ThisRef(RefType) grammar chunk.
     */

    public ThisRef newThisRef(RefType t)
    {
        return new ThisRef(t);
    }
    
    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */

    public ParameterRef newParameterRef(Type paramType, int number)
    {
        return new ParameterRef(paramType, number);
    }
    
    public StoreInst newStoreInst(Type opType, Local l)
    {
        return new BStoreInst(opType, l);
    }
    
    public LoadInst newLoadInst(Type opType, Local l)
    {
        return new BLoadInst(opType, l);
    }

    public ArrayWriteInst newArrayWriteInst(Type opType)
    {
        return new BArrayWriteInst(opType);
    }
    
    public ArrayReadInst newArrayReadInst(Type opType)
    {
        return new BArrayReadInst(opType);
    }

    public StaticGetInst newStaticGetInst(SootFieldRef fieldRef)
    {
        return new BStaticGetInst(fieldRef);
    }
    
    public StaticPutInst newStaticPutInst(SootFieldRef fieldRef)
    {
        return new BStaticPutInst(fieldRef);
    }

    public FieldGetInst newFieldGetInst(SootFieldRef fieldRef)
    {
        return new BFieldGetInst(fieldRef);
    }
    
    public FieldPutInst newFieldPutInst(SootFieldRef fieldRef)
    {
        return new BFieldPutInst(fieldRef);
    }
    
    public AddInst newAddInst(Type opType)
    {
        return new BAddInst(opType);
    }

    public PopInst newPopInst(Type aType) 
    {
        return new BPopInst(aType);
    }

    public SubInst newSubInst(Type opType)
    {
        return new BSubInst(opType);
    }

    public MulInst newMulInst(Type opType)
    {
        return new BMulInst(opType);
    }

    public DivInst newDivInst(Type opType)
    {
        return new BDivInst(opType);
    }

    public AndInst newAndInst(Type opType)
    {
        return new BAndInst(opType);
    }

    public ArrayLengthInst newArrayLengthInst()
    {
        return new BArrayLengthInst();
    }

    public NegInst newNegInst(Type opType)
    {
        return new BNegInst(opType);
    }

    public OrInst newOrInst(Type opType)
    {
        return new BOrInst(opType);
    }

    public RemInst newRemInst(Type opType)
    {
        return new BRemInst(opType);
    }

    public ShlInst newShlInst(Type opType)
    {
        return new BShlInst(opType);
    }





    public ShrInst newShrInst(Type opType)
    {
        return new BShrInst(opType);
    }

    public UshrInst newUshrInst(Type opType)
    {
        return new BUshrInst(opType);
    }

    public XorInst newXorInst(Type opType)
    {
        return new BXorInst(opType);
    }

    public InstanceCastInst newInstanceCastInst(Type opType)
    {
        return new BInstanceCastInst(opType);
    }

    public InstanceOfInst newInstanceOfInst(Type opType)
    {
        return new BInstanceOfInst(opType);
    }

    public PrimitiveCastInst newPrimitiveCastInst(Type fromType, Type toType)
    {
        return new BPrimitiveCastInst(fromType, toType);
    }

    public NewInst newNewInst(RefType opType)
    {
        return new BNewInst(opType);
    }

    public NewArrayInst newNewArrayInst(Type opType)
    {
        return new BNewArrayInst(opType);
    }

    public NewMultiArrayInst newNewMultiArrayInst(ArrayType opType, int dimensions)
    {
        return new BNewMultiArrayInst(opType, dimensions);
    }

    public DynamicInvokeInst newDynamicInvokeInst(SootMethodRef bsmMethodRef, List<Value> bsmArgs, SootMethodRef methodRef)
    {
    	return new BDynamicInvokeInst(bsmMethodRef,bsmArgs,methodRef);
    }
    
    public StaticInvokeInst newStaticInvokeInst(SootMethodRef methodRef)
    {
        return new BStaticInvokeInst(methodRef);
    }

    public SpecialInvokeInst newSpecialInvokeInst(SootMethodRef methodRef)
    {
        return new BSpecialInvokeInst(methodRef);
    }

    public VirtualInvokeInst newVirtualInvokeInst(SootMethodRef methodRef)
    {
        return new BVirtualInvokeInst(methodRef);
    }

    public InterfaceInvokeInst newInterfaceInvokeInst(SootMethodRef methodRef, int argCount)
    {
        return new BInterfaceInvokeInst(methodRef, argCount);
    }

    public ReturnInst newReturnInst(Type opType)
    {
        return new BReturnInst(opType);
    }

    public IfCmpEqInst newIfCmpEqInst(Type opType, Unit unit)
    {
        return new BIfCmpEqInst(opType, unit);
    }

    public IfCmpGeInst newIfCmpGeInst(Type opType, Unit unit)
    {
        return new BIfCmpGeInst(opType, unit);
    }

    public IfCmpGtInst newIfCmpGtInst(Type opType, Unit unit)
    {
        return new BIfCmpGtInst(opType, unit);
    }

    public IfCmpLeInst newIfCmpLeInst(Type opType, Unit unit)
    {
        return new BIfCmpLeInst(opType, unit);
    }

    public IfCmpLtInst newIfCmpLtInst(Type opType, Unit unit)
    {
        return new BIfCmpLtInst(opType, unit);
    }

    public IfCmpNeInst newIfCmpNeInst(Type opType, Unit unit)
    {
        return new BIfCmpNeInst(opType, unit);
    }

    public CmpInst newCmpInst(Type opType)
    {
        return new BCmpInst(opType);
    }

    public CmpgInst newCmpgInst(Type opType)
    {
        return new BCmpgInst(opType);
    }

    public CmplInst newCmplInst(Type opType)
    {
        return new BCmplInst(opType);
    }

    public IfEqInst newIfEqInst(Unit unit)
    {
        return new BIfEqInst(unit);
    }

    public IfGeInst newIfGeInst(Unit unit)
    {
        return new BIfGeInst(unit);
    }

    public IfGtInst newIfGtInst(Unit unit)
    {
        return new BIfGtInst(unit);
    }

    public IfLeInst newIfLeInst(Unit unit)
    {
        return new BIfLeInst(unit);
    }

    public IfLtInst newIfLtInst(Unit unit)
    {
        return new BIfLtInst(unit);
    }

    public IfNeInst newIfNeInst(Unit unit)
    {
        return new BIfNeInst(unit);
    }

    public IfNullInst newIfNullInst(Unit unit)
    {
        return new BIfNullInst(unit);
    }

    public IfNonNullInst newIfNonNullInst(Unit unit)
    {
        return new BIfNonNullInst(unit);
    }

    public ThrowInst newThrowInst()
    {
        return new BThrowInst();
    }

    public SwapInst newSwapInst(Type fromType, Type toType)
    {
        return new BSwapInst(fromType, toType);
    }
  
    /*
    public DupInst newDupInst(Type type)
    {
        return new BDupInst(new ArrayList(), Arrays.asList(new Type[] {type}));
        }*/
    

    public Dup1Inst newDup1Inst(Type type)
    {
        return new BDup1Inst(type);
    }

    public Dup2Inst newDup2Inst(Type aOp1Type, Type aOp2Type)
    {
        return new BDup2Inst(aOp1Type,aOp2Type);
    }

    public Dup1_x1Inst newDup1_x1Inst(Type aOpType, Type aUnderType)
    {
        return new BDup1_x1Inst(aOpType, aUnderType);
    }
    
    public Dup1_x2Inst newDup1_x2Inst(Type aOpType,
                                      Type aUnder1Type, Type aUnder2Type)
    {
        return new BDup1_x2Inst(aOpType, aUnder1Type, aUnder2Type);
    }

    public Dup2_x1Inst newDup2_x1Inst(Type aOp1Type, Type aOp2Type,
                                      Type aUnderType)
    {
        return new BDup2_x1Inst(aOp1Type, aOp2Type, aUnderType);
    }

    public Dup2_x2Inst newDup2_x2Inst(Type aOp1Type, Type aOp2Type,
                                      Type aUnder1Type, Type aUnder2Type)
    {
        return new BDup2_x2Inst(aOp1Type, aOp2Type, aUnder1Type, aUnder2Type);
    }

  public IncInst newIncInst(Local aLocal, Constant aConstant)
  {
       return new BIncInst(aLocal, aConstant);
  }
  
    public LookupSwitchInst newLookupSwitchInst(Unit defaultTarget, 
                             List lookupValues, List targets)
    {
        return new BLookupSwitchInst(defaultTarget, lookupValues, targets);
    }

    public TableSwitchInst newTableSwitchInst(Unit defaultTarget, 
                             int lowIndex, int highIndex, List targets)
    {
        return new BTableSwitchInst(defaultTarget, lowIndex,
                                     highIndex, targets);
    }



    public static String bafDescriptorOf(Type type)
    {
        TypeSwitch sw;

        type.apply(sw = new TypeSwitch()
        {
            public void caseBooleanType(BooleanType t)
            {
                setResult("b");
            }

            public void caseByteType(ByteType t)
            {
                setResult("b");
            }

            public void caseCharType(CharType t)
            {
                setResult("c");
            }

            public void caseDoubleType(DoubleType t)
            {
                setResult("d");
            }

            public void caseFloatType(FloatType t)
            {
                setResult("f");
            }

            public void caseIntType(IntType t)
            {
                setResult("i");
            }

            public void caseLongType(LongType t)
            {
                setResult("l");
            }

            public void caseShortType(ShortType t)
            {
                setResult("s");
            }

            
            public void defaultCase(Type t)
            {
                throw new RuntimeException("Invalid type: " + t);
            }

            public void caseRefType(RefType t)
            {
                setResult("r");
            }


        });

        return (String) sw.getResult();
    }


    /** Returns an empty BafBody associated with method m. */
    public BafBody newBody(SootMethod m)
    {
        return new BafBody(m);
    }

    /** Returns a BafBody constructed from b. */
    public BafBody newBody(Body b)
    {
        return new BafBody(b, new HashMap());
    }

    /** Returns a BafBody constructed from b. */
    public BafBody newBody(Body b, String phase)
    {
        Map options = PhaseOptions.v().getPhaseOptions(phase);
        return new BafBody(b, options);
    }
}
