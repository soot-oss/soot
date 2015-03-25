package soot.dexpler.typing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;

public class UntypedIntOrFloatConstant extends UntypedConstant {

	private static final Logger logger =LoggerFactory.getLogger(UntypedIntOrFloatConstant.class);

    /**
	 * 
	 */
	private static final long serialVersionUID = 4413439694269487822L;
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
