package soot.dex;

public class Debug {
  public static boolean DEXPLER_DEBUG = true; //false;
  
  public static void printDbg (String s) {
    if (DEXPLER_DEBUG)
      System.out.println (s);
  }
}
