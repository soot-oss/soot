public class InnerWhile {

    public static void main(String [] args){
        InnerWhile iw = new InnerWhile();
        iw.run();
    }

    public void run(){
    
        int i = 0;
        while (true){
            class MyClass {
                public void run(){
                    System.out.println("MyClass");
                }
            }

            MyClass mc = new MyClass();
            mc.run();
            if (i == 10) break;
            i++;
        }

    }
}
