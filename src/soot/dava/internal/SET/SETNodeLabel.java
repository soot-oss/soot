package soot.dava.internal.SET;
import soot.*;

public class SETNodeLabel
{
    private String name;

    public SETNodeLabel()
    {
	name = null;
    }

    public void set_Name()
    {
	if (name == null) 
	    name = "label_" + Integer.toString( G.v().SETNodeLabel_uniqueId++);
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
