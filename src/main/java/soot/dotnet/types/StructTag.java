package soot.dotnet.types;

import soot.tagkit.Tag;

/**
 * A tag indicating that the class is a .NET struct.
 */
public class StructTag implements Tag {

  public static final String NAME = "StructTag";

  @Override
  public String getName() {
    return NAME;
  }

}
