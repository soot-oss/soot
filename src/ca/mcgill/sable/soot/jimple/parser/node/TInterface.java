package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TInterface extends Token
{
    public TInterface()
    {
        super.setText("interface");
    }

    public TInterface(int line, int pos)
    {
        super.setText("interface");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TInterface(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTInterface(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TInterface text.");
    }
}
