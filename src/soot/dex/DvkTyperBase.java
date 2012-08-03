package soot.dex;

import soot.Type;
import soot.ValueBox;

public abstract class DvkTyperBase {
  public static boolean ENABLE_DVKTYPER = false;
  public abstract void setType(ValueBox v, Type type);
  public abstract void setObjectType(ValueBox v);
  public abstract void setConstraint(ValueBox box1, ValueBox box2);
  abstract void assignType();
  public static DvkTyperBase getDvkTyper() {
    // TODO Auto-generated method stub
    return null;
  }
}
