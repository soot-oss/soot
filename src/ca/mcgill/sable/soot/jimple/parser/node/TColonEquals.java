package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TColonEquals extends Token
{
    public TColonEquals()
    {
        super.setText(":=");
    }

    public TColonEquals(int line, int pos)
    {
        super.setText(":=");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TColonEquals(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTColonEquals(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TColonEquals text.");
    }
}
