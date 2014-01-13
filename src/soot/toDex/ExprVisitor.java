package soot.toDex;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Opcode;

import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.LongType;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.CastExpr;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.ExprSwitch;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn11x;
import soot.toDex.instructions.Insn12x;
import soot.toDex.instructions.Insn21c;
import soot.toDex.instructions.Insn21t;
import soot.toDex.instructions.Insn22b;
import soot.toDex.instructions.Insn22c;
import soot.toDex.instructions.Insn22s;
import soot.toDex.instructions.Insn22t;
import soot.toDex.instructions.Insn23x;
import soot.toDex.instructions.Insn35c;
import soot.toDex.instructions.Insn3rc;
import soot.toDex.instructions.InsnWithOffset;
import soot.util.Switchable;

/**
 * A visitor that builds a list of instructions from the Jimple expressions it visits.<br>
 * <br>
 * Use {@link Switchable#apply(soot.util.Switch)} with this visitor to add statements.
 * These are added to the instructions in the {@link StmtVisitor}.<br>
 * If the expression is part of an assignment or an if statement, use {@link #setDestinationReg(Register)}
 * and {@link #setTargetForOffset(Stmt)}, respectively.
 * 
 * @see StmtVisitor
 */
public class ExprVisitor implements ExprSwitch {
	
	private StmtVisitor stmtV;
	
	private ConstantVisitor constantV;
	
	private RegisterAllocator regAlloc;
	
	private Register destinationReg;
	
	private Stmt targetForOffset;
	
    private Stmt origStmt;

	public ExprVisitor(StmtVisitor stmtV, ConstantVisitor constantV, RegisterAllocator regAlloc) {
		this.stmtV = stmtV;
		this.constantV = constantV;
		this.regAlloc = regAlloc;
	}
	
	public void setDestinationReg(Register destinationReg) {
		this.destinationReg = destinationReg;
	}
	
    public void setOrigStmt(Stmt stmt) {
        this.origStmt = stmt;
    }

	public void setTargetForOffset(Stmt targetForOffset) {
		this.targetForOffset = targetForOffset;
	}

	@Override
	public void defaultCase(Object o) {
		// rsub-int and rsub-int/lit8 aren't generated, since we cannot detect there usage in jimple
		throw new Error("unknown Object (" + o.getClass() + ") as Expression: " + o);
	}

