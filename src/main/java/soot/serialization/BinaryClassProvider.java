package soot.serialization;

import com.esotericsoftware.kryo.io.Input;
import soot.ClassSource;
import soot.FoundFile;
import soot.SootClass;
import soot.SourceLocator;
import soot.javaToJimple.IInitialResolver;

/**
 * @author Manuel Benz at 2019-08-26
 */
public class BinaryClassProvider implements soot.ClassProvider {

  @Override
  public ClassSource find(String className) {
    String clsFile = className.replace('.', '/') + ".bin";
    FoundFile foundFile = SourceLocator.v().lookupInClassPath(clsFile);
    return foundFile == null ? null : new BinaryClassSource(className, foundFile);
  }

  private class BinaryClassSource extends ClassSource {
    private FoundFile foundFile;

    public BinaryClassSource(String className, FoundFile foundFile) {
      super(className);
      this.foundFile = foundFile;
    }

    @Override
    public IInitialResolver.Dependencies resolve(SootClass sc) {
      try (Input input = new Input(foundFile.inputStream())) {
        SootClass sootClass = SootSerializer.v().readObject(input, sc);
        return new IInitialResolver.Dependencies();
      }
    }

    @Override
    public void close() {
      if (foundFile != null) {
        foundFile.close();
        foundFile = null;
      }
    }
  }
}
