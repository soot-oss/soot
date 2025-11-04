package soot.tagkit;

public class SourceFileTag implements Tag {

  public static final String NAME = "SourceFileTag";

  private String sourceFile;
  private String absolutePath;

  public SourceFileTag(String sourceFile) {
    this(sourceFile, null);
  }

  public SourceFileTag(String sourceFile, String path) {
    this.sourceFile = sourceFile.intern();
    this.absolutePath = path;
  }

  public SourceFileTag() {
  }

  @Override
  public String getName() {
    return NAME;
  }

  public void setSourceFile(String srcFile) {
    sourceFile = srcFile.intern();
  }

  public String getSourceFile() {
    return sourceFile;
  }

  public void setAbsolutePath(String path) {
    absolutePath = path;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  @Override
  public String toString() {
    return sourceFile;
  }
}
