package soot.serialization;

import com.esotericsoftware.kryo.io.Input;

import soot.ClassSource;
import soot.FoundFile;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.Unit;
import soot.ValueBox;
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

        IInitialResolver.Dependencies dependencies = new IInitialResolver.Dependencies();

        DependencyCollector collector = new DependencyCollector();
        // fixme put this into the write out process
        for (SootMethod method : sootClass.getMethods()) {
          if (method.isConcrete()) {
            for (Unit unit : method.retrieveActiveBody().getUnits()) {
              for (ValueBox box : unit.getUseAndDefBoxes()) {
                box.getValue().apply(collector);
                dependencies.typesToHierarchy.addAll(collector.getResult());
              }
            }
          }
        }

        return dependencies;
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
