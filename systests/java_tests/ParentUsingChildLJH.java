
/** @testcase PUREJAVA PR#728 interface using preceding subinterface in its definition (order matters) */
interface Child extends Parent {
    interface Toy { }
}

interface Parent { // order matters - must be after Child
    Child.Toy battle();
}

public class ParentUsingChildLJH {
    public static void main (String[] args) {
        if(!Parent.class.isAssignableFrom(Child.class))
          System.out.println("!Parent.class.isAssignableFrom(Child.class)");
        Parent p = new Parent() {
                public Child.Toy battle() {
                    return new Child.Toy(){};
                }
            };
        Child.Toy battle = p.battle();
        if (!(battle instanceof Child.Toy))
          System.out.println("!battle instanceof Child.Toy");
    } 
}
