package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TCmp extends Token
{
    public TCmp()
    {
        super.setText("cmp");
    }

    public TCmp(int line, int pos)
    {
        super.setText("cmp");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TCmp(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCmp(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TCmp text.");
    }
}
