package soot.jimple.spark.sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import soot.G;
import soot.Singletons;
import soot.jimple.spark.sets.SharedListSet.ListNode;
import soot.jimple.spark.sets.SharedListSet.Pair;

/**A singleton to hold the hash table for SharedListSet*/

public class AllSharedListNodes {

	private static final Logger logger =LoggerFactory.getLogger(AllSharedListNodes.class);
    public AllSharedListNodes( Singletons.Global g ) {}
    public static AllSharedListNodes v() { return G.v().soot_jimple_spark_sets_AllSharedListNodes(); }
	public Map<Pair, ListNode> allNodes = new HashMap<Pair, ListNode>();
}
