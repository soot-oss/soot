package soot.dexpler.typing;

import soot.Type;
import soot.Value;
import soot.jimple.Constant;
import soot.util.Switch;

public abstract class UntypedConstant extends Constant {
    /**
	 * 
	 */
	private static final long serialVersionUID = -742448859930407635L;

	@Override
    public Type getType() {
        throw new RuntimeException("no type yet!");
    }

    @Override
    public void apply(Switch sw) {
    }

	public abstract Value defineType(Type type);
}
