package soot.jimple.toolkits.annotation.arraycheck;

class IntValueContainer
{
    private static final int BOT = 0;
    private static final int TOP = 1;
    private static final int INT = 2;

    private int type;
    private int value;

    public IntValueContainer()
    {
	this.type = BOT;
    }

    public IntValueContainer(int v)
    {
	this.type = INT;
	this.value = v;
    }

    public boolean isBottom()
    {
	return (this.type == BOT);
    }

    public boolean isTop()
    {
	return (this.type == TOP);
    }

    public boolean isInteger()
    {
	return this.type == INT;
    }

    public int getValue()
    {
	if (this.type != INT)
	    throw new RuntimeException("IntValueContainer: not integer type");

	return this.value;
    }

    public void setTop()
    {
	this.type = TOP;
    }

    public void setValue(int v)
    {
	this.type = INT;
	this.value = v;
    }

    public void setBottom()
    {
	this.type = BOT;
    }

    public String toString()
    {
	if (type == BOT)
	    return "[B]";
	else
        if (type == TOP)
	    return "[T]";
	else
	    return "["+value+"]";
    }

    public boolean equals(Object other)
    {
	if (!(other instanceof IntValueContainer))
	    return false;

	IntValueContainer otherv = (IntValueContainer)other;

	if ((this.type == INT) && (otherv.type == INT))
	    return (this.value == otherv.value);
	else
	    return (this.type == otherv.type) ;
    }

    public IntValueContainer dup()
    {
	IntValueContainer other = new IntValueContainer();
	other.type = this.type;
	other.value = this.value;
	
	return other;	
    }
}
 
