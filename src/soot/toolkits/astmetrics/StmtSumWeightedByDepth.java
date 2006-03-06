package soot.toolkits.astmetrics;

import polyglot.ast.*;
import polyglot.visit.NodeVisitor;

public class StmtSumWeightedByDepth extends ASTMetric {

	int currentDepth;
	int sum;
	int maxDepth;
	int returnSum;
	
	
	public StmtSumWeightedByDepth(Node node){
		super(node);
	}
	
	
	public void reset() {
		// TODO Auto-generated method stub
		currentDepth = 1; //inside a class
		maxDepth = 1;
		sum = 0;
		returnSum =0;
	}

	public void addMetrics(ClassData data) {
		// TODO Auto-generated method stub
		data.addMetric(new MetricData("MaxDepth",maxDepth));
		data.addMetric(new MetricData("D-W-Complexity",sum));
		data.addMetric(new MetricData("Return-Depth-Sum",returnSum));
	}

	private void increaseDepth(){
		currentDepth++;
		if(currentDepth > maxDepth)
			maxDepth = currentDepth;
	}
	
	private void decreaseDepth(){
		currentDepth--;
	}
	

	/*
	 * List of Node types which increase depth of traversal!!!
	 * Any construct where one can have a { } increases the depth
	 * hence even though if(cond) stmt doesnt expicitly use a block 
	 * its depth is still +1 when executing the stmt
	 * 
	 * If the "if" stmt has code if(cond) { stmt } this will actually increase the depth by 2 first by the if and then by the block
	 * 
	 * If .... add currentDepth to sum and then increase depth by one for both then and else branch irrespective of how many stmts there are in the body
	 * Loop (Takes care of do while and for): Add currentDepth then increment by 1
	 * Block ... add currentDepth plus increment depth
	 * LocalClassDecl.... add currentDepth plus increment since everything inside is more complex
	 * 
	 * Try ... add currentDepth (Dont incremenet as this will be done by the respective BLOCK as all try catch and finally are blocks!!
	 * Synchronized ... add currentDepth....dont increment depth as this will be done by the block as synchs are always blocks)
	 * ProcedureDecl (methods and constructors. thats where currentDepth is ONE as these belong to the class) ...
	 * Initializer (Handles static and non static class level chunks of code)
	 * Switch .... add current Depth however since each case is necessarily a block dont increment depth that WILL get incremented
	 *       
	 */
	public NodeVisitor enter(Node parent, Node n){
		if(n instanceof If || n instanceof Loop || n instanceof Block || n instanceof LocalClassDecl){
			sum += currentDepth*3;
			increaseDepth();
		}
		else if(n instanceof Try || n instanceof Synchronized || n instanceof ProcedureDecl || n instanceof Initializer || n instanceof Switch){
			sum+= currentDepth*3;
		}
		else{
			if(n instanceof Return){
				returnSum += currentDepth;
				System.out.println("RETURN111111111111111111111111111111111"+currentDepth);
			}
			
			sum+= currentDepth;
		}
		return enter(n);
	}
 
	 
	public Node leave(Node old, Node n, NodeVisitor v){
		if(n instanceof If || n instanceof Loop || n instanceof Block || n instanceof LocalClassDecl){
			decreaseDepth();
		}
		return n;
	}
	
}
