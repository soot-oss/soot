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
import soot.jbuddy.*;
import soot.util.*;
import java.util.*;

public class Relation
{ 
    public static final boolean CHECK = true;
    public static final boolean PROFILING = true;

    private Domain[] domains;
    private PhysicalDomain[] phys;
    private int bdd;
    public Domain[] domains() { return domains; }
    public PhysicalDomain[] phys() { return phys; }
    public int bdd() { return bdd; }

    public Relation( Domain[] domains, PhysicalDomain[] phys ) {
        init(domains, phys);
    }
    private void init( Domain[] domains, PhysicalDomain[] phys ) {
        this.domains = domains;
        this.phys = phys;
        bdd = JBuddy.bdd_false();
        if( CHECK ) {
            checkForDupes( domains );
            checkForDupes( phys );
            if( phys.length != domains.length ) throw new RuntimeException();
        }
    }

    private void checkForDupes( Object[] ar ) {
        for( int i = 0; i < ar.length; i++ ) {
            if( ar[i] == null ) throw new RuntimeException();
            for( int j = i+1; j < ar.length; j++ ) {
                if( ar[j].equals( ar[i] ) ) {
                    throw new RuntimeException("Duplicates: "+Domain.toString(ar));
                }
            }
        }
    }
    public Relation( Domain d1, PhysicalDomain p1 ) {
        Domain[] ds = { d1 };
        PhysicalDomain[] ps = { p1 };
        init(ds,ps);
    }
    public Relation( Domain d1, Domain d2, PhysicalDomain p1, PhysicalDomain p2 ) {
        Domain[] ds = { d1, d2 };
        PhysicalDomain[] ps = { p1, p2 };
        init(ds,ps);
    }
    public Relation( Domain d1, Domain d2, Domain d3, PhysicalDomain p1, PhysicalDomain p2, PhysicalDomain p3 ) {
        Domain[] ds = { d1, d2, d3 };
        PhysicalDomain[] ps = { p1, p2, p3 };
        init(ds,ps);
    }
    public void finalize() {
        JBuddy.bdd_delref( bdd );
    }

    public boolean add( Numberable[] tuple ) {
        if( tuple.length != domains.length ) throw new RuntimeException();
        int newBdd = JBuddy.bdd_true();
        for( int i = 0; i < tuple.length; i++ ) {
            int num = tuple[i].getNumber();
            int fdd = phys[i].ithvar( num );
            int newNewBdd = JBuddy.bdd_and( newBdd, fdd );
            JBuddy.bdd_addref( newNewBdd );
            JBuddy.bdd_delref( newBdd );
            newBdd = newNewBdd;
        }
        int bdd2 = JBuddy.bdd_or( bdd, newBdd );
        JBuddy.bdd_addref( bdd2 );
        JBuddy.bdd_delref( newBdd );

        if( bdd == bdd2 ) return false;
        JBuddy.bdd_delref( bdd );
        bdd = bdd2;
        return true;
    }

    public boolean add( Domain d1, Numberable n1 ) {
        Numberable[] ns = { n1 };
        if( !d1.equals( domains[0] ) ) throw new RuntimeException( "Trying to add with unknown domain "+d1 );
        return add( ns );
    }

    public boolean add( Domain d1, Numberable n1, Domain d2, Numberable n2 ) {
        Numberable[] ns;
        if( d1.equals( domains[0] ) && d2.equals( domains[1] ) ) {
            Numberable[] _ = {n1,n2}; ns = _;
        } else if( d1.equals( domains[1] ) && d2.equals( domains[0] ) ) {
            Numberable[] _ = {n2,n1}; ns = _;
        } else throw new RuntimeException( "Trying to add with domains "+d1+" and "+d2+" to relation with domains "+Domain.toString(domains) );
        return add( ns );
    }

    public boolean add( Domain d1, Numberable n1, Domain d2, Numberable n2, Domain d3, Numberable n3 ) {
        Numberable[] ns;
        if( d1.equals( domains[0] ) && d2.equals( domains[1] ) && d3.equals( domains[2] ) ) {
            Numberable[] _ = {n1,n2,n3}; ns = _;
        } else if( d1.equals( domains[0] ) && d3.equals( domains[1] ) && d2.equals( domains[2] ) ) {
            Numberable[] _ = {n1,n3,n2}; ns = _;
        } else if( d3.equals( domains[0] ) && d1.equals( domains[1] ) && d2.equals( domains[2] ) ) {
            Numberable[] _ = {n3,n1,n2}; ns = _;
        } else if( d3.equals( domains[0] ) && d2.equals( domains[1] ) && d1.equals( domains[2] ) ) {
            Numberable[] _ = {n3,n2,n1}; ns = _;
        } else if( d2.equals( domains[0] ) && d3.equals( domains[1] ) && d1.equals( domains[2] ) ) {
            Numberable[] _ = {n2,n3,n1}; ns = _;
        } else if( d1.equals( domains[0] ) && d3.equals( domains[1] ) && d2.equals( domains[2] ) ) {
            Numberable[] _ = {n1,n3,n2}; ns = _;
        } else throw new RuntimeException( "Trying to add with domains "+d1+", "+d2+" and "+d3+" to relation with domains "+Domain.toString(domains) );
        return add( ns );
    }

