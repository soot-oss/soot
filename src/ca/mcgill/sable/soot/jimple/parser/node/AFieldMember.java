package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AFieldMember extends PMember
{
    private final LinkedList _modifier_ = new TypedLinkedList(new Modifier_Cast());
    private PType _type_;
    private TName _name_;
    private TSemicolon _semicolon_;

    public AFieldMember()
    {
    }

    public AFieldMember(
        List _modifier_,
        PType _type_,
        TName _name_,
        TSemicolon _semicolon_)
    {
        {
            Object temp[] = _modifier_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._modifier_.add(temp[i]);
            }
        }

        setType(_type_);

        setName(_name_);

        setSemicolon(_semicolon_);

    }

    public AFieldMember(
        XPModifier _modifier_,
        PType _type_,
        TName _name_,
        TSemicolon _semicolon_)
    {
        if(_modifier_ != null)
        {
            while(_modifier_ instanceof X1PModifier)
            {
                this._modifier_.addFirst(((X1PModifier) _modifier_).getPModifier());
                _modifier_ = ((X1PModifier) _modifier_).getXPModifier();
            }
            this._modifier_.addFirst(((X2PModifier) _modifier_).getPModifier());
        }

        setType(_type_);

        setName(_name_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new AFieldMember(
            cloneList(_modifier_),
            (PType) cloneNode(_type_),
            (TName) cloneNode(_name_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFieldMember(this);
    }

    public LinkedList getModifier()
    {
        return _modifier_;
    }

    public void setModifier(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _modifier_.add(temp[i]);
        }
    }

    public PType getType()
    {
        return _type_;
    }

    public void setType(PType node)
    {
        if(_type_ != null)
        {
            _type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _type_ = node;
    }

    public TName getName()
    {
        return _name_;
    }

    public void setName(TName node)
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

    public TSemicolon getSemicolon()
    {
        return _semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(_semicolon_ != null)
        {
            _semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _semicolon_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_modifier_)
            + toString(_type_)
            + toString(_name_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_modifier_.remove(child))
        {
            return;
        }

        if(_type_ == child)
        {
            _type_ = null;
            return;
        }

        if(_name_ == child)
        {
            _name_ = null;
            return;
        }

        if(_semicolon_ == child)
        {
            _semicolon_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        for(ListIterator i = _modifier_.listIterator(); i.hasNext();)
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

        if(_type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        if(_name_ == oldChild)
        {
            setName((TName) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }

    private class Modifier_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PModifier node = (PModifier) o;

            if((node.parent() != null) &&
                (node.parent() != AFieldMember.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFieldMember.this))
            {
                node.parent(AFieldMember.this);
            }

            return node;
        }
    }
}
