
package soot.jimple.toolkits.thread.mhp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;
import java.util.*;
/**
 *
 */

public class MhpTransformer extends SceneTransformer{

	private static final Logger logger =LoggerFactory.getLogger(MhpTransformer.class);
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