    private int doReplaces( Relation r ) {
        int[] newd = new int[r.domains.length];
        int[] oldd = new int[r.domains.length];
        for( int i = 0; i < r.domains.length; i++ ) {
            newd[i] = phys[find(r.domains[i])].var();
            oldd[i] = r.phys[i].var();
        }
        int ret = replace( r.bdd, oldd, newd );
        return ret;
    }
    public void eq( Relation r ) {
        JBuddy.bdd_delref( bdd );
        bdd = doReplaces( r );
        JBuddy.bdd_addref( bdd );
    }
    private void eqOp( Relation r1, Relation r2, int op ) {
        JBuddy.bdd_delref( bdd );

        int bdd1 = doReplaces( r1 );
        JBuddy.bdd_addref( bdd1 );
        int bdd2 = doReplaces( r2 );

        bdd = JBuddy.bdd_apply(bdd1, bdd2, op);

        JBuddy.bdd_addref( bdd );
        JBuddy.bdd_delref( bdd1 );
    }
    public void eqUnion( Relation r1, Relation r2 ) {
        if( PROFILING ) JBuddyProfiler.v().start( "union", r1.bdd, r2.bdd );
        eqOp( r1, r2, JBuddy.bddop_or );
        if( PROFILING ) JBuddyProfiler.v().finish( "union", this.bdd );
    }
    public void eqMinus( Relation r1, Relation r2 ) {
        if( PROFILING ) JBuddyProfiler.v().start( "minus", r1.bdd, r2.bdd );
        eqOp( r1, r2, JBuddy.bddop_diff );
        if( PROFILING ) JBuddyProfiler.v().finish( "minus", this.bdd );
    }
    public void eqIntersect( Relation r1, Relation r2 ) {
        if( PROFILING ) JBuddyProfiler.v().start( "intersect", r1.bdd, r2.bdd );
        eqOp( r1, r2, JBuddy.bddop_and );
        if( PROFILING ) JBuddyProfiler.v().finish( "intersect", this.bdd );
    }

    public Relation restrict( Domain d, Numberable value ) {
        return restrict( d, value.getNumber() );
    }
    private int find( Domain d ) {
        for( int i = 0; i < domains.length; i++ ) {
            if( d.equals( domains[i] ) ) return i;
        }
        throw new RuntimeException( "bad domain "+d );
    }
    private Relation restrict( Domain d, int value ) {
        Relation result = new Relation( domains, phys );
        result.bdd = JBuddy.bdd_and( bdd, phys[find(d)].ithvar( value ) );
        JBuddy.bdd_addref( result.bdd );
        return result;
    }

    public Relation project( Domain remove ) {
        Domain[] resdomains = new Domain[domains.length-1];
        PhysicalDomain[] resphys = new PhysicalDomain[domains.length-1];
        int j = 0;
        for( int i = 0; i < domains.length; i++ ) {
            if( domains[i].equals(remove) ) continue;
            resdomains[j] = domains[i];
            resphys[j] = phys[i];
            j++;
        }
        Relation result = new Relation( resdomains, resphys );
        result.bdd = JBuddy.bdd_exist( bdd, JBuddy.fdd_ithset( phys[find(remove)].var() ) );
        JBuddy.bdd_addref( result.bdd );
        return result;
    }

    public Relation projectDownTo( Domain remaining ) {
        Domain[] resdomains = { remaining };
        PhysicalDomain[] resphys = { phys[find(remaining)] };
        Relation result = new Relation( resdomains, resphys );
        result.bdd = bdd;
        JBuddy.bdd_addref( result.bdd );
        for( int i = 0; i < domains.length; i++ ) {
            if( domains[i].equals( remaining ) ) continue;
            int newBdd = JBuddy.bdd_exist( result.bdd,
                    JBuddy.fdd_ithset( phys[i].var() ) );
            JBuddy.bdd_delref( result.bdd );
            JBuddy.bdd_addref( newBdd );
            result.bdd = newBdd;
        }
        return result;
    }

