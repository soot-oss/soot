/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.shimple;

import soot.*;
import soot.options.Options;
import java.util.*;

/**
 * Traverses all methods, in all classes from the Scene, and
 * transforms them to Shimple.  Typically used for whole-program
 * analysis on Shimple.
 *
 * @author Navindra Umanee
 **/
public class ShimpleTransformer extends SceneTransformer
{
    public ShimpleTransformer( Singletons.Global g ) {}
    public static ShimpleTransformer v() { return G.v().soot_shimple_ShimpleTransformer(); }

    protected void internalTransform(String phaseName, Map options)
    {
        if(Options.v().verbose())
            G.v().out.println("Transforming all classes in the Scene to Shimple...");

        // *** FIXME: Add debug output to indicate which class/method is being shimplified.
        // *** FIXME: Is ShimpleTransformer the right solution?  The call graph may deem
        //            some classes unreachable.
        
        Iterator classesIt = Scene.v().getClasses().iterator();
        while(classesIt.hasNext()){
            SootClass sClass = (SootClass) classesIt.next();
            if(sClass.isPhantom()) continue;
            
            Iterator methodsIt = sClass.getMethods().iterator();
            while(methodsIt.hasNext()){
                SootMethod method = (SootMethod) methodsIt.next();
                if(!method.isConcrete()) continue;

                if(method.hasActiveBody()){
                    Body body = method.getActiveBody();
                    ShimpleBody sBody = null;

                    if(body instanceof ShimpleBody){
                        sBody = (ShimpleBody) body;
                        if(!sBody.isSSA())
                            sBody.rebuild();
                    }
                    else{
                        sBody = Shimple.v().newBody(body);
                    }

                    method.setActiveBody(sBody);
                }
                else{
                    MethodSource ms = new ShimpleMethodSource(method.getSource());
                    method.setSource(ms);
                }
            }
        }
    }
}
