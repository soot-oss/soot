package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AIdQuotedIdentifier extends PQuotedIdentifier
{
    private TClassIdentifier _classIdentifier_;

    public AIdQuotedIdentifier()
    {
    }

    public AIdQuotedIdentifier(
        TClassIdentifier _classIdentifier_)
    {
        setClassIdentifier(_classIdentifier_);

    }
    public Object clone()
    {
        return new AIdQuotedIdentifier(
            (TClassIdentifier) cloneNode(_classIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIdQuotedIdentifier(this);
    }

    public TClassIdentifier getClassIdentifier()
    {
        return _classIdentifier_;
    }

    public void setClassIdentifier(TClassIdentifier node)
    {
        if(_classIdentifier_ != null)
        {
            _classIdentifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _classIdentifier_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_classIdentifier_);
    }

    void removeChild(Node child)
    {
        if(_classIdentifier_ == child)
        {
            _classIdentifier_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_classIdentifier_ == oldChild)
        {
            setClassIdentifier((TClassIdentifier) newChild);
            return;
        }

    }
}
