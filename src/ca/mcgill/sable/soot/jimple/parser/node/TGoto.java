package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TGoto extends Token
{
    public TGoto()
    {
        super.setText("goto");
    }

    public TGoto(int line, int pos)
    {
        super.setText("goto");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TGoto(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTGoto(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TGoto text.");
    }
}
