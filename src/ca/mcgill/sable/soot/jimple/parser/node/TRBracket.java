package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TRBracket extends Token
{
    public TRBracket()
    {
        super.setText("]");
    }

    public TRBracket(int line, int pos)
    {
        super.setText("]");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TRBracket(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRBracket(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TRBracket text.");
    }
}
