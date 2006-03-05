package soot.toolkits.astmetrics;

import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

/*
 * A unary boolean condition should have the complexity (BooleanLit) 1
 * A noted condition (!)  +0.5 
 * A binary relational operation ( < > <= >= == ) +0.5
 * A boolean logical operator ( AND and OR) +1.0
 */
public class ConditionComplexityMetric extends ASTMetric {
	int loopComplexity;
	int ifComplexity;
	
	public ConditionComplexityMetric(polyglot.ast.Node node){
		super(node);
	}
	
	
	public void reset() {
		loopComplexity=ifComplexity=0;
	}

	public void addMetrics(ClassData data) {
		data.addMetric(new MetricData("Loop-Cond-Complexity",loopComplexity));
		data.addMetric(new MetricData("If-Cond-Complexity",ifComplexity));
		data.addMetric(new MetricData("Total-Cond-Complexity", (loopComplexity+ifComplexity)));		
	}

	public NodeVisitor enter(Node parent, Node n){
		if(n instanceof Loop){
			Expr expr = ((Loop)n).cond();
			loopComplexity += condComplexity(expr);
		}
		else if (n instanceof If){
			Expr expr = ((If)n).cond();
			ifComplexity += condComplexity(expr);
		}
		
		return enter(n);
	}
	
	private int condComplexity(Expr expr){

		//boolean literal
		//binary   check for AND and  OR ... else its relational!!
		//unary  (Check for NOT)
		//if(expr instanceof BooleanLit)
		
		
		
		return 0;
	}

}
