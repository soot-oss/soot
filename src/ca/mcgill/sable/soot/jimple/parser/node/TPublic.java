package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TPublic extends Token
{
    public TPublic()
    {
        super.setText("public");
    }

    public TPublic(int line, int pos)
    {
        super.setText("public");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TPublic(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPublic(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TPublic text.");
    }
}
