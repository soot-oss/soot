/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.relations;
import soot.*;
import soot.jbuddy.*;
import soot.util.*;
import java.util.*;
import java.io.*;

public class JBuddyProfiler
{ 
    public JBuddyProfiler( Singletons.Global g ) {}
    public static JBuddyProfiler v() { return G.v().JBuddyProfiler(); }

    Map stackTraceToTime = new HashMap();
    LinkedList stack = new LinkedList();

    public void start( String eventName, int bdd ) {
        start( eventName, bdd, JBuddy.bdd_false() );
    }
    public void start( String eventName, int bdd1, int bdd2 ) {
        Event e = new Event();
        e.bdd1 = bdd1;
        e.bdd2 = bdd2;
        e.startTime = new Date();
        stack.addLast( e );
    }
    public void finish( String eventName, int bdd ) {
        Event e = (Event) stack.removeLast();
        String stackTrace = stackTrace();
        long time = new Date().getTime() - e.startTime.getTime();
        Info info = (Info) stackTraceToTime.get( stackTrace );
        if( info == null ) {
            stackTraceToTime.put( stackTrace, info = new Info(stackTrace) );
        }
        info.eventName = eventName;
        info.time += time;
    }
    public void printInfo() {
        List infos = new ArrayList( stackTraceToTime.values() );
        Collections.sort( infos );
        for( Iterator infoIt = infos.iterator(); infoIt.hasNext(); ) {
            final Info info = (Info) infoIt.next();
            G.v().out.println( info.toString() );
        }
    }
    private String stackTrace() {
        Throwable t = new Throwable();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.printStackTrace( new PrintStream(baos) );
        return baos.toString();
    }

    class Event {
        int bdd1;
        int bdd2;
        Date startTime;
    }

    class Info implements Comparable {
        public Info( String stackTrace ) {
            this.stackTrace = stackTrace;
        }
        // Note: this class has a natural ordering that is inconsistent
        // with equals. In particular, two Info's can be different, yet
        // compareTo 0 if they have the same time.
        public int compareTo( Object o ) {
            Info other = (Info) o;
            if( time > other.time ) return 1;
            if( time < other.time ) return -11;
            return 0;
        }
        String stackTrace;
        long time;
        String eventName;
        public String toString() {
            return ""+time+" "+eventName;//+" "+stackTrace;
        }
    }
}
