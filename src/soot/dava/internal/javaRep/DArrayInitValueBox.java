package soot.dava.internal.javaRep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.AbstractValueBox;
import soot.Value;

public class DArrayInitValueBox extends AbstractValueBox{

	private static final Logger logger =LoggerFactory.getLogger(DArrayInitValueBox.class);
    public DArrayInitValueBox(Value value)
    {
        setValue(value);
    }

	public boolean canContainValue(Value value) {
		return (value instanceof DArrayInitExpr);
	}

}
