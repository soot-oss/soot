
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
	boolean[] isNodeLocal;

	public CallLocalityContext(List nodes)
	{
//		if(nodes == null)
//			throw new RuntimeException("Cannot create CallLocalityContext with null nodes list... it's illogical.");
//		if(nodes.size() == 0)
//			throw new RuntimeException("Cannot create CallLocalityContext with empty nodes list... it's illogical.");
		this.nodes = new ArrayList();
		this.nodes.addAll(nodes);

		isNodeLocal = new boolean[nodes.size()];
		for(int i = 0; i < nodes.size(); i++)
		{
			isNodeLocal[i] = false;
		}
	}
	
	public void setAllFieldsLocal()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof InstanceFieldRef)
				isNodeLocal[i] = true;
		}
	}

	public void setAllFieldsShared()
	{
		for(int i = 0; i < nodes.size(); i++)
		{
			Ref r = (Ref) ((EquivalentValue) nodes.get(i)).getValue();
			if(r instanceof InstanceFieldRef)
				isNodeLocal[i] = false;
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
					isNodeLocal[i] = true;
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
					isNodeLocal[i] = false;
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
				isNodeLocal[i] = true;
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
				isNodeLocal[i] = false;
			}
		}
	}
	
	public List getLocalRefs()
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if(isNodeLocal[i])
				ret.add(nodes.get(i));
		}
		return ret;
	}
	
	public List getSharedRefs()
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if(!isNodeLocal[i])
				ret.add(nodes.get(i));
		}
		return ret;
	}
	
	public boolean isFieldLocal(EquivalentValue fieldRef)
	{
		List ret = new ArrayList();
		for(int i = 0; i < nodes.size(); i++)
		{
			if( nodes.get(i).equals(fieldRef) )
				return isNodeLocal[i];
		}
		throw new RuntimeException("Field is not present in CallLocalityContext");
//		return false;
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
				fieldrefs = fieldrefs + r + ": " + (isNodeLocal[i] ? "local" : "shared") + "\n";
			else if(r instanceof StaticFieldRef)
				staticrefs = staticrefs + r + ": " + (isNodeLocal[i] ? "local" : "shared") + "\n";
			else if(r instanceof ParameterRef)
				paramrefs = paramrefs + r + ": " + (isNodeLocal[i] ? "local" : "shared") + "\n";
			else if(r instanceof ThisRef)
				thisref = thisref + r + ": " + (isNodeLocal[i] ? "local" : "shared") + "\n";
			else
				return "Call Locality Context: HAS STRANGE NODE " + r + "\n";
		}
		return "Call Locality Context: \n" + fieldrefs + paramrefs + thisref + staticrefs;
	}
}
