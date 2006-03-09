package soot.toolkits.astmetrics;

import java.util.*;
import polyglot.ast.*;
import polyglot.visit.NodeVisitor;

public class StmtSumWeightedByDepth extends ASTMetric {
  
  int currentDepth;
  int sum;
  int maxDepth;
  int numNodes;
  //int returnSum;
  
  Stack labelNodesSoFar = new Stack();
  ArrayList blocksWithAbruptFlow = new ArrayList();
  
  public static boolean tmpAbruptChecker = false; 
  
  public StmtSumWeightedByDepth(Node node){
    super(node);
  }
  
  
  public void reset() {
    currentDepth = 1; //inside a class
    maxDepth = 1;
    sum = 0;
    numNodes = 0;
    //returnSum =0;
  }
  
  public void addMetrics(ClassData data) {
    data.addMetric(new MetricData("MaxDepth",new Integer(maxDepth)));
    data.addMetric(new MetricData("D-W-Complexity",new Integer(sum)));
    data.addMetric(new MetricData("AST-Node-Count",new Integer(numNodes)));
    //data.addMetric(new MetricData("Return-Depth-Sum",new Integer(returnSum)));
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
   * If the "if" stmt has code if(cond) { stmt } OR if(cond) stmt this will only increase the depth by 1 (ignores compound stmt blocks)
   * 
   * If, Loop, Try, Synch, ProcDecl, Init, Switch, LocalClassDecl .... add currentDepth to sum and then increase depth by one
   * irrespective of how many stmts there are in the body
   * 
   * Block ... if it is a block within a block, add currentDepth plus increment depth ONLY if it has abrupt flow out of it.
   */
  public NodeVisitor enter(Node parent, Node n){
    numNodes++;
    if (n instanceof CodeDecl) {
      // maintain stack of label arrays (can't have label from inside method to outside)
      labelNodesSoFar.push(new ArrayList());
    }
    else if (n instanceof Labeled) {
      // add any labels we find to the array
      ((ArrayList)labelNodesSoFar.peek()).add(((Labeled)n).label());
    }
    
    if(n instanceof If || n instanceof Loop || n instanceof Try || n instanceof Switch
        || n instanceof LocalClassDecl || n instanceof Synchronized 
        || n instanceof ProcedureDecl || n instanceof Initializer ){
      sum += currentDepth*2;
      increaseDepth();
    } else if (parent instanceof Block && n instanceof Block) {
      StmtSumWeightedByDepth.tmpAbruptChecker = false;
      n.visit(new NodeVisitor() {
        // extended NodeVisitor that checks for branching out of a block
        public NodeVisitor enter(Node parent, Node node){
          if(node instanceof Branch) {
            Branch b = (Branch)node;
            // null branching out of a plain block is NOT ALLOWED!
            if (b.label() != null && ((ArrayList)labelNodesSoFar.peek()).contains(b.label()))
            {
              StmtSumWeightedByDepth.tmpAbruptChecker = true;
            }
          }
          return enter(node);
        }
        // this method simply stops further node visiting if we found our info
        public Node override(Node parent, Node node) {
          if (StmtSumWeightedByDepth.tmpAbruptChecker)
            return node;
          return null;
        }
      });
      
      if (StmtSumWeightedByDepth.tmpAbruptChecker)
      {
        blocksWithAbruptFlow.add(n);
        sum += currentDepth*2;
        increaseDepth();
      }
    } else {
      //if(n instanceof Return){
      //	returnSum += currentDepth;
      //	System.out.println("RETURN111111111111111111111111111111111"+currentDepth);
      //}
      sum+= currentDepth;
    }
    
    return enter(n);
  }
  
  
  public Node leave(Node old, Node n, NodeVisitor v){
    
    // stack maintenance, if leaving a method
    if (n instanceof CodeDecl)
      labelNodesSoFar.pop();
    
    if(n instanceof If || n instanceof Loop || n instanceof Try || n instanceof Switch
        || n instanceof LocalClassDecl || n instanceof Synchronized 
        || n instanceof ProcedureDecl || n instanceof Initializer ) {
      decreaseDepth();
    } else if (n instanceof Block && blocksWithAbruptFlow.contains(n)) {
      decreaseDepth();
    }
    return n;
  }
}
