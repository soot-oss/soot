package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AFloatDegenerateFloatExt extends PFloatExt
{
    private TFloatDegenerate _floatDegenerate_;

    public AFloatDegenerateFloatExt()
    {
    }

    public AFloatDegenerateFloatExt(
        TFloatDegenerate _floatDegenerate_)
    {
        setFloatDegenerate(_floatDegenerate_);

    }
    public Object clone()
    {
        return new AFloatDegenerateFloatExt(
            (TFloatDegenerate) cloneNode(_floatDegenerate_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFloatDegenerateFloatExt(this);
    }

    public TFloatDegenerate getFloatDegenerate()
    {
        return _floatDegenerate_;
    }

    public void setFloatDegenerate(TFloatDegenerate node)
    {
        if(_floatDegenerate_ != null)
        {
            _floatDegenerate_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _floatDegenerate_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_floatDegenerate_);
    }

    void removeChild(Node child)
    {
        if(_floatDegenerate_ == child)
        {
            _floatDegenerate_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_floatDegenerate_ == oldChild)
        {
            setFloatDegenerate((TFloatDegenerate) newChild);
            return;
        }

    }
}
