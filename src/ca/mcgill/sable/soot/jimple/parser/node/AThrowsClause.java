package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AThrowsClause extends PThrowsClause
{
    private TThrows _throws_;
    private PNameList _nameList_;

    public AThrowsClause()
    {
    }

    public AThrowsClause(
        TThrows _throws_,
        PNameList _nameList_)
    {
        setThrows(_throws_);

        setNameList(_nameList_);

    }
    public Object clone()
    {
        return new AThrowsClause(
            (TThrows) cloneNode(_throws_),
            (PNameList) cloneNode(_nameList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAThrowsClause(this);
    }

    public TThrows getThrows()
    {
        return _throws_;
    }

    public void setThrows(TThrows node)
    {
        if(_throws_ != null)
        {
            _throws_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _throws_ = node;
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
            + toString(_throws_)
            + toString(_nameList_);
    }

    void removeChild(Node child)
    {
        if(_throws_ == child)
        {
            _throws_ = null;
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
        if(_throws_ == oldChild)
        {
            setThrows((TThrows) newChild);
            return;
        }

        if(_nameList_ == oldChild)
        {
            setNameList((PNameList) newChild);
            return;
        }

    }
}
