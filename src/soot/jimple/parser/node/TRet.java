package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TRet extends Token
{
    public TRet()
    {
        super.setText("ret");
    }

    public TRet(int line, int pos)
    {
        super.setText("ret");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TRet(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRet(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TRet text.");
    }
}
