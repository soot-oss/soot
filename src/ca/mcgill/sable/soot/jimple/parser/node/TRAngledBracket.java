package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TRAngledBracket extends Token
{
    public TRAngledBracket()
    {
        super.setText(">");
    }

    public TRAngledBracket(int line, int pos)
    {
        super.setText(">");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TRAngledBracket(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRAngledBracket(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TRAngledBracket text.");
    }
}
