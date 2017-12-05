public class NestedClass {

    public static void main(String [] args){
        NestedClass nc = new NestedClass();
        nc.run();
    }
        
    public void run (){
    
        Link l = new Link();
        l.go();
        Linker lr = new Linker();
        lr.gofor();
    }

    class Link {
        
        public void go(){
            System.out.println("go");
        }
        
    }

    class Linker {
        public void gofor(){
            Link l = new Link();
            l.go();
        }
    }
}
