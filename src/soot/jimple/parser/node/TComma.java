package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TComma extends Token
{
    public TComma()
    {
        super.setText(",");
    }

    public TComma(int line, int pos)
    {
        super.setText(",");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TComma(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTComma(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TComma text.");
    }
}
