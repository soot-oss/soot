package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TLBrace extends Token
{
    public TLBrace()
    {
        super.setText("{");
    }

    public TLBrace(int line, int pos)
    {
        super.setText("{");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLBrace(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLBrace(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLBrace text.");
    }
}
