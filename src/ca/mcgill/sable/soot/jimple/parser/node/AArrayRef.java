package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AArrayRef extends PArrayRef
{
    private PLocalName _localName_;
    private PFixedArrayDescriptor _fixedArrayDescriptor_;

    public AArrayRef()
    {
    }

    public AArrayRef(
        PLocalName _localName_,
        PFixedArrayDescriptor _fixedArrayDescriptor_)
    {
        setLocalName(_localName_);

        setFixedArrayDescriptor(_fixedArrayDescriptor_);

    }
    public Object clone()
    {
        return new AArrayRef(
            (PLocalName) cloneNode(_localName_),
            (PFixedArrayDescriptor) cloneNode(_fixedArrayDescriptor_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAArrayRef(this);
    }

    public PLocalName getLocalName()
    {
        return _localName_;
    }

    public void setLocalName(PLocalName node)
    {
        if(_localName_ != null)
        {
            _localName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _localName_ = node;
    }

    public PFixedArrayDescriptor getFixedArrayDescriptor()
    {
        return _fixedArrayDescriptor_;
    }

    public void setFixedArrayDescriptor(PFixedArrayDescriptor node)
    {
        if(_fixedArrayDescriptor_ != null)
        {
            _fixedArrayDescriptor_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _fixedArrayDescriptor_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_localName_)
            + toString(_fixedArrayDescriptor_);
    }

    void removeChild(Node child)
    {
        if(_localName_ == child)
        {
            _localName_ = null;
            return;
        }

        if(_fixedArrayDescriptor_ == child)
        {
            _fixedArrayDescriptor_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_localName_ == oldChild)
        {
            setLocalName((PLocalName) newChild);
            return;
        }

        if(_fixedArrayDescriptor_ == oldChild)
        {
            setFixedArrayDescriptor((PFixedArrayDescriptor) newChild);
            return;
        }

    }
}
