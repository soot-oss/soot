package soot.toDex;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.Type;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.internal.JimpleLocal;

public class RegisterAllocator {
	
	// this can increase beyond 65535 / 16 bit, since the insn formats should check for their reg limits
	private int nextRegNum;
	
	private Map<String, Integer> localToLastRegNum;
	
	private int paramRegCount;
	
	public RegisterAllocator() {
		localToLastRegNum = new HashMap<String, Integer>();
	}

	private Register asConstant(Value v, ConstantVisitor constantV) {
		Constant c = (Constant) v;
		Register constantRegister = new Register(c.getType(), nextRegNum);
		nextRegNum++;
		if (constantRegister.isWide()) {
			nextRegNum++;
		}
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
			nextRegNum++;
			if (localRegister.isWide()) {
				nextRegNum++;
			}
		}
		return localRegister;
	}

	public void asParameter(Local l) {
		// since a parameter in dex always has a register, we handle it like a new local without the need of a new register
		localToLastRegNum.put(l.getName(), nextRegNum);
		nextRegNum++;
		paramRegCount++;
		if (SootToDexUtils.isWide(l.getType())) {
			nextRegNum++;
			paramRegCount++;
		}
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