package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AQualifiedName extends PQualifiedName
{
    private TQuote _left_;
    private PNameList _nameList_;
    private TQuote _right_;

    public AQualifiedName()
    {
    }

    public AQualifiedName(
        TQuote _left_,
        PNameList _nameList_,
        TQuote _right_)
    {
        setLeft(_left_);

        setNameList(_nameList_);

        setRight(_right_);

    }
    public Object clone()
    {
        return new AQualifiedName(
            (TQuote) cloneNode(_left_),
            (PNameList) cloneNode(_nameList_),
            (TQuote) cloneNode(_right_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAQualifiedName(this);
    }

    public TQuote getLeft()
    {
        return _left_;
    }

    public void setLeft(TQuote node)
    {
        if(_left_ != null)
        {
            _left_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _left_ = node;
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

    public TQuote getRight()
    {
        return _right_;
    }

    public void setRight(TQuote node)
    {
        if(_right_ != null)
        {
            _right_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _right_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_left_)
            + toString(_nameList_)
            + toString(_right_);
    }

    void removeChild(Node child)
    {
        if(_left_ == child)
        {
            _left_ = null;
            return;
        }

        if(_nameList_ == child)
        {
            _nameList_ = null;
            return;
        }

        if(_right_ == child)
        {
            _right_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_left_ == oldChild)
        {
            setLeft((TQuote) newChild);
            return;
        }

        if(_nameList_ == oldChild)
        {
            setNameList((PNameList) newChild);
            return;
        }

        if(_right_ == oldChild)
        {
            setRight((TQuote) newChild);
            return;
        }

    }
}
