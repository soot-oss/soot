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

package soot.jimple.spark.internal;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.*;
import soot.util.*;
import java.util.Iterator;
import soot.util.queue.*;
import soot.Type;
import soot.options.SparkOptions;
import soot.relations.*;
import soot.jbuddy.JBuddy;

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public final class BDDTypeManager extends AbstractTypeManager {
    public BDDTypeManager( BDDPAG bddpag ) {
        super(bddpag);
    }
    final public void clearTypeMask() {
        lastAllocNode = 0;
        lastVarNode = 0;
        typeMask.makeEmpty();
        varNodeType.makeEmpty();
        allocNodeType.makeEmpty();
        typeSubtype.makeEmpty();
    }
    final public void makeTypeMask() {
        if( fh == null ) {
            typeMask.makeFull();
        }
        update();
    }
    final public Relation get() {
        update();
        return typeMask;
    }

    int lastAllocNode = 0;
    int lastVarNode = 0;
    private void update() {
        if( fh == null ) return;

        Numberer anNumb = pag.getAllocNodeNumberer();
        Numberer vnNumb = pag.getVarNodeNumberer();
        for( int i = 1; i <= vnNumb.size(); i++ ) {
            for( int j = lastAllocNode+1; j <= anNumb.size(); j++ ) {
                updatePair( (VarNode) vnNumb.get( i ), (AllocNode) anNumb.get( j ) );
            }
        }
        for( int i = lastVarNode+1; i <= vnNumb.size(); i++ ) {
            for( int j = 1; j <= lastAllocNode; j++ ) {
                updatePair( (VarNode) vnNumb.get( i ), (AllocNode) anNumb.get( j ) );
            }
        }

        varNodeType.eqUnion( varNodeType, newVnType );
        allocNodeType.eqUnion( allocNodeType, newAnType );

        final Relation tmp = new Relation( var, subt,
                                           v1,  t1 );
        final Relation tmp2 = new Relation( var, obj,
                                            v1,  h1 );
        tmp.eqRelprod( typeSubtype, supt, newVnType, type,
                       var, newVnType,   var,
                       subt,  typeSubtype, subt );
        tmp2.eqRelprod( allocNodeType, type, tmp, subt,
                        var, tmp,           var,
                        obj, allocNodeType, obj );
        typeMask.eqUnion( typeMask, tmp2 );

        tmp.eqRelprod( typeSubtype, supt, varNodeType, type,
                       var, varNodeType,   var,
                       subt,  typeSubtype, subt );
        tmp2.eqRelprod( newAnType, type, tmp, subt,
                        var, tmp,       var,
                        obj, newAnType, obj );
        typeMask.eqUnion( typeMask, tmp2 );

        newVnType.makeEmpty();
        newAnType.makeEmpty();

        lastAllocNode = anNumb.size();
        lastVarNode = vnNumb.size();

        tmp.makeEmpty();
        tmp2.makeEmpty();
    }

    private void updatePair( VarNode vn, AllocNode an ) {
        Type vtype = vn.getType();
        Type atype = an.getType();

        if( varNodeType.restrict( type, vtype ).isEmpty()
        ||  allocNodeType.restrict( type, atype ).isEmpty() ) {
            // never seen this pair of types before
            if( castNeverFails( atype, vtype ) ) {
                typeSubtype.add( subt, atype,
                                 supt, vtype );
            }
        }
        newVnType.add( var, vn,
                       type,  vtype );
        newAnType.add( obj, an,
                       type,  atype );
    }

    final private PhysicalDomain t1 = ((BDDPAG)pag).t1;
    final private PhysicalDomain t2 = ((BDDPAG)pag).t2;
    final private PhysicalDomain v1 = ((BDDPAG)pag).v1;
    final private PhysicalDomain h1 = ((BDDPAG)pag).h1;

    final private Domain subt = new Domain( Scene.v().getTypeNumberer(), "subt" );
    final private Domain supt = new Domain( Scene.v().getTypeNumberer(), "supt" );
    final private Domain type = new Domain( Scene.v().getTypeNumberer(), "type" );
    final private Domain var = ((BDDPAG)pag).var;
    final private Domain obj = ((BDDPAG)pag).obj;

    final private Relation typeSubtype = new Relation( subt, supt,
                                                       t1,   t2 );
    final private Relation varNodeType = new Relation( var, type,
                                                       v1,  t1 );
    final private Relation newVnType = varNodeType.sameDomains();
    final private Relation allocNodeType = new Relation( obj, type,
                                                         h1,  t1 );
    final private Relation newAnType = allocNodeType.sameDomains();
    final private Relation typeMask = new Relation( var, obj,
                                                    v1,  h1 );

}

