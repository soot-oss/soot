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

package soot.jimple.paddle;
import soot.*;
import soot.options.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Keeps track of which methods are reachable.
 * @author Ondrej Lhotak
 */
public abstract class AbsStaticCallBuilder 
{ 
    protected Rctxt_method in;
    protected Qsrcc_srcm_stmt_kind_tgtc_tgtm out;
    protected Qlocal_srcm_stmt_signature_kind receivers;
    protected Qlocal_srcm_stmt_tgtm specials;
    protected CGOptions options;
    AbsStaticCallBuilder( Rctxt_method in, Qsrcc_srcm_stmt_kind_tgtc_tgtm out, Qlocal_srcm_stmt_signature_kind receivers, Qlocal_srcm_stmt_tgtm specials ) {
        this.in = in;
        this.out = out;
        this.receivers = receivers;
        this.specials = specials;
        this.options = new CGOptions( PhaseOptions.v().getPhaseOptions( "cg" ) );
    }
    public abstract void update();
}

