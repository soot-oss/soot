package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TDot extends Token
{
    public TDot()
    {
        super.setText(".");
    }

    public TDot(int line, int pos)
    {
        super.setText(".");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TDot(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDot(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TDot text.");
    }
}
