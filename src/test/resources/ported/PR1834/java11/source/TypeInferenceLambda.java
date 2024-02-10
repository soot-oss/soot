import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.BinaryOperator;

class TypeInferenceLambda{

  public void lambda() {
    BinaryOperator<Integer> binOp = (var x, var y) -> x+y;

    int result = binOp.apply(2,3);
  }
}