	@Override
	public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
		throw new Error("DynamicInvokeExpr not supported: " + v);
	}
	
	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr sie) {
		MethodIdItem method = DexPrinter.toMethodIdItem(sie.getMethodRef(), stmtV.getBelongingFile());
		List<Register> arguments = getInstanceInvokeArgumentRegs(sie);
		if (isCallToConstructor(sie) || isCallToPrivate(sie)) {
            stmtV.addInsn(buildInvokeInsn("invoke-direct", method, arguments), origStmt);
		} else if (isCallToSuper(sie)) {
            stmtV.addInsn(buildInvokeInsn("invoke-super", method, arguments), origStmt);
		} else
			throw new Error("unknown SpecialInvokeExpr (no call to constructor, super or private method): " + sie);
	}

	private Insn buildInvokeInsn(String invokeOpcode, MethodIdItem method, List<Register> argumentRegs) {
		Insn invokeInsn;
		int regCountForArguments = SootToDexUtils.getRealRegCount(argumentRegs);
		if (regCountForArguments <= 5) {
			Register[] paddedArray = pad35cRegs(argumentRegs);
			Opcode opc = Opcode.getOpcodeByName(invokeOpcode);
			invokeInsn = new Insn35c(opc, regCountForArguments, paddedArray[0], paddedArray[1], paddedArray[2], paddedArray[3], paddedArray[4], method);
		} else if (regCountForArguments <= 255) {
			Opcode opc = Opcode.getOpcodeByName(invokeOpcode + "/range");
			invokeInsn = new Insn3rc(opc, argumentRegs, (short) regCountForArguments, method);
		} else {
			throw new Error("too many parameter registers for invoke-* (> 255): " + regCountForArguments + " or registers too big (> 4 bits)");
		}
		// save the return type for the move-result insn
		stmtV.setLastReturnTypeDescriptor(method.getPrototype().getReturnType().getTypeDescriptor());
		return invokeInsn;
	}

	private boolean isCallToPrivate(SpecialInvokeExpr sie) {
		return sie.getMethod().isPrivate();
	}

	private boolean isCallToConstructor(SpecialInvokeExpr sie) {
		return sie.getMethod().isConstructor();
	}

	private boolean isCallToSuper(SpecialInvokeExpr sie) {
		SootClass classWithInvokation = sie.getMethod().getDeclaringClass();
		SootClass currentClass = stmtV.getBelongingClass();
		while (currentClass.hasSuperclass()) {
			currentClass = currentClass.getSuperclass();
			if (currentClass.equals(classWithInvokation)) {
				return true;
			}
		}
		
		// If we're dealing with phantom classes, we might not actually
		// arrive at java.lang.Object. In this case, we should not fail
		// the check
		if (currentClass.isPhantom() && !currentClass.getName().equals("java.lang.Object"))
			return true;
		
		return false; // we arrived at java.lang.Object and did not find a declaration
	}

	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr vie) {
		/*
		 * for final methods we build an invoke-virtual opcode, too, although the dex spec says that a virtual method is not final.
		 * An alternative would be the invoke-direct opcode, but this is inconsistent with dx's output...
		 */
		MethodIdItem method = DexPrinter.toMethodIdItem(vie.getMethodRef(), stmtV.getBelongingFile());
		List<Register> argumentRegs = getInstanceInvokeArgumentRegs(vie);
        stmtV.addInsn(buildInvokeInsn("invoke-virtual", method, argumentRegs), origStmt);
	}
	
	private List<Register> getInvokeArgumentRegs(InvokeExpr ie) {
	    regAlloc.setMultipleConstantsPossible(true);
		List<Register> argumentRegs = new ArrayList<Register>();
		for (Value arg : ie.getArgs()) {
			Register currentReg = regAlloc.asImmediate(arg, constantV);
			argumentRegs.add(currentReg);
		}
		regAlloc.setMultipleConstantsPossible(false);
		return argumentRegs;
	}

	private List<Register> getInstanceInvokeArgumentRegs(InstanceInvokeExpr iie) {
	    regAlloc.setMultipleConstantsPossible(true);
		List<Register> argumentRegs = getInvokeArgumentRegs(iie);
		// always add reference to callee as first parameter (instance != static)
		Value callee = iie.getBase();
		Register calleeRegister = regAlloc.asLocal(callee);
		argumentRegs.add(0, calleeRegister);
		regAlloc.setMultipleConstantsPossible(false);
		return argumentRegs;
	}

	private Register[] pad35cRegs(List<Register> realRegs) {
		// pad real registers to an array of five bytes, as required by the instruction format "35c"
		Register[] paddedArray = new Register[5];
		int nextReg = 0;
		for (Register realReg : realRegs) {
			paddedArray[nextReg] = realReg;
			/*
			 * we include the second half of a wide with an empty reg for the "gap".
			 * this will be made explicit for dexlib later on.
			 */
			if (realReg.isWide()) {
				nextReg++;
				paddedArray[nextReg] = Register.EMPTY_REGISTER;
			}
			nextReg++;
		}
		// do the real padding with empty regs
		for (; nextReg < 5; nextReg++) {
			paddedArray[nextReg] = Register.EMPTY_REGISTER;
		}
		return paddedArray;
	}

	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr iie) {
		MethodIdItem method = DexPrinter.toMethodIdItem(iie.getMethodRef(), stmtV.getBelongingFile());
		List<Register> arguments = getInstanceInvokeArgumentRegs(iie);
        stmtV.addInsn(buildInvokeInsn("invoke-interface", method, arguments), origStmt);
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr sie) {
		MethodIdItem method = DexPrinter.toMethodIdItem(sie.getMethodRef(), stmtV.getBelongingFile());
		List<Register> arguments = getInvokeArgumentRegs(sie);
        stmtV.addInsn(buildInvokeInsn("invoke-static", method, arguments), origStmt);
	}
	
	private Insn buildCalculatingBinaryInsn(String binaryOperation, Value firstOperand, Value secondOperand) {
		/*
		 * it is assumed (but not enforced!) that the types of firstOperand, secondOperand and destinationRegister are compatible
		 */
		Register firstOpReg = regAlloc.asImmediate(firstOperand, constantV);
		
		// use special "/lit"-opcodes if int is the destination's type, the second op is an integer literal and the opc is not sub (no sub*/lit opc available)
		if (destinationReg.getType() instanceof IntType && secondOperand instanceof IntConstant && !binaryOperation.equals("sub")) {
			int secondOpConstant = ((IntConstant) secondOperand).value;
			if (SootToDexUtils.fitsSigned8(secondOpConstant)) {
				return buildLit8BinaryInsn(binaryOperation, firstOpReg, (byte) secondOpConstant);
			}
			if (SootToDexUtils.fitsSigned16(secondOpConstant)) {
				if (!binaryOperation.equals("shl") && !binaryOperation.equals("shr") && !binaryOperation.equals("ushr")) {
					// no shift opc available for /lit16
					return buildLit16BinaryInsn(binaryOperation, firstOpReg, (short) secondOpConstant);
				}
			}
			// constant is too big, so use it as second op and build normally
		}
		Register secondOpReg = regAlloc.asImmediate(secondOperand, constantV);
		// use special "/2addr"-opcodes if destination equals first op
		if (destinationReg.getNumber() == firstOpReg.getNumber()) {
			return build2AddrBinaryInsn(binaryOperation, secondOpReg);
		}
		// no optimized binary opcode possible
		return buildNormalBinaryInsn(binaryOperation, firstOpReg, secondOpReg);
	}

	private String fixIntTypeString(String typeString) {
		if (typeString.equals("boolean") || typeString.equals("byte") || typeString.equals("char") || typeString.equals("short")) {
			return "int";
		}
		return typeString;
	}

	private Insn build2AddrBinaryInsn(String binaryOperation, Register secondOpReg) {
		String localTypeString = destinationReg.getTypeString();
		localTypeString = fixIntTypeString(localTypeString);
		Opcode opc = Opcode.getOpcodeByName(binaryOperation + "-" + localTypeString + "/2addr");
		return new Insn12x(opc, destinationReg, secondOpReg);
	}

	private Insn buildNormalBinaryInsn(String binaryOperation, Register firstOpReg, Register secondOpReg) {
		String localTypeString = firstOpReg.getTypeString();
		localTypeString = fixIntTypeString(localTypeString);
		Opcode opc = Opcode.getOpcodeByName(binaryOperation + "-" + localTypeString);
		return new Insn23x(opc, destinationReg, firstOpReg, secondOpReg);
	}
	
	private Insn buildLit16BinaryInsn(String binaryOperation, Register firstOpReg, short secondOpLieteral) {
		Opcode opc = Opcode.getOpcodeByName(binaryOperation + "-int/lit16");
		return new Insn22s(opc, destinationReg, firstOpReg, secondOpLieteral);
	}

	private Insn buildLit8BinaryInsn(String binaryOperation, Register firstOpReg, byte secondOpLiteral) {
		Opcode opc = Opcode.getOpcodeByName(binaryOperation + "-int/lit8");
		return new Insn22b(opc, destinationReg, firstOpReg, secondOpLiteral);
	}

	@Override
	public void caseAddExpr(AddExpr ae) {
        stmtV.addInsn(buildCalculatingBinaryInsn("add", ae.getOp1(), ae.getOp2()), origStmt);
	}
	
	@Override
	public void caseSubExpr(SubExpr se) {
        stmtV.addInsn(buildCalculatingBinaryInsn("sub", se.getOp1(), se.getOp2()), origStmt);
	}
	
	@Override
	public void caseMulExpr(MulExpr me) {
        stmtV.addInsn(buildCalculatingBinaryInsn("mul", me.getOp1(), me.getOp2()), origStmt);
	}
	
	@Override
	public void caseDivExpr(DivExpr de) {
        stmtV.addInsn(buildCalculatingBinaryInsn("div", de.getOp1(), de.getOp2()), origStmt);
	}
	
	@Override
	public void caseRemExpr(RemExpr re) {
        stmtV.addInsn(buildCalculatingBinaryInsn("rem", re.getOp1(), re.getOp2()), origStmt);
	}
	
	@Override
	public void caseAndExpr(AndExpr ae) {
        stmtV.addInsn(buildCalculatingBinaryInsn("and", ae.getOp1(), ae.getOp2()), origStmt);
	}
	
	@Override
	public void caseOrExpr(OrExpr oe) {
        stmtV.addInsn(buildCalculatingBinaryInsn("or", oe.getOp1(), oe.getOp2()), origStmt);
	}
	
	@Override
	public void caseXorExpr(XorExpr xe) {
		Value firstOperand = xe.getOp1();
		Value secondOperand = xe.getOp2();
		// see for unary ones-complement shortcut, may be a result of Dexpler's conversion
		if (secondOperand.equals(IntConstant.v(-1))) {
			Register sourceReg = regAlloc.asImmediate(firstOperand, constantV);
            stmtV.addInsn(new Insn12x(Opcode.NOT_INT, destinationReg, sourceReg), origStmt);
		} else if (secondOperand.equals(LongConstant.v(-1))) {
			Register sourceReg = regAlloc.asImmediate(firstOperand, constantV);
            stmtV.addInsn(new Insn12x(Opcode.NOT_LONG, destinationReg, sourceReg), origStmt);
		} else {
            stmtV.addInsn(buildCalculatingBinaryInsn("xor", firstOperand, secondOperand), origStmt);
		}
	}
	
	@Override
	public void caseShlExpr(ShlExpr sle) {
        stmtV.addInsn(buildCalculatingBinaryInsn("shl", sle.getOp1(), sle.getOp2()), origStmt);
	}
	
	@Override
	public void caseShrExpr(ShrExpr sre) {
        stmtV.addInsn(buildCalculatingBinaryInsn("shr", sre.getOp1(), sre.getOp2()), origStmt);
	}

	@Override
	public void caseUshrExpr(UshrExpr usre) {
        stmtV.addInsn(buildCalculatingBinaryInsn("ushr", usre.getOp1(), usre.getOp2()), origStmt);
	}
	
	private Insn buildComparingBinaryInsn(String binaryOperation, Value firstOperand, Value secondOperand) {
		Value realFirstOperand = fixNullConstant(firstOperand);
		Value realSecondOperand = fixNullConstant(secondOperand);
		Register firstOpReg = regAlloc.asImmediate(realFirstOperand, constantV);
		// select fitting opcode ("if" or "if-zero")
		InsnWithOffset comparingBinaryInsn;
		String opcodeName = "if-" + binaryOperation;
		boolean secondOpIsInt = realSecondOperand instanceof IntConstant;
		boolean secondOpIsZero = secondOpIsInt && ((IntConstant) realSecondOperand).value == 0;
		if (secondOpIsZero) {
			Opcode opc = Opcode.getOpcodeByName(opcodeName.concat("z"));
			comparingBinaryInsn = new Insn21t(opc, firstOpReg);
			comparingBinaryInsn.setOffset(targetForOffset);
		} else {
			Opcode opc = Opcode.getOpcodeByName(opcodeName);
			Register secondOpReg = regAlloc.asImmediate(realSecondOperand, constantV);
			comparingBinaryInsn = new Insn22t(opc, firstOpReg, secondOpReg);
			comparingBinaryInsn.setOffset(targetForOffset);
		}
		return comparingBinaryInsn;
	}
	
	private Value fixNullConstant(Value potentialNullConstant) {
		/*
		 * The bytecode spec says: "In terms of bitwise representation, (Object) null == (int) 0."
		 * So use an IntConstant(0) for null comparison in if-*z opcodes.
		 */
		if (potentialNullConstant instanceof NullConstant) {
			return IntConstant.v(0);
		}
		return potentialNullConstant;
	}

	@Override
	public void caseEqExpr(EqExpr ee) {
        stmtV.addInsn(buildComparingBinaryInsn("eq", ee.getOp1(), ee.getOp2()), origStmt);
	}

	@Override
	public void caseGeExpr(GeExpr ge) {
        stmtV.addInsn(buildComparingBinaryInsn("ge", ge.getOp1(), ge.getOp2()), origStmt);
	}

	@Override
	public void caseGtExpr(GtExpr ge) {
        stmtV.addInsn(buildComparingBinaryInsn("gt", ge.getOp1(), ge.getOp2()), origStmt);
	}

	@Override
	public void caseLeExpr(LeExpr le) {
        stmtV.addInsn(buildComparingBinaryInsn("le", le.getOp1(), le.getOp2()), origStmt);
	}

	@Override
	public void caseLtExpr(LtExpr le) {
        stmtV.addInsn(buildComparingBinaryInsn("lt", le.getOp1(), le.getOp2()), origStmt);
	}
	
	@Override
	public void caseNeExpr(NeExpr ne) {
        stmtV.addInsn(buildComparingBinaryInsn("ne", ne.getOp1(), ne.getOp2()), origStmt);
	}
	
	@Override
	public void caseCmpExpr(CmpExpr v) {
        stmtV.addInsn(buildCmpInsn("cmp-long", v.getOp1(), v.getOp2()), origStmt);
	}
	
	@Override
	public void caseCmpgExpr(CmpgExpr v) {
        stmtV.addInsn(buildCmpInsn("cmpg", v.getOp1(), v.getOp2()), origStmt);
	}
	
	@Override
	public void caseCmplExpr(CmplExpr v) {
        stmtV.addInsn(buildCmpInsn("cmpl", v.getOp1(), v.getOp2()), origStmt);
	}
	
	private Insn buildCmpInsn(String opcodePrefix, Value firstOperand, Value secondOperand) {
		Register firstReg = regAlloc.asImmediate(firstOperand, constantV);
		Register secondReg = regAlloc.asImmediate(secondOperand, constantV);
		// select fitting opcode according to the prefix and the type of the operands
		Opcode opc = null;
		// we assume that the type of the operands are equal and use only the first one
		if (opcodePrefix.equals("cmp-long")) {
			opc = Opcode.CMP_LONG;
		} else if (firstReg.isFloat()) {
			opc = Opcode.getOpcodeByName(opcodePrefix + "-float");
		} else if (firstReg.isDouble()) {
			opc = Opcode.getOpcodeByName(opcodePrefix + "-double");
		} else {
			throw new RuntimeException("unsupported type of operands for cmp* opcode: " + firstOperand.getType());
		}
		return new Insn23x(opc, destinationReg, firstReg, secondReg);
	}
	
	@Override
	public void caseLengthExpr(LengthExpr le) {
		Value array = le.getOp();
		Register arrayReg = regAlloc.asLocal(array);
        stmtV.addInsn(new Insn12x(Opcode.ARRAY_LENGTH, destinationReg, arrayReg), origStmt);
	}
	
	@Override
	public void caseNegExpr(NegExpr ne) {
		Value source = ne.getOp();
		Register sourceReg = regAlloc.asImmediate(source, constantV);
		Opcode opc;
		Type type = source.getType();
		if (type instanceof IntegerType) {
			opc = Opcode.NEG_INT;
		} else if (type instanceof FloatType) {
			opc = Opcode.NEG_FLOAT;
		} else if (type instanceof DoubleType) {
			opc = Opcode.NEG_DOUBLE;
		} else if (type instanceof LongType) {
			opc = Opcode.NEG_LONG;
		} else {
			throw new RuntimeException("unsupported value type for neg-* opcode: " + type);
		}
        stmtV.addInsn(new Insn12x(opc, destinationReg, sourceReg), origStmt);
	}
	
	@Override
	public void caseInstanceOfExpr(InstanceOfExpr ioe) {
		Value referenceToCheck = ioe.getOp();
		Register referenceToCheckReg = regAlloc.asLocal(referenceToCheck);
		TypeIdItem checkType = DexPrinter.toTypeIdItem(ioe.getCheckType(), stmtV.getBelongingFile());
        stmtV.addInsn(new Insn22c(Opcode.INSTANCE_OF, destinationReg, referenceToCheckReg,
                checkType), null);
	}
	
	@Override
	public void caseCastExpr(CastExpr ce) {
		Type castType = ce.getCastType();
		Value source = ce.getOp();
		Register sourceReg = regAlloc.asImmediate(source, constantV);
		if (SootToDexUtils.isObject(castType)) {
			castObject(sourceReg, castType);
		} else {
			castPrimitive(sourceReg, source, castType);
		}
	}

	private void castObject(Register sourceReg, Type castType) {
		/*
		 * No real "cast" is done: move the object to a tmp reg, check the cast with check-cast and finally move to the "cast" object location.
		 * This way a) the old reg is not touched by check-cast, and b) the new reg does change its type only after a successful check-cast.
		 * 
		 * a) is relevant e.g. for "r1 = (String) r0" if r0 contains null, since the internal type of r0
		 * would change from Null to String after the check-cast opcode, which alerts the verifier in future uses of r0
		 * although nothing should change during execution.
		 * 
		 * b) is relevant for exceptional control flow: if we move to the new reg and do the check-cast there, an exception between the end of
		 * the move's execution and the end of the check-cast execution leaves the new reg with the type of the old reg.
		 */
		TypeIdItem castTypeItem = DexPrinter.toTypeIdItem(castType, stmtV.getBelongingFile());
		if (sourceReg.getNumber() == destinationReg.getNumber()) {
			// simplyfied case if reg numbers do not differ
            stmtV.addInsn(new Insn21c(Opcode.CHECK_CAST, destinationReg, castTypeItem), origStmt);
		} else {
			// move to tmp reg, check cast, move to destination
			Register tmp = regAlloc.asTmpReg(sourceReg.getType());
            stmtV.addInsn(StmtVisitor.buildMoveInsn(tmp, sourceReg), origStmt);
            stmtV.addInsn(new Insn21c(Opcode.CHECK_CAST, tmp.clone(), castTypeItem), origStmt);
            stmtV.addInsn(StmtVisitor.buildMoveInsn(destinationReg, tmp.clone()), origStmt);
		}
	}
	
	private void castPrimitive(Register sourceReg, Value source, Type castSootType) {
		// select fitting conversion opcode, depending on the source and cast type
		PrimitiveType sourceType = PrimitiveType.getByName(source.getType().toString());
		PrimitiveType castType = PrimitiveType.getByName(castSootType.toString());
		if (castType == PrimitiveType.BOOLEAN) {
			// there is no "-to-boolean" opcode, so just pretend to move an int to an int
			castType = PrimitiveType.INT;
			sourceType = PrimitiveType.INT;
		}
		if (shouldCastFromInt(sourceType, castType)) {
			// pretend to cast from int since that is OK
			sourceType = PrimitiveType.INT;
			Opcode opc = getCastOpc(sourceType, castType);
            stmtV.addInsn(new Insn12x(opc, destinationReg, sourceReg), origStmt);
		} else if (isMoveCompatible(sourceType, castType)) {
			/*
			 * no actual cast needed, just move the reg content if regs differ
			 */
			if (destinationReg.getNumber() != sourceReg.getNumber()) {
                stmtV.addInsn(StmtVisitor.buildMoveInsn(destinationReg, sourceReg), origStmt);
			}
		} else if (needsCastThroughInt(sourceType, castType)) {
			/* 
			 * an unsupported "dest = (cast) src" is broken down to
			 * "tmp = (int) src" and
			 * "dest = (cast) tmp",
			 * using a tmp reg to not mess with the original reg types
			 */
			Opcode castToIntOpc = getCastOpc(sourceType, PrimitiveType.INT);
			Opcode castFromIntOpc = getCastOpc(PrimitiveType.INT, castType);
			Register tmp = regAlloc.asTmpReg(IntType.v());
            stmtV.addInsn(new Insn12x(castToIntOpc, tmp, sourceReg), origStmt);
            stmtV.addInsn(new Insn12x(castFromIntOpc, destinationReg, tmp.clone()), origStmt);
		} else {
			// the leftover simple cases, where we just cast as stated
			Opcode opc = getCastOpc(sourceType, castType);
            stmtV.addInsn(new Insn12x(opc, destinationReg, sourceReg), origStmt);
		}
	}

	private boolean needsCastThroughInt(PrimitiveType sourceType, PrimitiveType castType) {
		// source >= long && cast < int -> too far away to cast directly
		return isEqualOrBigger(sourceType, PrimitiveType.LONG) && !isEqualOrBigger(castType, PrimitiveType.INT);
	}

	private boolean isMoveCompatible(PrimitiveType sourceType, PrimitiveType castType) {
		if (sourceType == castType) {
			// at this point, the types are "bigger" or equal to int, so no "should cast from int" is needed 
			return true;
		}
		if (castType == PrimitiveType.INT && !isEqualOrBigger(sourceType, PrimitiveType.INT)) {
			// there is no "upgrade" cast from "smaller than int" to int, so move it
			return true;
		}
		return false;
	}

	private boolean shouldCastFromInt(PrimitiveType sourceType, PrimitiveType castType) {
		if (isEqualOrBigger(sourceType, PrimitiveType.INT)) {
			// source is already "big" enough
			return false;
		}
		if (castType == PrimitiveType.INT) {
			// would lead to an int-to-int cast, so leave it as it is
			return false;
		}
		return true;
	}

	private boolean isEqualOrBigger(PrimitiveType type, PrimitiveType relativeTo) {
		return type.compareTo(relativeTo) >= 0;
	}

	private Opcode getCastOpc(PrimitiveType sourceType, PrimitiveType castType) {
		Opcode opc = Opcode.getOpcodeByName(sourceType.getName() + "-to-" + castType.getName());
		if (opc == null) {
			throw new RuntimeException("unsupported cast from " + sourceType + " to " + castType);
		}
		return opc;
	}

	@Override
	public void caseNewArrayExpr(NewArrayExpr nae) {
		Value size = nae.getSize();
		Register sizeReg = regAlloc.asImmediate(size, constantV);
		ArrayType arrayType = nae.getBaseType().getArrayType();
		TypeIdItem arrayTypeItem = DexPrinter.toTypeIdItem(arrayType, stmtV.getBelongingFile());
        stmtV.addInsn(new Insn22c(Opcode.NEW_ARRAY, destinationReg, sizeReg, arrayTypeItem), origStmt);
	}
	
	@Override
	public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae) {
		// get array dimensions
		if (nmae.getSizeCount() > 255) {
			throw new RuntimeException("number of dimensions is too high (> 255) for the filled-new-array* opcodes: " + nmae.getSizeCount());
		}
		short dimensions = (short) nmae.getSizeCount();
		// get array base type
		ArrayType arrayType = ArrayType.v(nmae.getBaseType().baseType, dimensions);
		TypeIdItem arrayTypeItem = DexPrinter.toTypeIdItem(arrayType, stmtV.getBelongingFile());
		// get the dimension size registers
		List<Register> dimensionSizeRegs = new ArrayList<Register>();
		regAlloc.setMultipleConstantsPossible(true); // in case there are multiple integer constants
		for (int i = 0; i < dimensions; i++) {
			Value currentDimensionSize = nmae.getSize(i);
			Register currentReg = regAlloc.asImmediate(currentDimensionSize, constantV);
			dimensionSizeRegs.add(currentReg);
		}
		regAlloc.setMultipleConstantsPossible(false); // in case there are multiple integer constants
		// create filled-new-array instruction, depending on the dimension sizes
		if (dimensions <= 5) {
			Register[] paddedRegs = pad35cRegs(dimensionSizeRegs);
            stmtV.addInsn(new Insn35c(Opcode.FILLED_NEW_ARRAY, dimensions, paddedRegs[0],
                    paddedRegs[1], paddedRegs[2], paddedRegs[3], paddedRegs[4], arrayTypeItem),
                    null);
		} else {
            stmtV.addInsn(new Insn3rc(Opcode.FILLED_NEW_ARRAY_RANGE, dimensionSizeRegs, dimensions,
                    arrayTypeItem), null);
		} // check for > 255 is done already
		// move the resulting array into the destination register
        stmtV.addInsn(new Insn11x(Opcode.MOVE_RESULT_OBJECT, destinationReg), origStmt);
	}

	@Override
	public void caseNewExpr(NewExpr ne) {
		TypeIdItem baseType = DexPrinter.toTypeIdItem(ne.getBaseType(), stmtV.getBelongingFile());
        stmtV.addInsn(new Insn21c(Opcode.NEW_INSTANCE, destinationReg, baseType), origStmt);
	}
}