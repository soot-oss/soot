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


