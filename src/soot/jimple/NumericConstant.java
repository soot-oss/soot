package soot.jimple;

import soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public abstract class NumericConstant extends Constant
{
    // PTC 1999/06/28
    public abstract NumericConstant add(NumericConstant c);

    public abstract NumericConstant subtract(NumericConstant c);

    public abstract NumericConstant multiply(NumericConstant c);

    public abstract NumericConstant divide(NumericConstant c);

    public abstract NumericConstant remainder(NumericConstant c);

    public abstract NumericConstant equalEqual(NumericConstant c);

    public abstract NumericConstant notEqual(NumericConstant c);

    public abstract NumericConstant lessThan(NumericConstant c);

    public abstract NumericConstant lessThanOrEqual(NumericConstant c);

    public abstract NumericConstant greaterThan(NumericConstant c);

    public abstract NumericConstant greaterThanOrEqual(NumericConstant c);

    public abstract NumericConstant negate();
}
