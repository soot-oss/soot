package soot.toDex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import soot.Local;
import soot.NullType;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JimpleLocal;

/**
 * An allocator for registers. It keeps track of locals to re-use their registers.<br>
 * <br>
 * Note that a register number can increase beyond 65535 / 16 bit, since the instruction formats
 * should check for their register limits themselves.
 */
public class RegisterAllocator {
	
	private int nextRegNum;
	
	private Map<String, Integer> localToLastRegNum;
	
	private int paramRegCount;
	
	public RegisterAllocator() {
		localToLastRegNum = new HashMap<String, Integer>();
	}
	
	//
	// Keep the same register for immediate constants. 
	// Tested on application uk.co.nickfines.RealCalc.apk, sha256:
	// 5386d024d135d270ecba3ac5c11b23609b8510184c440647a60690d6b2c957ab
	//
	// Results on 992 methods:
	// - on average 4.89 less registers
	// - bigger differences in methods initializing a lot of data:
	//  from 603 to 16
	//  from 482 to  6
	//  from 376 to 14
	//  from 142 to 74
	//
	// Having the smallest number of registers is important.
	// If there are too many register, the VM can reject the method.
	// In fact that is what happens with RealCalc and the method with
	// 603 registers (Android 2.2 on emulator); the VM stops and prints 
	// the following message:
	// "W/dalvikvm(  804): VFY: arbitrarily rejecting large method 
	// (regs=603 count=4980)"
	//
	// The following constants are considered here:
	//
	// soot.Constant
	// |- ClassConstant, 
	// |- NullConstant, 
	// |- NumericConstant, 
	//    |- FloatConstant
	//    ...
	// |- StringConstant
	//
	// In some cases there can be multiple constants of the same type:
	// - method invocation with multiple parameters
	// - array reference in assignment (ex: a[1] = 2)
	// - multi-dimension array initialization (ex: a = new int[1][2][3])
	//
	List<Register> classConstantReg = new ArrayList<Register>();
	List<Register> nullConstantReg = new ArrayList<Register>();
	List<Register> floatConstantReg = new ArrayList<Register>();
	List<Register> intConstantReg = new ArrayList<Register>();
	List<Register> longConstantReg = new ArrayList<Register>();
	List<Register> doubleConstantReg = new ArrayList<Register>();
	List<Register> stringConstantReg = new ArrayList<Register>();
	AtomicInteger classI = new AtomicInteger(0);
	AtomicInteger nullI = new AtomicInteger(0);
	AtomicInteger floatI = new AtomicInteger(0);
	AtomicInteger intI = new AtomicInteger(0);
	AtomicInteger longI = new AtomicInteger(0);
	AtomicInteger doubleI = new AtomicInteger(0);
	AtomicInteger stringI = new AtomicInteger(0);
	
	private Register asConstant(Value v, ConstantVisitor constantV) {
		Constant c = (Constant) v;
		
		Register constantRegister = null;
		
		List<Register> rArray = null;
		AtomicInteger iI = null;
		if (c instanceof ClassConstant) {
		    rArray = classConstantReg;
		    iI = classI;
		} else if (c instanceof NullConstant) {
            rArray = nullConstantReg;
            iI = nullI;
        } else if (c instanceof FloatConstant) {
            rArray = floatConstantReg;
            iI = floatI;
        } else if (c instanceof IntConstant) {
            rArray = intConstantReg;
            iI = intI;
        } else if (c instanceof LongConstant) {
            rArray = longConstantReg;
            iI = longI;
        } else if (c instanceof DoubleConstant) {
            rArray = doubleConstantReg;
            iI = doubleI;
        } else if (c instanceof StringConstant) {
            rArray = stringConstantReg;
            iI = stringI;
        } else {
            throw new RuntimeException("Error. Unknown constant type: '"+ c.getType() +"'");
        }
		
		if (rArray.size() == 0 || iI.intValue() >= rArray.size()) {
		    rArray.add(new Register(c.getType(), nextRegNum));
		    nextRegNum += SootToDexUtils.getDexWords(c.getType());
		}
		
		if (isMultipleConstantsPossible()) {
		    constantRegister = rArray.get(iI.intValue()).clone();  
		    iI.set(iI.intValue() + 1);
		} else {
		    constantRegister = rArray.get(0).clone();  
		}

		// "load" constant into the register...
		constantV.setDestination(constantRegister);
		c.apply(constantV);
		// ...but return an independent register object
		return constantRegister.clone();
	}
	
