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

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public final class BDDTypeManager extends AbstractTypeManager {
    public BDDTypeManager() {
        BDDPAG bddpag = (BDDPAG) pag;
        typeMask = new Relation( bddpag.var, bddpag.obj,
                                 bddpag.v1,  bddpag.h1 );
        clearTypeMask();
    }
    final public void clearTypeMask() {
        typeMask.makeFull();
    }
    final public void makeTypeMask( AbstractPAG pag ) {
        if(true) throw new RuntimeException( "NYI" );
    }

    final private Relation typeMask;
}

