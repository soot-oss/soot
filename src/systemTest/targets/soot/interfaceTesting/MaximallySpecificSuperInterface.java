package soot.interfaceTesting;

/** @author Manuel Benz at 21.10.19 */
public class MaximallySpecificSuperInterface extends B implements D {

  public void main() {
    new B().print(); // Prints C
  }
}

class B implements C {}

interface C extends D {
  default void print() {
    System.out.println("C");
  }
}

interface D {
  default void print() {
    System.out.println("D");
  }
}
