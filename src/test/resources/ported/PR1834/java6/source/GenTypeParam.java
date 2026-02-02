import java.lang.reflect.Array;
import java.util.*;

/** @author Kaustubh Kelkar */

class GenTypeParam{

  public <T> void copy ( List<? super T> dest, List<? extends T> src) {
        for (int i=0; i<src.size(); i++)
        {dest.add(src.get(i));
        System.out.print(dest.get(i)+" ");}
    }

    // <T extends class, interface1, interface2, ....>
    public <T extends Number & Comparable<T>> T largestNum(T num1, T num2, T num3){
        T max =num1;
        if (num2.compareTo(max) > 0) max=num2;
            if(num3.compareTo(max) >0) max=num3;

        return max;
    }

   public void geneTypeParamDisplay(){
       List<Object> output = new ArrayList< Object >(3);
       List<Integer> input = Arrays.asList(1,2,3);
       GenTypeParam genTypeParam= new GenTypeParam();
       genTypeParam.copy(output,input);
       System.out.println(genTypeParam.largestNum(2,8,3));
   }

  public static void main(String[] args) {
    GenTypeParam param = new GenTypeParam();
    param.geneTypeParamDisplay();
    }
}
