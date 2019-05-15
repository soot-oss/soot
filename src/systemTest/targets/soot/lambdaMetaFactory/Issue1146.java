package soot.lambdaMetaFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Code according to issue 1146: https://github.com/Sable/soot/issues/1146
 *
 * @author Manuel Benz at 2019-05-14
 */
public class Issue1146 {
  public Vertrag getVertrag(String vsnr) {
    List<String> myList = Arrays.asList("element1", "element2", "element3");
    myList.forEach(element -> System.out.println(element));
    return new Vertrag();
  }

  public Vertrag getVertrag2(String vsnr) throws BpmnError {
    Vertrag vertrag = null;
    return Optional.ofNullable(vertrag).orElseThrow(() -> {
      return new BpmnError("not found");
    });
  }

  private class Vertrag {
  }

  private class BpmnError extends Exception {
    public BpmnError(String msg) {
      super(msg);
    }
  }
}
