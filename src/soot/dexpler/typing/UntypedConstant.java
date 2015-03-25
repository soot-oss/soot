package soot.dexpler.typing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Type;
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
}
