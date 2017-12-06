/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2013 Eric Bodden and others
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
package soot.jimple.toolkits.ide;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;
import heros.solver.IFDSSolver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;

import com.google.common.collect.Table.Cell;


public class JimpleIFDSSolver<D, I extends InterproceduralCFG<Unit, SootMethod>> extends IFDSSolver<Unit, D, SootMethod, I> {

	private final boolean DUMP_RESULTS;

	public JimpleIFDSSolver(IFDSTabulationProblem<Unit, D, SootMethod, I> problem) {
		this(problem,false);
	}

	public JimpleIFDSSolver(IFDSTabulationProblem<Unit,D,SootMethod,I> problem, boolean dumpResults) {
		super(problem);
		this.DUMP_RESULTS = dumpResults;
	}

	@Override
	public void solve() {
		super.solve();
		if (DUMP_RESULTS)
			dumpResults();
	}

	public void dumpResults() {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream("ideSolverDump" + System.currentTimeMillis() + ".csv"));
			List<SortableCSVString> res = new ArrayList<SortableCSVString>();
			for (Cell<Unit, D, ?> entry : val.cellSet()) {
				SootMethod methodOf = (SootMethod) icfg.getMethodOf(entry.getRowKey());
				PatchingChain<Unit> units = methodOf.getActiveBody().getUnits();
				int i = 0;
				for (Unit unit : units) {
					if (unit == entry.getRowKey())
						break;
					i++;
				}
				
				res.add(new SortableCSVString(methodOf + ";" + entry.getRowKey() + "@" + i + ";" + entry.getColumnKey() + ";" + entry.getValue(), i));
			}
			Collections.sort(res);
			// replacement is bugfix for excel view:
			for (SortableCSVString string : res) {
				out.println(string.value.replace("\"", "'"));
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
