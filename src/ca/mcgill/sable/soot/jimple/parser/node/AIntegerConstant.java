package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AIntegerConstant extends PConstant
{
    private TIntegerConstant _integerConstant_;

    public AIntegerConstant()
    {
    }

    public AIntegerConstant(
        TIntegerConstant _integerConstant_)
    {
        setIntegerConstant(_integerConstant_);

    }
    public Object clone()
    {
        return new AIntegerConstant(
            (TIntegerConstant) cloneNode(_integerConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIntegerConstant(this);
    }

    public TIntegerConstant getIntegerConstant()
    {
        return _integerConstant_;
    }

    public void setIntegerConstant(TIntegerConstant node)
    {
        if(_integerConstant_ != null)
        {
            _integerConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _integerConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_integerConstant_);
    }

    void removeChild(Node child)
    {
        if(_integerConstant_ == child)
        {
            _integerConstant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_integerConstant_ == oldChild)
        {
            setIntegerConstant((TIntegerConstant) newChild);
            return;
        }

    }
}
