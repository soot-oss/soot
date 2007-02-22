package soot.jimple.toolkits.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.mhp.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// ClassDataFlowAnalysis written by Richard L. Halpert, 2006-12-26
// Finds the class's inputs and outputs

public class ClassDataFlowAnalysis // extends ForwardFlowAnalysis
{
	Map classToMethods;
	Map methodToDataFlowGraph;
	
	public ClassDataFlowAnalysis()
	{
		 classToMethods = new HashMap();
		 methodToDataFlowGraph = new HashMap();
		 
		 
	}

	private MutableDirectedGraph FlowInsensitiveMethodAnalysis(UnitGraph g)
	{
		// Checks flow-insensitively which fields, globals, and parameters are accessed
		HashSet fieldsStaticsParamsAccessed = new HashSet();		

		// Get list of fields, globals, and parameters that are accessed
		Iterator stmtIt = g.iterator();
		while(stmtIt.hasNext())
		{
			Stmt s = (Stmt) stmtIt.next();
			if( s instanceof IdentityStmt )
			{
				IdentityStmt is = (IdentityStmt) s;
				IdentityRef ir = (IdentityRef) is.getRightOp();
				if( ir instanceof ParameterRef )
				{
					fieldsStaticsParamsAccessed.add(ir);
				}
			}
			if(s.containsFieldRef())
			{
				FieldRef ref = s.getFieldRef();
				if( ref instanceof StaticFieldRef )
				{
					// This should be added to the list of fields accessed
					// static fields "belong to everyone"
					fieldsStaticsParamsAccessed.add(ref);
				}
				else if( ref instanceof InstanceFieldRef )
				{
					// If this field is a field of this class,
					// then this should be added to the list of fields accessed
					InstanceFieldRef ifr = (InstanceFieldRef) ref;
					Value base = ifr.getBase();
					if(base instanceof Local)
					{
//		            	Iterator baseDefsIt = sld.getDefsOfAt( (Local) base , s ).iterator();
//		            	if( baseDefsIt.hasNext() )
//		            	{
//		                	DefinitionStmt baseDef = (DefinitionStmt) baseDefsIt.next();
//	                		if( baseDef.getRightOp() instanceof ThisRef )
//	                		{
								fieldsStaticsParamsAccessed.add(ref);
//	                		}
//		                }
		            }
				}
			}
		}
		
		// Assume all fields, globals, and parameters flow data to all others
		MutableDirectedGraph dataFlowGraph = new MemoryEfficientGraph();
		Iterator accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Ref r = (Ref) accessedIt1.next();
			dataFlowGraph.addNode(r);
		}

		accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Ref r = (Ref) accessedIt1.next();
			Iterator accessedIt2 = fieldsStaticsParamsAccessed.iterator();
			while(accessedIt2.hasNext())
			{
				Ref s = (Ref) accessedIt2.next();
				dataFlowGraph.addEdge(r, s);
			}
		}
		
		return dataFlowGraph;
	}
	
}

