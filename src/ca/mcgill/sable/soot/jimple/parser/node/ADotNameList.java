package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ADotNameList extends PNameList
{
    private PQuotedIdentifier _quotedIdentifier_;
    private TDot _dot_;
    private PNameList _nameList_;

    public ADotNameList()
    {
    }

    public ADotNameList(
        PQuotedIdentifier _quotedIdentifier_,
        TDot _dot_,
        PNameList _nameList_)
    {
        setQuotedIdentifier(_quotedIdentifier_);

        setDot(_dot_);

        setNameList(_nameList_);

    }
    public Object clone()
    {
        return new ADotNameList(
            (PQuotedIdentifier) cloneNode(_quotedIdentifier_),
            (TDot) cloneNode(_dot_),
            (PNameList) cloneNode(_nameList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADotNameList(this);
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

    public TDot getDot()
    {
        return _dot_;
    }

    public void setDot(TDot node)
    {
        if(_dot_ != null)
        {
            _dot_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _dot_ = node;
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
            + toString(_quotedIdentifier_)
            + toString(_dot_)
            + toString(_nameList_);
    }

    void removeChild(Node child)
    {
        if(_quotedIdentifier_ == child)
        {
            _quotedIdentifier_ = null;
            return;
        }

        if(_dot_ == child)
        {
            _dot_ = null;
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
        if(_quotedIdentifier_ == oldChild)
        {
            setQuotedIdentifier((PQuotedIdentifier) newChild);
            return;
        }

        if(_dot_ == oldChild)
        {
            setDot((TDot) newChild);
            return;
        }

        if(_nameList_ == oldChild)
        {
            setNameList((PNameList) newChild);
            return;
        }

    }
}
