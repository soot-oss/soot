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

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class BDDPAG extends AbstractPAG {
    public BDDPAG( final BDDSparkOptions opts ) {
        super( opts );
    }

    public PointsToSet reachingObjects( Local l ) {
        VarNode vn = findVarNode( l );
        if( vn == null ) return EmptyPointsToSet.v();
        return new BDDPointsToSet(
                pt.restrict( pt_var, vn ).projectDownTo( pt_obj ) );
    }
    public PointsToSet reachingObjects( SootField f ) {
        throw new RuntimeException( "NYI" );
    }
    public PointsToSet reachingObjects( PointsToSet ptset, SootField f ) {
        throw new RuntimeException( "NYI" );
    }

    public Iterator simpleSourcesIterator() {
        return es.projectDownTo( es_src ).iterator();
    }
    public Iterator allocSourcesIterator() {
        return alloc.projectDownTo( pt_obj ).iterator();
    }
    public Iterator storeSourcesIterator() {
        return st.projectDownTo( st_src ).iterator();
    }
    public Iterator loadSourcesIterator() {
        throw new RuntimeException( "NYI" );
    }
    public Iterator simpleInvSourcesIterator() {
        return es.projectDownTo( es_dst ).iterator();
    }
    public Iterator allocInvSourcesIterator() {
        return alloc.projectDownTo( pt_var ).iterator();
    }
    public Iterator storeInvSourcesIterator() {
        throw new RuntimeException( "NYI" );
    }
    public Iterator loadInvSourcesIterator() {
        return ld.projectDownTo( ld_dst ).iterator();
    }

    public boolean doAddSimpleEdge( VarNode from, VarNode to ) {
        return es.add( from, to );
    }

    public boolean doAddStoreEdge( VarNode from, FieldRefNode to ) {
        return st.add( from, to.getField(), to.getBase() );
    }

    public boolean doAddLoadEdge( FieldRefNode from, VarNode to ) {
        return ld.add( from.getBase(), from.getField(), to );
    }

    public boolean doAddAllocEdge( AllocNode from, VarNode to ) {
        return alloc.add( from, to );
    }


    private BDDSparkOptions opts;

    public PhysicalDomain v1 = new PhysicalDomain(20,"V1");
    public PhysicalDomain v2 = new PhysicalDomain(20,"V2");
    public PhysicalDomain fd = new PhysicalDomain(20,"FD");
    public PhysicalDomain h1 = new PhysicalDomain(20,"H1");
    public PhysicalDomain h2 = new PhysicalDomain(20,"H2");

    public Domain pt_var = new Domain( getVarNodeNumberer(), v1, "pt.var" );
    public Domain pt_obj = new Domain( getAllocNodeNumberer(), h1, "pt.obj" );
    public Relation alloc = new Relation( pt_obj, pt_var );
    public Relation pt = new Relation( pt_var, pt_obj );

    public Domain es_src = new Domain( getVarNodeNumberer(), v1, "es.src" );
    public Domain es_dst = new Domain( getVarNodeNumberer(), v2, "es.dst" );
    public Relation es = new Relation( es_src, es_dst );

    public Domain ld_src = new Domain( getVarNodeNumberer(), v1, "ld.src" );
    public Domain ld_fd = new Domain( Scene.v().getFieldNumberer(), fd, "ld.fd" );
    public Domain ld_dst = new Domain( getVarNodeNumberer(), v2, "ld.dst" );
    public Relation ld = new Relation( ld_src, ld_fd, ld_dst );

    public Domain st_src = new Domain( getVarNodeNumberer(), v1, "st.src" );
    public Domain st_fd = new Domain( Scene.v().getFieldNumberer(), fd, "st.fd" );
    public Domain st_dst = new Domain( getVarNodeNumberer(), v2, "st.dst" );
    public Relation st = new Relation( st_src, st_fd, st_dst );

    public Domain hpt_base = new Domain( getAllocNodeNumberer(), h1, "hpt.base" );
    public Domain hpt_fd = new Domain( getAllocNodeNumberer(), fd, "hpt.fd" );
    public Domain hpt_obj = new Domain( getAllocNodeNumberer(), h2, "hpt.obj" );
    public Relation hpt = new Relation( hpt_base, hpt_fd, hpt_obj );

}

