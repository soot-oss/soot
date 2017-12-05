package soot.jimple.toolkits.pointer;

import java.util.*;

import soot.PointsToSet;
import soot.SootField;

import soot.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;


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
	
    public String toString()
    {
		boolean empty = true;
		final StringBuffer ret = new StringBuffer();
		if( fields != null )
		{
			for (Object element : fields.keySet()) {
				final Object field = element;
				ret.append( "[Field: "+field+" ");
				Object baseObj = fields.get(field);
				if(baseObj instanceof PointsToSetInternal)
				{
/*					PointsToSetInternal base = (PointsToSetInternal) fields.get(field);
					base.forall( 
						new P2SetVisitor() 
						{
	    					public void visit( Node n )
	    					{
	        					ret.append(n.getNumber() + " ");
	    					}
						}
					);
*/					
					int baseSize = ((PointsToSetInternal)baseObj).size();
					ret.append(baseSize + (baseSize == 1 ? " Node]\n" : " Nodes]\n"));
				}
				else
				{
					ret.append(baseObj + "]\n");
				}
				empty = false;
			}
		}
		if( globals != null )
		{
			for( Iterator globalIt = globals.iterator(); globalIt.hasNext(); )
			{
				final Object global = globalIt.next();
				ret.append( "[Global: "+global+"]\n" );
				empty = false;
			}
		}
		if(empty) ret.append("[emptyset]\n");
		return ret.toString();
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
				for (Object element : o.fields.keySet()) {
				    final Object field = element;
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
		    for (RWSet set : oth.sets) {
		    	this.union(set);
		    }
		}
		if( !getCallsNative() && other.getCallsNative() )
		{
		    setCallsNative();
		    return true;
		}
		return ret;
    }
    
    public boolean containsField( Object field )
    {
    	if(fields == null) return false;
    	return fields.containsKey(field);
    }
    
    public CodeBlockRWSet intersection( MethodRWSet other )
    {// May run slowly... O(n^2)
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
		    for (Object element : other.fields.keySet()) {
		        final Object field = element;
		        
				if( fields.containsKey( field ) ) 
				{
					PointsToSet pts1 = getBaseForField( field );
					PointsToSet pts2 = other.getBaseForField( field );
					if(pts1 instanceof FullObjectSet)
						ret.addFieldRef(pts2, field);
					else if(pts2 instanceof FullObjectSet)
						ret.addFieldRef(pts1, field);
					else if( pts1.hasNonEmptyIntersection(pts2) )
					{
						if((pts1 instanceof PointsToSetInternal) && (pts2 instanceof PointsToSetInternal))
						{
							final PointsToSetInternal pti1 = (PointsToSetInternal) pts1;
							final PointsToSetInternal pti2 = (PointsToSetInternal) pts2;
							final PointsToSetInternal newpti = new HashPointsToSet(pti1.getType(), (PAG) Scene.v().getPointsToAnalysis());

							pti1.forall( 
								new P2SetVisitor() 
								{
    	        					public void visit( Node n )
    	        					{
    	            					if( pti2.contains( n ) ) newpti.add(n);
    	        					}
    	    					}
    	    				);
    	    				
							ret.addFieldRef(newpti, field);
    	    			}
			    	}
				}
		    }
		}
		return ret;
    }

	public boolean addFieldRef( PointsToSet otherBase, Object field )
	{
		boolean ret = false;
		if( fields == null )
			fields = new HashMap();
		
		// Get our points-to set, merge with other
		PointsToSet base = getBaseForField( field );
		if( base instanceof FullObjectSet )
			return false;
		if( otherBase instanceof FullObjectSet )
		{
			fields.put( field, otherBase );
			return true;
		}
		if( otherBase.equals( base ) )
			return false;
		if( base == null )
		{			
			// NOTE: this line makes unsafe assumptions about the PTA
			PointsToSetInternal newpti = new HashPointsToSet(((PointsToSetInternal)otherBase).getType(), (PAG) Scene.v().getPointsToAnalysis());
			base = newpti;
			fields.put( field, base );
		} 
		
		ret = ((PointsToSetInternal)base).addAll((PointsToSetInternal) otherBase, null) | ret;
		return ret;
	}
}
