package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TAbstract extends Token
{
    public TAbstract()
    {
        super.setText("abstract");
    }

    public TAbstract(int line, int pos)
    {
        super.setText("abstract");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TAbstract(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTAbstract(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TAbstract text.");
    }
}
