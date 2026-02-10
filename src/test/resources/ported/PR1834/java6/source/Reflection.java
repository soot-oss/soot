import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** @author Kaustubh Kelkar */
public class Reflection{
    private String s;
    public Reflection(){
        s="String";
    }
    public void checkReflection()  throws  NoSuchMethodException{

        Reflection reflection = new Reflection();
        Class reflectionClass = Reflection.class;
        System.out.println(reflectionClass);
        Constructor constructor = reflectionClass.getConstructor();
        System.out.println(constructor.getName());
        System.out.println(reflectionClass.getMethods().length);
    }

  public static void main(String[] args) throws  NoSuchMethodException{
      Reflection reflection = new Reflection();
      reflection.checkReflection();
  }
}