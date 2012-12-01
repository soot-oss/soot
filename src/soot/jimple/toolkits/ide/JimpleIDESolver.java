package soot.jimple.toolkits.ide;

import heros.IDETabulationProblem;
import heros.InterproceduralCFG;
import heros.debugsupport.NewEdgeSerializer;
import heros.solver.IDESolver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;

import com.google.common.collect.Table.Cell;

public class JimpleIDESolver<D, V> extends IDESolver<Unit, D, SootMethod, V, InterproceduralCFG<Unit,SootMethod>> {

	private final boolean DUMP_RESULTS;

	public JimpleIDESolver(IDETabulationProblem<Unit,D,SootMethod,V,InterproceduralCFG<Unit,SootMethod>> problem) {
		this(problem,false);
	}
	
	public JimpleIDESolver(IDETabulationProblem<Unit,D,SootMethod,V,InterproceduralCFG<Unit,SootMethod>> problem, boolean dumpResults) {
		super(problem);
		this.DUMP_RESULTS = dumpResults;
	}
	
	@Override
	public void solve(int numThreads) {
		super.solve(numThreads);
		if(DUMP_RESULTS)
			dumpResults();
	}
	
	public void dumpResults() {
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream("ideSolverDump"+System.currentTimeMillis()+".csv"));
			List<String> res = new ArrayList<String>();
			for(Cell<Unit, D, V> entry: val.cellSet()) {
				SootMethod methodOf = (SootMethod) icfg.getMethodOf(entry.getRowKey());
				PatchingChain<Unit> units = methodOf.getActiveBody().getUnits();
				int i=0;
				for (Unit unit : units) {
					if(unit==entry.getRowKey())
						break;
					i++;
				}

				res.add(methodOf+";"+entry.getRowKey()+"@"+i+";"+entry.getColumnKey()+";"+entry.getValue());
			}
			Collections.sort(res);
			for (String string : res) {
				out.println(string);
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected NewEdgeSerializer<SootMethod, D, Unit, V> createEdgeListener(ObjectOutputStream oos) {
		return new EdgeSerializer<D,V>(oos);
	}

}
