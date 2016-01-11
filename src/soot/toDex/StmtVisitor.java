package soot.toDex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.writer.builder.BuilderFieldReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.ConcreteRef;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FloatConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.toDex.instructions.AbstractPayload;
import soot.toDex.instructions.AddressInsn;
import soot.toDex.instructions.ArrayDataPayload;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn10t;
import soot.toDex.instructions.Insn10x;
import soot.toDex.instructions.Insn11x;
import soot.toDex.instructions.Insn12x;
import soot.toDex.instructions.Insn21c;
import soot.toDex.instructions.Insn22c;
import soot.toDex.instructions.Insn22x;
import soot.toDex.instructions.Insn23x;
import soot.toDex.instructions.Insn31t;
import soot.toDex.instructions.Insn32x;
import soot.toDex.instructions.InsnWithOffset;
import soot.toDex.instructions.PackedSwitchPayload;
import soot.toDex.instructions.SparseSwitchPayload;
import soot.toDex.instructions.SwitchPayload;
import soot.util.Switchable;

/**
 * A visitor that builds a list of instructions from the Jimple statements it visits.<br>
 * <br>
 * Use {@link Switchable#apply(soot.util.Switch)} with this visitor to add statements
 * and {@link #getFinalInsns()} to get the final dexlib instructions.<br>
 * <br>
 * These final instructions do have correct offsets, jump targets and register numbers.
 * 
 * @see Insn intermediate representation of an instruction
 * @see Instruction final representation of an instruction
 */
class StmtVisitor implements StmtSwitch {
	
	private static final Map<Opcode, Opcode> oppositeIfs;
	
	static {
		oppositeIfs = new HashMap<Opcode, Opcode>();
		
		oppositeIfs.put(Opcode.IF_EQ, Opcode.IF_NE);
		oppositeIfs.put(Opcode.IF_NE, Opcode.IF_EQ);
		oppositeIfs.put(Opcode.IF_EQZ, Opcode.IF_NEZ);
		oppositeIfs.put(Opcode.IF_NEZ, Opcode.IF_EQZ);
		
		oppositeIfs.put(Opcode.IF_GT, Opcode.IF_LE);
		oppositeIfs.put(Opcode.IF_LE, Opcode.IF_GT);
		oppositeIfs.put(Opcode.IF_GTZ, Opcode.IF_LEZ);
		oppositeIfs.put(Opcode.IF_LEZ, Opcode.IF_GTZ);
		
		oppositeIfs.put(Opcode.IF_GE, Opcode.IF_LT);
		oppositeIfs.put(Opcode.IF_LT, Opcode.IF_GE);
		oppositeIfs.put(Opcode.IF_GEZ, Opcode.IF_LTZ);
		oppositeIfs.put(Opcode.IF_LTZ, Opcode.IF_GEZ);
	}
	
	private final SootMethod belongingMethod;
	private final DexBuilder belongingFile;
	private final DexArrayInitDetector arrayInitDetector;
	
	private ConstantVisitor constantV;
	
	private RegisterAllocator regAlloc;
	
	private ExprVisitor exprV;
	
	private String lastReturnTypeDescriptor;
	
	private List<Insn> insns;
	
	private List<AbstractPayload> payloads;
	
    // maps used to map Jimple statements to dalvik instructions
    private Map<Insn, Stmt> insnStmtMap = new HashMap<Insn, Stmt>();
    private Map<Instruction, LocalRegisterAssignmentInformation> instructionRegisterMap = new IdentityHashMap<Instruction, LocalRegisterAssignmentInformation>();
    private Map<Instruction, Insn> instructionInsnMap = new IdentityHashMap<Instruction, Insn>();
    private Map<Insn, LocalRegisterAssignmentInformation> insnRegisterMap = new IdentityHashMap<Insn, LocalRegisterAssignmentInformation>();
    private Map<Instruction, AbstractPayload> instructionPayloadMap = new IdentityHashMap<Instruction, AbstractPayload>();
	private List<LocalRegisterAssignmentInformation> parameterInstructionsList = new ArrayList<LocalRegisterAssignmentInformation>();
	
	private Map<Constant, Register> monitorRegs = new HashMap<Constant, Register>();
    
