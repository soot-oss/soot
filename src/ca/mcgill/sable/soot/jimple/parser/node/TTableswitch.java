package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TTableswitch extends Token
{
    public TTableswitch()
    {
        super.setText("tableswitch");
    }

    public TTableswitch(int line, int pos)
    {
        super.setText("tableswitch");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TTableswitch(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTTableswitch(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TTableswitch text.");
    }
}
