package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TLBracket extends Token
{
    public TLBracket()
    {
        super.setText("[");
    }

    public TLBracket(int line, int pos)
    {
        super.setText("[");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLBracket(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLBracket(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLBracket text.");
    }
}