    public StmtVisitor(SootMethod belongingMethod, DexBuilder belongingFile,
    		DexArrayInitDetector arrayInitDetector) {
		this.belongingMethod = belongingMethod;
		this.belongingFile = belongingFile;
		this.arrayInitDetector = arrayInitDetector;
		constantV = new ConstantVisitor(belongingFile, this);
		regAlloc = new RegisterAllocator();
		exprV = new ExprVisitor(this, constantV, regAlloc, belongingFile);
		insns = new ArrayList<Insn>();
		payloads = new ArrayList<AbstractPayload>();
    }
	
	protected void setLastReturnTypeDescriptor(String typeDescriptor) {
		lastReturnTypeDescriptor = typeDescriptor;
	}
	
	protected DexBuilder getBelongingFile() {
		return belongingFile;
	}
	
	protected SootClass getBelongingClass() {
		return belongingMethod.getDeclaringClass();
	}
	
    public Stmt getStmtForInstruction(Instruction instruction) {
        Insn insn = this.instructionInsnMap.get(instruction);
        if (insn == null)
        	return null;
        return this.insnStmtMap.get(insn);
    }
    
    public Insn getInsnForInstruction(Instruction instruction) {
    	return instructionInsnMap.get(instruction);
    }
	
    public Map<Instruction, LocalRegisterAssignmentInformation> getInstructionRegisterMap() {
        return this.instructionRegisterMap;
    }
    
    public List<LocalRegisterAssignmentInformation> getParameterInstructionsList() {
    	return parameterInstructionsList;
    }

    public Map<Instruction, AbstractPayload> getInstructionPayloadMap() {
        return this.instructionPayloadMap;
    }
    
    protected void addInsn(Insn insn, Stmt s) {
		int highestIndex = insns.size();
		addInsn(highestIndex, insn);
		if (s != null)
			if (insnStmtMap.put(insn, s) != null)
				throw new RuntimeException("Duplicate instruction");
	}
	
	private void addInsn(int positionInList, Insn insn) {
		insns.add(positionInList, insn);
	}
	
	protected void beginNewStmt(Stmt s) {
		// It's a new statement, so we can re-use registers
		regAlloc.resetImmediateConstantsPool();
		
		addInsn(new AddressInsn(s), null);
	}
	
	public void finalizeInstructions() {
		addPayloads();
		finishRegs();
		reduceInstructions();
	}
	
	/**
	 * Reduces the instruction list by removing unnecessary instruction pairs
	 * such as move v0 v1; move v1 v0;
	 */
	private void reduceInstructions() {
		for (int i = 0; i < this.insns.size() - 1; i++) {
			Insn curInsn = this.insns.get(i);
			// Only consider real instructions
			if (curInsn instanceof AddressInsn)
				continue;
			if (!isReducableMoveInstruction(curInsn.getOpcode().name))
				continue;
			
			// Skip over following address instructions
			Insn nextInsn = null;
			int nextIndex = -1;
			for (int j = i + 1; j < this.insns.size(); j++) {
				Insn candidate = this.insns.get(j);
				if (candidate instanceof AddressInsn)
					continue;
				nextInsn = candidate;
				nextIndex = j;
				break;
			}
			if (nextInsn == null || !isReducableMoveInstruction(nextInsn.getOpcode().name))
				continue;
			
			// Do not remove the last instruction in the body as we need to remap
			// jump targets to the successor
			if (nextIndex == this.insns.size() - 1)
				continue;
			
			// Check if we have a <- b; b <- a;
			Register firstTarget = curInsn.getRegs().get(0);
			Register firstSource = curInsn.getRegs().get(1);
			Register secondTarget = nextInsn.getRegs().get(0);
			Register secondSource = nextInsn.getRegs().get(1);
			if (firstTarget.equals(secondSource) && secondTarget.equals(firstSource)) {
				Stmt origStmt = insnStmtMap.get(nextInsn);
				
				// Remove the second instruction as it does not change any
				// state. We cannot remove the first instruction as other
				// instructions may depend on the register being set.
				if (origStmt == null || !isJumpTarget(origStmt)) {
					Insn nextStmt = this.insns.get(nextIndex + 1);
					insns.remove(nextIndex);
					
					if (origStmt != null) {
						insnStmtMap.remove(nextInsn);
						insnStmtMap.put(nextStmt, origStmt);
					}
				}
			}
		}
	}
	
	private boolean isReducableMoveInstruction(String name) {
		return name.startsWith("move/")
				|| name.startsWith("move-object/")
				|| name.startsWith("move-wide/");
	}

