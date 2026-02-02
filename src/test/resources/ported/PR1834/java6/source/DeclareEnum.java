
/** @author Hasitha Rajapakse */


public class DeclareEnum {
    public enum Type{
        TYPE1,
        TYPE2,
        TYPE3
    }
    public void declareEnum(){
        for(Type type:Type.values()){
            System.out.println(type);
        }
    }
}
