package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TFloatDegenerate extends Token
{
    public TFloatDegenerate(String text)
    {
        setText(text);
    }

    public TFloatDegenerate(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TFloatDegenerate(getText(), getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTFloatDegenerate(this);
    }
}