    public static int relprod( int a, int b, int var ) {
        return JBuddy.bdd_appex(a, b, JBuddy.bddop_and, var);
    }

    private static int replace( int bdd, int[] oldd, int[] newd ) {
        boolean didSomething = false;

        bddPair pair = JBuddy.bdd_newpair();
        for( int i = 0; i < oldd.length; i++ ) {
            if( oldd[i] != newd[i] ) {
                JBuddy.fdd_setpair( pair, oldd[i], newd[i] );
                didSomething = true;
            }
        }

        if( !didSomething ) return bdd;

        if( PROFILING ) JBuddyProfiler.v().start( "replace", bdd );
        bdd = JBuddy.bdd_replace( bdd, pair );
        if( PROFILING ) JBuddyProfiler.v().finish( "replace", bdd );
        return bdd;
    }

    private static boolean in( Object o, Object[] ar ) {
        for( int i = 0; i < ar.length; i++ )
            if( ar[i].equals(o) ) return true;
        return false;
    }

    public void eqRelprod( Relation r1, Domain[] eq1, Relation r2, Domain[] eq2,
                               Domain[] result, Relation[] base, Domain[] dom ) {
        if( CHECK ) {
            if( eq1.length != eq2.length ) throw new RuntimeException();
            if( result.length != base.length ) throw new RuntimeException();
            if( result.length != dom.length ) throw new RuntimeException();
            checkForDupes( result );
            checkForDupes( eq1 );
            checkForDupes( eq2 );
            for( int i = 0; i < r2.domains.length; i++ ) {
                if( in( r2.domains[i], eq2 ) ) continue;
                if( in( r2.phys[i], r1.phys ) )
                    throw new RuntimeException( "Attempt to do relprod on "+
                            Domain.toString( r1.phys )+" and "+Domain.toString( r2.phys )+"; "+r2.phys[i]+" conflicts" );
            }
        }

        JBuddy.bdd_delref( bdd );

        int bdd1 = r1.bdd;
        int bdd2 = r2.bdd;
        int oldbdd;
        int vars = JBuddy.bdd_true();
        int[] newd = new int[eq1.length];
        int[] oldd = new int[eq1.length];

        for( int i = 0; i < eq1.length; i++ ) {
            newd[i] = r1.phys[r1.find(eq1[i])].var();
            oldd[i] = r2.phys[r2.find(eq2[i])].var();
            vars = JBuddy.bdd_and( vars, JBuddy.fdd_ithset( newd[i] ) );
        }

        bdd2 = replace( bdd2, oldd, newd );
        JBuddy.bdd_addref( bdd2 );


        if( PROFILING ) JBuddyProfiler.v().start( "relprod", bdd1, bdd2 );
        oldbdd = bdd2;
        bdd = relprod( bdd1, bdd2, vars );
        JBuddy.bdd_addref( bdd );
        JBuddy.bdd_delref( oldbdd );
        if( PROFILING ) JBuddyProfiler.v().finish( "relprod", bdd );


        newd = new int[result.length];
        oldd = new int[result.length];
        for( int i = 0; i < result.length; i++ ) {
            newd[i] = phys[find(result[i])].var();
            oldd[i] = base[i].phys[base[i].find(dom[i])].var();
        }
        oldbdd = bdd;
        bdd = replace( bdd, oldd, newd );
        JBuddy.bdd_addref( bdd );
        JBuddy.bdd_delref( oldbdd );
    }
     
    public void eqRelprod( Relation r1, Domain eq1, Relation r2, Domain eq2,
                           Domain res1, Relation base1, Domain dom1,
                           Domain res2, Relation base2, Domain dom2 ) {
        Domain[] eqs1 = { eq1 }; 
        Domain[] eqs2 = { eq2 }; 
        Domain[] res = { res1, res2 };
        Relation[] bases = { base1, base2 };
        Domain[] doms = { dom1, dom2 };
        eqRelprod( r1, eqs1, r2, eqs2, res, bases, doms );
    }
     
