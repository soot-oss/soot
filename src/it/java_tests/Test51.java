public class Test51{
    
    public static void main(String [] args){
        Test51 t51 = new Test51();
    }

    public Test51(){
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
