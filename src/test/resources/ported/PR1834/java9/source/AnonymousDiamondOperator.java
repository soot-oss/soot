/** @author Kaustubh Kelkar */
abstract class MyClass<T>{
    abstract T add(T num, T num2);
}

public class AnonymousDiamondOperator {
   public int innerClassDiamond() {
       MyClass<Integer> obj = new MyClass<>() {
           Integer add(Integer x, Integer y) {
                 return x+y;
           }
       };
           Integer sum = obj.add(22,23);
            return sum;
   }

   public static void main(String args[]){
       AnonymousDiamondOperator obj= new AnonymousDiamondOperator();
	   System.out.println(obj.innerClassDiamond());
   
   }

}