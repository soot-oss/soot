package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TExitmonitor extends Token
{
    public TExitmonitor()
    {
        super.setText("exitmonitor");
    }

    public TExitmonitor(int line, int pos)
    {
        super.setText("exitmonitor");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TExitmonitor(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTExitmonitor(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TExitmonitor text.");
    }
}
