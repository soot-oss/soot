package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Qidan He
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Scene;
import soot.jimple.toolkits.callgraph.Edge;
import soot.testing.framework.AbstractTestingFramework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class AndroidCallGraphVirtualEdgesTest extends AbstractTestingFramework {

    private static final String TARGET_CLASS = "soot.jimple.toolkit.callgraph.AsyncTaskTestMainSample";
    private static final String TARGET_METHOD = "void target1()";
    private static final int DO_IN_BG = 0x1;
    private static final int ON_PRE_EXE = DO_IN_BG << 1;
    private static final int ON_PRO_UPD = DO_IN_BG << 2;
    private static final int ON_POS_EXE = DO_IN_BG << 4;
    private static final Map<String, Integer> asyncFuncMaps = new HashMap<>();

    @Test
    public void TestAsyncTaskBasicCG() {
        prepareTarget(methodSigFromComponents(TARGET_CLASS, TARGET_METHOD), TARGET_CLASS);

        asyncFuncMaps.clear();
        asyncFuncMaps.put("doInBackground", DO_IN_BG);
        asyncFuncMaps.put("onPreExecute", ON_PRE_EXE);
        asyncFuncMaps.put("onPostExecute", ON_POS_EXE);
        asyncFuncMaps.put("onProgressUpdate", ON_PRO_UPD);

        int full = 0, ret = 0;
        for(String key: asyncFuncMaps.keySet())
        {
            full |= asyncFuncMaps.get(key);
        }

        for (Edge edge : Scene.v().getCallGraph()) {
            String sig = edge.getTgt().method().toString();
            for (String key : asyncFuncMaps.keySet()) {
                if (sig.contains(key))
                    ret |= asyncFuncMaps.get(key);
            }
        }

        //The four functions shall all appear in call graph
        Assert.assertEquals(ret, full);
    }
}