	private boolean isJumpTarget(Stmt target) {
		for (Insn insn : this.insns)
			if (insn instanceof InsnWithOffset)
				if (((InsnWithOffset) insn).getTarget() == target)
					return true;
		return false;
	}

	private void addPayloads() {
		// add switch payloads to the end of the insns
		for (AbstractPayload payload : payloads) {
            addInsn(new AddressInsn(payload), null);
            addInsn(payload, null);
		}
	}

	public List<BuilderInstruction> getRealInsns(LabelAssigner labelAssigner) {
		List<BuilderInstruction> finalInsns = new ArrayList<BuilderInstruction>();
		for (Insn i : insns) {
			if (i instanceof AddressInsn) {
				continue; // skip non-insns
			}
			BuilderInstruction realInsn = i.getRealInsn(labelAssigner);
			finalInsns.add(realInsn);
            if (insnStmtMap.containsKey(i)) { // get tags
                instructionInsnMap.put(realInsn, i);
            }
            if (insnRegisterMap.containsKey(i)) {
            	instructionRegisterMap.put(realInsn, insnRegisterMap.get(i));
            }
            if (i instanceof AbstractPayload)
            	instructionPayloadMap.put(realInsn, (AbstractPayload) i);
		}
		return finalInsns;
	}
	
	public void fakeNewInsn(Stmt s, Insn insn, Instruction instruction) {
		this.insnStmtMap.put(insn, s);
		this.instructionInsnMap.put(instruction, insn);
	}
	
	private void finishRegs() {
		// fit registers into insn formats, potentially replacing insns
		RegisterAssigner regAssigner = new RegisterAssigner(regAlloc);
		insns = regAssigner.finishRegs(insns, insnStmtMap, insnRegisterMap, parameterInstructionsList);
	}
	
	protected int getRegisterCount() {
		return regAlloc.getRegCount();
	}

	@Override
	public void defaultCase(Object o) {
		// not-int and not-long aren't implemented because soot converts "~x" to "x ^ (-1)"
		// fill-array-data isn't implemented since soot converts "new int[]{x, y}" to individual "array put" expressions for x and y
		throw new Error("unknown Object (" + o.getClass() + ") as Stmt: " + o);
	}
	
	@Override
	public void caseBreakpointStmt(BreakpointStmt stmt) {
		return; // there are no breakpoints in dex bytecode
	}
	
	@Override
	public void caseNopStmt(NopStmt stmt) {
        addInsn(new Insn10x(Opcode.NOP), stmt);
	}

