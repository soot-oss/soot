
public class Engine implements Runnable {

  public Engine(){
    Thread thread = new Thread(this);
    thread.start();
  }

  public void run(){
    System.out.println("hello world");
  }
}
