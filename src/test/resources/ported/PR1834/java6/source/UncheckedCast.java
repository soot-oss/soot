import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***@author Kaustubh Kelkar*/

class UncheckedCast{

    public void uncheckedCastDisplay(){
        List list = Arrays.asList(5,8,9,6);
        List<Double> intList= list;
        System.out.println(intList);
    }

  public static void main(String[] args) {
        UncheckedCast uncheckedCast = new UncheckedCast();
        uncheckedCast.uncheckedCastDisplay();
  }
}