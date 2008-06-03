import java.lang.reflect.*;

public class ReflectProblem {
    public static void main(String [] args) throws Exception{
        Field f = C.class.getField("cf");
        System.out.println(f);
    }
}

interface I{}

class C implements I{
    public int cf;
}
