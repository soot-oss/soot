package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ALookupSwitch extends PSwitch
{
    private TLookupswitch _lookupswitch_;

    public ALookupSwitch()
    {
    }

    public ALookupSwitch(
        TLookupswitch _lookupswitch_)
    {
        setLookupswitch(_lookupswitch_);

    }
    public Object clone()
    {
        return new ALookupSwitch(
            (TLookupswitch) cloneNode(_lookupswitch_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALookupSwitch(this);
    }

    public TLookupswitch getLookupswitch()
    {
        return _lookupswitch_;
    }

    public void setLookupswitch(TLookupswitch node)
    {
        if(_lookupswitch_ != null)
        {
            _lookupswitch_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lookupswitch_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lookupswitch_);
    }

    void removeChild(Node child)
    {
        if(_lookupswitch_ == child)
        {
            _lookupswitch_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lookupswitch_ == oldChild)
        {
            setLookupswitch((TLookupswitch) newChild);
            return;
        }

    }
}
