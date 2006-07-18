package soot.jimple.toolkits.pointer;

import java.util.HashSet;
import java.util.Iterator;

import soot.PointsToSet;
import soot.SootField;

public class CodeBlockRWSet extends MethodRWSet
{
	public int size()
	{
		if(globals == null)
		{
			if(fields == null)
				return 0;
			else
				return fields.size();
		}
		else
		{
			if(fields == null)
				return globals.size();
			else
				return globals.size() + fields.size();
		}
	}
	
    /** Adds the RWSet other into this set. */
    public boolean union( RWSet other )
    {
		if( other == null ) return false;
		if( isFull ) return false;
		boolean ret = false;
		if( other instanceof MethodRWSet )
		{
			MethodRWSet o = (MethodRWSet) other;
			if( o.getCallsNative() ) 
			{
				ret = !getCallsNative() | ret;
				setCallsNative();
		    }
		    if( o.isFull )
		    {
				ret = !isFull | ret;
				isFull = true;
		        if( true ) throw new RuntimeException( "attempt to add full set "+o+" into "+this );
				globals = null;
				fields = null;
				return ret;
		    }
		    if( o.globals != null )
		    {
		    	if( globals == null ) globals = new HashSet();
		    	ret = globals.addAll( o.globals ) | ret;
				if( globals.size() > MAX_SIZE )
				{
				    globals = null;
				    isFull = true;
		                throw new RuntimeException( "attempt to add full set "+o+" into "+this );
				}
		    }
		    if( o.fields != null )
		    {
				for( Iterator fieldIt = o.fields.keySet().iterator(); fieldIt.hasNext(); )
				{
				    final Object field = (Object) fieldIt.next();
				    PointsToSet os = o.getBaseForField( field );
				    ret = addFieldRef( os, field ) | ret;
				}
		    }
		} 
		else if( other instanceof StmtRWSet )
		{
		    StmtRWSet oth = (StmtRWSet) other;
		    if( oth.base != null ) 
		    {
		    	ret = addFieldRef( oth.base, oth.field ) | ret;
		    }
		    else if( oth.field != null ) 
		    {
		    	ret = addGlobal( (SootField) oth.field ) | ret;
		    }
		}
		else if( other instanceof SiteRWSet)
		{
		    SiteRWSet oth = (SiteRWSet) other;
		    for( Iterator sIt = oth.sets.iterator(); sIt.hasNext(); ) 
		    {
		    	this.union((RWSet) sIt.next());
		    }
		}
		if( !getCallsNative() && other.getCallsNative() )
		{
		    setCallsNative();
		    return true;
		}
		return ret;
    }
    
    public CodeBlockRWSet intersection( MethodRWSet other )
    {// May run for a LONG time... O(n^2)
		CodeBlockRWSet ret = new CodeBlockRWSet();

		if( isFull )
			return ret;

		if( globals != null && other.globals != null
			&& !globals.isEmpty() && !other.globals.isEmpty() )
		{
		    for( Iterator it = other.globals.iterator(); it.hasNext(); )
		    {
		    	SootField sg = (SootField) it.next();
				if( globals.contains(sg) ) 
					ret.addGlobal(sg);
		    }
		}
		
		if( fields != null && other.fields != null
			&& !fields.isEmpty() && !other.fields.isEmpty() )
		{
		    for( Iterator fieldIt = other.fields.keySet().iterator(); fieldIt.hasNext(); )
		    {
		        final Object field = (Object) fieldIt.next();
		        
				if( fields.containsKey( field ) ) 
				{
			    	if( Union.hasNonEmptyIntersection(getBaseForField( field ),
										other.getBaseForField( field ) ) )
					{
						ret.addFieldRef(getBaseForField(field),field);
			    	}
				}
		    }
		}
		return ret;
    }


}
