package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ALabelName extends PLabelName
{
    private TSimpleIdentifier _simpleIdentifier_;

    public ALabelName()
    {
    }

    public ALabelName(
        TSimpleIdentifier _simpleIdentifier_)
    {
        setSimpleIdentifier(_simpleIdentifier_);

    }
    public Object clone()
    {
        return new ALabelName(
            (TSimpleIdentifier) cloneNode(_simpleIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALabelName(this);
    }

    public TSimpleIdentifier getSimpleIdentifier()
    {
        return _simpleIdentifier_;
    }

    public void setSimpleIdentifier(TSimpleIdentifier node)
    {
        if(_simpleIdentifier_ != null)
        {
            _simpleIdentifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _simpleIdentifier_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_simpleIdentifier_);
    }

    void removeChild(Node child)
    {
        if(_simpleIdentifier_ == child)
        {
            _simpleIdentifier_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_simpleIdentifier_ == oldChild)
        {
            setSimpleIdentifier((TSimpleIdentifier) newChild);
            return;
        }

    }
}
