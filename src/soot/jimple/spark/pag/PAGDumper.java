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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.spark.solver.TopoSorter;

/** Dumps a pointer assignment graph to a file.
 * @author Ondrej Lhotak
 */
public class PAGDumper {
    public PAGDumper( PAG pag , String output_dir ) {
        this.pag = pag;
        this.output_dir = output_dir;
    }
    public void dumpPointsToSets() {
        try {
            final PrintWriter file = new PrintWriter(
                    new FileOutputStream( new File(output_dir, "solution") ) );
            file.println( "Solution:" );
            for( Iterator vnIt = pag.getVarNodeNumberer().iterator(); vnIt.hasNext(); ) {
                final VarNode vn = (VarNode) vnIt.next();
                if( vn.getReplacement() != vn ) {
                    continue;
                }
                PointsToSetInternal p2set = vn.getP2Set();
                if( p2set == null ) continue;
                p2set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        try {
                            dumpNode( vn, file );
                            file.print( " " );
                            dumpNode( n, file );
                            file.println( "" );
                        } catch( IOException e ) {
                            throw new RuntimeException( "Couldn't dump solution."+e );
                        }
                    }
                } );
            }
            file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
    }
    public void dump() {
        try {
            PrintWriter file = new PrintWriter(
                new FileOutputStream( new File(output_dir, "pag") ) );

            if( pag.getOpts().topo_sort() ) {
                new TopoSorter( pag, false ).sort();
            }
            file.println( "Allocations:" );
            for (Object object : pag.allocSources()) {
                final AllocNode n = (AllocNode) object;
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.allocLookup( n );
                for (Node element0 : succs) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( element0, file );
                    file.println( "");
                }
            }

            file.println( "Assignments:" );
            for (Object object : pag.simpleSources()) {
                final VarNode n = (VarNode) object;
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.simpleLookup( n );
                for (Node element0 : succs) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( element0, file );
                    file.println( "");
                }
            }
            
            file.println( "Loads:" );
            for (Object object : pag.loadSources()) {
                final FieldRefNode n = (FieldRefNode) object;
                Node[] succs = pag.loadLookup( n );
                for (Node element0 : succs) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( element0, file );
                    file.println( "");
                }
            }
            file.println( "Stores:" );
            for (Object object : pag.storeSources()) {
                final VarNode n = (VarNode) object;
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.storeLookup( n );
                for (Node element0 : succs) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( element0, file );
                    file.println( "");
                }
            }
            if( pag.getOpts().dump_types() ) {
                dumpTypes( file );
            }
            file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump PAG."+e );
        }
    }


    /* End of public methods. */
    /* End of package methods. */

    protected PAG pag;
    protected String output_dir;
    protected int fieldNum = 0;
    protected HashMap<SparkField, Integer> fieldMap = new HashMap<SparkField, Integer>();
    protected ObjectNumberer root = new ObjectNumberer( null, 0 );

    protected void dumpTypes( PrintWriter file ) throws IOException {
        HashSet<Type> declaredTypes = new HashSet<Type>();
        HashSet<Type> actualTypes = new HashSet<Type>();
        HashSet<SparkField> allFields = new HashSet<SparkField>();
        for( Iterator nIt = pag.getVarNodeNumberer().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
        }
        for (Object object : pag.loadSources()) {
            final Node n = (Node) object;
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
            allFields.add( ((FieldRefNode) n ).getField() );
        }
        for (Object object : pag.storeInvSources()) {
            final Node n = (Node) object;
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
            allFields.add( ((FieldRefNode) n ).getField() );
        }
        for (Object object : pag.allocSources()) {
            final Node n = (Node) object;
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) actualTypes.add( t );
        }
        HashMap<Type, Integer> typeToInt = new HashMap<Type, Integer>();
        int nextint = 1;
        for (Type type : declaredTypes) {
            typeToInt.put( type, new Integer( nextint++ ) );
        }
        for (Type t : actualTypes) {
            if( !typeToInt.containsKey( t ) ) {
                typeToInt.put( t, new Integer( nextint++ ) );
            }
        }
        file.println( "Declared Types:" );
        for (Type declType : declaredTypes) {
            for (Type actType : actualTypes) {
                if( pag.getTypeManager().castNeverFails( actType, declType ) ) {
                    file.println( ""+typeToInt.get( declType )+" "+typeToInt.get( actType ) );
                }
            }
        }
        file.println( "Allocation Types:" );
        for (Object object : pag.allocSources()) {
            final Node n = (Node) object;
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            dumpNode( n, file );
            if( t == null ) {
                throw new RuntimeException( "allocnode with null type" );
                //file.println( " 0" );
            } else {
                file.println( " "+typeToInt.get( t ) );
            }
        }
        file.println( "Variable Types:" );
        for( Iterator nIt = pag.getVarNodeNumberer().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            dumpNode( n, file );
            if( t == null ) {
                file.println( " 0" );
            } else {
                file.println( " "+typeToInt.get( t ) );
            }
        }
    }
    protected int fieldToNum( SparkField f ) {
        Integer ret = fieldMap.get( f );
        if( ret == null ) {
            ret = new Integer( ++ fieldNum );
            fieldMap.put( f, ret );
        }
        return ret.intValue();
    }
    protected void dumpNode( Node n, PrintWriter out ) throws IOException {
        if( n.getReplacement() != n ) throw new RuntimeException( "Attempt to dump collapsed node." );
        if( n instanceof FieldRefNode ) {
            FieldRefNode fn = (FieldRefNode) n;
            dumpNode( fn.getBase(), out );
            out.print( " "+fieldToNum( fn.getField() ) );
        } else if( pag.getOpts().class_method_var() && n instanceof VarNode ) {
            VarNode vn = (VarNode) n;
            SootMethod m = null;
            if( vn instanceof LocalVarNode ) {
            	m = ((LocalVarNode)vn).getMethod();
            }
            SootClass c = null;
            if( m != null ) c = m.getDeclaringClass();
            ObjectNumberer cl = root.findOrAdd( c );
            ObjectNumberer me = cl.findOrAdd( m );
            ObjectNumberer vr = me.findOrAdd( vn );
            /*
            if( vr.num > 256 ) {
                G.v().out.println( "Var with num: "+vr.num+" is "+vn+
                        " in method "+m+" in class "+c );
            }
            */
            out.print( ""+cl.num+" "+me.num+" "+vr.num );
        } else if( pag.getOpts().topo_sort() && n instanceof VarNode ) {
            out.print( ""+((VarNode) n).finishingNumber );
        } else {
            out.print( ""+n.getNumber() );
        }
    }

    class ObjectNumberer {
        Object o = null;
        int num = 0;
        int nextChildNum = 1;
        HashMap<Object, ObjectNumberer> children = null;

        ObjectNumberer( Object o, int num ) {
            this.o = o; this.num = num;
        }

        ObjectNumberer findOrAdd( Object child ) {
            if( children == null ) children = new HashMap<Object, ObjectNumberer>();
            ObjectNumberer ret = children.get( child );
            if( ret == null ) {
                ret = new ObjectNumberer( child, nextChildNum++ );
                children.put( child, ret );
            }
            return ret;
        }
    }
}

