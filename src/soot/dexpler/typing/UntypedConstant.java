package soot.dexpler.typing;

import soot.Type;
import soot.jimple.Constant;
import soot.util.Switch;

public abstract class UntypedConstant extends Constant {
    @Override
    public Type getType() {
        throw new RuntimeException("no type yet!");
    }

    @Override
    public void apply(Switch sw) {
    }
}
