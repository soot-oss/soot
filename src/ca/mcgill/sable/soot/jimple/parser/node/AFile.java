package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AFile extends PFile
{
    private final LinkedList _modifier_ = new TypedLinkedList(new Modifier_Cast());
    private PFileType _fileType_;
    private TName _name_;
    private PExtendsClause _extendsClause_;
    private PImplementsClause _implementsClause_;
    private PFileBody _fileBody_;

    public AFile()
    {
    }

    public AFile(
        List _modifier_,
        PFileType _fileType_,
        TName _name_,
        PExtendsClause _extendsClause_,
        PImplementsClause _implementsClause_,
        PFileBody _fileBody_)
    {
        {
            Object temp[] = _modifier_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._modifier_.add(temp[i]);
            }
        }

        setFileType(_fileType_);

        setName(_name_);

        setExtendsClause(_extendsClause_);

        setImplementsClause(_implementsClause_);

        setFileBody(_fileBody_);

    }

    public AFile(
        XPModifier _modifier_,
        PFileType _fileType_,
        TName _name_,
        PExtendsClause _extendsClause_,
        PImplementsClause _implementsClause_,
        PFileBody _fileBody_)
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

        setFileType(_fileType_);

        setName(_name_);

        setExtendsClause(_extendsClause_);

        setImplementsClause(_implementsClause_);

        setFileBody(_fileBody_);

    }
    public Object clone()
    {
        return new AFile(
            cloneList(_modifier_),
            (PFileType) cloneNode(_fileType_),
            (TName) cloneNode(_name_),
            (PExtendsClause) cloneNode(_extendsClause_),
            (PImplementsClause) cloneNode(_implementsClause_),
            (PFileBody) cloneNode(_fileBody_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFile(this);
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

    public PFileType getFileType()
    {
        return _fileType_;
    }

    public void setFileType(PFileType node)
    {
        if(_fileType_ != null)
        {
            _fileType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _fileType_ = node;
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

    public PExtendsClause getExtendsClause()
    {
        return _extendsClause_;
    }

    public void setExtendsClause(PExtendsClause node)
    {
        if(_extendsClause_ != null)
        {
            _extendsClause_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _extendsClause_ = node;
    }

    public PImplementsClause getImplementsClause()
    {
        return _implementsClause_;
    }

    public void setImplementsClause(PImplementsClause node)
    {
        if(_implementsClause_ != null)
        {
            _implementsClause_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _implementsClause_ = node;
    }

    public PFileBody getFileBody()
    {
        return _fileBody_;
    }

    public void setFileBody(PFileBody node)
    {
        if(_fileBody_ != null)
        {
            _fileBody_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _fileBody_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_modifier_)
            + toString(_fileType_)
            + toString(_name_)
            + toString(_extendsClause_)
            + toString(_implementsClause_)
            + toString(_fileBody_);
    }

    void removeChild(Node child)
    {
        if(_modifier_.remove(child))
        {
            return;
        }

        if(_fileType_ == child)
        {
            _fileType_ = null;
            return;
        }

        if(_name_ == child)
        {
            _name_ = null;
            return;
        }

        if(_extendsClause_ == child)
        {
            _extendsClause_ = null;
            return;
        }

        if(_implementsClause_ == child)
        {
            _implementsClause_ = null;
            return;
        }

        if(_fileBody_ == child)
        {
            _fileBody_ = null;
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

        if(_fileType_ == oldChild)
        {
            setFileType((PFileType) newChild);
            return;
        }

        if(_name_ == oldChild)
        {
            setName((TName) newChild);
            return;
        }

        if(_extendsClause_ == oldChild)
        {
            setExtendsClause((PExtendsClause) newChild);
            return;
        }

        if(_implementsClause_ == oldChild)
        {
            setImplementsClause((PImplementsClause) newChild);
            return;
        }

        if(_fileBody_ == oldChild)
        {
            setFileBody((PFileBody) newChild);
            return;
        }

    }

    private class Modifier_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PModifier node = (PModifier) o;

            if((node.parent() != null) &&
                (node.parent() != AFile.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFile.this))
            {
                node.parent(AFile.this);
            }

            return node;
        }
    }
}
