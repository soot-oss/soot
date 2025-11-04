package soot.dotnet;

import soot.tagkit.Tag;

/**
 * Saves a reference to the assembly file the entity originates.
 */
public class AssemblyTag implements Tag {

  public static final String ASSEMBLY = "Assembly";
  private String filename;

  public AssemblyTag(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  @Override
  public String getName() {
    return ASSEMBLY;
  }

}
