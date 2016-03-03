package soot.cil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import soot.Body;
import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.MethodSource;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.VoidType;
import soot.cil.ast.CilLocal;
import soot.cil.ast.CilLocal.TypeFlag;
import soot.cil.ast.CilLocalSet;
import soot.cil.ast.CilTrap;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JimpleLocal;

import com.google.common.collect.Lists;

class CilMethodSource implements MethodSource {
	
	/* -state fields- */
	private List<Cil_BranchStmt> jumpList;
	
	private Map<String, Unit> units;
	private List<Cil_SwitchStmtWrapper> switchStmts;
	
	private JimpleBody body;
	
	/* -const fields- */
	private final List<Cil_Instruction> instructions;
	
	private final CilLocalSet localVars;
	private final Map<CilLocal, Local> cilLocalsToLocals = new HashMap<CilLocal, Local>();
	
	private final List<CilTrap> traps;
	private List<Local> parameters = new LinkedList<Local>();
	private final List<String> paramtersNames;
	private LocalGenerator localGenerator = null;
	
	CilMethodSource(List<Cil_Instruction> insns,
			CilLocalSet localVars, 
			List<CilTrap> tryCatchBlocks,
			List<String> paramtersNames, 
			Map<String, String> genericFunctionType,
			List<String> genericFunctionTypeList,
			Map<String, String> genericClassType) {
		this.instructions = insns;
		this.localVars = localVars;
		this.traps = tryCatchBlocks;
		this.paramtersNames = paramtersNames;
	}
	
	private void addUnit(Unit unit, String label) {
		this.units.put(label, unit);
		
		this.body.getUnits().add(unit);
	}
		
	private void emitLocals() {
		JimpleBody jb = body;
		SootMethod m = jb.getMethod();
		Collection<Unit> jbu = jb.getUnits();
		
		// Generate the "this" local if necessary. It becomes argument 0.
		if (!m.isStatic()) {
			Type thisType = m.getDeclaringClass().getType();
			Local l = Jimple.v().newLocal("this", thisType);
			jb.getLocals().add(l);

			jbu.add(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(m.getDeclaringClass().getType())));
			this.parameters.add(l);
		}
		
		// Create locals for all the the parameters
		for (int i=0; i < m.getParameterTypes().size(); i++) {
			Type type = m.getParameterType(i);
			String name = this.paramtersNames.get(i);
			
			JimpleLocal local = new JimpleLocal(name, type);
			jb.getLocals().add(local);

			jbu.add(Jimple.v().newIdentityStmt(local, Jimple.v().newParameterRef(type, i)));
			this.parameters.add(local);
		} 
		
		// TODO: check doubles
		
