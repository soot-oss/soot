package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.jimple.*;

public interface PushInst extends Inst
{
    public Constant getConstant();
    public void setConstant(Constant c);
}
