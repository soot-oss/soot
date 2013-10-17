package soot.dexpler.typing;

import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;

public class UntypedIntOrFloatConstant extends UntypedConstant {

    public final int value;

    private UntypedIntOrFloatConstant(int value)
    {
        this.value = value;
    }

    public static UntypedIntOrFloatConstant v(int value)
    {
        return new UntypedIntOrFloatConstant(value);
    }

    public boolean equals(Object c)
    {
        return c instanceof UntypedIntOrFloatConstant && ((UntypedIntOrFloatConstant) c).value == this.value;
    }

    /** Returns a hash code for this DoubleConstant object. */
    public int hashCode()
    {
        return (int)(value^(value>>>32));
    }
    
    public FloatConstant toFloatConstant() {
        return  FloatConstant.v(Float.intBitsToFloat((int) value));
    }
    
    public IntConstant toIntConstant() {
        return IntConstant.v(value);
    }

}
