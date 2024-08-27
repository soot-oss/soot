package soot.dotnet;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

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

  @Override
  public byte[] getValue() throws AttributeValueException {
    return null;
  }

}
