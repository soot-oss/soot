package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AImplementsClause extends PImplementsClause
{
    private TImplements _implements_;
    private PNameList _nameList_;

    public AImplementsClause()
    {
    }

    public AImplementsClause(
        TImplements _implements_,
        PNameList _nameList_)
    {
        setImplements(_implements_);

        setNameList(_nameList_);

    }
    public Object clone()
    {
        return new AImplementsClause(
            (TImplements) cloneNode(_implements_),
            (PNameList) cloneNode(_nameList_));
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

    public PNameList getNameList()
    {
        return _nameList_;
    }

    public void setNameList(PNameList node)
    {
        if(_nameList_ != null)
        {
            _nameList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _nameList_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_implements_)
            + toString(_nameList_);
    }

    void removeChild(Node child)
    {
        if(_implements_ == child)
        {
            _implements_ = null;
            return;
        }

        if(_nameList_ == child)
        {
            _nameList_ = null;
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

        if(_nameList_ == oldChild)
        {
            setNameList((PNameList) newChild);
            return;
        }

    }
}