		// Create the remaining locals
		for (CilLocal local : localVars) {
			Type type = Cil_Utils.getSootType(local.getType());
			Local jLocal= new JimpleLocal(local.getName(), type);
			jb.getLocals().add(jLocal);
			cilLocalsToLocals.put(local, jLocal);
			
			// Value types are implicitly initialized in CIL. In Jimple, we need
			// to make this explicit. We always call the default constructor. The
			// CLI would instead zero out all the memory. In C#, you cannot have
			// a custom parameterless constructor, so we fake one.
			// Ugly: In CIL, you could have one, but it wouldn't be called on
			// struct uses without explicitly being called :-(
			if (type instanceof RefType && local.getTypeFlag() == TypeFlag.ValueType) {
				RefType refType = (RefType) type;
				jbu.add(Jimple.v().newAssignStmt(jLocal, Jimple.v().newNewExpr(refType)));
				jbu.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(jLocal,
						Scene.v().makeConstructorRef(refType.getSootClass(),
								Collections.<Type>emptyList()))));
			}
		}
	}
	
	private void emitUnits() {
		Deque<Value> stack = new ArrayDeque<Value>(); 

		for(int i=0; i<this.instructions.size(); ++i) {
			Cil_Instruction inst = this.instructions.get(i);
			Cil_Instruction inst_next = null;
			
			String opcode = inst.getOpcode();
			String label = inst.getLabel();
			List<String> parameters = inst.getParameters();
			
			if(i<this.instructions.size()-1) {
				inst_next = this.instructions.get(i+1);
			}
			
			if(opcode.equals("nop")
					|| opcode.equals("break")) {
				// do nothing
			}
			else if(opcode.equals("ret")) {
				if(stack.isEmpty()) {
					this.addUnit(Jimple.v().newReturnVoidStmt(), label);
				} else {
					Value value = stack.pop();
					this.addUnit(Jimple.v().newReturnStmt(value), label); 
				}
			}
			// Load Constant
			else if(opcode.startsWith("ldc")) {
				emitUnitsLDC(stack, inst);
			}
			else if(opcode.startsWith("stloc")) {
				Value rvalue = stack.pop();
				String local_idx;
				
				if(opcode.equals("stloc.s") || opcode.equals("stloc")) {
					local_idx = inst.getParameters().get(0);
					CilLocal cilLocal = Cil_Utils.isNumeric(local_idx)
							? localVars.getLocalByID(Integer.parseInt(local_idx))
									: localVars.getLocalByName(local_idx);
										
					Local variable = cilLocalsToLocals.get(cilLocal);				
					this.addUnit(this.handleStore(rvalue, variable),label);
				} else if (opcode.startsWith("stloc.")) {
					local_idx = String.valueOf(opcode.charAt(opcode.length()-1));
					CilLocal cilLocal = localVars.getLocalByID(Integer.parseInt(local_idx));
					Local variable = cilLocalsToLocals.get(cilLocal);
					this.addUnit(this.handleStore(rvalue, variable),label);
				} 
			} else if(opcode.startsWith("ldloc")) {
				// We do not distinguish between address and content loads yet
				if(opcode.equals("ldloc.s") || opcode.equals("ldloc")
						|| opcode.equals("ldloca") || opcode.equals("ldloca.s")) {
					String idx_local = inst.getParameters().get(0);
					CilLocal cilLocal = Cil_Utils.isNumeric(idx_local)
							? localVars.getLocalByID(Integer.parseInt(idx_local))
									: localVars.getLocalByName(idx_local);
										
					Local variable = cilLocalsToLocals.get(cilLocal);				
					stack.push(variable);
				} else if(opcode.startsWith("ldloc.")) {
					String local_idx = String.valueOf(opcode.charAt(opcode.length()-1));
					CilLocal cilLocal = localVars.getLocalByID(Integer.parseInt(local_idx));
					Local variable = cilLocalsToLocals.get(cilLocal);
					stack.push(variable);
				}
			} else if(opcode.startsWith("ldarg")) {
				String variable;
				Local l = null;
				
				// We do not distinguish loading addresses from loading
				// values at the moment
				if (opcode.equals("ldarg.s") || opcode.equals("ldarg")
						|| opcode.equals("ldarga") || opcode.equals("ldarga.s")) {
					variable = inst.getParameters().get(0);
					if(!Cil_Utils.isNumeric(variable)){
						for(int j=0; j<this.paramtersNames.size(); ++j) {
							String s = this.paramtersNames.get(j);
							if(s.equals(variable)) {
								l = this.parameters.get(j);
								break;
							}
						}
					} else {
						int idx = Integer.parseInt(variable);
						l = this.parameters.get(idx);
					}
					stack.push(l); 
				} else if(opcode.startsWith("ldarg.")) {
					char param = opcode.charAt(opcode.length()-1);
					int idx = Character.getNumericValue(param);
					l = this.parameters.get(idx);
					stack.push(l); 
				} 
			} else if(opcode.startsWith("starg")) {
				String variable;
				Local l = null;
				Value rvalue = stack.pop();
				
				if(opcode.equals("starg.s") || opcode.equals("starg")) {
					variable = inst.getParameters().get(0);
					
					if(!Cil_Utils.isNumeric(variable)){
						for(int j=0; j<this.paramtersNames.size(); ++j) {
							String s = this.paramtersNames.get(j);
							if(s.equals(variable)) {
 								l = this.parameters.get(j);
								break;
							}
						}
					} else {
						int idx = Integer.parseInt(variable);
						l = this.parameters.get(idx);
					}
					this.addUnit(this.handleStore(rvalue, l),label);

				} else if(opcode.startsWith("starg.")) {
					char param = opcode.charAt(opcode.length()-1);
					int idx = Character.getNumericValue(param);
					l = this.parameters.get(idx);
					this.addUnit(this.handleStore(rvalue, l),label);
 
				} 
			} else if(opcode.equals("add")
					|| opcode.equals("add.ovf")
					|| opcode.equals("add.ovf.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newAddExpr(var2, var1);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType() ,label);
				} else {
					stack.push(value); 
				} 
				
			} else if(opcode.equals("sub")
					|| opcode.equals("sub.ovf")
					|| opcode.equals("sub.ovf")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newSubExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				} 
				
			} else if(opcode.equals("mul")
					|| opcode.equals("mul.ovf")
					|| opcode.equals("mul.ovf")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newMulExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				} 
				
			} else if(opcode.equals("div") 
					|| opcode.equals("div.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newDivExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				} 
			} else if(opcode.equals("rem")
					|| opcode.equals("rem.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newRemExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("and")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newAndExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("or")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newOrExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("xor")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newXorExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType() ,label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("shl")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newShlExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(),label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("shr")
					|| opcode.equals("shr.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newShrExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(), label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("neg")) {
				Value var = stack.pop();
				Value value = Jimple.v().newNegExpr(var);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var.getType() ,label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("not")) {
				Value var = stack.pop();
				Value value = Jimple.v().newNegExpr(var);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var.getType(),label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("stsfld")) {
				Value rvalue = stack.pop(); 
				SootFieldRef fieldRef = getFieldRef(inst.getParameters(), true);
				Value variable = Jimple.v().newStaticFieldRef(fieldRef);
				Unit u = Jimple.v().newAssignStmt(variable, rvalue);
				this.addUnit(u, label);
			} else if(opcode.equals("ldsfld")) {
				SootFieldRef fieldRef = getFieldRef(inst.getParameters(), true);
				Value value = Jimple.v().newStaticFieldRef(fieldRef);
				this.createLocalForChainedStamt(stack, value, fieldRef.type(), label);
			}
			else if(opcode.equals("newobj")) {				
				String className = inst.getParameters().get(inst.getParameters().size()-1);
				int pos = className.indexOf(":");
				className = className.substring(0, pos);
								
				RefType type = (RefType) Cil_Utils.getSootType(className); 
				Value value = Jimple.v().newNewExpr(type);
				this.createLocalForChainedStamt(stack, value, type, label);
				
				Local base = (Local) stack.pop();
				
				SootMethodRef method = getMethodRef(inst.getParameters(), false); 
				
				int numberOfParameter = method.parameterTypes().size();
				List<Value> args = new ArrayList<Value>(numberOfParameter);
				for(int j=0; j<numberOfParameter; ++j) {
					args.add(stack.pop());
				}
				args = Lists.reverse(args);
				
				Unit v = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(base, method, args));
				this.addUnit(v, label); 
				stack.push(base); 
				
			}
			else if(opcode.equals("call")) {
				emitUnitsCall(stack, inst, false);
			}
			else if(opcode.equals("callvirt")) {
				emitUnitsCall(stack, inst, true);
			} else if(opcode.equals("ldnull")) {
				Value value = NullConstant.v();
				stack.push(value);
			} else if(opcode.equals("pop")) {
				if(!stack.isEmpty()) {
					stack.pop();
				}
			} else if(opcode.equals("stfld")) {
				Value rvalue = stack.pop();
				Local base = (Local) stack.pop();
				SootFieldRef fieldRef = getFieldRef(inst.getParameters(), false);
				
				Value value = Jimple.v().newInstanceFieldRef(base, fieldRef);
				this.addUnit(this.handleStore(rvalue, value),label);
				
			}
			// We do not distinguish field contents and field addresses yet
			else if(opcode.equals("ldfld") || opcode.equals("ldflda")) {
				Local base = (Local) stack.pop();
				SootFieldRef fieldRef = getFieldRef(inst.getParameters(), false);
				
				Value value = Jimple.v().newInstanceFieldRef(base, fieldRef);
				this.createLocalForChainedStamt(stack, value, fieldRef.type(), label);
			} else if(opcode.equals("ceq")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newCmpExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(), label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("cgt") || opcode.equals("cgt.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newCmpgExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(), label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("clt") || opcode.equals("clt.un")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				Value value = Jimple.v().newCmplExpr(var2, var1);
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, var1.getType(), label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.startsWith("conv")) {
				Value var = stack.pop();
				Type type = UnknownType.v();
				
				if(opcode.equals("conv.i1")
						|| opcode.equals("conv.ovf.i1")
						|| opcode.equals("conv.ovf.i1.un")
						|| opcode.equals("conv.u1")
						|| opcode.equals("conv.ovf.u1")
						|| opcode.equals("conv.ovf.u1.un")) {
					type = ByteType.v();
				} else if(opcode.equals("conv.i2")
						|| opcode.equals("conv.ovf.i2")
						|| opcode.equals("conv.ovf.i2.un")
						|| opcode.equals("conv.u2")
						|| opcode.equals("conv.ovf.u2")
						|| opcode.equals("conv.ovf.u2.un")) {
					type = ShortType.v();
				} else if(opcode.equals("conv.i4")
						|| opcode.equals("conv.ovf.i4")
						|| opcode.equals("conv.ovf.i4.un")
						|| opcode.equals("conv.u4")
						|| opcode.equals("conv.ovf.u4")
						|| opcode.equals("conv.ovf.u4.un")) {
					type = IntType.v();
				} else if(opcode.equals("conv.i8")
						|| opcode.equals("conv.ovf.8")
						|| opcode.equals("conv.ovf.i8.un")
						|| opcode.equals("conv.u8")
						|| opcode.equals("conv.ovf.u8")
						|| opcode.equals("conv.ovf.u8.un")) {
					type = LongType.v();
				} else if(opcode.equals("conv.r4") || opcode.equals("conv.r.un")) {
					type = FloatType.v();
				} else if(opcode.equals("conv.r8")) {
					type = DoubleType.v();
				} 
				
				Value value = Jimple.v().newCastExpr(var, type);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("ble")
					|| opcode.equals("ble.s")
					|| opcode.equals("ble.un")
					|| opcode.equals("ble.un.s")) {
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				
				String targetLabel = inst.getParameters().get(0);
				
				JLeExpr exp = new JLeExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
			
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
				
			} else if(opcode.equals("br")
					|| opcode.equals("br.s")
					|| opcode.equals("br.un")
					|| opcode.equals("br.un.s")
					|| opcode.equals("leave.s")
					|| opcode.equals("leave")) {
				String targetLabel = inst.getParameters().get(0);
			
				Unit target = null;
				Unit value = Jimple.v().newGotoStmt(target);
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			
			} else if(opcode.equals("blt")
					|| opcode.equals("blt.s")
					|| opcode.equals("blt.un")
					|| opcode.equals("blt.un.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				
				JLtExpr exp = new JLtExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("bgt")
					|| opcode.equals("bgt.s")
					|| opcode.equals("bgt.un")
					|| opcode.equals("bgt.un.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				 
				JGtExpr exp = new JGtExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("bge")
					|| opcode.equals("bge.s")
					|| opcode.equals("bge.un")
					|| opcode.equals("bge.un.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				 
				JGeExpr exp = new JGeExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			}  else if(opcode.equals("beq")
					|| opcode.equals("beq.s")
					|| opcode.equals("beq.un")
					|| opcode.equals("beq.un.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				 
				JEqExpr exp = new JEqExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("bne.un")
					|| opcode.equals("bne.un.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = stack.pop();
				 
				JNeExpr exp = new JNeExpr(var2, var1); 
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("brfalse")
					|| opcode.equals("brfalse.s")
					|| opcode.equals("brnull")
					|| opcode.equals("brnull.s")
					|| opcode.equals("brzero")
					|| opcode.equals("brzero.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				
				Value var2 = IntConstant.v(0);
				
				if(opcode.equals("brnull") 
						|| opcode.equals("brnull.s")
						|| var1.getType() instanceof RefType) {
					var2 = NullConstant.v();
				} 
				
				JEqExpr exp = new JEqExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("brtrue")
					|| opcode.equals("brtrue.s")
					|| opcode.equals("brinst")
					|| opcode.equals("brinst.s")) {
				String targetLabel = inst.getParameters().get(0);
				
				Value var1 = stack.pop();
				Value var2 = IntConstant.v(0);
				
				if(opcode.equals("brinst") || opcode.equals("brinst.s")) {
					var2 = NullConstant.v();
				} 
				 
				JNeExpr exp = new JNeExpr(var2, var1);
				Unit target = null;
				Unit value = Jimple.v().newIfStmt(exp, target); 
				
				this.jumpList.add(new Cil_BranchStmt(label, targetLabel, value));
				this.addUnit(value, label);
			} else if(opcode.equals("switch")) {
				List<String> targetLabels = getSwitchTargetLabes(parameters.get(0));
				
				Value var = stack.pop();
				
				Unit placeholderUnit = Jimple.v().newNopStmt();
				this.addUnit(placeholderUnit, label);
				
				Unit afterUnit = Jimple.v().newNopStmt();
				body.getUnits().add(afterUnit);
				
				Cil_SwitchStmtWrapper stmt = new Cil_SwitchStmtWrapper(targetLabels,
						afterUnit, placeholderUnit, var, label);
				this.switchStmts.add(stmt);
				
			} else if(opcode.equals("newarr")) { 
				String classs = parameters.get(parameters.size()-1);
				IntConstant size = (IntConstant) stack.pop();
				
				classs = getClassNameFromSignature(classs);
				if(classs.equals("!T")) {
					classs = "System.Object";
				}
				
				Type type = Cil_Utils.getSootType(classs); 
				Value value = Jimple.v().newNewArrayExpr(type, size);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
			} else if (opcode.startsWith("stelem")) {
				Value variable = stack.pop();
				Value index = stack.pop();
				Value array = stack.pop();
				Value value;
				Unit unit = null;
				
				value = Jimple.v().newArrayRef(array, index); 
				unit = Jimple.v().newAssignStmt(value, variable);
				
				addUnit(unit, label); 
			} else if (opcode.startsWith("ldelem")) {
				Value index = stack.pop();
				Value array = stack.pop();
				
				Value value = Jimple.v().newArrayRef(array, index);
				
				Type baseType = UnknownType.v();
				if(!parameters.isEmpty()) {
					String type = parameters.get(0);
					
					if(type.startsWith("!")) {
						type = "System.Object";
					}
					baseType = Cil_Utils.getSootType(type);
				}
				
				if(opcode.equals("ldelem.i1")
						|| opcode.equals("ldelem.u1")) {
					baseType = ByteType.v();
				} else if(opcode.equals("ldelem.i2")
						|| opcode.equals("ldelem.u2")) {
					baseType = ShortType.v();
				} else if(opcode.equals("ldelem.i4")
						|| opcode.equals("ldelem.u4")) {
					baseType = IntType.v();
				} else if(opcode.equals("ldelem.i8")
						|| opcode.equals("ldelem.u8")) {
					baseType = LongType.v();
				} else if(opcode.equals("ldelem.r4")) {
					baseType = FloatType.v();
				} else if(opcode.equals("ldelem.r8")) {
					baseType = DoubleType.v();
				}
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, baseType, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("ldstr")) {
				String val = parameters.get(0);
				val = val.substring(1, val.length()-1); 
				Value v = StringConstant.v(val);
				stack.push(v);
			} else if(opcode.equals("box")) {
				Value val = stack.pop();
				String type = getClassNameFromSignature(parameters.get(0));
				Type t = RefType.v(type); 
				Value value = Jimple.v().newCastExpr(val, t);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, t, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("unbox.any")) {
				Value val = stack.pop();
				String t = parameters.get(0);
				Type type = Cil_Utils.getSootType(Cil_Utils.clearString(t));
				Value value = Jimple.v().newCastExpr(val, type);
				 
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("castclass")) {
				Value var = stack.pop();
				String typeName = parameters.get(0); 
				Type type = Cil_Utils.getSootType(Cil_Utils.clearString(typeName));
				Value value = Jimple.v().newCastExpr(var, type);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
				
			} else if(opcode.equals("isinst")) {
				Value var = stack.pop();
				
				String typeName = parameters.get(0); 
				Type type = Cil_Utils.getSootType(Cil_Utils.clearString(typeName));
				Value value = Jimple.v().newInstanceOfExpr(var, type);
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("ldlen")) {
				Value var = stack.pop();
				Value value = Jimple.v().newLengthExpr(var);
				Type type = IntType.v();
				
				if(!opcodeIsStore(inst_next.getOpcode())) {
					this.createLocalForChainedStamt(stack, value, type, label);
				} else {
					stack.push(value); 
				}
			} else if(opcode.equals("dup")) {
				Value var = stack.pop();
				stack.push(var);
				stack.push(var);
			} else if(opcode.equals("leave.s")
					|| opcode.equals("leave")) {
			} else if(opcode.equals("endfinally")
					|| opcode.equals("endfault")) {
				Unit value = Jimple.v().newNopStmt();
				this.addUnit(value, label); 
			} else if(opcode.equals("throw")) {
				Value var = stack.pop();
				Unit unit = Jimple.v().newThrowStmt(var);
				this.addUnit(unit, label);
			} else if(opcode.equals("generic")) {
				SootMethod method = body.getMethod();
				
				SootClass superClass = method.getDeclaringClass().getSuperclass();
				SootMethod super_method = superClass.getMethodByName(method.getName());
				SootMethodRef methodRef = Scene.v().makeMethodRef(superClass, super_method.getName(), super_method.getParameterTypes(), super_method.getReturnType(), super_method.isStatic());
				
				List<Value> args = new ArrayList<Value>();
				
				for(int j=1; j<this.parameters.size(); ++j) {
					Local l = this.parameters.get(j);
					Type parameterType = super_method.getParameterType(j-1);
					if(l.getType().equals(parameterType)) {
						args.add(l);
					}
					else {
						Local tmp = localGenerator.generateLocal(parameterType);
						Value u2 = Jimple.v().newCastExpr(tmp, parameterType);
						Unit u = Jimple.v().newAssignStmt(tmp, u2);
						this.addUnit(u, label);
						
						args.add(tmp);
					}
				}
				
				Local base = body.getThisLocal(); 
				Value value = Jimple.v().newSpecialInvokeExpr(base, methodRef, args);
				
				
				if(method.getReturnType().equals(VoidType.v())) {
					Unit unit = Jimple.v().newInvokeStmt(value); 
					this.addUnit(unit, label);
					Unit retUnit = Jimple.v().newReturnVoidStmt();
					this.addUnit(retUnit, label);
				} else {
					Local tmp = localGenerator.generateLocal(method.getReturnType());
					if(super_method.getReturnType().equals(method.getReturnType())) {						
						Unit u = Jimple.v().newAssignStmt(tmp, value);
						this.addUnit(u, label);
					} else {
						Local tmp2 = localGenerator.generateLocal(super_method.getReturnType());						
						Unit u = Jimple.v().newAssignStmt(tmp2, value);
						Value u2 = Jimple.v().newCastExpr(tmp2, method.getReturnType());
						Unit u3 = Jimple.v().newAssignStmt(tmp, u2);
						this.addUnit(u, label);
						this.addUnit(u3, label);
					}
					
					
					Unit retUnit = Jimple.v().newReturnStmt(tmp);
					this.addUnit(retUnit, label);
				}
			}
			else if(opcode.equals("ldftn")) {
				emitUnitsLDFTN(stack, inst);
			}
			else if(opcode.equals("ldtoken")) {
				// Get the target data structure
				String refClassName = "";
				switch (Cil_Utils.getTokenType(parameters.get(0))) {
				case TypeRef:
					refClassName = G.v().soot_cil_CilNameMangling()
							.createMethodRefClassName(parameters.get(0));
					break;
				case MethodRef:
					refClassName = G.v().soot_cil_CilNameMangling()
							.createMethodRefClassName(parameters.get(0));
					break;
				case FieldRef:
					refClassName = G.v().soot_cil_CilNameMangling()
							.createFieldRefClassName(parameters.get(0));
					break;
				default:
					throw new RuntimeException("Unsupported token type");
				}
				
				// Create an instance of the correct data structure
				RefType tp = (RefType) Cil_Utils.getSootType(refClassName);
				Local refLocal = localGenerator.generateLocal(tp);
				body.getUnits().add(Jimple.v().newAssignStmt(refLocal, Jimple.v().newNewExpr(tp)));
				body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(refLocal,
						Scene.v().makeConstructorRef(tp.getSootClass(), Collections.<Type>emptyList()))));
				stack.push(refLocal);
			}

			else {
				System.out.println("Unknown Opcode: " + opcode);
			}
		} 
		
		updateBranchTarget();
		updateSwitch();
	}

	/**
	 * Emits Jimple statements for a load-function-pointer instruction in CIL
	 * @param stack The call stack
	 * @param inst The CIL call instruction
	 */
	private void emitUnitsLDFTN(Deque<Value> stack, Cil_Instruction inst) {
		// Get the fake dispatcher class
		String className = inst.getParameters().get(inst.getParameters().size() - 1);
		className = G.v().soot_cil_CilNameMangling().createDispatcherClassName(className);
		
		// Create an instance of our fake dispatcher class
		RefType tpDispatcher = (RefType) Cil_Utils.getSootType(className);
		Local locDispatcher = localGenerator.generateLocal(tpDispatcher);
		NewExpr newExpr = Jimple.v().newNewExpr(tpDispatcher);
		AssignStmt assign = Jimple.v().newAssignStmt(locDispatcher, newExpr);
		body.getUnits().add(assign);
		stack.push(locDispatcher);
		
		// Call the constructor
		SootMethodRef consRef = Scene.v().makeConstructorRef(tpDispatcher.getSootClass(),
				Collections.<Type>emptyList());
		InvokeStmt invCons = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(
				locDispatcher, consRef));
		body.getUnits().add(invCons);
	}

	/**
	 * Emits Jimple statements for a call instruction in CIL
	 * @param stack The call stack
	 * @param inst The CIL call instruction
	 * @param forceVirt True if the call is definitely virtual, otherwise false
	 * to automatically detect whether the call is virtual or static
	 */
	private void emitUnitsCall(Deque<Value> stack, Cil_Instruction inst,
			boolean forceVirt) {
		final List<String> parameters = new ArrayList<String>(inst.getParameters());
		final String opcode = inst.getOpcode();
		
		// Is this a static method?
		boolean isStatic = !forceVirt;
		if (!parameters.isEmpty() && parameters.get(0).equals("instance")) {
			parameters.remove(0);
			isStatic = false;
		}
		
		SootMethodRef method = getMethodRef(parameters, isStatic);
		int numberOfParameter = method.parameterTypes().size();
		
		List<Value> args = new ArrayList<Value>(numberOfParameter);
		
		for(int j=0; j<numberOfParameter; ++j) {
			args.add(stack.pop());
		}
		
		// Reverse argument list
		args = Lists.reverse(args);
		Value value = null;
		
		// Special handling for constructors. Classes are created using
		// "newobj".
		if (method.name().equals(".ctor")
				|| method.name().equals("<init>")) {
			Local base = (Local) stack.pop();
			
			// Make sure to also assign the object instance
			String className = parameters.get(parameters.size() - 1);
			className = className.substring(0, className.indexOf("::"));
			body.getUnits().add(Jimple.v().newAssignStmt(base, Jimple.v().newNewExpr(
					(RefType) Cil_Utils.getSootType(className))));
			
			// Then call the constructor
			value = Jimple.v().newSpecialInvokeExpr(base, method, args);
		}
		else if (method.isStatic() || isStatic) {
			value = Jimple.v().newStaticInvokeExpr(method, args);
		} 
		else {
			Local base = (Local) stack.pop();
			value = Jimple.v().newVirtualInvokeExpr(base, method, args);
		}
		
		if(!method.returnType().equals(VoidType.v()) && !opcodeIsStore(opcode)) {
			this.createLocalForChainedStamt(stack, value, method.returnType(), inst.getLabel());
		} else if(method.returnType().equals(VoidType.v()) && !opcodeIsStore(opcode)) {
			Unit u = Jimple.v().newInvokeStmt(value);
			this.addUnit(u, inst.getLabel());
		} else {
			stack.push(value);
		}
	}

	/**
	 * This method emits emits code for LDC* instructions
	 * @param stack The call stack
	 * @param inst The instruction to convert to Jimple
	 */
	private void emitUnitsLDC(Deque<Value> stack, Cil_Instruction inst) {
		final String opcode = inst.getOpcode();
		
		if(opcode.equals("ldc.i4.m1") || opcode.equals("ldc.i4.M1")) {
			Value v = IntConstant.v(-1);
			stack.push(v);
		} else if(opcode.equals("ldc.i4.s")) {
			String param = inst.getParameters().get(0);
			stack.push(IntConstant.v(Integer.parseInt(param)));
		} else if(opcode.startsWith("ldc.i4.")) {
			String value = String.valueOf(opcode.charAt(opcode.length()-1)); 
			stack.push(IntConstant.v(Integer.parseInt(value)));
		} else if(opcode.startsWith("ldc.i4")) {
			String value = inst.getParameters().get(0);
			int val = (int) Cil_Utils.parseInteger(value).longValue();
			stack.push(IntConstant.v(val));
		} else if(opcode.startsWith("ldc.i8")) {
			String value = inst.getParameters().get(0); 
			long val = Cil_Utils.parseInteger(value).longValue();
			stack.push(LongConstant.v(val));
		} else if(opcode.startsWith("ldc.r4")) {
			String value = inst.getParameters().get(0); 
			stack.push(FloatConstant.v(Float.parseFloat(value)));
		} else if(opcode.startsWith("ldc.r8")) {
			String value = inst.getParameters().get(0); 
			stack.push(DoubleConstant.v(Double.parseDouble(value)));
		}
	}
	
	/**
	 * Emits all exception handlers in the CIL code as traps
	 */
	private void emitTraps() {
		JimpleBody jb = body; 
		Collection<Trap> traps = jb.getTraps();
		
		for(CilTrap trap : this.traps) {
			Unit start = findNextUnitByLabel(trap.getTryStartLabel());
			Unit end = findNextUnitByLabel(trap.getTryEndLablel()); 
			Unit handler = findNextUnitByLabel(trap.getHandlerStartLabel());
			
			// If we don't have a catch type, this is a "finally" block
			SootClass exceptionClass;
			boolean isFinally = false;
			if (trap.getCatchType() == null) {
				exceptionClass = Scene.v().getSootClass("System.Exception");
				isFinally = true;
			}
			else
				exceptionClass = Scene.v().getSootClass(trap.getCatchType());
			
			Trap t = Jimple.v().newTrap(exceptionClass, start, end, handler);
			traps.add(t);
			
			// create caughtException statement
			Local tmp = localGenerator.generateLocal(exceptionClass.getType());
			
			Value a = Jimple.v().newCaughtExceptionRef();
			Unit u = Jimple.v().newIdentityStmt(tmp, a);
			this.body.getUnits().insertBefore(u, handler);
			
			// If we have a "finally" block, we need to duplicate it so that
			// it also gets executed if no exception is thrown
			if (isFinally) {
				// TODO
			}
			
		}
	}
	
	private Unit findNextUnitByLabel(String targetLabel) {
		Unit unit = null;
		if(this.units.containsKey(targetLabel)) {
			unit = this.units.get(targetLabel);
		} else {
			SortedSet<String> list = new TreeSet<String>(this.units.keySet());
			for(String label : list) {
				if(1 == compareTargetLabel(label, targetLabel)){
					unit = this.units.get(label);
					break;
				}
			}
		}
		return unit;
	}
	
	private List<String> getSwitchTargetLabes(String param) {
		List<String> list = new ArrayList<String>();
		
		String[] tokens = param.split(",");
		
		for(String token : tokens) {
			token = token.replace(",", " ");
			token = token.replace("(", " ");
			token = token.replace(")", " ");
			list.add(token.trim());
		}
		
		return list;
	}
	
	private void updateSwitch() {
		for(Cil_SwitchStmtWrapper switchWrapper : this.switchStmts) {
			Unit placeholderUnit = switchWrapper.getPlaceholder();
			Unit defaultTarget = switchWrapper.getDefaultTarget();
			
			List<Unit> targetList = new ArrayList<Unit>();
			for(String targetLabel: switchWrapper.getTargetLabels()) {
				Unit target = findNextUnitByLabel(targetLabel);
				targetList.add(target);
			}
			
			Unit value = Jimple.v().newTableSwitchStmt(switchWrapper.getVariable(),
					0, switchWrapper.getTargetLabels().size()-1, targetList, defaultTarget);
			
			this.units.put(switchWrapper.getLabel(), value);
			
			this.body.getUnits().swapWith(placeholderUnit, value);
		}
	}
	
	
	private void updateBranchTarget() {
		
		for(Cil_BranchStmt stmt : this.jumpList) {
			String targetLabel = stmt.getTargetLabel();
			Unit unit = stmt.getUnit();
			if(this.units.containsKey(targetLabel)) {
				Unit target = this.units.get(targetLabel);
				//unit.redirectJumpsToThisTo(target);
				if(unit instanceof IfStmt) {
					IfStmt tmp_stmt = (IfStmt) unit; 
					tmp_stmt.setTarget(target);
				} else if(unit instanceof GotoStmt) {
					GotoStmt tmp_stmt = (GotoStmt) unit;
					tmp_stmt.setTarget(target);
				}	
			} else {
				SortedSet<String> list = new TreeSet<String>(this.units.keySet());
				
				for(String label: list) {
					if(1 == compareTargetLabel(label, targetLabel)) {
						Unit target = this.units.get(label);
						if(unit instanceof IfStmt) {
							IfStmt tmp_stmt = (IfStmt) unit; 
							tmp_stmt.setTarget(target);
						} else if(unit instanceof GotoStmt) {
							GotoStmt tmp_stmt = (GotoStmt) unit;
							tmp_stmt.setTarget(target);
						}	
						 
						break;
					}
				}
			}
			
		}
	}
		
	/*
	 * -1 = Target is smaller than label
	 *  1 = Target is greater or equal than label 
	 */
	private int compareTargetLabel(String label, String target) {
		int ret = -1; 
		ret = convertLabelToInt(target) > convertLabelToInt(label) ? -1 : 1;
		return ret;
	}
	
	private int convertLabelToInt(String label) {
		int value = 0;
	
		String t = label.substring(3, label.length());
		value = Integer.parseInt(t, 16);
		return value;
	}
	
	private SootFieldRef getFieldRef(List<String> param, boolean isStatic) {
		String signature = param.get(param.size()-1);
		String className = getClassNameFromSignature(signature);
		SootClass declaringClass = ((RefType) Cil_Utils.getSootType(className)).getSootClass();
		
		int pos = signature.lastIndexOf(":");
		String fieldName = signature.substring(pos+1);
		
		String fieldType = param.get(0);
		
		Type type = Cil_Utils.getSootType(fieldType);
		SootFieldRef ref = Scene.v().makeFieldRef(declaringClass, fieldName, type, isStatic);
		return ref;
	}
	
 	private SootMethodRef getMethodRef(List<String> param, boolean isStatic) {
 		String signature="";		
 		for(String str : param) {
 			signature = signature + " "+ str;
 		}
 		
		return Cil_Utils.getMethodRef(signature, isStatic);
	}
	
	private String getClassNameFromSignature(String str) {
		str = Cil_Utils.removeTokenFromString(str, "class");
		if(str.contains("`")) {
			String type = str;
			int pos = type.indexOf(":"); 
			type = type.substring(0, pos);
			
			if(type.contains(" ")) {
				type = type.substring(type.lastIndexOf(" ")).trim();
			} else {
				type = type.trim();
			}
			str = type;			
		} else {
			if(str.startsWith("[")) {
				String assemblyName = Cil_Utils.getAssemblyNameFromClassSig(str);
				str = Cil_Utils.removeAssemblyRefs(str);
				Cil_Utils.addClassToAssemblyMap(str, assemblyName);
			}

			int pos2 = str.indexOf(":");
	
			if(pos2 == -1) {
				pos2 = str.length();
			}
			str = str.substring(0, pos2);
			if(str.contains(" ")) {
				pos2 = str.lastIndexOf(" ");
				str = str.substring(pos2);
			}
		}
		return str.trim();
	}
		
	private void createLocalForChainedStamt(Deque<Value> stack, Value v, Type type, String label) {		
		Local tmp = localGenerator.generateLocal(type);
		stack.push(tmp);
		this.addUnit(this.handleStore(v, tmp),label);
	}
	
	private boolean opcodeIsStore(String opcode) {
		boolean ret = false;
		
		if(opcode.startsWith("stloc") 
				||opcode.startsWith("starg")) {
			ret = true;
		}
		
		return ret;
	}
	
	private Unit handleStore(Value rvalue, Value variable) {
		Unit u = null;
		u = Jimple.v().newAssignStmt(variable, rvalue);
		return u;
	}
		
	@Override
	public Body getBody(SootMethod m, String phaseName) {
		if (!m.isConcrete())
			return null;			
		
		JimpleBody jb = Jimple.v().newBody(m);
		/* initialize */
		int nrInsn = instructions.size();
		units = new HashMap<String, Unit>(nrInsn);
		jumpList = new ArrayList<Cil_BranchStmt>();
		switchStmts = new ArrayList<Cil_SwitchStmtWrapper>();
		
		body = jb;
		localGenerator = new LocalGenerator(jb);
		
		/* build body (add units, locals, traps, etc.) */
		emitLocals();
		emitUnits();
		emitTraps();
		
		/* clean up */
		body = null;
		jumpList = null;
		switchStmts = null;
		
		try {
	       // PackManager.v().getPack("jb").apply(jb);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to apply jb to " + m, t);
		}
 		return jb;
 	}
}