	@Override
	public void caseRetStmt(RetStmt stmt) {
		throw new Error("ret statements are deprecated!");
	}
	
	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
        addInsn(buildMonitorInsn(stmt, Opcode.MONITOR_ENTER), stmt);
	}
	
	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
        addInsn(buildMonitorInsn(stmt, Opcode.MONITOR_EXIT), stmt);
	}
	
	private Insn buildMonitorInsn(MonitorStmt stmt, Opcode opc) {
		Value lockValue = stmt.getOp();
        constantV.setOrigStmt(stmt);
        
        // When leaving a monitor, we must make sure to re-use the old
        // register. If we assign the same class constant to a new register
        // before leaving the monitor, Android's bytecode verifier will assume
        // that this constant assignment can throw an exception, leaving us
        // with a dangling monitor. Imprecise static analyzers ftw.
		Register lockReg = null;
		if (lockValue instanceof Constant)
			if ((lockReg = monitorRegs.get(lockValue)) != null)
				lockReg = lockReg.clone();
		if (lockReg == null) {
			lockReg = regAlloc.asImmediate(lockValue, constantV);
			regAlloc.lockRegister(lockReg);
			if (lockValue instanceof Constant) {
				monitorRegs.put((Constant) lockValue, lockReg);
				regAlloc.lockRegister(lockReg);
			}
		}
		return new Insn11x(opc, lockReg);
	}
	
	@Override
	public void caseThrowStmt(ThrowStmt stmt) {
		Value exception = stmt.getOp();
        constantV.setOrigStmt(stmt);
		Register exceptionReg = regAlloc.asImmediate(exception, constantV);
        addInsn(new Insn11x(Opcode.THROW, exceptionReg), stmt);
	}
	
	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		// If this is the beginning of an array initialization, we shortcut the
		// normal translation process
		List<Value> arrayValues = arrayInitDetector.getValuesForArrayInit(stmt);
		if (arrayValues != null) {
			Insn insn = buildArrayFillInsn((ArrayRef) stmt.getLeftOp(), arrayValues);
			if (insn != null) {
				addInsn(insn, stmt);
				return;
			}
		}
		if (arrayInitDetector.getIgnoreUnits().contains(stmt)) {
			return;
		}
		
		constantV.setOrigStmt(stmt);
        exprV.setOrigStmt(stmt);
		Value lhs = stmt.getLeftOp();
		if (lhs instanceof ConcreteRef) {
			// special cases that lead to *put* opcodes
			Value source = stmt.getRightOp();
            addInsn(buildPutInsn((ConcreteRef) lhs, source), stmt);
			return;
		}
		// other cases, where lhs is a local
		if (!(lhs instanceof Local)) {
			throw new Error("left-hand side of AssignStmt is not a Local: " + lhs.getClass());
		}
		Local lhsLocal = (Local) lhs;
		
		Register lhsReg = regAlloc.asLocal(lhsLocal);
		
		Value rhs = stmt.getRightOp();
		if (rhs instanceof Local) {
			// move rhs local to lhs local, if different
			String lhsName = ((Local)lhs).getName();
			String rhsName = ((Local)rhs).getName();
			if (lhsName.equals(rhsName)) {
				return;
			}
			Register sourceReg = regAlloc.asLocal((Local) rhs);
            addInsn(buildMoveInsn(lhsReg, sourceReg), stmt);
		} else if (rhs instanceof Constant) {
			// move rhs constant into the lhs local
			constantV.setDestination(lhsReg);
			rhs.apply(constantV);
		} else if (rhs instanceof ConcreteRef) {
            addInsn(buildGetInsn((ConcreteRef) rhs, lhsReg), stmt);
		} else {
			// evaluate rhs expression, saving the result in the lhs local
			exprV.setDestinationReg(lhsReg);
			rhs.apply(exprV);
			if (rhs instanceof InvokeExpr) {
				// do the actual "assignment" for an invocation: move its result to the lhs reg (it was not used yet)
				Insn moveResultInsn = buildMoveResultInsn(lhsReg);
				int invokeInsnIndex = insns.indexOf(getLastInvokeInsn());
				addInsn(invokeInsnIndex + 1, moveResultInsn);
			}
		}

		this.insnRegisterMap.put(insns.get(insns.size() - 1), LocalRegisterAssignmentInformation.v(lhsReg, lhsLocal));
	}

	private Insn buildGetInsn(ConcreteRef sourceRef, Register destinationReg) {
		if (sourceRef instanceof StaticFieldRef) {
			return buildStaticFieldGetInsn(destinationReg, (StaticFieldRef) sourceRef);
		} else if (sourceRef instanceof InstanceFieldRef) {
			return buildInstanceFieldGetInsn(destinationReg, (InstanceFieldRef) sourceRef);
		} else if (sourceRef instanceof ArrayRef) {
			return buildArrayGetInsn(destinationReg, (ArrayRef) sourceRef);
		} else {
			throw new RuntimeException("unsupported type of ConcreteRef: " + sourceRef.getClass());
		}
	}

	private Insn buildPutInsn(ConcreteRef destRef, Value source) {
		if (destRef instanceof StaticFieldRef) {
			return buildStaticFieldPutInsn((StaticFieldRef) destRef, source);
		} else if (destRef instanceof InstanceFieldRef) {
			return buildInstanceFieldPutInsn((InstanceFieldRef) destRef, source);
		} else if (destRef instanceof ArrayRef) {
			return buildArrayPutInsn((ArrayRef) destRef, source);
		} else {
			throw new RuntimeException("unsupported type of ConcreteRef: " + destRef.getClass());
		}
	}

	protected static Insn buildMoveInsn(Register destinationReg, Register sourceReg) {
		// get the opcode type, depending on the source reg (we assume that the destination has the same type)
		String opcType;
		if (sourceReg.isObject()) {
			opcType = "MOVE_OBJECT";
		} else if (sourceReg.isWide()) {
			opcType = "MOVE_WIDE";
		} else {
			opcType = "MOVE";
		}
		// get the optional opcode suffix, depending on the sizes of the regs
		if (!destinationReg.fitsShort()) {
			Opcode opc = Opcode.valueOf(opcType + "_16");
			return new Insn32x(opc, destinationReg, sourceReg);
		} else if (!destinationReg.fitsByte() || !sourceReg.fitsByte()) {
			Opcode opc = Opcode.valueOf(opcType + "_FROM16");
			return new Insn22x(opc, destinationReg, sourceReg);
		}
		Opcode opc = Opcode.valueOf(opcType);
		return new Insn12x(opc, destinationReg, sourceReg);
	}
	
	private Insn buildStaticFieldPutInsn(StaticFieldRef destRef, Value source) {
		SootField destSootField = destRef.getField();
		Register sourceReg = regAlloc.asImmediate(source, constantV);
		BuilderFieldReference destField = DexPrinter.toFieldReference(destSootField, belongingFile);
		Opcode opc = getPutGetOpcodeWithTypeSuffix("sput", destField.getType());
		return new Insn21c(opc, sourceReg, destField);
	}
	
	private Insn buildInstanceFieldPutInsn(InstanceFieldRef destRef, Value source) {
		SootField destSootField = destRef.getField();
		BuilderFieldReference destField = DexPrinter.toFieldReference(destSootField, belongingFile);
		Local instance = (Local) destRef.getBase();
		Register instanceReg = regAlloc.asLocal(instance);
		Register sourceReg = regAlloc.asImmediate(source, constantV);
		Opcode opc = getPutGetOpcodeWithTypeSuffix("iput", destField.getType());
		return new Insn22c(opc, sourceReg, instanceReg, destField);
	}

	private Insn buildArrayPutInsn(ArrayRef destRef, Value source) {
		Local array = (Local) destRef.getBase();
		Register arrayReg = regAlloc.asLocal(array);
		Value index = destRef.getIndex();
		Register indexReg = regAlloc.asImmediate(index, constantV);
		Register sourceReg  = regAlloc.asImmediate(source, constantV);
		String arrayTypeDescriptor = SootToDexUtils.getArrayTypeDescriptor((ArrayType) array.getType());
		Opcode opc = getPutGetOpcodeWithTypeSuffix("aput", arrayTypeDescriptor);
		return new Insn23x(opc, sourceReg, arrayReg, indexReg);
	}
	
	private Insn buildArrayFillInsn(ArrayRef destRef, List<Value> values) {
		Local array = (Local) destRef.getBase();
		Register arrayReg = regAlloc.asLocal(array);
		
		// Convert the list of values into a list of numbers
		int elementSize = 0;
		List<Number> numbers = new ArrayList<Number>(values.size());
		for (Value val : values) {
			if (val instanceof IntConstant) {
				elementSize = Math.max(elementSize, 4);
				numbers.add(((IntConstant) val).value);
			}
			else if (val instanceof LongConstant) {
				elementSize = Math.max(elementSize, 8);
				numbers.add(((LongConstant) val).value);				
			}
			else if (val instanceof FloatConstant) {
				elementSize = Math.max(elementSize, 4);
				numbers.add(((FloatConstant) val).value);				
			}
			else if (val instanceof DoubleConstant) {
				elementSize = Math.max(elementSize, 8);
				numbers.add(((DoubleConstant) val).value);				
			}
			else
				return null;
		}
		
		// For some local types, we know the size upfront
		if (destRef.getType() instanceof BooleanType)
			elementSize = 1;
		else if (destRef.getType() instanceof ByteType)
			elementSize = 1;
		else if (destRef.getType() instanceof CharType)
			elementSize = 2;
		else if (destRef.getType() instanceof ShortType)
			elementSize = 2;
		else if (destRef.getType() instanceof IntType)
			elementSize = 4;
		else if (destRef.getType() instanceof FloatType)
			elementSize = 4;
		else if (destRef.getType() instanceof LongType)
			elementSize = 8;
		else if (destRef.getType() instanceof DoubleType)
			elementSize = 8;
		
		ArrayDataPayload payload = new ArrayDataPayload(elementSize, numbers);
		payloads.add(payload);
		Insn31t insn = new Insn31t(Opcode.FILL_ARRAY_DATA, arrayReg);
		insn.setPayload(payload);
		return insn;
	}
	
	private Insn buildStaticFieldGetInsn(Register destinationReg, StaticFieldRef sourceRef) {
		SootField sourceSootField = sourceRef.getField();
		BuilderFieldReference sourceField = DexPrinter.toFieldReference(sourceSootField, belongingFile);
		Opcode opc = getPutGetOpcodeWithTypeSuffix("sget", sourceField.getType());
		return new Insn21c(opc, destinationReg, sourceField);
	}
	
	private Insn buildInstanceFieldGetInsn(Register destinationReg, InstanceFieldRef sourceRef) {
		Local instance = (Local) sourceRef.getBase();
		Register instanceReg = regAlloc.asLocal(instance);
		SootField sourceSootField = sourceRef.getField();
		BuilderFieldReference sourceField = DexPrinter.toFieldReference(sourceSootField, belongingFile);
		Opcode opc = getPutGetOpcodeWithTypeSuffix("iget", sourceField.getType());
		return new Insn22c(opc, destinationReg, instanceReg, sourceField);
	}

	private Insn buildArrayGetInsn(Register destinationReg, ArrayRef sourceRef) {
		Value index = sourceRef.getIndex();
		Register indexReg = regAlloc.asImmediate(index, constantV);
		Local array = (Local) sourceRef.getBase();
		Register arrayReg = regAlloc.asLocal(array);
		String arrayTypeDescriptor = SootToDexUtils.getArrayTypeDescriptor((ArrayType) array.getType());
		Opcode opc = getPutGetOpcodeWithTypeSuffix("aget", arrayTypeDescriptor);
		return new Insn23x(opc, destinationReg, arrayReg, indexReg);
	}

	private Opcode getPutGetOpcodeWithTypeSuffix(String prefix, String fieldType) {
		prefix = prefix.toUpperCase();
		if (fieldType.equals("Z")) {
			return Opcode.valueOf(prefix + "_BOOLEAN");
		} else if (fieldType.equals("I") || fieldType.equals("F")) {
			return Opcode.valueOf(prefix);
		} else if (fieldType.equals("B")) {
			return Opcode.valueOf(prefix + "_BYTE");
		} else if (fieldType.equals("C")) {
			return Opcode.valueOf(prefix + "_CHAR");
		} else if (fieldType.equals("S")) {
			return Opcode.valueOf(prefix + "_SHORT");
		} else if (SootToDexUtils.isWide(fieldType)) {
			return Opcode.valueOf(prefix + "_WIDE");
		} else if (SootToDexUtils.isObject(fieldType)) {
			return Opcode.valueOf(prefix + "_OBJECT");
		} else {
			throw new RuntimeException("unsupported field type for *put*/*get* opcode: " + fieldType);
		}
	}

	private Insn getLastInvokeInsn() {
		// traverse backwards through the instructions, searching for "invoke-"
		ListIterator<Insn> listIterator = insns.listIterator(insns.size());
		while (listIterator.hasPrevious()) {
			Insn inst = listIterator.previous();
			if (inst.getOpcode().name.startsWith("invoke-")) {
				return inst;
			}
		}
		throw new Error("tried to get last invoke-* instruction, but there was none!");
	}

	private Insn buildMoveResultInsn(Register destinationReg) {
		// build it right after the invoke instruction (more than one instruction could have been generated)
		Opcode opc;
		if (SootToDexUtils.isObject(lastReturnTypeDescriptor)) {
			opc = Opcode.MOVE_RESULT_OBJECT;
		} else if (SootToDexUtils.isWide(lastReturnTypeDescriptor)) {
			opc = Opcode.MOVE_RESULT_WIDE;
		} else {
			opc = Opcode.MOVE_RESULT;
		}
		return new Insn11x(opc, destinationReg);
	}
	
	@Override
	public void caseInvokeStmt(InvokeStmt stmt) {
        exprV.setOrigStmt(stmt);
		stmt.getInvokeExpr().apply(exprV);
	}
	
	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
        addInsn(new Insn10x(Opcode.RETURN_VOID), stmt);
	}
	
	@Override
	public void caseReturnStmt(ReturnStmt stmt) {
		Value returnValue = stmt.getOp();
		constantV.setOrigStmt(stmt);
		Register returnReg = regAlloc.asImmediate(returnValue, constantV);
		Opcode opc;
		Type retType = returnValue.getType();
		if (SootToDexUtils.isObject(retType)) {
			opc = Opcode.RETURN_OBJECT;
		} else if (SootToDexUtils.isWide(retType)) {
			opc = Opcode.RETURN_WIDE;
		} else {
			opc = Opcode.RETURN;
		}
        addInsn(new Insn11x(opc, returnReg), stmt);
	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt) {
		Local lhs = (Local) stmt.getLeftOp();
		Value rhs = stmt.getRightOp();
		if (rhs instanceof CaughtExceptionRef) {
			// save the caught exception with move-exception
			Register localReg = regAlloc.asLocal(lhs);
			
            addInsn(new Insn11x(Opcode.MOVE_EXCEPTION, localReg), stmt);

            this.insnRegisterMap.put(insns.get(insns.size() - 1), LocalRegisterAssignmentInformation.v(localReg, (Local)lhs));
		} else if (rhs instanceof ThisRef || rhs instanceof ParameterRef) {
			/* 
			 * do not save the ThisRef or ParameterRef in a local, because it always has a parameter register already.
			 * at least use the local for further reference in the statements
			 */
			Local localForThis = (Local) lhs;
			regAlloc.asParameter(belongingMethod, localForThis);
			
			parameterInstructionsList.add(LocalRegisterAssignmentInformation.v(regAlloc.asLocal(localForThis).clone(), localForThis));
		} else {
			throw new Error("unknown Value as right-hand side of IdentityStmt: " + rhs);
		}
	}
	
	@Override
	public void caseGotoStmt(GotoStmt stmt) {
		Stmt target = (Stmt) stmt.getTarget();
        addInsn(buildGotoInsn(target), stmt);
	}
	
	private Insn buildGotoInsn(Stmt target) {
		if (target == null)
			throw new RuntimeException("Cannot jump to a NULL target");
		
		Insn10t insn = new Insn10t(Opcode.GOTO);
		insn.setTarget(target);
		return insn;
	}
	
	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
        exprV.setOrigStmt(stmt);
        constantV.setOrigStmt(stmt);
		// create payload that references the switch's targets
		List<IntConstant> keyValues = stmt.getLookupValues();
		int[] keys = new int[keyValues.size()];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = keyValues.get(i).value;
		}
		List<Unit> targets = stmt.getTargets();
		SparseSwitchPayload payload = new SparseSwitchPayload(keys, targets);
		payloads.add(payload);
		// create sparse-switch instruction that references the payload
		Value key = stmt.getKey();
		Stmt defaultTarget = (Stmt) stmt.getDefaultTarget();
		if (defaultTarget == stmt)
			throw new RuntimeException("Looping switch block detected");
        addInsn(buildSwitchInsn(Opcode.SPARSE_SWITCH, key, defaultTarget,
        		payload, stmt), stmt);
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt stmt) {
        exprV.setOrigStmt(stmt);
        constantV.setOrigStmt(stmt);
		// create payload that references the switch's targets
		int firstKey = stmt.getLowIndex();		
		List<Unit> targets = stmt.getTargets();
		PackedSwitchPayload payload = new PackedSwitchPayload(firstKey, targets);
		payloads.add(payload);
		// create packed-switch instruction that references the payload
		Value key = stmt.getKey();
		Stmt defaultTarget = (Stmt) stmt.getDefaultTarget();
        addInsn(buildSwitchInsn(Opcode.PACKED_SWITCH, key, defaultTarget,
        		payload, stmt), stmt);
	}
	
	private Insn buildSwitchInsn(Opcode opc, Value key, Stmt defaultTarget,
			SwitchPayload payload, Stmt stmt) {
		Register keyReg = regAlloc.asImmediate(key, constantV);
		Insn31t switchInsn = new Insn31t(opc, keyReg);
		switchInsn.setPayload(payload);
		payload.setSwitchInsn(switchInsn);
        addInsn(switchInsn, stmt);
		// create instruction to jump to the default target, always follows the switch instruction
		return buildGotoInsn(defaultTarget);
	}
	
	@Override
	public void caseIfStmt(IfStmt stmt) {
		Stmt target = stmt.getTarget();
        exprV.setOrigStmt(stmt);
		exprV.setTargetForOffset(target);
		stmt.getCondition().apply(exprV);
	}
	
	/**
	 * Pre-allocates and locks registers for the constants used in monitor
	 * expressions
	 * @param monitorConsts The set of monitor constants fow which to assign
	 * fixed registers
	 */
	public void preAllocateMonitorConsts(Set<ClassConstant> monitorConsts) {
		for (ClassConstant c : monitorConsts) {
			Register lhsReg = regAlloc.asImmediate(c, constantV);
			regAlloc.lockRegister(lhsReg);
			monitorRegs.put(c, lhsReg);
		}
	}
}