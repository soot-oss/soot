package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AName extends PName
{
    private TQuote _left_;
    private PQuotedIdentifier _quotedIdentifier_;
    private TQuote _right_;

    public AName()
    {
    }

    public AName(
        TQuote _left_,
        PQuotedIdentifier _quotedIdentifier_,
        TQuote _right_)
    {
        setLeft(_left_);

        setQuotedIdentifier(_quotedIdentifier_);

        setRight(_right_);

    }
    public Object clone()
    {
        return new AName(
            (TQuote) cloneNode(_left_),
            (PQuotedIdentifier) cloneNode(_quotedIdentifier_),
            (TQuote) cloneNode(_right_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAName(this);
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

    public PQuotedIdentifier getQuotedIdentifier()
    {
        return _quotedIdentifier_;
    }

    public void setQuotedIdentifier(PQuotedIdentifier node)
    {
        if(_quotedIdentifier_ != null)
        {
            _quotedIdentifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _quotedIdentifier_ = node;
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
            + toString(_quotedIdentifier_)
            + toString(_right_);
    }

    void removeChild(Node child)
    {
        if(_left_ == child)
        {
            _left_ = null;
            return;
        }

        if(_quotedIdentifier_ == child)
        {
            _quotedIdentifier_ = null;
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

        if(_quotedIdentifier_ == oldChild)
        {
            setQuotedIdentifier((PQuotedIdentifier) newChild);
            return;
        }

        if(_right_ == oldChild)
        {
            setRight((TQuote) newChild);
            return;
        }

    }
}
