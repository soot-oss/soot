package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TAnd extends Token
{
    public TAnd()
    {
        super.setText("&");
    }

    public TAnd(int line, int pos)
    {
        super.setText("&");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TAnd(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTAnd(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TAnd text.");
    }
}
