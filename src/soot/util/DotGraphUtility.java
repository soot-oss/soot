
package soot.util;

import java.util.*;
import java.io.*;

public class DotGraphUtility {

  /* repalce any " to \" in the string */
  public static String replaceQuotes(String original){
    byte[] ord = original.getBytes();
    int quotes = 0;
    for (int i=0, n=ord.length; i<n; i++) {
      if (ord[i] == '\"') quotes++;
    }

    if (quotes == 0) return original;

    byte[] newsrc = new byte[ord.length+quotes];
    for (int i=0, j=0, n=ord.length; i<n; i++, j++){
      if (ord[i] == '\"') {
	newsrc[j++] = '\\';
      }
      newsrc[j] = ord[i];
    }

    /*
    System.out.println("before "+original);
    System.out.println("after  "+(new String(newsrc)));
    */

    return new String(newsrc);
  }

  /* replace any return by to "\n" */
  public static String replaceReturns(String original){
    byte[] ord = original.getBytes();
    int quotes = 0;
    for (int i=0, n=ord.length; i<n; i++) {
      if (ord[i] == '\n') quotes++;
    }

    if (quotes == 0) return original;

    byte[] newsrc = new byte[ord.length+quotes];
    for (int i=0, j=0, n=ord.length; i<n; i++, j++){
      if (ord[i] == '\n') {
	newsrc[j++] = '\\';
	newsrc[j] = 'n';
      } else {
	newsrc[j] = ord[i];
      }
    }

    /*
    System.out.println("before "+original);
    System.out.println("after  "+(new String(newsrc)));
    */

    return new String(newsrc);    
  }

  public static void renderLine(OutputStream out,
		      String content, 
		      int indent) throws IOException {
    for (int i=0; i<indent; i++) {
      out.write(' ');
    }
    
    content = content + "\n";

    out.write(content.getBytes());
  }
}
