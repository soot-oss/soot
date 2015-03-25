package soot.jimple.toolkits.thread.mhp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.tagkit.*;
import soot.util.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class LoopFinder{
	final static Logger logger = LoggerFactory.getLogger(LoopFinder.class);

	private final Map<Chain, Set<Set<Object>>> chainToLoop = new HashMap<Chain, Set<Set<Object>>>();
	
	LoopFinder(PegGraph peg){
		Chain chain = peg.getMainPegChain();
		DfsForBackEdge dfsForBackEdge = new DfsForBackEdge(chain, peg);
		Map<Object, Object> backEdges = dfsForBackEdge.getBackEdges();
		LoopBodyFinder lbf = new LoopBodyFinder(backEdges, peg);
		Set<Set<Object>> loopBody = 	lbf.getLoopBody();
		testLoops(loopBody);
		chainToLoop.put(chain, loopBody);
		
	}
	private void testLoops(Set<Set<Object>> loopBody){
		logger.info("====loops===");
		Iterator<Set<Object>> it = loopBody.iterator();
		while (it.hasNext()){
			Set loop = it.next();
			Iterator loopIt = loop.iterator();
			logger.info("---loop---");
			while (loopIt.hasNext()){
				JPegStmt o = (JPegStmt)loopIt.next();
				Tag tag = (Tag)o.getTags().get(0);
				logger.info("{} {}",tag,o);
			}
		}
		logger.info("===end===loops===");
	}
	
	
	
	
}
