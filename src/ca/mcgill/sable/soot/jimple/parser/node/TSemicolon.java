package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TSemicolon extends Token
{
    public TSemicolon()
    {
        super.setText(";");
    }

    public TSemicolon(int line, int pos)
    {
        super.setText(";");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TSemicolon(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSemicolon(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TSemicolon text.");
    }
}
