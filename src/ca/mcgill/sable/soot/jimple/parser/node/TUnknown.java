package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TUnknown extends Token
{
    public TUnknown()
    {
        super.setText("unknown");
    }

    public TUnknown(int line, int pos)
    {
        super.setText("unknown");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TUnknown(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTUnknown(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TUnknown text.");
    }
}
