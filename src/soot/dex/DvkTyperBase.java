package soot.dex;

import soot.Local;
import soot.Type;
import soot.ValueBox;
import soot.dex.DvkTyper.DeferedTypeConstraint;

public abstract class DvkTyperBase {
  public static boolean ENABLE_DVKTYPER = false;
  public abstract void setType(ValueBox v, Type type);
}
