/*
 * The JastAdd Extensible Java Compiler (http://jastadd.org) is covered
 * by the modified BSD License. You should have received a copy of the
 * modified BSD license with this compiler.
 * 
 * Copyright (c) 2005-2008, Torbjorn Ekman
 * All rights reserved.
 */

package soot.JastAddJ;
import java.io.*;

public class Unicode extends FilterReader {
  public Unicode(Reader in) {
    super(in);
    // initialize read ahead character
    try {
      next();
    } catch (IOException e) {
    }
  }

  public Unicode(InputStream in) {
    this(new InputStreamReader(in));
  }
  
  // buffer reads from filtered stream
  private void refill() throws IOException {
    if(pos >= length) {
      pos = 0;
      int i = in.read(buffer, 0, SIZE);
      length = i != -1 ? i : 0;
    }
  }
  private static final int SIZE = 1024;
  private char[] buffer = new char[SIZE];
  private int pos = 0;
  private int length = 0;

  // interal read with support for lookahead 
  private int next() throws java.io.IOException {
    int c = lookahead;
    refill();
    lookahead = pos >= length ? -1 : buffer[pos++];
    return c;
  }
  private int lookahead = -1;
  
  // read character and translate unicode escapes
  public int read() throws java.io.IOException {
    int current = next();
    if(current != '\\') {
      numConsecutiveBackSlash = 0;
      return current;
    }
    boolean isEven = (numConsecutiveBackSlash & 0x01) == 0;
    if(!isEven || lookahead != 'u') {
      numConsecutiveBackSlash++;
      return current;
    }
    numConsecutiveBackSlash = 0;
    // UnicodeEscape found
    while(lookahead == 'u')
      next();
    // The next four characters must be hexadecimal digits or else a compile-time error is thrown
    int result = 0;
    for(int i = 0; i < 4; i++) {
      int c = next();
      int value = Character.digit((char)c, 16);
      if(value == -1)
        throw new Error("Invalid Unicode Escape");
      result <<= 4;
      result += value;
    }
    return result;
  }
  private int numConsecutiveBackSlash = 0;
  
  public int read(char cbuf[], int off, int len) throws IOException {
    if(!ready())
      return -1;
    len+= off;

    for (int i=off; i<len; i++) {
      // simplified common loop for non unicode escapes (read(), next(), refill(), cbuf[i])
      while(pos < length && i < len-1 && lookahead != '\\') {
        if(lookahead < 0) return i - off;
        cbuf[i++] = (char)lookahead;
        lookahead = buffer[pos++];
        numConsecutiveBackSlash = 0;
      }
      
      int c = read();
      if (c < 0)
        return i-off;
      else
        cbuf[i] = (char) c;
    }
    return len-off;
  }
  
  public boolean ready() throws IOException {
    return pos < length || super.ready();
  }
}
