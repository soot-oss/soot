package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ALocalName extends PLocalName
{
    private TSimpleIdentifier _simpleIdentifier_;

    public ALocalName()
    {
    }

    public ALocalName(
        TSimpleIdentifier _simpleIdentifier_)
    {
        setSimpleIdentifier(_simpleIdentifier_);

    }
    public Object clone()
    {
        return new ALocalName(
            (TSimpleIdentifier) cloneNode(_simpleIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALocalName(this);
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
