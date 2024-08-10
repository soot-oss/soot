package soot.dotnet.members;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class InitialFieldTagValue implements Tag {

  public static final String NAME = "InitialFieldValue";
  private byte[] content;

  public InitialFieldTagValue(byte[] content) {
    this.content = content;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return content;
  }

}
