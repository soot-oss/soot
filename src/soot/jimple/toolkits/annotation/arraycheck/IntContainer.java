package soot.jimple.toolkits.annotation.arraycheck;

class IntContainer
{
    static IntContainer[] pool = new IntContainer[100];
    static {
	for (int i=0; i<100; i++)
	    pool[i] = new IntContainer(i-50);
    }

    int value;
    public IntContainer(int v)
    {
	this.value = v;
    }

    public static IntContainer v(int v)
    {
	if ((v >= -50) && (v <= 49))
	{
	    return pool[v+50];
	}
	else
	    return new IntContainer(v);
    }

    public IntContainer dup()
    {
	return new IntContainer(value);
    }

    public int hashCode()
    {
	return value;
    }

    public boolean equals(Object other)
    {
	if (other instanceof IntContainer)
	{
	    return ((IntContainer)other).value == this.value ;
	}
	
	return false;
    }

    public String toString()
    {
	return ""+value;
    }

}	

