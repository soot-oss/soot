/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.util;

import java.io.*;
import java.util.*;

/** Abstract class underlying all instances of InputStreams.
 * 
 * SootInputStreams are used to fetch data from some external
 * source, like class files or jimple files. */
abstract public class SootInputStream extends InputStream
{
    private InputStream mStream;

    /** Constructs a SootInputStream from the given InputStream. */
    public SootInputStream(InputStream istream)
    {
	mStream = istream;
    }

    // delegate all invocations.

    /** Returns the number of bytes that can be read (or skipped over)
        from this input stream without blocking by the next caller of
        a method for this input stream. 
    */
    public int available() throws IOException { return mStream.available();}

    /** Closes this input stream and releases any system resources
        associated with the stream. */
    public void close() throws IOException { mStream.close();}

    /** Marks the current position in this input stream. */
    public void mark(int readlimit) { mStream.mark(readlimit);} 

    /** Tests if this input stream supports the <code>mark</code> and <code>reset</code> methods. */
    public boolean markSupported() { return mStream.markSupported();}

    /** Reads the next byte of data from the input stream. */
    public int read() throws IOException { return mStream.read();}
    
    /** Reads some number of bytes from the input stream and stores them into the buffer array <code>b</code>. */
    public int read(byte[] b) throws IOException { return mStream.read(b);}

    /** Reads up to <code>len</code> bytes of data from the input stream into an array of bytes. */
    public int read(byte[] b, int off, int len) throws IOException { return mStream.read(b, off, len);}

    /** Repositions this stream to the position at the time the
        <code>mark</code> method was last called on this input
        stream. */
    public void reset() throws IOException { mStream.reset();}

    /** Skips over and discards n bytes of data from this input stream. */
    public long skip(long n) throws IOException { return mStream.skip(n);}
}


