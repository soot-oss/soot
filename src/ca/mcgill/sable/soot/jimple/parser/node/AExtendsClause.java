package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AExtendsClause extends PExtendsClause
{
    private TExtends _extends_;
    private PQualifiedName _qualifiedName_;

    public AExtendsClause()
    {
    }

    public AExtendsClause(
        TExtends _extends_,
        PQualifiedName _qualifiedName_)
    {
        setExtends(_extends_);

        setQualifiedName(_qualifiedName_);

    }
    public Object clone()
    {
        return new AExtendsClause(
            (TExtends) cloneNode(_extends_),
            (PQualifiedName) cloneNode(_qualifiedName_));
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

    public String toString()
    {
        return ""
            + toString(_extends_)
            + toString(_qualifiedName_);
    }

    void removeChild(Node child)
    {
        if(_extends_ == child)
        {
            _extends_ = null;
            return;
        }

        if(_qualifiedName_ == child)
        {
            _qualifiedName_ = null;
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

        if(_qualifiedName_ == oldChild)
        {
            setQualifiedName((PQualifiedName) newChild);
            return;
        }

    }
}
