package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AImplementsClause extends PImplementsClause
{
    private TImplements _implements_;
    private PQualifiedNameList _qualifiedNameList_;

    public AImplementsClause()
    {
    }

    public AImplementsClause(
        TImplements _implements_,
        PQualifiedNameList _qualifiedNameList_)
    {
        setImplements(_implements_);

        setQualifiedNameList(_qualifiedNameList_);

    }
    public Object clone()
    {
        return new AImplementsClause(
            (TImplements) cloneNode(_implements_),
            (PQualifiedNameList) cloneNode(_qualifiedNameList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAImplementsClause(this);
    }

    public TImplements getImplements()
    {
        return _implements_;
    }

    public void setImplements(TImplements node)
    {
        if(_implements_ != null)
        {
            _implements_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _implements_ = node;
    }

    public PQualifiedNameList getQualifiedNameList()
    {
        return _qualifiedNameList_;
    }

    public void setQualifiedNameList(PQualifiedNameList node)
    {
        if(_qualifiedNameList_ != null)
        {
            _qualifiedNameList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _qualifiedNameList_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_implements_)
            + toString(_qualifiedNameList_);
    }

    void removeChild(Node child)
    {
        if(_implements_ == child)
        {
            _implements_ = null;
            return;
        }

        if(_qualifiedNameList_ == child)
        {
            _qualifiedNameList_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_implements_ == oldChild)
        {
            setImplements((TImplements) newChild);
            return;
        }

        if(_qualifiedNameList_ == oldChild)
        {
            setQualifiedNameList((PQualifiedNameList) newChild);
            return;
        }

    }
}
