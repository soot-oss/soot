package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TStatic extends Token
{
    public TStatic()
    {
        super.setText("static");
    }

    public TStatic(int line, int pos)
    {
        super.setText("static");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TStatic(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTStatic(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TStatic text.");
    }
}
