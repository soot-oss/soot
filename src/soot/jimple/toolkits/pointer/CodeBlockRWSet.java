package soot.jimple.toolkits.pointer;

import java.util.*;

import soot.PointsToSet;
import soot.SootField;

import soot.*;
import soot.jimple.spark.*;
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
			for( Iterator fieldIt = fields.keySet().iterator(); fieldIt.hasNext(); )
			{
				final Object field = (Object) fieldIt.next();
				ret.append( "[Field: "+field+" ");
				PointsToSetInternal base = (PointsToSetInternal) fields.get(field);
				base.forall( 
					new P2SetVisitor() 
					{
    					public void visit( Node n )
    					{
        					ret.append(n.getNumber() + " ");
    					}
					}
				);
				ret.append("]\n");
				empty = false;
			}
		}
		if( globals != null )
		{
			for( Iterator globalIt = globals.iterator(); globalIt.hasNext(); )
			{
				final Object global = (Object) globalIt.next();
				ret.append( "[Global: "+global+"]\n" );
				empty = false;
			}
		}
		if(empty) ret.append("empty\n");
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
		    for( Iterator fieldIt = other.fields.keySet().iterator(); fieldIt.hasNext(); )
		    {
		        final Object field = (Object) fieldIt.next();
		        
				if( fields.containsKey( field ) ) 
				{
					PointsToSet pts1 = (PointsToSet) getBaseForField( field );
					PointsToSet pts2 = (PointsToSet) other.getBaseForField( field );
			    	if( pts1.hasNonEmptyIntersection(pts2) )
					{
						if(pts1 instanceof FullObjectSet)
							ret.addFieldRef(pts2, field);
						else if(pts2 instanceof FullObjectSet)
							ret.addFieldRef(pts1, field);
						else if((pts1 instanceof PointsToSetInternal) && (pts2 instanceof PointsToSetInternal))
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
		Union u;
		if( base == null )
		{			
//			final PointsToSetInternal newpti = new HashPointsToSet(((PointsToSetInternal)otherBase).getType(), (PAG) Scene.v().getPointsToAnalysis());
//			otherBase.forall(
//				new P2SetVisitor() 
//				{
//					public void visit( Node n )
//					{
//						newpti.add(n);
//					}
//				}
//			);
			PointsToSetInternal newpti = new HashPointsToSet(((PointsToSetInternal)otherBase).getType(), (PAG) Scene.v().getPointsToAnalysis());
			base = newpti;
			fields.put( field, base );
/*			u = G.v().Union_factory.newUnion();
			if( base != null)
				u.addAll( base );
			fields.put( field, u );
			if( base == null )
				addedField( fields.size() );
			ret = true;
			if( fields.keySet().size() > MAX_SIZE )
			{
				fields = null;
				isFull = true;
				if( true )    
					throw new RuntimeException( "attempt to add more than "+MAX_SIZE+" fields into "+this );
				return true;
			}
*/
		} 
		
		ret = ((PointsToSetInternal)base).addAll((PointsToSetInternal) otherBase, null) | ret;
		return ret;
	}
}
