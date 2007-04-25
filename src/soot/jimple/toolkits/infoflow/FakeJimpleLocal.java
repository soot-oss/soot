package soot.jimple.toolkits.infoflow;

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class FakeJimpleLocal extends JimpleLocal
{
	Local realLocal;
	
    /** Constructs a FakeJimpleLocal of the given name and type. */
    public FakeJimpleLocal(String name, Type t, Local realLocal)
    {
    	super(name, t);
    }

    /** Returns true if the given object is structurally equal to this one. */
    public boolean equivTo(Object o)
    {
    	if(o == null)
    		return false;
    	if(o instanceof JimpleLocal) 
    	{
    		if(getName() != null && getType() != null)
	        	return getName().equals(((Local) o).getName()) && getType().equals(((Local) o).getType());
	        else if(getName() != null)
	        	return getName().equals(((Local) o).getName()) && ((Local) o).getType() == null;
	        else if(getType() != null)
	        	return ((Local) o).getName() == null && getType().equals(((Local) o).getType());
	        else
	        	return ((Local) o).getName() == null && ((Local) o).getType() == null;
    	}
        return false;
    }
    
    public boolean equals(Object o)
    {
    	return equivTo(o);
    }

    /** Returns a clone of the current JimpleLocal. */
    public Object clone()
    {
        return new FakeJimpleLocal(getName(), getType(), realLocal);
    }
    
    public Local getRealLocal()
    {
    	return realLocal;
    }
}

