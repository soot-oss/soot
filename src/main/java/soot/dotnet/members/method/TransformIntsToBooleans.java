package soot.dotnet.members.method;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.BooleanConstant;
import soot.BooleanType;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.IntConstant;

public class TransformIntsToBooleans extends BodyTransformer {

  public TransformIntsToBooleans(Singletons.Global g) {
  }

  public static TransformIntsToBooleans v() {
    return G.v().soot_dotnet_members_method_TransformIntsToBooleans();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> opts) {
    for (Unit u : b.getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        Value rop = assign.getRightOp();
        if (rop instanceof CastExpr) {
          CastExpr cast = (CastExpr) rop;
          if (cast.getType() instanceof BooleanType) {
            if (cast.getOp() instanceof IntConstant) {
              IntConstant ic = (IntConstant) cast.getOp();
              assign.setRightOp(BooleanConstant.v(ic.value == 1));
            }
          }
        }
      }
    }
  }

}
