package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ANewExpr extends PNewExpr
{
    private TNew _new_;
    private PBaseType _baseType_;
    private final LinkedList _arrayDescriptor_ = new TypedLinkedList(new ArrayDescriptor_Cast());

    public ANewExpr()
    {
    }

    public ANewExpr(
        TNew _new_,
        PBaseType _baseType_,
        List _arrayDescriptor_)
    {
        setNew(_new_);

        setBaseType(_baseType_);

        {
            Object temp[] = _arrayDescriptor_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._arrayDescriptor_.add(temp[i]);
            }
        }

    }

    public ANewExpr(
        TNew _new_,
        PBaseType _baseType_,
        XPArrayDescriptor _arrayDescriptor_)
    {
        setNew(_new_);

        setBaseType(_baseType_);

        if(_arrayDescriptor_ != null)
        {
            while(_arrayDescriptor_ instanceof X1PArrayDescriptor)
            {
                this._arrayDescriptor_.addFirst(((X1PArrayDescriptor) _arrayDescriptor_).getPArrayDescriptor());
                _arrayDescriptor_ = ((X1PArrayDescriptor) _arrayDescriptor_).getXPArrayDescriptor();
            }
            this._arrayDescriptor_.addFirst(((X2PArrayDescriptor) _arrayDescriptor_).getPArrayDescriptor());
        }

    }
    public Object clone()
    {
        return new ANewExpr(
            (TNew) cloneNode(_new_),
            (PBaseType) cloneNode(_baseType_),
            cloneList(_arrayDescriptor_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANewExpr(this);
    }

    public TNew getNew()
    {
        return _new_;
    }

    public void setNew(TNew node)
    {
        if(_new_ != null)
        {
            _new_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _new_ = node;
    }

    public PBaseType getBaseType()
    {
        return _baseType_;
    }

    public void setBaseType(PBaseType node)
    {
        if(_baseType_ != null)
        {
            _baseType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _baseType_ = node;
    }

    public LinkedList getArrayDescriptor()
    {
        return _arrayDescriptor_;
    }

    public void setArrayDescriptor(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _arrayDescriptor_.add(temp[i]);
        }
    }

    public String toString()
    {
        return ""
            + toString(_new_)
            + toString(_baseType_)
            + toString(_arrayDescriptor_);
    }

    void removeChild(Node child)
    {
        if(_new_ == child)
        {
            _new_ = null;
            return;
        }

        if(_baseType_ == child)
        {
            _baseType_ = null;
            return;
        }

        if(_arrayDescriptor_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_new_ == oldChild)
        {
            setNew((TNew) newChild);
            return;
        }

        if(_baseType_ == oldChild)
        {
            setBaseType((PBaseType) newChild);
            return;
        }

        for(ListIterator i = _arrayDescriptor_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

    }

    private class ArrayDescriptor_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PArrayDescriptor node = (PArrayDescriptor) o;

            if((node.parent() != null) &&
                (node.parent() != ANewExpr.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ANewExpr.this))
            {
                node.parent(ANewExpr.this);
            }

            return node;
        }
    }
}
