package soot.dava.internal.javaRep;

import soot.AbstractValueBox;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.tagkit.AbstractHost;

public class DArrayInitValueBox extends AbstractValueBox{
    public DArrayInitValueBox(Value value)
    {
        setValue(value);
    }

	public boolean canContainValue(Value value) {
		return (value instanceof DArrayInitExpr);
	}

}
