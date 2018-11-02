package soot.lambdaMetaFactory.jcg;

import java.util.function.Function;

class Lambda1 {
  private static void doSomething() {
    // call in lambda
  }

  public void main() {
    Function<Integer, Boolean> isEven = (Integer a) -> {
      doSomething();
      return a % 2 == 0;
    };
    isEven.apply(2);
  }
}