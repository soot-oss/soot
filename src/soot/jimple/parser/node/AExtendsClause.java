package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AExtendsClause extends PExtendsClause
{
    private TExtends _extends_;
    private PName _name_;

    public AExtendsClause()
    {
    }

    public AExtendsClause(
        TExtends _extends_,
        PName _name_)
    {
        setExtends(_extends_);

        setName(_name_);

    }
    public Object clone()
    {
        return new AExtendsClause(
            (TExtends) cloneNode(_extends_),
            (PName) cloneNode(_name_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExtendsClause(this);
    }

    public TExtends getExtends()
    {
        return _extends_;
    }

    public void setExtends(TExtends node)
    {
        if(_extends_ != null)
        {
            _extends_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _extends_ = node;
    }

    public PName getName()
    {
        return _name_;
    }

    public void setName(PName node)
    {
        if(_name_ != null)
        {
            _name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _name_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_extends_)
            + toString(_name_);
    }

    void removeChild(Node child)
    {
        if(_extends_ == child)
        {
            _extends_ = null;
            return;
        }

        if(_name_ == child)
        {
            _name_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_extends_ == oldChild)
        {
            setExtends((TExtends) newChild);
            return;
        }

        if(_name_ == oldChild)
        {
            setName((PName) newChild);
            return;
        }

    }
}
