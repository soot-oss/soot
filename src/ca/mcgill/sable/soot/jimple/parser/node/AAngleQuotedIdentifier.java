package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AAngleQuotedIdentifier extends PQuotedIdentifier
{
    private TCmplt _cmplt_;
    private TIdentifier _identifier_;
    private TCmpgt _cmpgt_;

    public AAngleQuotedIdentifier()
    {
    }

    public AAngleQuotedIdentifier(
        TCmplt _cmplt_,
        TIdentifier _identifier_,
        TCmpgt _cmpgt_)
    {
        setCmplt(_cmplt_);

        setIdentifier(_identifier_);

        setCmpgt(_cmpgt_);

    }
    public Object clone()
    {
        return new AAngleQuotedIdentifier(
            (TCmplt) cloneNode(_cmplt_),
            (TIdentifier) cloneNode(_identifier_),
            (TCmpgt) cloneNode(_cmpgt_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAngleQuotedIdentifier(this);
    }

    public TCmplt getCmplt()
    {
        return _cmplt_;
    }

    public void setCmplt(TCmplt node)
    {
        if(_cmplt_ != null)
        {
            _cmplt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmplt_ = node;
    }

    public TIdentifier getIdentifier()
    {
        return _identifier_;
    }

    public void setIdentifier(TIdentifier node)
    {
        if(_identifier_ != null)
        {
            _identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _identifier_ = node;
    }

    public TCmpgt getCmpgt()
    {
        return _cmpgt_;
    }

    public void setCmpgt(TCmpgt node)
    {
        if(_cmpgt_ != null)
        {
            _cmpgt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmpgt_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_cmplt_)
            + toString(_identifier_)
            + toString(_cmpgt_);
    }

    void removeChild(Node child)
    {
        if(_cmplt_ == child)
        {
            _cmplt_ = null;
            return;
        }

        if(_identifier_ == child)
        {
            _identifier_ = null;
            return;
        }

        if(_cmpgt_ == child)
        {
            _cmpgt_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_cmplt_ == oldChild)
        {
            setCmplt((TCmplt) newChild);
            return;
        }

        if(_identifier_ == oldChild)
        {
            setIdentifier((TIdentifier) newChild);
            return;
        }

        if(_cmpgt_ == oldChild)
        {
            setCmpgt((TCmpgt) newChild);
            return;
        }

    }
}
