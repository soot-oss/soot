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
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.toolkits.callgraph.VirtualCalls;

import java.util.*;
import soot.util.queue.*;
import soot.jimple.*;

/** Resolves virtual calls based on the actual type of the receiver.
 * @author Ondrej Lhotak
 */
public class TradVirtualCalls extends AbsVirtualCalls
{ 
    TradVirtualCalls( Rvar_obj pt,
            Rlocal_srcm_stmt_signature_kind receivers,
            Rlocal_srcm_stmt_tgtm specials,
            Qctxt_local_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics
        ) {
        super( pt, receivers, specials, out, statics );
    }

    private Map receiverMap = new HashMap();
    private Map specialMap = new HashMap();
    public void update() {
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

        for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {

            final Rlocal_srcm_stmt_signature_kind.Tuple receiver = (Rlocal_srcm_stmt_signature_kind.Tuple) receiverIt.next();
            LinkedList l = (LinkedList) receiverMap.get( receiver.local() );
            if( l == null ) {
                l = new LinkedList();
                receiverMap.put( receiver.local(), l );
            }
            l.addFirst( receiver );
        }

        for( Iterator specialIt = specials.iterator(); specialIt.hasNext(); ) {

            final Rlocal_srcm_stmt_tgtm.Tuple special = (Rlocal_srcm_stmt_tgtm.Tuple) specialIt.next();
            LinkedList l = (LinkedList) specialMap.get( special.local() );
            if( l == null ) {
                l = new LinkedList();
                specialMap.put( special.local(), l );
            }
            l.addFirst( special );
        }

        ChunkedQueue targetsQueue = new ChunkedQueue();
        QueueReader targets = targetsQueue.reader();

        for( Iterator ptpairIt = pt.iterator(); ptpairIt.hasNext(); ) {

            final Rvar_obj.Tuple ptpair = (Rvar_obj.Tuple) ptpairIt.next();
            Collection sites = (Collection)
                receiverMap.get(ptpair.var().getVariable());
            if( sites != null ) {
                for( Iterator siteIt = sites.iterator(); siteIt.hasNext(); ) {
                    final Rlocal_srcm_stmt_signature_kind.Tuple site = (Rlocal_srcm_stmt_signature_kind.Tuple) siteIt.next();
                    if( site.kind() == Kind.CLINIT ) {
                        handleStringConstants( ptpair, site );
                        continue;
                    }

                    Type type = ptpair.obj().getType();

                    if( site.kind() == Kind.THREAD 
                    && !fh.canStoreType( type, clRunnable ) )
                        continue;

                    VirtualCalls.v().resolve( type, site.local().getType(), site.signature(), site.srcm(), targetsQueue );
                    while( targets.hasNext() ) {
                        SootMethod target = (SootMethod) targets.next();
                        out.add( ptpair.var().context(),
                                (Local) ptpair.var().getVariable(),
                                ptpair.obj(),
                                site.srcm(),
                                site.stmt(),
                                site.kind(),
                                target );
                    }
                }
            }
            sites = (Collection) specialMap.get(ptpair.var().getVariable());
            if( sites != null ) {
                for( Iterator siteIt = sites.iterator(); siteIt.hasNext(); ) {
                    final Rlocal_srcm_stmt_tgtm.Tuple site = (Rlocal_srcm_stmt_tgtm.Tuple) siteIt.next();
                    out.add( ptpair.var().context(),
                            (Local) ptpair.var().getVariable(),
                            ptpair.obj(),
                            site.srcm(),
                            site.stmt(),
                            Kind.SPECIAL,
                            site.tgtm() );
                }
            }
        }
    }

    private void handleStringConstants( Rvar_obj.Tuple ptpair,
            Rlocal_srcm_stmt_signature_kind.Tuple site ) {
        AllocNode obj = ptpair.obj();
        if( !( obj instanceof StringConstantNode ) ) {
            if( PaddleScene.v().options().verbose() ) {
                G.v().out.println( "Warning: Method "+site.srcm()+
                    " is reachable, and calls Class.forName on a"+
                    " non-constant String; graph will be incomplete!"+
                    " Use safe-forname option for a conservative result." );
            }
        } else {
            StringConstantNode scn = (StringConstantNode) obj;
            String constant = scn.getString();
            if( constant.charAt(0) == '[' ) {
                if( constant.length() > 1 && constant.charAt(1) == 'L' 
                    && constant.charAt(constant.length()-1) == ';' ) {
                        constant = constant.substring(2,constant.length()-1);
                } else return;
            }
            if( !Scene.v().containsClass( constant ) ) {
                if( PaddleScene.v().options().verbose() ) {
                    G.v().out.println( "Warning: Class "+constant+" is"+
                        " a dynamic class, and you did not specify"+
                        " it as such; graph will be incomplete!" );
                }
            } else {
                SootClass sootcls = Scene.v().getSootClass( constant );
                if( !sootcls.isApplicationClass() ) {
                    sootcls.setLibraryClass();
                }
                for( Iterator clinitIt = EntryPoints.v().clinitsOf(sootcls).iterator(); clinitIt.hasNext(); ) {
                    final SootMethod clinit = (SootMethod) clinitIt.next();
                    statics.add(ptpair.var().context(),
                                site.srcm(),
                                site.stmt(),
                                Kind.CLINIT,
                                null,
                                clinit );
                }
            }
        }
    }

    protected final RefType clRunnable = RefType.v("java.lang.Runnable");

}


