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

    private Domain[] domains;
    public Domain[] domains() { return domains; }
    private int bdd;
    public int bdd() { return bdd; }
    public Relation( Domain[] domains ) {
        init(domains);
    }
    private void init( Domain[] domains ) {
        this.domains = domains;
        bdd = JBuddy.bdd_false();
        if( CHECK ) {
            for( int i = 0; i < domains.length; i++ ) {
                for( int j = 0; j < domains.length; j++ ) {
                    if( j == i ) continue;
                    if( domains[j].equals( domains[i] ) ) {
                        throw new RuntimeException("Duplicate domains: "+Domain.toString(domains));
                    }
                }
            }
        }
    }
    public Relation( Domain d1 ) {
        Domain[] ds = { d1 };
        init(ds);
    }
    public Relation( Domain d1, Domain d2 ) {
        Domain[] ds = { d1, d2 };
        init(ds);
    }
    public Relation( Domain d1, Domain d2, Domain d3 ) {
        Domain[] ds = { d1, d2, d3 };
        init(ds);
    }
    public void finalize() {
        JBuddy.bdd_delref( bdd );
    }

    public boolean add( Numberable[] tuple ) {
        if( tuple.length != domains.length ) throw new RuntimeException();
        int newBdd = JBuddy.bdd_true();
        for( int i = 0; i < tuple.length; i++ ) {
            int num = tuple[i].getNumber();
            int fdd = domains[i].phys().ithvar( num );
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

    public boolean add( Numberable n1 ) {
        Numberable[] ns = { n1 };
        return add( ns );
    }

    public boolean add( Numberable n1, Numberable n2 ) {
        Numberable[] ns = { n1, n2 };
        return add( ns );
    }

    public boolean add( Numberable n1, Numberable n2, Numberable n3 ) {
        Numberable[] ns = { n1, n2, n3 };
        return add( ns );
    }

    private void checkEqual( Domain[] a, Domain[] b ) {
        if( CHECK ) {
            if( a.length != b.length ) {
                throw new RuntimeException( "Should be equal: "+Domain.toString( a )+" and "+Domain.toString( b ) );
            }
            outer: for( int i = 0; i < a.length; i++ ) {
                for( int j = 0; j < b.length; j++ ) {
                    if( a[i].equals( b[j] ) ) continue outer;
                }
                throw new RuntimeException( "Should be equal: "+Domain.toString( a )+" and "+Domain.toString( b ) );
            }
        }
    }
    public boolean unionEq( Relation other ) {
        checkEqual( domains, other.domains );
        int oldbdd = bdd;
        bdd = JBuddy.bdd_or( bdd, other.bdd );
        if( oldbdd == bdd ) return false;
        JBuddy.bdd_addref( bdd );
        JBuddy.bdd_delref( oldbdd );
        return true;
    }
    public boolean minusEq( Relation other ) {
        checkEqual( domains, other.domains );
        int oldbdd = bdd;
        bdd = JBuddy.bdd_apply(bdd, other.bdd, JBuddy.bddop_diff);
        if( oldbdd == bdd ) return false;
        JBuddy.bdd_addref( bdd );
        JBuddy.bdd_delref( oldbdd );
        return true;
    }
    public Relation union( Relation other ) {
        checkEqual( domains, other.domains );
        Domain[] newDomains = new Domain[domains.length];
        System.arraycopy( domains, 0, newDomains, 0, domains.length );
        Relation ret = new Relation( newDomains );
        ret.bdd = JBuddy.bdd_or( bdd, other.bdd );
        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }
    public Relation minus( Relation other ) {
        checkEqual( domains, other.domains );
        Domain[] newDomains = new Domain[domains.length];
        System.arraycopy( domains, 0, newDomains, 0, domains.length );
        Relation ret = new Relation( newDomains );
        ret.bdd = JBuddy.bdd_apply(bdd, other.bdd, JBuddy.bddop_diff);
        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }

    public Relation restrict( Domain d, Numberable value ) {
        return restrict( d, value.getNumber() );
    }
    private Relation restrict( Domain d, int value ) {
        Relation result = new Relation( domains );
        result.bdd = JBuddy.bdd_and( bdd, d.phys().ithvar( value ) );
        JBuddy.bdd_addref( result.bdd );
        return result;
    }

    public Relation project( Domain remove ) {
        Domain[] resdomains = new Domain[domains.length-1];
        int j = 0;
        for( int i = 0; i < domains.length; i++ ) {
            if( !domains[i].equals(remove) ) resdomains[j++] = domains[i];
        }
        Relation result = new Relation( resdomains );
        result.bdd = JBuddy.bdd_exist( bdd, JBuddy.fdd_ithset( remove.phys().var() ) );
        JBuddy.bdd_addref( result.bdd );
        return result;
    }

    public Relation projectDownTo( Domain remaining ) {
        Domain[] resdomains = { remaining };
        Relation result = new Relation( resdomains );
        result.bdd = bdd;
        JBuddy.bdd_addref( result.bdd );
        for( int i = 0; i < domains.length; i++ ) {
            if( domains[i].equals( remaining ) ) continue;
            int newBdd = JBuddy.bdd_exist( result.bdd, JBuddy.fdd_ithset(
                        domains[i].phys().var() ) );
            JBuddy.bdd_delref( result.bdd );
            JBuddy.bdd_addref( newBdd );
            result.bdd = newBdd;
        }
        return result;
    }

    public static int relprod( int a, int b, int var ) {
        return JBuddy.bdd_appex(a, b, JBuddy.bddop_and, var);
    }

    public Relation relprod( Domain col11, Domain col12, Relation other, Domain col21, Domain col22 ) {
        Relation o = other.replace( Domain.box( col11, col12), Domain.box( col21, col22 ) );

        Domain[] newDomains =
            new Domain[domains.length + other.domains.length - 4];

        int i=0,j;
        for( j = 0; j < domains.length; j++ ) {
            if( domains[j].equals( col11 ) ) continue;
            if( domains[j].equals( col12 ) ) continue;
            newDomains[i++] = domains[j];
        }
        Domain[] otherDomains = other.domains;
        for( j = 0; j < otherDomains.length; j++ ) {
            if( otherDomains[j].equals( col21 ) ) continue;
            if( otherDomains[j].equals( col22 ) ) continue;
            newDomains[i++] = otherDomains[j];
        }

        Relation ret = new Relation( newDomains );
        
        ret.bdd = relprod( bdd, o.bdd, JBuddy.bdd_and( JBuddy.fdd_ithset( col11.phys().var() ), JBuddy.fdd_ithset( col12.phys().var() ) ) );
        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }
    public Relation relprod( Domain col1, Relation other, Domain col2 ) {
        Relation o = other.replace( col2, col1 );

        Domain[] newDomains =
            new Domain[domains.length + other.domains.length - 2];

        int i=0,j;
        for( j = 0; j < domains.length; j++ ) {
            if( domains[j].equals( col1 ) ) continue;
            newDomains[i++] = domains[j];
        }
        Domain[] otherDomains = other.domains;
        for( j = 0; j < otherDomains.length; j++ ) {
            if( otherDomains[j].equals( col2 ) ) continue;
            newDomains[i++] = otherDomains[j];
        }

        Relation ret = new Relation( newDomains );
        
        ret.bdd = relprod( bdd, o.bdd, JBuddy.fdd_ithset( col1.phys().var() ) );
        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }

    public Relation replace( Domain oldd, Domain newd ) {
        Domain[] od = { oldd };
        Domain[] nd = { newd };
        return replace( od, nd );
    }
    
    public Relation replace( Domain[] oldd, Domain[] newd ) {
        Domain[] newDomains = new Domain[domains.length];
i:      for( int i = 0; i < domains.length; i++ ) {
            for( int j = 0; j < oldd.length; j++ ) {
                if( domains[i].equals( oldd[j] ) ) {
                    newDomains[i] = newd[j];
                    continue i;
                }
            }
            newDomains[i] = domains[i];
        }

        Relation ret = new Relation(newDomains);

        bddPair pair = JBuddy.bdd_newpair();
        for( int j = 0; j < oldd.length; j++ ) {
            JBuddy.fdd_setpair( pair, oldd[j].phys().var(), newd[j].phys().var() );
        }
        ret.bdd = JBuddy.bdd_replace( bdd, pair );

        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }

    public int size() {
        if( CHECK ) {
            if( domains.length != 1 ) throw new RuntimeException( "Can only get size over single-column relation" );
        }
        return JBuddy.fdd_satcount( bdd, domains[0].phys().var() );
    }

    private void toString( String prefix, StringBuffer b ) {
        if( domains.length == 0 ) {
            b.append( prefix+"\n" );
            return;
        }
        int var = domains[0].phys().var();
        Relation firstDomain = projectDownTo( domains[0] );
        int size = JBuddy.fdd_satcount( firstDomain.bdd, var );
        int[] sats = new int[size];
        int confirmsize = JBuddy.fdd_allsat( firstDomain.bdd, var, sats );
        if( size != confirmsize ) {
            System.out.println( "size: "+size+" confirmsize: "+confirmsize );
            JBuddy.bdd_printset( firstDomain.bdd );
            throw new RuntimeException();
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
        Domain[] newDomains = new Domain[domains.length];
        System.arraycopy( domains, 0, newDomains, 0, domains.length );
        Relation ret = new Relation( newDomains );
        ret.bdd = bdd;
        JBuddy.bdd_addref( bdd );
        return ret;
    }
    public boolean isEmpty() { 
        return bdd == JBuddy.bdd_false();
    }
    public Relation intersect( Relation other ) {
        checkEqual( domains, other.domains );
        Domain[] newDomains = new Domain[domains.length];
        System.arraycopy( domains, 0, newDomains, 0, domains.length );
        Relation ret = new Relation( newDomains );
        ret.bdd = JBuddy.bdd_and( bdd, other.bdd );
        JBuddy.bdd_addref( ret.bdd );
        return ret;
    }
    public Iterator iterator() {
        if( CHECK ) {
            if( domains.length != 1 ) throw new RuntimeException( "Can only get iterator over single-column relation" );
        }
        int size = JBuddy.fdd_satcount( bdd, domains[0].phys().var() );
        int[] sats = new int[size];
        int confirmsize = JBuddy.fdd_allsat( bdd, domains[0].phys().var(), sats );
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
}
