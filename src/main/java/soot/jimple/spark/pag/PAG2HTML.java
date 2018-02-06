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
import java.util.Iterator;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import soot.SootMethod;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/** Dumps a pointer assignment graph to a html files.
 * @author Ondrej Lhotak
 */
public class PAG2HTML {
    public PAG2HTML( PAG pag, String output_dir ) {
        this.pag = pag;
        this.output_dir = output_dir;
    }
    public void dump() {
        for( Iterator vIt = pag.getVarNodeNumberer().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            mergedNodes.put( v.getReplacement(), v );
            if( v instanceof LocalVarNode ) {
                SootMethod m = ((LocalVarNode)v).getMethod();
                if( m != null ) {
                    methodToNodes.put( m, v );
                }
            }
        }
        try {
            JarOutputStream jarOut = new JarOutputStream(
                    new FileOutputStream( new File(output_dir, "pag.jar") ) );
            for( Iterator vIt = mergedNodes.keySet().iterator(); vIt.hasNext(); ) {
                final VarNode v = (VarNode) vIt.next();
                dumpVarNode( v, jarOut );
            }
            for( Iterator mIt = methodToNodes.keySet().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                dumpMethod( m, jarOut );
            }
            addSymLinks( pag.getVarNodeNumberer().iterator(), jarOut );
            jarOut.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump html"+e );
        }
    }


    /* End of public methods. */
    /* End of package methods. */

    protected PAG pag;
    protected String output_dir;
    protected MultiMap mergedNodes = new HashMultiMap();
    protected MultiMap methodToNodes = new HashMultiMap();

    protected void dumpVarNode( VarNode v, JarOutputStream jarOut ) throws IOException {
        jarOut.putNextEntry( new ZipEntry( "nodes/n"+v.getNumber()+".html" ) );
        final PrintWriter out = new PrintWriter( jarOut );
        out.println( "<html>" );
        
        out.println( "Green node for:" );
        out.println( varNodeReps( v ) );

        out.println( "Declared type: "+v.getType() );
        
        out.println( "<hr>Reaching blue nodes:" );
        out.println( "<ul>" );
        v.getP2Set().forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                out.println( "<li>"+htmlify(n.toString()) );
            }
        } );
        out.println( "</ul>" );

        out.println( "<hr>Outgoing edges:" );
        Node[] succs = pag.simpleLookup( v );
        for (Node element : succs) {
            VarNode succ = (VarNode) element;
            out.println( varNodeReps( succ ) );
        }
        
        out.println( "<hr>Incoming edges: " );
        succs = pag.simpleInvLookup( v );
        for (Node element : succs) {
            VarNode succ = (VarNode) element;
            out.println( varNodeReps( succ ) );
        }

        out.println( "</html>" );
        out.flush();
    }
    protected String varNodeReps( VarNode v ) {
        StringBuffer ret = new StringBuffer();
        ret.append( "<ul>\n" );
        for( Iterator vvIt = mergedNodes.get( v ).iterator(); vvIt.hasNext(); ) {
            final VarNode vv = (VarNode) vvIt.next();
            ret.append( varNode( "", vv ) );
        }
        ret.append( "</ul>\n" );
        return ret.toString();
    }
    protected String varNode( String dirPrefix, VarNode vv ) {
        StringBuffer ret = new StringBuffer();
        ret.append( "<li><a href=\""+dirPrefix+"n"+vv.getNumber()+".html\">" );
        if(vv.getVariable()!=null)
        	ret.append( ""+htmlify(vv.getVariable().toString()) );
    	ret.append( "GlobalVarNode" );
        ret.append( "</a><br>" );
        ret.append( "<li>Context: " );
        ret.append( ""+(vv.context() == null ?"null":htmlify(vv.context().toString()) ) );
        ret.append( "</a><br>" );
        if( vv instanceof LocalVarNode ) {
            LocalVarNode lvn = (LocalVarNode) vv;
            SootMethod m = lvn.getMethod();
            if( m != null ) {
                ret.append( "<a href=\"../"
                        +toFileName(m.toString() )+".html\">" );
                ret.append( htmlify(m.toString())+"</a><br>" );
            }
        }
        ret.append( htmlify(vv.getType().toString())+"\n" );
        return ret.toString();
    }
    protected static String htmlify( String s ) {
        StringBuffer b = new StringBuffer( s );
        for( int i = 0; i < b.length(); i++ ) {
            if( b.charAt( i ) == '<' ) {
                b.replace( i, i+1, "&lt;" );
            }
            if( b.charAt( i ) == '>' ) {
                b.replace( i, i+1, "&gt;" );
            }
        }
        return b.toString();
    }
    protected void dumpMethod( SootMethod m, JarOutputStream jarOut ) throws IOException {
        jarOut.putNextEntry( new ZipEntry( 
                    ""+toFileName( m.toString() )+".html" ) );
        final PrintWriter out = new PrintWriter( jarOut );
        out.println( "<html>" );
        
        out.println( "This is method "+htmlify( m.toString() )+"<hr>" );
        for( Iterator it = methodToNodes.get( m ).iterator(); it.hasNext(); ) {
            out.println( varNode( "nodes/", (VarNode) it.next() ) );
        }
        out.println( "</html>" );
        out.flush();
    }
    protected void addSymLinks( Iterator nodes, JarOutputStream jarOut ) throws IOException {
        jarOut.putNextEntry( new ZipEntry( "symlinks.sh" ) );
        final PrintWriter out = new PrintWriter( jarOut );
        out.println( "#!/bin/sh" );
        while( nodes.hasNext() ) {
            VarNode v = (VarNode) nodes.next();
            VarNode rep = (VarNode) v.getReplacement();
            if( v != rep ) {
                out.println( "ln -s n"+rep.getNumber()+".html n"+v.getNumber()+".html" );
            }
        }
        out.flush();
    }
    protected String toFileName( String s ) {
        StringBuffer ret = new StringBuffer();
        for( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            if( c == '<' ) ret.append( '{' );
            else if( c == '>' ) ret.append( '}' );
            else ret.append( c );
        }
        return ret.toString();
    }
}

