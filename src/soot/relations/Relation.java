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

public class Relation
{ 
    private Domain[] domains;
    private int bdd;
    public int bdd() { return bdd; }
    public Relation( Domain[] domains ) {
        this.domains = domains;
        bdd = JBuddy.bdd_false();
    }
    public void finalize() {
        JBuddy.bdd_delref( bdd );
    }
    public Domain[] domains() { return domains; }

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
        bdd = bdd2;
        return true;
    }

    public boolean addAll( Relation other ) {
        throw new RuntimeException( "NYI" );
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
            if( domains[i] != remove ) resdomains[j++] = domains[i];
        }
        Relation result = new Relation( resdomains );
        result.bdd = JBuddy.bdd_exist( bdd, JBuddy.fdd_ithset( remove.phys().var() ) );
        JBuddy.bdd_addref( result.bdd );
        return result;
    }

    public Relation projectDownTo( Domain remaining ) {
        Domain[] resdomains = new Domain[1];
        resdomains[0] = remaining;
        Relation result = new Relation( resdomains );
        result.bdd = bdd;
        JBuddy.bdd_addref( result.bdd );
        for( int i = 0; i < domains.length; i++ ) {
            if( domains[i] == remaining ) continue;
            int newBdd = JBuddy.bdd_exist( result.bdd, JBuddy.fdd_ithset(
                        domains[i].phys().var() ) );
            JBuddy.bdd_delref( result.bdd );
            JBuddy.bdd_addref( newBdd );
            result.bdd = newBdd;
        }
        return result;
    }

    public Relation equijoin( Domain col1, Relation other, Domain col2 ) {
        throw new RuntimeException( "NYI" );
    }

    public Relation rename( Domain oldd, Domain newd ) {
        throw new RuntimeException( "NYI" );
    }

    private void toString( String prefix, StringBuffer b ) {
        if( domains.length == 0 ) {
            b.append( prefix+"\n" );
            return;
        }
        int var = domains[0].phys().var();
        Relation firstDomain = projectDownTo( domains[0] );
        int size = (int) JBuddy.fdd_satcount( firstDomain.bdd, var );
        int[] sats = new int[size];
        JBuddy.fdd_allsat( firstDomain.bdd, var, sats );
        for( int i = 0; i < sats.length; i++ ) {
            restrict( domains[0], sats[i] )
                .project( domains[0] )
                    .toString( prefix + sats[i] + 
                            ((domains.length > 1) ? ", " : "]") , b );
        }
    }
    public String toString() {
        StringBuffer b = new StringBuffer();
        toString("[", b);
        return b.toString();
    }
}
