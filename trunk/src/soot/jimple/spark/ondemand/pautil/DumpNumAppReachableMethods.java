/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
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
package soot.jimple.spark.ondemand.pautil;

import java.util.Iterator;
import java.util.Map;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;

public class DumpNumAppReachableMethods extends SceneTransformer {

    protected void internalTransform(String phaseName, Map options) {
        int numAppMethods = 0;
        for (Iterator mIt = Scene.v().getReachableMethods().listener(); mIt
                .hasNext();) {
            final SootMethod m = (SootMethod) mIt.next();

            if (isAppMethod(m)) {
                // System.out.println(m);
                // assert OnFlyCallGraphBuilder.processedMethods.contains(m) : m
                // + " not processed!!";
                numAppMethods++;
            }
        }
        G.v().out.println("Number of reachable methods in application: "
                + numAppMethods);
    }

    private boolean isAppMethod(final SootMethod m) {
        return !SootUtil.inLibrary(m.getDeclaringClass().getName());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PackManager.v().getPack("wjtp").add(
                new Transform("wjtp.narm", new DumpNumAppReachableMethods()));
        soot.Main.main(args);

    }

}
