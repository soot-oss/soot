package soot.jimple;

import soot.*;
import soot.util.*;
import java.util.*;

public abstract class ArithmeticConstant extends NumericConstant
{
    // PTC 1999/06/28
    public abstract ArithmeticConstant and(ArithmeticConstant c);

    public abstract ArithmeticConstant or(ArithmeticConstant c);

    public abstract ArithmeticConstant xor(ArithmeticConstant c);

    public abstract ArithmeticConstant shiftLeft(ArithmeticConstant c);

    public abstract ArithmeticConstant shiftRight(ArithmeticConstant c);

    public abstract ArithmeticConstant unsignedShiftRight(ArithmeticConstant c);

}
