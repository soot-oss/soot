package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TProtected extends Token
{
    public TProtected()
    {
        super.setText("protected");
    }

    public TProtected(int line, int pos)
    {
        super.setText("protected");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TProtected(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTProtected(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TProtected text.");
    }
}
