package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TMult extends Token
{
    public TMult()
    {
        super.setText("*");
    }

    public TMult(int line, int pos)
    {
        super.setText("*");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TMult(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTMult(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TMult text.");
    }
}
