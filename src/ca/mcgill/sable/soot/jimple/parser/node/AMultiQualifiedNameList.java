package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AMultiQualifiedNameList extends PQualifiedNameList
{
    private PQualifiedName _qualifiedName_;
    private TComma _comma_;
    private PQualifiedNameList _qualifiedNameList_;

    public AMultiQualifiedNameList()
    {
    }

    public AMultiQualifiedNameList(
        PQualifiedName _qualifiedName_,
        TComma _comma_,
        PQualifiedNameList _qualifiedNameList_)
    {
        setQualifiedName(_qualifiedName_);

        setComma(_comma_);

        setQualifiedNameList(_qualifiedNameList_);

    }
    public Object clone()
    {
        return new AMultiQualifiedNameList(
            (PQualifiedName) cloneNode(_qualifiedName_),
            (TComma) cloneNode(_comma_),
            (PQualifiedNameList) cloneNode(_qualifiedNameList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMultiQualifiedNameList(this);
    }

    public PQualifiedName getQualifiedName()
    {
        return _qualifiedName_;
    }

    public void setQualifiedName(PQualifiedName node)
    {
        if(_qualifiedName_ != null)
        {
            _qualifiedName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _qualifiedName_ = node;
    }

    public TComma getComma()
    {
        return _comma_;
    }

    public void setComma(TComma node)
    {
        if(_comma_ != null)
        {
            _comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _comma_ = node;
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
            + toString(_qualifiedName_)
            + toString(_comma_)
            + toString(_qualifiedNameList_);
    }

    void removeChild(Node child)
    {
        if(_qualifiedName_ == child)
        {
            _qualifiedName_ = null;
            return;
        }

        if(_comma_ == child)
        {
            _comma_ = null;
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
        if(_qualifiedName_ == oldChild)
        {
            setQualifiedName((PQualifiedName) newChild);
            return;
        }

        if(_comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(_qualifiedNameList_ == oldChild)
        {
            setQualifiedNameList((PQualifiedNameList) newChild);
            return;
        }

    }
}
