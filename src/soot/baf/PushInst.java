package soot.baf;

import soot.jimple.*;

public interface PushInst extends Inst
{
    public Constant getConstant();
    public void setConstant(Constant c);
}
