package soot.toDex;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.Constant;
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

	private Register asConstant(Value v, ConstantVisitor constantV) {
		Constant c = (Constant) v;
		Register constantRegister = new Register(c.getType(), nextRegNum);
		nextRegNum += SootToDexUtils.getDexWords(c.getType());
		// "load" constant into the register...
		constantV.setDestination(constantRegister);
		c.apply(constantV);
		// ...but return an independent register object
		return constantRegister.clone();
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
		int paramRegNum = -1;
		if (!sm.isStatic() && sm.getActiveBody().getThisLocal() == l)
			paramRegNum = 0;
		else
			for (int i = 0; i < sm.getParameterCount(); i++)
				if (sm.getActiveBody().getParameterLocal(i) == l) {
					paramRegNum = i;
					if (!sm.isStatic())
						paramRegNum++;
					break;
				}
		if (paramRegNum < 0)
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