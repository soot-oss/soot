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

import java.util.Deque;
import java.util.LinkedList;

/**
 * Implementation of the worklist with FIFO strategy.
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
