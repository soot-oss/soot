package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AMethodSignature extends PMethodSignature
{
    private TLAngledBracket _lAngledBracket_;
    private PQualifiedName _qualifiedName_;
    private TColon _firstColon_;
    private PName _name_;
    private TColon _secondColon_;
    private TLParen _lParen_;
    private PParameterList _parameterList_;
    private TRParen _rParen_;
    private TColon _thirdColon_;
    private PType _type_;
    private TRAngledBracket _rAngledBracket_;

    public AMethodSignature()
    {
    }

    public AMethodSignature(
        TLAngledBracket _lAngledBracket_,
        PQualifiedName _qualifiedName_,
        TColon _firstColon_,
        PName _name_,
        TColon _secondColon_,
        TLParen _lParen_,
        PParameterList _parameterList_,
        TRParen _rParen_,
        TColon _thirdColon_,
        PType _type_,
        TRAngledBracket _rAngledBracket_)
    {
        setLAngledBracket(_lAngledBracket_);

        setQualifiedName(_qualifiedName_);

        setFirstColon(_firstColon_);

        setName(_name_);

        setSecondColon(_secondColon_);

        setLParen(_lParen_);

        setParameterList(_parameterList_);

        setRParen(_rParen_);

        setThirdColon(_thirdColon_);

        setType(_type_);

        setRAngledBracket(_rAngledBracket_);

    }
    public Object clone()
    {
        return new AMethodSignature(
            (TLAngledBracket) cloneNode(_lAngledBracket_),
            (PQualifiedName) cloneNode(_qualifiedName_),
            (TColon) cloneNode(_firstColon_),
            (PName) cloneNode(_name_),
            (TColon) cloneNode(_secondColon_),
            (TLParen) cloneNode(_lParen_),
            (PParameterList) cloneNode(_parameterList_),
            (TRParen) cloneNode(_rParen_),
            (TColon) cloneNode(_thirdColon_),
            (PType) cloneNode(_type_),
            (TRAngledBracket) cloneNode(_rAngledBracket_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMethodSignature(this);
    }

    public TLAngledBracket getLAngledBracket()
    {
        return _lAngledBracket_;
    }

    public void setLAngledBracket(TLAngledBracket node)
    {
        if(_lAngledBracket_ != null)
        {
            _lAngledBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lAngledBracket_ = node;
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

    public TColon getFirstColon()
    {
        return _firstColon_;
    }

    public void setFirstColon(TColon node)
    {
        if(_firstColon_ != null)
        {
            _firstColon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _firstColon_ = node;
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

    public TColon getSecondColon()
    {
        return _secondColon_;
    }

    public void setSecondColon(TColon node)
    {
        if(_secondColon_ != null)
        {
            _secondColon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _secondColon_ = node;
    }

    public TLParen getLParen()
    {
        return _lParen_;
    }

    public void setLParen(TLParen node)
    {
        if(_lParen_ != null)
        {
            _lParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lParen_ = node;
    }

    public PParameterList getParameterList()
    {
        return _parameterList_;
    }

    public void setParameterList(PParameterList node)
    {
        if(_parameterList_ != null)
        {
            _parameterList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _parameterList_ = node;
    }

    public TRParen getRParen()
    {
        return _rParen_;
    }

    public void setRParen(TRParen node)
    {
        if(_rParen_ != null)
        {
            _rParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rParen_ = node;
    }

    public TColon getThirdColon()
    {
        return _thirdColon_;
    }

    public void setThirdColon(TColon node)
    {
        if(_thirdColon_ != null)
        {
            _thirdColon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _thirdColon_ = node;
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

    public TRAngledBracket getRAngledBracket()
    {
        return _rAngledBracket_;
    }

    public void setRAngledBracket(TRAngledBracket node)
    {
        if(_rAngledBracket_ != null)
        {
            _rAngledBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rAngledBracket_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lAngledBracket_)
            + toString(_qualifiedName_)
            + toString(_firstColon_)
            + toString(_name_)
            + toString(_secondColon_)
            + toString(_lParen_)
            + toString(_parameterList_)
            + toString(_rParen_)
            + toString(_thirdColon_)
            + toString(_type_)
            + toString(_rAngledBracket_);
    }

    void removeChild(Node child)
    {
        if(_lAngledBracket_ == child)
        {
            _lAngledBracket_ = null;
            return;
        }

        if(_qualifiedName_ == child)
        {
            _qualifiedName_ = null;
            return;
        }

        if(_firstColon_ == child)
        {
            _firstColon_ = null;
            return;
        }

        if(_name_ == child)
        {
            _name_ = null;
            return;
        }

        if(_secondColon_ == child)
        {
            _secondColon_ = null;
            return;
        }

        if(_lParen_ == child)
        {
            _lParen_ = null;
            return;
        }

        if(_parameterList_ == child)
        {
            _parameterList_ = null;
            return;
        }

        if(_rParen_ == child)
        {
            _rParen_ = null;
            return;
        }

        if(_thirdColon_ == child)
        {
            _thirdColon_ = null;
            return;
        }

        if(_type_ == child)
        {
            _type_ = null;
            return;
        }

        if(_rAngledBracket_ == child)
        {
            _rAngledBracket_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lAngledBracket_ == oldChild)
        {
            setLAngledBracket((TLAngledBracket) newChild);
            return;
        }

        if(_qualifiedName_ == oldChild)
        {
            setQualifiedName((PQualifiedName) newChild);
            return;
        }

        if(_firstColon_ == oldChild)
        {
            setFirstColon((TColon) newChild);
            return;
        }

        if(_name_ == oldChild)
        {
            setName((PName) newChild);
            return;
        }

        if(_secondColon_ == oldChild)
        {
            setSecondColon((TColon) newChild);
            return;
        }

        if(_lParen_ == oldChild)
        {
            setLParen((TLParen) newChild);
            return;
        }

        if(_parameterList_ == oldChild)
        {
            setParameterList((PParameterList) newChild);
            return;
        }

        if(_rParen_ == oldChild)
        {
            setRParen((TRParen) newChild);
            return;
        }

        if(_thirdColon_ == oldChild)
        {
            setThirdColon((TColon) newChild);
            return;
        }

        if(_type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        if(_rAngledBracket_ == oldChild)
        {
            setRAngledBracket((TRAngledBracket) newChild);
            return;
        }

    }
}
