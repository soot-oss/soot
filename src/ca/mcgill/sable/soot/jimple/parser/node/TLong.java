package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TLong extends Token
{
    public TLong()
    {
        super.setText("long");
    }

    public TLong(int line, int pos)
    {
        super.setText("long");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLong(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLong(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLong text.");
    }
}
