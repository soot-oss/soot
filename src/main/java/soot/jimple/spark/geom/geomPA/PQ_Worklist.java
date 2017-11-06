/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.geom.geomPA;

/**
 * The implementation of prioritized worklist.
 * The priority is computed by two parameters: the Topsort order and the least recent fired (LRF) timestamp.
 * For two pointers p and q
 * p has higher priority than q iff:
 * 1. topsort_order(p) < topsort_order(q)
 * 2. topsort_order(p) == topsort_order(q) && LRF(p) < LRF(q)
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
