package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AFloatConstant extends PConstant
{
    private TMinus _minus_;
    private PFloatExt _floatExt_;

    public AFloatConstant()
    {
    }

    public AFloatConstant(
        TMinus _minus_,
        PFloatExt _floatExt_)
    {
        setMinus(_minus_);

        setFloatExt(_floatExt_);

    }
    public Object clone()
    {
        return new AFloatConstant(
            (TMinus) cloneNode(_minus_),
            (PFloatExt) cloneNode(_floatExt_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFloatConstant(this);
    }

    public TMinus getMinus()
    {
        return _minus_;
    }

    public void setMinus(TMinus node)
    {
        if(_minus_ != null)
        {
            _minus_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _minus_ = node;
    }

    public PFloatExt getFloatExt()
    {
        return _floatExt_;
    }

    public void setFloatExt(PFloatExt node)
    {
        if(_floatExt_ != null)
        {
            _floatExt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _floatExt_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_minus_)
            + toString(_floatExt_);
    }

    void removeChild(Node child)
    {
        if(_minus_ == child)
        {
            _minus_ = null;
            return;
        }

        if(_floatExt_ == child)
        {
            _floatExt_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_minus_ == oldChild)
        {
            setMinus((TMinus) newChild);
            return;
        }

        if(_floatExt_ == oldChild)
        {
            setFloatExt((PFloatExt) newChild);
            return;
        }

    }
}
