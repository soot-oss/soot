package soot.dava.internal.SET;

public class SETNodeLabel
{
    private String name;
    private static int uniqueId = 0;

    public SETNodeLabel()
    {
	name = null;
    }

    public void set_Name()
    {
	if (name == null) 
	    name = "label_" + Integer.toString( SETNodeLabel.uniqueId++);
    }

    public void set_Name( String name)
    {
	this.name = name;
    }

    public String toString()
    {
	return name;
    }

    public void clear_Name()
    {
	name = null;
    }
}
