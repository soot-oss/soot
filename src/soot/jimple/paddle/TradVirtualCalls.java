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
import soot.jimple.paddle.queue.*;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import java.util.*;
import soot.util.queue.*;

/** Resolves virtual calls based on the actual type of the receiver.
 * @author Ondrej Lhotak
 */
public class TradVirtualCalls extends AbsVirtualCalls
{ 
    TradVirtualCalls( Rvarc_var_objc_obj pt,
            Rvar_srcm_stmt_signature_kind receivers,
            Rvar_srcm_stmt_tgtm specials,
            Qctxt_var_obj_srcm_stmt_kind_tgtm out,
            Qsrcc_srcm_stmt_kind_tgtc_tgtm statics
        ) {
        super( pt, receivers, specials, out, statics );
    }

    private Map receiverMap = new HashMap();
    private void receiverMapPut( VarNode receiver, LinkedList sites ) {
        receiverMap.put(receiver, sites);
    }
    private LinkedList receiverMapGet( VarNode receiver ) {
        return (LinkedList) receiverMap.get(receiver);
    }
    private Map specialMap = new HashMap();
    private void specialMapPut( VarNode receiver, LinkedList sites ) {
        specialMap.put(receiver, sites);
    }
    private LinkedList specialMapGet( VarNode receiver ) {
        return (LinkedList) specialMap.get(receiver);
    }
    private boolean change;
    public boolean update() {
        change = false;
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

        for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {

            final Rvar_srcm_stmt_signature_kind.Tuple receiver = (Rvar_srcm_stmt_signature_kind.Tuple) receiverIt.next();
            LinkedList l = (LinkedList) receiverMapGet( receiver.var() );
            if( l == null ) {
                l = new LinkedList();
                receiverMapPut( receiver.var(), l );
            }
            l.addFirst( receiver );
        }

        for( Iterator specialIt = specials.iterator(); specialIt.hasNext(); ) {

            final Rvar_srcm_stmt_tgtm.Tuple special = (Rvar_srcm_stmt_tgtm.Tuple) specialIt.next();
            LinkedList l = (LinkedList) specialMapGet( special.var() );
            if( l == null ) {
                l = new LinkedList();
                specialMapPut( special.var(), l );
            }
            l.addFirst( special );
        }

        ChunkedQueue targetsQueue = new ChunkedQueue();
        QueueReader targets = targetsQueue.reader();

        for( Iterator ptpairIt = pt.iterator(); ptpairIt.hasNext(); ) {

            final Rvarc_var_objc_obj.Tuple ptpair = (Rvarc_var_objc_obj.Tuple) ptpairIt.next();
            Collection sites = (Collection)
                receiverMapGet(ptpair.var());
            if( sites != null ) {
                for( Iterator siteIt = sites.iterator(); siteIt.hasNext(); ) {
                    final Rvar_srcm_stmt_signature_kind.Tuple site = (Rvar_srcm_stmt_signature_kind.Tuple) siteIt.next();
                    if( site.kind() == Kind.CLINIT ) {
                        handleStringConstants( ptpair, site );
                        continue;
                    }

                    Type type = ptpair.obj().getType();

                    if( site.kind() == Kind.THREAD 
                    && !fh.canStoreType( type, clRunnable ) )
                        continue;

                    VirtualCalls.v().resolve( type, site.var().getType(), site.signature(), site.srcm(), targetsQueue );
                    while( targets.hasNext() ) {
                        SootMethod target = (SootMethod) targets.next();
                        change = true;
                        out.add( ptpair.varc(),
                                ptpair.var(),
                                ptpair.obj(),
                                site.srcm(),
                                site.stmt(),
                                site.kind(),
                                target );
                    }
                }
            }
            sites = (Collection) specialMapGet(ptpair.var());
            if( sites != null ) {
                for( Iterator siteIt = sites.iterator(); siteIt.hasNext(); ) {
                    final Rvar_srcm_stmt_tgtm.Tuple site = (Rvar_srcm_stmt_tgtm.Tuple) siteIt.next();
                    change = true;
                    out.add( ptpair.varc(),
                            ptpair.var(),
                            ptpair.obj(),
                            site.srcm(),
                            site.stmt(),
                            Kind.SPECIAL,
                            site.tgtm() );
                }
            }
        }
        return change;
    }

    private void handleStringConstants( Rvarc_var_objc_obj.Tuple ptpair,
            Rvar_srcm_stmt_signature_kind.Tuple site ) {
        AllocNode obj = ptpair.obj();
        if( !( obj instanceof StringConstantNode ) ) {
            for( Iterator clsIt = Scene.v().dynamicClasses().iterator(); clsIt.hasNext(); ) {
                final SootClass cls = (SootClass) clsIt.next();
                for( Iterator clinitIt = EntryPoints.v().clinitsOf(cls).iterator(); clinitIt.hasNext(); ) {
                    final SootMethod clinit = (SootMethod) clinitIt.next();
                    change = true;
                    statics.add(ptpair.varc(),
                                site.srcm(),
                                site.stmt(),
                                Kind.CLINIT,
                                null,
                                clinit );
                }
            }
            if( PaddleScene.v().options().verbose() && Scene.v().dynamicClasses().isEmpty()) {
                G.v().out.println( "Warning: Method "+site.srcm()+
                    " is reachable, and calls Class.forName on a"+
                    " non-constant String and you didn't specify"+
                    " and dynamic classe; graph may be incomplete!"+
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
                    change = true;
                    statics.add(ptpair.varc(),
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


