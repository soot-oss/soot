public class WeirdPrivateSets {

    private int x = 9;
    
    public WeirdPrivateSets foo(){
        System.out.println("foo called");
        //return this;
        return new WeirdPrivateSets();
    }

    public static void main(String [] args){
        WeirdPrivateSets wps = new WeirdPrivateSets();
        Inner i = wps.new Inner();
        System.out.println("x: "+wps.x);
        i.run();
        System.out.println("x: "+wps.x);
    }
    
    public class Inner {
        
        public void run(){
            foo().x += 2;
        }
    }
}
