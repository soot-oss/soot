package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class APlainNameList extends PNameList
{
    private PQuotedIdentifier _quotedIdentifier_;

    public APlainNameList()
    {
    }

    public APlainNameList(
        PQuotedIdentifier _quotedIdentifier_)
    {
        setQuotedIdentifier(_quotedIdentifier_);

    }
    public Object clone()
    {
        return new APlainNameList(
            (PQuotedIdentifier) cloneNode(_quotedIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPlainNameList(this);
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

    public String toString()
    {
        return ""
            + toString(_quotedIdentifier_);
    }

    void removeChild(Node child)
    {
        if(_quotedIdentifier_ == child)
        {
            _quotedIdentifier_ = null;
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

    }
}
