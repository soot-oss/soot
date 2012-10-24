/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

/**
 * The implementation of prioritized worklist.
 * The priority is computed by two parameters: the topsort code and the least recent fired code
 * For two pointers p and q
 * p has higher priority than q iff:
 * 1. topsort_code(p) < topsort_code(q)
 * 2. topsort_code(p) == topsort_code(q) && lrf(p) < lrf(q)
 * 
 * @author xiao
 *
 */
public class PQ_Worklist implements IWorklist {

	private IVarAbstraction[] heap = null;
	int cur_tail = 0;
	
	@Override
	public void initialize( int size )
	{
		heap = new IVarAbstraction[ size ];
		cur_tail = 1;
	}
	
	@Override
	public boolean has_job() {
		return cur_tail > 1;
	}

	@Override
	public IVarAbstraction next() {
		IVarAbstraction ret = heap[1];
		--cur_tail;
		
		if ( cur_tail > 1 ) {
			IVarAbstraction e = heap[cur_tail];
			int k = 1;
			
			while ( (k*2) < cur_tail ) {
				int kk = k * 2;
				if ( (kk+1) < cur_tail && heap[kk+1].lessThan(heap[kk]) )
					kk++;
				if ( e.lessThan(heap[kk]) )
					break;
				heap[k] = heap[kk];
				heap[k].Qpos = k;
				k = kk;
			}
			
			e.Qpos = k;
			heap[k] = e;
		}
		
		ret.Qpos = 0;
		return ret;
	}

	@Override
	public void push(IVarAbstraction e) {
		e.lrf_value++;
		
		if ( e.Qpos == 0 ) {
			// This element has not been inserted
			int k = cur_tail;
			
			while ( k > 1 ) {
				int kk = k / 2;
				if ( heap[kk].lessThan(e) )
					break;
				heap[k] = heap[kk];
				heap[k].Qpos = k;
				k /= 2;
			}
			
			e.Qpos = k;
			heap[k] = e;
			++cur_tail;
		}
		else {
			// We decrease this element whenever possible
			int k = e.Qpos;
			while ( (k*2) < cur_tail ) {
				int kk = k * 2;
				if ( (kk+1) < cur_tail && heap[kk+1].lessThan(heap[kk]) )
					kk++;
				if ( e.lessThan(heap[kk]) )
					break;
				heap[k] = heap[kk];
				heap[kk].Qpos = k;
				k = kk;
			}
			e.Qpos = k;
			heap[k] = e;
		}
	}

	@Override
	public int size() {
		return cur_tail - 1;
	}

	@Override
	public void clear() {
		cur_tail = 1;
	}
}
