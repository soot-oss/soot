package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface CheckCastInst extends Inst
{
    public RefType getCastType();
    public void setCastType(RefType type);
}
