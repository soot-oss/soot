/** @testcase PR#645 PUREJAVA Parent interface using public inner interface of child in same file */
interface Child extends Parent {
    public interface Inner { 
        public String ok();
    }
}

/** Parent must be in same file as child and be declared AFTER */
interface Parent { 
    public Child.Inner getChildInner(); 
}

public class ParentInterfaceUsingChildInnerInterfaceLJH {
    public static void main (String[] args) {
        Example me = new Example();   
        String result = me.getChildInner().ok(); 
        if(!((result != null) && result.startsWith("ok")))
          System.out.println("expected ok... got " + result); 
    } 
}

class Example implements Parent {
    public Child.Inner getChildInner() {
        return new Child.Inner() {
                public String ok() {
                    return "ok: " + getClass().getName();
                }
            };
    }
}
