/* This file was generated by SableCC (http://www.sablecc.org/). */

package nada.node;

import nada.analysis.*;

@SuppressWarnings("nls")
public final class AAnotherParamSpec extends PAnotherParamSpec
{
    private TSemi _semi_;
    private PParamSpec _paramSpec_;

    public AAnotherParamSpec()
    {
        // Constructor
    }

    public AAnotherParamSpec(
        @SuppressWarnings("hiding") TSemi _semi_,
        @SuppressWarnings("hiding") PParamSpec _paramSpec_)
    {
        // Constructor
        setSemi(_semi_);

        setParamSpec(_paramSpec_);

    }

    @Override
    public Object clone()
    {
        return new AAnotherParamSpec(
            cloneNode(this._semi_),
            cloneNode(this._paramSpec_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAnotherParamSpec(this);
    }

    public TSemi getSemi()
    {
        return this._semi_;
    }

    public void setSemi(TSemi node)
    {
        if(this._semi_ != null)
        {
            this._semi_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semi_ = node;
    }

    public PParamSpec getParamSpec()
    {
        return this._paramSpec_;
    }

    public void setParamSpec(PParamSpec node)
    {
        if(this._paramSpec_ != null)
        {
            this._paramSpec_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._paramSpec_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._semi_)
            + toString(this._paramSpec_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._semi_ == child)
        {
            this._semi_ = null;
            return;
        }

        if(this._paramSpec_ == child)
        {
            this._paramSpec_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._semi_ == oldChild)
        {
            setSemi((TSemi) newChild);
            return;
        }

        if(this._paramSpec_ == oldChild)
        {
            setParamSpec((PParamSpec) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
