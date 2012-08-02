package soot.dex;

import soot.options.Options;

public class Debug {
  public static boolean DEXPLER_DEBUG;
  
  public static void printDbg (String s) {
    DEXPLER_DEBUG = Options.v().verbose();
    if (DEXPLER_DEBUG)
      System.out.println (s);
  }
}
