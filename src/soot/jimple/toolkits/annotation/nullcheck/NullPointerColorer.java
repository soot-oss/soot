package soot.jimple.toolkits.annotation.nullcheck;

import soot.*;
import soot.tagkit.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;

public class NullPointerColorer extends BodyTransformer {

	public NullPointerColorer( Singletons.Global g ) {}
    public static NullPointerColorer v() { return G.v().NullPointerColorer(); }

	protected void internalTransform (Body b, String phaseName, Map options) {
		
		BranchedRefVarsAnalysis analysis = new BranchedRefVarsAnalysis (
				new CompleteUnitGraph(b));

		Iterator it = b.getUnits().iterator();

		while (it.hasNext()) {
			Stmt s = (Stmt)it.next();
			
			Iterator usesIt = s.getUseBoxes().iterator();
			FlowSet beforeSet = (FlowSet)analysis.getFlowBefore(s);
				
			while (usesIt.hasNext()) {
				ValueBox vBox = (ValueBox)usesIt.next();
				addColorTags(vBox, beforeSet, s, analysis);
			}

			Iterator defsIt = s.getDefBoxes().iterator();
			FlowSet afterSet = (FlowSet)analysis.getFallFlowAfter(s);

			while (defsIt.hasNext()){
				ValueBox vBox = (ValueBox)defsIt.next();
				addColorTags(vBox, afterSet, s, analysis);
			}
		}
	}
	
	private void addColorTags(ValueBox vBox, FlowSet set, Stmt s, BranchedRefVarsAnalysis analysis){
		
		Value val = vBox.getValue();
		if (val.getType() instanceof RefLikeType) {
			//G.v().out.println(val+": "+val.getClass().toString());
		
			int vInfo = analysis.anyRefInfo(val, set);

			switch (vInfo) {
				case 1 : {
					// analysis.kNull
					s.addTag(new StringTag(val+": Null"));
					vBox.addTag(new ColorTag(ColorTag.RED));
					break;
						 }
				case 2 : {
					// analysis.kNonNull 
					s.addTag(new StringTag(val+": NonNull"));
					vBox.addTag(new ColorTag(ColorTag.GREEN));
					break;
						 }
				case 99 : {
					// analysis.KTop:
					s.addTag(new StringTag(val+": Nullness Unknown"));
					vBox.addTag(new ColorTag(ColorTag.BLUE));
					break;
						  }
				case 0 : {
					// analysis.kBottom
					s.addTag(new StringTag(val+": Nullness Unknown"));
					vBox.addTag(new ColorTag(ColorTag.BLUE));
					break;
						 }
			}
		}
		else {
			
		}
	}
}
