package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ANewExpression extends PExpression
{
    private TNew _new_;
    private PNonvoidType _nonvoidType_;

    public ANewExpression()
    {
    }

    public ANewExpression(
        TNew _new_,
        PNonvoidType _nonvoidType_)
    {
        setNew(_new_);

        setNonvoidType(_nonvoidType_);

    }
    public Object clone()
    {
        return new ANewExpression(
            (TNew) cloneNode(_new_),
            (PNonvoidType) cloneNode(_nonvoidType_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANewExpression(this);
    }

    public TNew getNew()
    {
        return _new_;
    }

    public void setNew(TNew node)
    {
        if(_new_ != null)
        {
            _new_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _new_ = node;
    }

    public PNonvoidType getNonvoidType()
    {
        return _nonvoidType_;
    }

    public void setNonvoidType(PNonvoidType node)
    {
        if(_nonvoidType_ != null)
        {
            _nonvoidType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _nonvoidType_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_new_)
            + toString(_nonvoidType_);
    }

    void removeChild(Node child)
    {
        if(_new_ == child)
        {
            _new_ = null;
            return;
        }

        if(_nonvoidType_ == child)
        {
            _nonvoidType_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_new_ == oldChild)
        {
            setNew((TNew) newChild);
            return;
        }

        if(_nonvoidType_ == oldChild)
        {
            setNonvoidType((PNonvoidType) newChild);
            return;
        }

    }
}
