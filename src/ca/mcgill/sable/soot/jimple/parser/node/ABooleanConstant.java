package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ABooleanConstant extends PConstant
{
    private TBoolConstant _boolConstant_;

    public ABooleanConstant()
    {
    }

    public ABooleanConstant(
        TBoolConstant _boolConstant_)
    {
        setBoolConstant(_boolConstant_);

    }
    public Object clone()
    {
        return new ABooleanConstant(
            (TBoolConstant) cloneNode(_boolConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABooleanConstant(this);
    }

    public TBoolConstant getBoolConstant()
    {
        return _boolConstant_;
    }

    public void setBoolConstant(TBoolConstant node)
    {
        if(_boolConstant_ != null)
        {
            _boolConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _boolConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_boolConstant_);
    }

    void removeChild(Node child)
    {
        if(_boolConstant_ == child)
        {
            _boolConstant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_boolConstant_ == oldChild)
        {
            setBoolConstant((TBoolConstant) newChild);
            return;
        }

    }
}
