package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TWith extends Token
{
    public TWith()
    {
        super.setText("with");
    }

    public TWith(int line, int pos)
    {
        super.setText("with");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TWith(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTWith(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TWith text.");
    }
}
