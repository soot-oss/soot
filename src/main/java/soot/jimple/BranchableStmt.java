package soot.jimple;

import soot.Unit;
import soot.UnitBox;

public interface BranchableStmt extends Stmt {
  public Unit getTarget();

  public void setTarget(Unit target);

  public UnitBox getTargetBox();
}
