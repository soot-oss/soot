package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TBreakpoint extends Token
{
    public TBreakpoint()
    {
        super.setText("breakpoint");
    }

    public TBreakpoint(int line, int pos)
    {
        super.setText("breakpoint");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TBreakpoint(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTBreakpoint(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TBreakpoint text.");
    }
}
