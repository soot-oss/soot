package soot.util;

import java.io.*;
import java.util.*;



abstract public  class SootInputStream extends InputStream
{
    private InputStream mStream;

    public SootInputStream(InputStream istream)
    {
	mStream = istream;
    }

    // delegate all invocations
    public int available()throws IOException {return mStream.available();}

    public void close() throws IOException {mStream.close();}

    public void mark(int readlimit) {mStream.mark(readlimit);} 

    public boolean markSupported() {return mStream.markSupported();}

    public int read() throws IOException {return mStream.read();}
    
    public int read(byte[] b) throws IOException {return mStream.read(b);}

    public int read(byte[] b, int off, int len) throws IOException {return mStream.read(b, off, len);}

    public void reset() throws IOException {mStream.reset();}

    public long skip(long n) throws IOException {return mStream.skip(n);}
}


