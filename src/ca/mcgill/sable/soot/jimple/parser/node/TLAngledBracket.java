package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TLAngledBracket extends Token
{
    public TLAngledBracket()
    {
        super.setText("<");
    }

    public TLAngledBracket(int line, int pos)
    {
        super.setText("<");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLAngledBracket(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLAngledBracket(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLAngledBracket text.");
    }
}
