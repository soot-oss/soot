package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TLParen extends Token
{
    public TLParen()
    {
        super.setText("(");
    }

    public TLParen(int line, int pos)
    {
        super.setText("(");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLParen(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLParen(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLParen text.");
    }
}
