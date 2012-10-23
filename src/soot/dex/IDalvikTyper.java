package soot.dex;

import soot.Type;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;

public interface IDalvikTyper {
  
  public static boolean ENABLE_DVKTYPER = true; //false;
  
  public abstract void setType(ValueBox v, Type type);
  public abstract void setObjectType(ValueBox v);
  public abstract void addConstraint(ValueBox box1, ValueBox box2);
  abstract void assignType();
  //public static IDalvikTyper getDvkTyper(); 
  public Stmt captureAssign(JAssignStmt stmt, int current);
}
