/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class Baf implements BodyRepresentation
{
    private static Baf bafRepresentation = new Baf();


    static Type getDescriptorTypeOf(Type opType) 
    {        
        if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
            opType = RefType.v();
        
        return opType;
    }

    private Baf()
    {
    }
    
    public static Baf v()
    {
        return bafRepresentation;
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
        Constructs a ThisRef(SootClass) grammar chunk.
     */

    public ThisRef newThisRef(SootClass c)
    {
        return new ThisRef(c);
    }
    
    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */

    public ParameterRef newParameterRef(SootMethod m, int number)
    {
        return new ParameterRef(m, number);
    }

    
    /**
        Constructs a CaughtExceptionRef() grammar chunk.
     */

    public CaughtExceptionRef newCaughtExceptionRef(BafBody b)
    {
        return new JCaughtExceptionRef(b);
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

    public StaticGetInst newStaticGetInst(SootField field)
    {
        return new BStaticGetInst(field);
    }
    
    public StaticPutInst newStaticPutInst(SootField field)
    {
        return new BStaticPutInst(field);
    }

    public FieldGetInst newFieldGetInst(SootField field)
    {
        return new BFieldGetInst(field);
    }
    
    public FieldPutInst newFieldPutInst(SootField field)
    {
        return new BFieldPutInst(field);
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

    public StaticInvokeInst newStaticInvokeInst(SootMethod method)
    {
        return new BStaticInvokeInst(method);
    }

    public SpecialInvokeInst newSpecialInvokeInst(SootMethod method)
    {
        return new BSpecialInvokeInst(method);
    }

    public VirtualInvokeInst newVirtualInvokeInst(SootMethod method)
    {
        return new BVirtualInvokeInst(method);
    }

    public InterfaceInvokeInst newInterfaceInvokeInst(SootMethod method, int argCount)
    {
        return new BInterfaceInvokeInst(method, argCount);
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


}
