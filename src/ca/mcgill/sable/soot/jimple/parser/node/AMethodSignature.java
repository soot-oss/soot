package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AMethodSignature extends PMethodSignature
{
    private TCmplt _cmplt_;
    private TName _className_;
    private TColon _first_;
    private TName _methodName_;
    private TColon _second_;
    private TLParen _lParen_;
    private PParameterList _parameterList_;
    private TRParen _rParen_;
    private TColon _third_;
    private PType _type_;
    private TCmpgt _cmpgt_;

    public AMethodSignature()
    {
    }

    public AMethodSignature(
        TCmplt _cmplt_,
        TName _className_,
        TColon _first_,
        TName _methodName_,
        TColon _second_,
        TLParen _lParen_,
        PParameterList _parameterList_,
        TRParen _rParen_,
        TColon _third_,
        PType _type_,
        TCmpgt _cmpgt_)
    {
        setCmplt(_cmplt_);

        setClassName(_className_);

        setFirst(_first_);

        setMethodName(_methodName_);

        setSecond(_second_);

        setLParen(_lParen_);

        setParameterList(_parameterList_);

        setRParen(_rParen_);

        setThird(_third_);

        setType(_type_);

        setCmpgt(_cmpgt_);

    }
    public Object clone()
    {
        return new AMethodSignature(
            (TCmplt) cloneNode(_cmplt_),
            (TName) cloneNode(_className_),
            (TColon) cloneNode(_first_),
            (TName) cloneNode(_methodName_),
            (TColon) cloneNode(_second_),
            (TLParen) cloneNode(_lParen_),
            (PParameterList) cloneNode(_parameterList_),
            (TRParen) cloneNode(_rParen_),
            (TColon) cloneNode(_third_),
            (PType) cloneNode(_type_),
            (TCmpgt) cloneNode(_cmpgt_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMethodSignature(this);
    }

    public TCmplt getCmplt()
    {
        return _cmplt_;
    }

    public void setCmplt(TCmplt node)
    {
        if(_cmplt_ != null)
        {
            _cmplt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmplt_ = node;
    }

    public TName getClassName()
    {
        return _className_;
    }

    public void setClassName(TName node)
    {
        if(_className_ != null)
        {
            _className_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _className_ = node;
    }

    public TColon getFirst()
    {
        return _first_;
    }

    public void setFirst(TColon node)
    {
        if(_first_ != null)
        {
            _first_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _first_ = node;
    }

    public TName getMethodName()
    {
        return _methodName_;
    }

    public void setMethodName(TName node)
    {
        if(_methodName_ != null)
        {
            _methodName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _methodName_ = node;
    }

    public TColon getSecond()
    {
        return _second_;
    }

    public void setSecond(TColon node)
    {
        if(_second_ != null)
        {
            _second_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _second_ = node;
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

    public TColon getThird()
    {
        return _third_;
    }

    public void setThird(TColon node)
    {
        if(_third_ != null)
        {
            _third_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _third_ = node;
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

    public TCmpgt getCmpgt()
    {
        return _cmpgt_;
    }

    public void setCmpgt(TCmpgt node)
    {
        if(_cmpgt_ != null)
        {
            _cmpgt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmpgt_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_cmplt_)
            + toString(_className_)
            + toString(_first_)
            + toString(_methodName_)
            + toString(_second_)
            + toString(_lParen_)
            + toString(_parameterList_)
            + toString(_rParen_)
            + toString(_third_)
            + toString(_type_)
            + toString(_cmpgt_);
    }

    void removeChild(Node child)
    {
        if(_cmplt_ == child)
        {
            _cmplt_ = null;
            return;
        }

        if(_className_ == child)
        {
            _className_ = null;
            return;
        }

        if(_first_ == child)
        {
            _first_ = null;
            return;
        }

        if(_methodName_ == child)
        {
            _methodName_ = null;
            return;
        }

        if(_second_ == child)
        {
            _second_ = null;
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

        if(_third_ == child)
        {
            _third_ = null;
            return;
        }

        if(_type_ == child)
        {
            _type_ = null;
            return;
        }

        if(_cmpgt_ == child)
        {
            _cmpgt_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_cmplt_ == oldChild)
        {
            setCmplt((TCmplt) newChild);
            return;
        }

        if(_className_ == oldChild)
        {
            setClassName((TName) newChild);
            return;
        }

        if(_first_ == oldChild)
        {
            setFirst((TColon) newChild);
            return;
        }

        if(_methodName_ == oldChild)
        {
            setMethodName((TName) newChild);
            return;
        }

        if(_second_ == oldChild)
        {
            setSecond((TColon) newChild);
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

        if(_third_ == oldChild)
        {
            setThird((TColon) newChild);
            return;
        }

        if(_type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        if(_cmpgt_ == oldChild)
        {
            setCmpgt((TCmpgt) newChild);
            return;
        }

    }
}
