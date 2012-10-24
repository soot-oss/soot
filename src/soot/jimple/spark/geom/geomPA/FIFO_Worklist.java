/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Implement the worklist with FIFO strategy.
 * 
 * @author xiao
 *
 */
public class FIFO_Worklist implements IWorklist 
{
	Deque<IVarAbstraction> Q = null;

	@Override
	public void initialize( int size )
	{
		Q = new LinkedList<IVarAbstraction>();
	}
	
	@Override
	public boolean has_job() {
		return Q.size() != 0;
	}

	@Override
	public IVarAbstraction next() {
		IVarAbstraction t = Q.getFirst();
		Q.removeFirst();
		t.Qpos = 0;
		return t;
	}

	@Override
	public void push(IVarAbstraction pv) {
		if (pv.Qpos == 0) {
			Q.addLast(pv);
			pv.Qpos = 1;
		}
	}

	@Override
	public int size() {
		return Q.size();
	}

	@Override
	public void clear() {
		Q = null;
	}
}
