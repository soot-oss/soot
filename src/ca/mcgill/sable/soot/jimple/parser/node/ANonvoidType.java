package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ANonvoidType extends PNonvoidType
{
    private PBaseType _baseType_;
    private final LinkedList _arrayBrackets_ = new TypedLinkedList(new ArrayBrackets_Cast());

    public ANonvoidType()
    {
    }

    public ANonvoidType(
        PBaseType _baseType_,
        List _arrayBrackets_)
    {
        setBaseType(_baseType_);

        {
            Object temp[] = _arrayBrackets_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._arrayBrackets_.add(temp[i]);
            }
        }

    }

    public ANonvoidType(
        PBaseType _baseType_,
        XPArrayBrackets _arrayBrackets_)
    {
        setBaseType(_baseType_);

        if(_arrayBrackets_ != null)
        {
            while(_arrayBrackets_ instanceof X1PArrayBrackets)
            {
                this._arrayBrackets_.addFirst(((X1PArrayBrackets) _arrayBrackets_).getPArrayBrackets());
                _arrayBrackets_ = ((X1PArrayBrackets) _arrayBrackets_).getXPArrayBrackets();
            }
            this._arrayBrackets_.addFirst(((X2PArrayBrackets) _arrayBrackets_).getPArrayBrackets());
        }

    }
    public Object clone()
    {
        return new ANonvoidType(
            (PBaseType) cloneNode(_baseType_),
            cloneList(_arrayBrackets_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANonvoidType(this);
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

    public LinkedList getArrayBrackets()
    {
        return _arrayBrackets_;
    }

    public void setArrayBrackets(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _arrayBrackets_.add(temp[i]);
        }
    }

    public String toString()
    {
        return ""
            + toString(_baseType_)
            + toString(_arrayBrackets_);
    }

    void removeChild(Node child)
    {
        if(_baseType_ == child)
        {
            _baseType_ = null;
            return;
        }

        if(_arrayBrackets_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_baseType_ == oldChild)
        {
            setBaseType((PBaseType) newChild);
            return;
        }

        for(ListIterator i = _arrayBrackets_.listIterator(); i.hasNext();)
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

    private class ArrayBrackets_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PArrayBrackets node = (PArrayBrackets) o;

            if((node.parent() != null) &&
                (node.parent() != ANonvoidType.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ANonvoidType.this))
            {
                node.parent(ANonvoidType.this);
            }

            return node;
        }
    }
}
