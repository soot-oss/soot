package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TIf extends Token
{
    public TIf()
    {
        super.setText("if");
    }

    public TIf(int line, int pos)
    {
        super.setText("if");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TIf(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTIf(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TIf text.");
    }
}
