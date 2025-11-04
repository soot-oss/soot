package soot.tagkit;

import soot.SootClass;

public class OuterClassTag implements Tag {

  public static final String NAME = "OuterClassTag";

  private final SootClass outerClass;
  private final String simpleName;
  private final boolean anon;

  public OuterClassTag(SootClass outer, String simpleName, boolean anon) {
    this.outerClass = outer;
    this.simpleName = simpleName;
    this.anon = anon;
  }

  @Override
  public String getName() {
    return NAME;
  }

  public SootClass getOuterClass() {
    return outerClass;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public boolean isAnon() {
    return anon;
  }

  @Override
  public String toString() {
    return "[outer class=" + outerClass.getName() + "]";
  }
}
