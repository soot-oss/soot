public class ExtendProtectedSuper extends ProtectedSuper {

    public static void main(String [] args){
        ExtendProtectedSuper e = new ExtendProtectedSuper("Jennifer");
    }
    
    public ExtendProtectedSuper(String name){
        super(name);
    }
}
class ProtectedSuper {

    String name;
    protected ProtectedSuper(String name){
        this.name = name;
        System.out.println(this.name);
    }
}

