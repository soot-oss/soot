package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TExtends extends Token
{
    public TExtends()
    {
        super.setText("extends");
    }

    public TExtends(int line, int pos)
    {
        super.setText("extends");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TExtends(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTExtends(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TExtends text.");
    }
}
