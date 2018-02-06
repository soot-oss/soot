public class test
{
     public void A() {
         if (null instanceof String)
             System.out.println("is String");
         else
             System.out.println("is not String");
     }
     public void B() {
         int[] array = null;
         System.out.println("array = " + array[0]);
     }
     public static void main(String args[]) {
         int[] array = null;
         System.out.println("array = " + array.length);
     }
}

