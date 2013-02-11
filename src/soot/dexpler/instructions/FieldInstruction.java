/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler.instructions;

import static soot.dexpler.Util.dottedClassName;
import static soot.dexpler.Util.isFloatLike;

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionWithReference;
import org.jf.dexlib.Code.Format.Instruction21c;
import org.jf.dexlib.Code.Format.Instruction22c;
import org.jf.dexlib.Code.Format.Instruction23x;

import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootResolver;
import soot.Type;
import soot.UnknownType;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.jimple.AssignStmt;
import soot.jimple.ConcreteRef;
import soot.jimple.Jimple;

public abstract class FieldInstruction extends DexlibAbstractInstruction {

    public FieldInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    /**
     * Return a static SootFieldRef for a dexlib FieldIdItem.
     *
     * @param item the dexlib FieldIdItem.
     */
    protected SootFieldRef getStaticSootFieldRef(FieldIdItem item) {
        return getSootFieldRef(item, true);
    }

    /**
     * Return a SootFieldRef for a dexlib FieldIdItem.
     *
     * @param item the dexlib FieldIdItem.
     */
    protected SootFieldRef getSootFieldRef(FieldIdItem item) {
        return getSootFieldRef(item, false);
    }

    /**
     * Return a SootFieldRef for a dexlib FieldIdItem.
     *
     * @param item the dexlib FieldIdItem.
     * @param isStatic if the FieldRef should be static
     */
    private SootFieldRef getSootFieldRef(FieldIdItem item, boolean isStatic) {
        String className = dottedClassName(((TypeIdItem) item.getContainingClass()).getTypeDescriptor());
        SootClass sc = SootResolver.v().makeClassRef(className);
        return Scene.v().makeFieldRef(sc,
                                      item.getFieldName().getStringValue(),
                                      DexType.toSoot(item.getFieldType()),
                                      isStatic);
    }
    
    /**
     * Check if the field type equals the type of the value that will be stored in the field. A cast expression has to be introduced for the unequal case.
     * @return assignment statement which hold a cast or not depending on the types of the operation
     */
    protected AssignStmt getAssignStmt(DexBody body, Local sourceValue, ConcreteRef instanceField) {
    	AssignStmt assign;
//		Type targetType = getTargetType(body);
//		if(targetType != UnknownType.v() && targetType != sourceValue.getType() && ! (targetType instanceof RefType)) {
//			CastExpr castExpr = Jimple.v().newCastExpr(sourceValue, targetType);
//			Local local = body.generateLocal(targetType);
//			assign = Jimple.v().newAssignStmt(local, castExpr);
//			body.add(assign);
//			beginUnit = assign;
//			assign = Jimple.v().newAssignStmt(instanceField, local);
//		}
//		else {
			assign = Jimple.v().newAssignStmt(instanceField, sourceValue);
//		}
		return assign;
    }

    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        return sourceRegister() == register && isFloatLike(getTargetType(body));
    }

    /**
     * Return the source register for this instruction.
     */
    private int sourceRegister() {
        // I hate smali's API ..
        if (instruction instanceof Instruction23x)
            return ((Instruction23x) instruction).getRegisterA();
        else if (instruction instanceof Instruction22c)
            return ((Instruction22c) instruction).getRegisterA();
        else if (instruction instanceof Instruction21c)
            return ((Instruction21c) instruction).getRegisterA();
        else throw new RuntimeException("Instruction is not a instance, array or static op");
    }


    /**
     * Return the target type for put instructions.
     *
     * Putters should override this.
     *
     * @param body the body containing this instruction
     */
    protected Type getTargetType(DexBody body) {
        return UnknownType.v();
    }

    @Override
    public Set<DexType> introducedTypes() {
        Set<DexType> types = new HashSet<DexType>();
        // Aput instructions don't have references
        if (!(instruction instanceof InstructionWithReference))
            return types;

        InstructionWithReference i = (InstructionWithReference) instruction;
        FieldIdItem field = (FieldIdItem) i.getReferencedItem();

        types.add(new DexType(field.getFieldType()));
        types.add(new DexType(field.getContainingClass()));
        return types;
    }
}
