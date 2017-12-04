package soot.toDex;

import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.JimpleValueSwitch;
import soot.util.Switch;

public class TemporaryRegisterLocal implements Local {
	private static final long serialVersionUID = 1L;
	private Type type;

	public TemporaryRegisterLocal(Type regType) {
		setType(regType);
	}
	
	public Local clone() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public final List<ValueBox> getUseBoxes() {
		return Collections.emptyList();
	}


	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void toString(UnitPrinter up) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void apply(Switch sw) {
		((JimpleValueSwitch) sw).caseLocal(this);
	}

	@Override
	public boolean equivTo(Object o) {
		return this.equals(o);
	}

	@Override
	public int equivHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public void setNumber(int number) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public int getNumber() {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public String getName() {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void setName(String name) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void setType(Type t) {
		this.type = t;
	}

}
