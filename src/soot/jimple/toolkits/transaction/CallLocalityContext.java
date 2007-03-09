
package soot.jimple.toolkits.transaction;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.toolkits.mhp.findobject.*;
import soot.jimple.internal.*;
import soot.toolkits.mhp.stmt.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.options.SparkOptions;
import soot.util.*;
import java.util.*;
import java.io.*;

/** CallLocalityContext written by Richard L. Halpert 2007-03-05
 *  Acts as a container for the locality information collected about a call site
 *  by one of the Local Objects Analyses.
 */

public class CallLocalityContext
{
	List nodes;
	List isNodeLocal;

	public CallLocalityContext(List nodes)
	{
//		if(nodes == null)
//			throw new RuntimeException("Cannot create CallLocalityContext with null nodes list... it's illogical.");
//		if(nodes.size() == 0)
//			throw new RuntimeException("Cannot create CallLocalityContext with empty nodes list... it's illogical.");
		this.nodes = new ArrayList();
		this.nodes.addAll(nodes);

		isNodeLocal = new ArrayList(nodes.size());
		for(int i = 0; i < nodes.size(); i++)
		{
			isNodeLocal.add(i, Boolean.FALSE);
		}
	}
	
	public void setFieldLocal(EquivalentValue fieldRef)
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if( fieldRef.equals(nodes.get(i)) )
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.TRUE);
				return;
			}
		}
		throw new RuntimeException("Field " + fieldRef + " is not present in CallLocalityContext\n" + toString());
//		return false;
	}
	
	public void setFieldShared(EquivalentValue fieldRef)
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if( fieldRef.equals(nodes.get(i)) )
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.FALSE);
				return;
			}
		}
		throw new RuntimeException("Field " + fieldRef + " is not present in CallLocalityContext\n" + toString());
//		return false;
	}
	
	public void setAllFieldsLocal()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof InstanceFieldRef)
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.TRUE);
			}
		}
	}

	public void setAllFieldsShared()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof InstanceFieldRef)
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.FALSE);
			}
		}
	}
	
	public void setParamLocal(int index)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ParameterRef)
			{
				ParameterRef pr = (ParameterRef) r;
				if(pr.getIndex() == index)
				{
					isNodeLocal.remove(i);
					isNodeLocal.add(i, Boolean.TRUE);
				}
			}
		}
	}
	
	public void setParamShared(int index)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ParameterRef)
			{
				ParameterRef pr = (ParameterRef) r;
				if(pr.getIndex() == index)
				{
					isNodeLocal.remove(i);
					isNodeLocal.add(i, Boolean.FALSE);
				}
			}
		}
	}
	
	public void setAllParamsLocal()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ParameterRef)
			{
				ParameterRef pr = (ParameterRef) r;
				if(pr.getIndex() != -1)
				{
					isNodeLocal.remove(i);
					isNodeLocal.add(i, Boolean.TRUE);
				}
			}
		}
	}
	
	public void setAllParamsShared()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ParameterRef)
			{
				ParameterRef pr = (ParameterRef) r;
				if(pr.getIndex() != -1)
				{
					isNodeLocal.remove(i);
					isNodeLocal.add(i, Boolean.FALSE);
				}
			}
		}
	}
	
	public void setThisLocal()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ThisRef)
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.TRUE);
			}
		}
	}
	
	public void setThisShared()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof ThisRef)
			{
				isNodeLocal.remove(i);
				isNodeLocal.add(i, Boolean.FALSE);
			}
		}
	}
	
	public void setReturnLocal()
	{
		setParamLocal(-1);
	}
	
	public void setReturnShared()
	{
		setParamShared(-1);
	}
	
	public List getLocalRefs()
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if(((Boolean) isNodeLocal.get(i)).booleanValue())
				ret.add(nodes.get(i));
		}
		return ret;
	}
	
	public List getSharedRefs()
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if(!((Boolean) isNodeLocal.get(i)).booleanValue())
				ret.add(nodes.get(i));
		}
		return ret;
	}
	
	public boolean isFieldLocal(EquivalentValue fieldRef)
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if( fieldRef.equals(nodes.get(i)) )
				return ((Boolean) isNodeLocal.get(i)).booleanValue();
		}
		return false; // catches static fields that were not included in the original context
//		throw new RuntimeException("Field " + fieldRef + " is not present in CallLocalityContext\n" + toString());
//		return false;
	}
	
	public void merge(CallLocalityContext other)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Boolean temp = new Boolean(((Boolean) isNodeLocal.get(i)).booleanValue() && ((Boolean) other.isNodeLocal.get(i)).booleanValue());
			isNodeLocal.remove(i);
			isNodeLocal.add(i, temp);
		}
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof CallLocalityContext)
		{
			CallLocalityContext other = (CallLocalityContext) o;
			return isNodeLocal.equals(other.isNodeLocal);// && nodes.equals(other.nodes);
		}
		return false;
	}
	
	public int hashCode()
	{
		return isNodeLocal.hashCode();
	}
	
	public boolean isAllShared(boolean refsOnly)
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			if( (!refsOnly || ((EquivalentValue) nodes.get(i)).getValue().getType() instanceof RefLikeType) && ((Boolean) isNodeLocal.get(i)).booleanValue())
				return false;
		}
		return true;
	}
	
	public String toString()
	{
		String fieldrefs = "";
		String staticrefs = "";
		String paramrefs = ""; // includes returnref
		String thisref = "";
		if(nodes.size() == 0)
			return "Call Locality Context: NO NODES\n";
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof InstanceFieldRef)
				fieldrefs = fieldrefs + r + ": " + (((Boolean) isNodeLocal.get(i)).booleanValue() ? "local" : "shared") + "\n";
			else if(r instanceof StaticFieldRef)
				staticrefs = staticrefs + r + ": " + (((Boolean) isNodeLocal.get(i)).booleanValue() ? "local" : "shared") + "\n";
			else if(r instanceof ParameterRef)
				paramrefs = paramrefs + r + ": " + (((Boolean) isNodeLocal.get(i)).booleanValue() ? "local" : "shared") + "\n";
			else if(r instanceof ThisRef)
				thisref = thisref + r + ": " + (((Boolean) isNodeLocal.get(i)).booleanValue() ? "local" : "shared") + "\n";
			else
				return "Call Locality Context: HAS STRANGE NODE " + r + "\n";
		}
		return "Call Locality Context: \n" + fieldrefs + paramrefs + thisref + staticrefs;
	}
	
	public String toShortString()
	{
		String ret = "[";
		for(int i = 0; i < nodes.size(); i++)
		{
			ret = ret + (((Boolean) isNodeLocal.get(i)).booleanValue() ? "L" : "S");
		}
		return ret + "]";
	}
}
