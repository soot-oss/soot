package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AFloatConstant extends PConstant
{
    private TFloatConstant _floatConstant_;

    public AFloatConstant()
    {
    }

    public AFloatConstant(
        TFloatConstant _floatConstant_)
    {
        setFloatConstant(_floatConstant_);

    }
    public Object clone()
    {
        return new AFloatConstant(
            (TFloatConstant) cloneNode(_floatConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFloatConstant(this);
    }

    public TFloatConstant getFloatConstant()
    {
        return _floatConstant_;
    }

    public void setFloatConstant(TFloatConstant node)
    {
        if(_floatConstant_ != null)
        {
            _floatConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _floatConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_floatConstant_);
    }

    void removeChild(Node child)
    {
        if(_floatConstant_ == child)
        {
            _floatConstant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_floatConstant_ == oldChild)
        {
            setFloatConstant((TFloatConstant) newChild);
            return;
        }

    }
}
