/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark.pag;
import java.util.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.solver.OnFlyCallGraph;
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.util.queue.*;
import soot.options.BDDSparkOptions;
import soot.tagkit.*;
import soot.relations.*;
import soot.jbuddy.JBuddy;

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class BDDPAG extends AbstractPAG {
    public BDDPAG( final BDDSparkOptions opts ) {
        super( opts );

        typeManager = new BDDTypeManager(this);
        if( !opts.ignore_types() ) {
            typeManager.setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
        }

        PhysicalDomain[] interleaved = { v1, v2, fd, h1, h2, t1, t2 };
        PhysicalDomain[] v1v2 = { v1, v2 };
        Object[] order = { fd, v1v2, h1, h2, t1, t2 };
        // Object[] order = { interleaved };
        //PhysicalDomain.setOrder( order, true );
    }

    public PointsToSet reachingObjects( Local l ) {
        VarNode vn = findVarNode( l );
        if( vn == null ) return EmptyPointsToSet.v();
        return new BDDPointsToSet(
                pointsTo.restrict( var, vn ).projectDownTo( obj ) );
    }
    public PointsToSet reachingObjects( SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( PointsToSet ptset, SootField f ) {
        throw new RuntimeException( "NYI" );
    }

    public Iterator simpleSourcesIterator() {
        return edgeSet.projectDownTo( src ).iterator();
    }
    public Iterator allocSourcesIterator() {
        return alloc.projectDownTo( obj ).iterator();
    }
    public Iterator storeSourcesIterator() {
        return stores.projectDownTo( src ).iterator();
    }
    public Iterator loadSourcesIterator() {
        throw new RuntimeException( "NYI" );
    }
    public Iterator simpleInvSourcesIterator() {
        return edgeSet.projectDownTo( dst ).iterator();
    }
    public Iterator allocInvSourcesIterator() {
        return alloc.projectDownTo( var ).iterator();
    }
    public Iterator storeInvSourcesIterator() {
        throw new RuntimeException( "NYI" );
    }
    public Iterator loadInvSourcesIterator() {
        return loads.projectDownTo( dst ).iterator();
    }

    public boolean doAddSimpleEdge( VarNode from, VarNode to ) {
        return edgeSet.add( src, from,
                            dst, to );
    }

    public boolean doAddStoreEdge( VarNode from, FieldRefNode to ) {
        return stores.add( src, from,
                           dst, to.getBase(),
                           fld, to.getField() );
    }

    public boolean doAddLoadEdge( FieldRefNode from, VarNode to ) {
        return loads.add( src, from.getBase(),
                       fld, from.getField(),
                       dst, to );
    }

    public boolean doAddAllocEdge( AllocNode from, VarNode to ) {
        return alloc.add( obj, from,
                          var, to );
    }


    private BDDSparkOptions opts;

    public PhysicalDomain v1 = new PhysicalDomain(20,"V1");
    public PhysicalDomain v2 = new PhysicalDomain(20,"V2");
    public PhysicalDomain fd = new PhysicalDomain(20,"FD");
    public PhysicalDomain h1 = new PhysicalDomain(20,"H1");
    public PhysicalDomain h2 = new PhysicalDomain(20,"H2");
    public PhysicalDomain t1 = new PhysicalDomain(20,"T1");
    public PhysicalDomain t2 = new PhysicalDomain(20,"T2");

    //public PhysicalDomain v1 = new PhysicalDomain(18,"V1");
    //public PhysicalDomain v2 = new PhysicalDomain(18,"V2");
    //public PhysicalDomain fd = new PhysicalDomain(13,"FD");
    //public PhysicalDomain h1 = new PhysicalDomain(14,"H1");
    //public PhysicalDomain h2 = new PhysicalDomain(14,"H2");
    //public PhysicalDomain t1 = new PhysicalDomain(12,"T1");
    //public PhysicalDomain t2 = new PhysicalDomain(12,"T2");

    public Domain var = new Domain( getVarNodeNumberer(), "var" );
    public Domain src = new Domain( getVarNodeNumberer(), "src" );
    public Domain dst = new Domain( getVarNodeNumberer(), "dst" );

    public Domain base = new Domain( getAllocNodeNumberer(), "base" );
    public Domain obj = new Domain( getAllocNodeNumberer(), "obj" );

    public Domain fld = new Domain( Scene.v().getFieldNumberer(), "fld" );

    // var := new obj()
    final public Relation alloc = new Relation( obj, var,
                                                h1,  v1 );
    // var points to object obj
    final public Relation pointsTo = new Relation( var, obj,
                                                   v1,  h1 );
    // dst := src
    final public Relation edgeSet = new Relation( src, dst,
                                                  v1,  v2 );
    // dst := src.fld
    final public Relation loads = new Relation( src, fld, dst,
                                                v1,  fd,  v2 );
    // dst.fld := src
    final public Relation stores = new Relation( src, dst, fld,
                                                 v1,  v2,  fd );
    // base.fld points to object obj
    final public Relation fieldPt = new Relation( base, fld, obj,
                                                  h1,   fd,  h2 );


    private void reportOrdering() {
      reportVarOrderingOfDomain("FD", fd);
      reportVarOrderingOfDomain("V1", v1);
      reportVarOrderingOfDomain("V2", v2);
      reportVarOrderingOfDomain("H1", h1);
      reportVarOrderingOfDomain("H2", h2); 
    }

    /* report variable orderings for single domain */
    private void reportVarOrderingOfDomain(String dname, PhysicalDomain var) {
      int vnum = JBuddy.fdd_varnum(var.var());
      int[] vars = new int[vnum];
      JBuddy.fdd_getvars(vars, var.var());
      for (int i=0; i<vnum; i++) {
        System.out.print(""+JBuddy.bdd_var2level(vars[i])+" ");
      }
      System.out.println("");
    }
}