	private boolean multipleConstantsPossible = false;
	public void setMultipleConstantsPossible(boolean b) {
	    multipleConstantsPossible = b;
	    classI = new AtomicInteger(0);
	    nullI = new AtomicInteger(0);
	    floatI = new AtomicInteger(0);
	    intI = new AtomicInteger(0);
	    longI = new AtomicInteger(0);
	    doubleI = new AtomicInteger(0);
	    stringI = new AtomicInteger(0);
	}
	public boolean isMultipleConstantsPossible() {
	    return multipleConstantsPossible;
	}

	public Register asLocal(Value v) {
		Local l = (Local) v;
		String localName = l.getName();
		Register localRegister;
		if (localToLastRegNum.containsKey(localName)) {
			// reuse the reg num last seen for this local, since this is where the content is
			int oldRegNum = localToLastRegNum.get(localName);
			localRegister = new Register(l.getType(), oldRegNum);
		} else {
			// use a new reg num for this local
			localRegister = new Register(l.getType(), nextRegNum);
			localToLastRegNum.put(localName, nextRegNum);
			nextRegNum += SootToDexUtils.getDexWords(l.getType());
		}
		return localRegister;
	}

	public void asParameter(SootMethod sm, Local l) {
		// If we already have a register for this parameter, there is nothing
		// more to be done here.
		if (localToLastRegNum.containsKey(l.getName()))
			return;
		
		// since a parameter in dex always has a register, we handle it like a new local without the need of a new register
		// Register allocation is fixed! 0 for this, 1...n for parameters. We do not expect
		// the IdentityStmts in the body in any fixed order, so we directly calculate
		// the correct register number.
		int paramRegNum = 0;
		boolean found = false;
		if (!sm.isStatic()) {
			//there might be bodies that do not have a this-local; ignore these gracefully
			try {
				if(sm.getActiveBody().getThisLocal() == l) { 
					paramRegNum = 0;
					found = true;
				}
			} catch(RuntimeException e) {
				//ignore
			}
		}
		if(!found)
			for (int i = 0; i < sm.getParameterCount(); i++) {
				if (sm.getActiveBody().getParameterLocal(i) == l) {
					// For a non-static method, p0 is <this>.
					if (!sm.isStatic())
						paramRegNum++;
					found = true;
					break;
				}

				// Long and Double values consume two registers
				Type paramType = sm.getParameterType(i);
				paramRegNum += SootToDexUtils.getDexWords(paramType);
			}
		if(!found)
			throw new RuntimeException("Parameter local not found");
		
		localToLastRegNum.put(l.getName(), paramRegNum);
		int wordsforParameters = SootToDexUtils.getDexWords(l.getType());
		nextRegNum = Math.max(nextRegNum + wordsforParameters, paramRegNum + wordsforParameters);
		paramRegCount += wordsforParameters;
	}

	public Register asImmediate(Value v, ConstantVisitor constantV) {
		if (v instanceof Constant) {
			 return asConstant(v, constantV);
		} else if (v instanceof Local) {
			return asLocal(v);
		} else {
			throw new RuntimeException("expected Immediate (Constant or Local), but was: " + v.getClass());
		}
	}
	
	public Register asTmpReg(Type regType) {
		String tmpRegName = "tmp" + getRegCount();
		return asLocal(new JimpleLocal(tmpRegName, regType));
	}
	
	public void increaseRegCount(int amount) {
		nextRegNum += amount;
	}

	public int getParamRegCount() {
		return paramRegCount;
	}
	
	public int getRegCount() {
		return nextRegNum;
	}
}