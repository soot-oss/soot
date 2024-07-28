package soot.dotnet.types;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class StructTag implements Tag {

  public static final String NAME = "StructTag";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return null;
  }

}
