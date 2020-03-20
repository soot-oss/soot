package soot.jimple.toolkits.callgraph;

import soot.SootMethod;
import soot.jimple.Stmt;

/**
 * Abstract base class for call sites
 * 
 * @author Steven Arzt
 *
 */
public class AbstractCallSite {

  protected Stmt stmt;
  protected SootMethod container;

  public AbstractCallSite(Stmt stmt, SootMethod container) {
    this.stmt = stmt;
    this.container = container;
  }

  public Stmt getStmt() {
    return stmt;
  }

  public SootMethod getContainer() {
    return container;
  }

}
