/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.paddle.queue;
import soot.*;
import soot.util.*;
import java.util.*;

/** Singleton to keep track of all readers.
 * @author Ondrej Lhotak
 */

public class Readers {
    public Readers( Singletons.Global g ) {}
    public static Readers v() { return G.v().soot_jimple_paddle_queue_Readers(); }

    private List readers = new ArrayList();

    public interface Reader {
        public boolean hasNext();
    }
    public void add( Reader r ) {
        readers.add(r);
    }
    public void checkEmptiness() {
        for( Iterator rIt = readers.iterator(); rIt.hasNext(); ) {
            final Reader r = (Reader) rIt.next();
            if( r.hasNext() ) {
                G.v().out.println( "Reader "+r+" is not empty." );
            }
        }
    }
}

