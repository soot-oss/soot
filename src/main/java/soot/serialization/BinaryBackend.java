package soot.serialization;

import com.esotericsoftware.kryo.io.Output;

import java.io.OutputStream;

import soot.SootClass;
import soot.SootMethod;

/**
 * @author Manuel Benz at 2019-08-26
 */
public class BinaryBackend {

  public void write(SootClass c, OutputStream outStream) {
    try (Output out = new Output(outStream)) {
      // make sure all bodies are loaded before we serialize. we won't have any method sources afterwards
      c.getMethods().stream().filter(SootMethod::isConcrete).forEach(SootMethod::retrieveActiveBody);
      // TODO add dependent types as tags so we can have eager loading or stay with lazy loading for bodies?
      SootSerializer.v().writeObject(out, c);
    }
  }
}
