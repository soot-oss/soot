public class Test50{
    
    public static void main(String [] args){
        Test50 t50 = new Test50();
        t50.run();
    }

    public void run(){
        go(new Object(){
            public String toString(){
                return "Jennifer";
            }
        });
    }

    public void go(Object o){
        System.out.println(o);
    }
}
