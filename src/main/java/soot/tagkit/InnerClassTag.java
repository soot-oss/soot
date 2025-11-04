package soot.tagkit;

public class InnerClassTag implements Tag {

  public static final String NAME = "InnerClassTag";

  private final String innerClass;
  private final String outerClass;
  private final String name;
  private final int accessFlags;

  public InnerClassTag(String innerClass, String outerClass, String name, int accessFlags) {
    this.innerClass = innerClass;
    this.outerClass = outerClass;
    this.name = name;
    this.accessFlags = accessFlags;
    if (innerClass != null && (innerClass.startsWith("L") && innerClass.endsWith(";"))) {
      throw new RuntimeException(
          "InnerClass annotation type string must be of the form a/b/ClassName not '" + innerClass + "'");
    }
    if (outerClass != null && (outerClass.startsWith("L") && outerClass.endsWith(";"))) {
      throw new RuntimeException(
          "OuterType annotation type string must be of the form a/b/ClassName not '" + innerClass + "'");
    }
    if (name != null && name.endsWith(";")) {
      throw new RuntimeException("InnerClass name cannot end with ';', got '" + name + "'");
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  public String getInnerClass() {
    return innerClass;
  }

  public String getOuterClass() {
    return outerClass;
  }

  public String getShortName() {
    return name;
  }

  public int getAccessFlags() {
    return accessFlags;
  }

  @Override
  public String toString() {
    return "[inner=" + innerClass + ", outer=" + outerClass + ", name=" + name + ",flags=" + accessFlags + "]";
  }
}
