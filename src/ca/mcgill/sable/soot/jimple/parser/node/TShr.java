package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TShr extends Token
{
    public TShr()
    {
        super.setText(">>");
    }

    public TShr(int line, int pos)
    {
        super.setText(">>");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TShr(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShr(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TShr text.");
    }
}
