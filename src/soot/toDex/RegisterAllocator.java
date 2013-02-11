package soot.toDex;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
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

	public void asParameter(Local l) {
		// since a parameter in dex always has a register, we handle it like a new local without the need of a new register
		localToLastRegNum.put(l.getName(), nextRegNum);
		int wordsforParameters = SootToDexUtils.getDexWords(l.getType());
		nextRegNum += wordsforParameters;
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