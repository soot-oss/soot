package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TSpecialinvoke extends Token
{
    public TSpecialinvoke()
    {
        super.setText("specialinvoke");
    }

    public TSpecialinvoke(int line, int pos)
    {
        super.setText("specialinvoke");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TSpecialinvoke(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSpecialinvoke(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TSpecialinvoke text.");
    }
}