    public void eqRelprod( Relation r1, Domain eq11, Domain eq12, Relation r2, Domain eq21, Domain eq22,
                           Domain res1, Relation base1, Domain dom1,
                           Domain res2, Relation base2, Domain dom2 ) {
        Domain[] eqs1 = { eq11, eq12 }; 
        Domain[] eqs2 = { eq21, eq22 }; 
        Domain[] res = { res1, res2 };
        Relation[] bases = { base1, base2 };
        Domain[] doms = { dom1, dom2 };
        eqRelprod( r1, eqs1, r2, eqs2, res, bases, doms );
    }
    public void eqRelprod( Relation r1, Domain eq1, Relation r2, Domain eq2,
                           Domain res1, Relation base1, Domain dom1,
                           Domain res2, Relation base2, Domain dom2,
                           Domain res3, Relation base3, Domain dom3 ) {
        Domain[] eqs1 = { eq1 }; 
        Domain[] eqs2 = { eq2 }; 
        Domain[] res = { res1, res2, res3 };
        Relation[] bases = { base1, base2, base3 };
        Domain[] doms = { dom1, dom2, dom3 };
        eqRelprod( r1, eqs1, r2, eqs2, res, bases, doms );
    }

    public long size() {
        if( domains.length == 0 ) {
            return 1L;
        }
        int var = phys[0].var();
        Relation firstDomain = projectDownTo( domains[0] );
        int size = JBuddy.fdd_satcount( firstDomain.bdd, var );
        int[] sats = new int[size];
        int confirmsize = JBuddy.fdd_allsat( firstDomain.bdd, var, sats );
        if( size != confirmsize ) {
            String sizes = "size: "+size+" confirmsize: "+confirmsize;
            JBuddy.bdd_printset( firstDomain.bdd );
            throw new RuntimeException(sizes);
        }
        long ret = 0;
        for( int i = 0; i < sats.length; i++ ) {
            ret += restrict( domains[0], sats[i] )
                .project( domains[0] )
                    .size();
        }
        return ret;
    }
    private void toString( String prefix, StringBuffer b ) {
        if( domains.length == 0 ) {
            b.append( prefix+"\n" );
            return;
        }
        int var = phys[0].var();
        Relation firstDomain = projectDownTo( domains[0] );
        int size = JBuddy.fdd_satcount( firstDomain.bdd, var );
        int[] sats = new int[size];
        int confirmsize = JBuddy.fdd_allsat( firstDomain.bdd, var, sats );
        if( size != confirmsize ) {
            String sizes = "size: "+size+" confirmsize: "+confirmsize;
            JBuddy.bdd_printset( firstDomain.bdd );
            throw new RuntimeException(sizes);
        }
        for( int i = 0; i < sats.length; i++ ) {
            restrict( domains[0], sats[i] )
                .project( domains[0] )
                    .toString( prefix + domains[0].numberer().get(sats[i]) + 
                            ((domains.length > 1) ? ", " : "]") , b );
        }
    }
    public String toString() {
        StringBuffer b = new StringBuffer();
        toString("[", b);
        return b.toString();
    }
    public Relation cloneRelation() {
        Relation ret = sameDomains();
        ret.bdd = bdd;
        JBuddy.bdd_addref( bdd );
        return ret;
    }
    public Relation sameDomains() {
        Domain[] newDomains = new Domain[domains.length];
        System.arraycopy( domains, 0, newDomains, 0, domains.length );
        PhysicalDomain[] newPhys = new PhysicalDomain[domains.length];
        System.arraycopy( phys, 0, newPhys, 0, domains.length );
        return new Relation( newDomains, newPhys );
    }
    public boolean isEmpty() { 
        return bdd == JBuddy.bdd_false();
    }
    public Iterator iterator() {
        if( CHECK ) {
            if( domains.length != 1 ) throw new RuntimeException( "Can only get iterator over single-column relation" );
        }
        int size = JBuddy.fdd_satcount( bdd, phys[0].var() );
        int[] sats = new int[size];
        int confirmsize = JBuddy.fdd_allsat( bdd, phys[0].var(), sats );
        return new RelationIterator( sats, domains[0].numberer() );
    }
    class RelationIterator implements Iterator {
        int[] sats;
        Numberer numberer;
        int cur = 0;
        RelationIterator( int[] sats, Numberer numberer ) {
            this.sats = sats;
            this.numberer = numberer;
        }
        public final boolean hasNext() { return cur < sats.length; }
        public void remove() {
            throw new RuntimeException( "Not implemented." );
        }
        public final Object next() {
            return numberer.get(sats[cur++]);
        }
    }
    public void makeEmpty() {
        JBuddy.bdd_delref( bdd );
        bdd = JBuddy.bdd_false();
    }
    public void makeFull() {
        JBuddy.bdd_delref( bdd );
        bdd = JBuddy.bdd_true();
    }
    public boolean isFull() { 
        return bdd == JBuddy.bdd_true();
    }
}
