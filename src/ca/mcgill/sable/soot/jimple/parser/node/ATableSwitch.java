package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ATableSwitch extends PSwitch
{
    private TTableswitch _tableswitch_;

    public ATableSwitch()
    {
    }

    public ATableSwitch(
        TTableswitch _tableswitch_)
    {
        setTableswitch(_tableswitch_);

    }
    public Object clone()
    {
        return new ATableSwitch(
            (TTableswitch) cloneNode(_tableswitch_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATableSwitch(this);
    }

    public TTableswitch getTableswitch()
    {
        return _tableswitch_;
    }

    public void setTableswitch(TTableswitch node)
    {
        if(_tableswitch_ != null)
        {
            _tableswitch_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _tableswitch_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_tableswitch_);
    }

    void removeChild(Node child)
    {
        if(_tableswitch_ == child)
        {
            _tableswitch_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_tableswitch_ == oldChild)
        {
            setTableswitch((TTableswitch) newChild);
            return;
        }

    }
}
