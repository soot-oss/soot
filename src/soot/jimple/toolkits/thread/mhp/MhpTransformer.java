
package soot.jimple.toolkits.thread.mhp;

import soot.*;
import java.util.*;
/**
 *
 */

public class MhpTransformer extends SceneTransformer{
    public MhpTransformer(Singletons.Global g){}
    public static MhpTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_thread_mhp_MhpTransformer();
	}	
	
    MhpTester mhpTester;
    
	protected void internalTransform(String phaseName, Map options)
	{
		getMhpTester().printMhpSummary();
	}
	
	public MhpTester getMhpTester() {
		if(mhpTester == null)
			mhpTester = new SynchObliviousMhpAnalysis();
		return mhpTester;
	}
	public void setMhpTester(MhpTester mhpTester) {
		this.mhpTester = mhpTester;
	}
}

