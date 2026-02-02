package soot.jimple.spark.solver;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import soot.Scene;
import soot.Type;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.options.SparkOptions;

public class SCCCollapserTest {

    @Test
    public void testSeparateComponents() {
        Scene.v().loadBasicClasses();
        Type type = Scene.v().getObjectType();

        SparkOptions sparkOptions = new SparkOptions(Collections.emptyMap());
        PAG pag = new PAG(sparkOptions);

        VarNode a = pag.makeGlobalVarNode("a", type);
        VarNode b = pag.makeGlobalVarNode("b", type);
        VarNode c = pag.makeGlobalVarNode("c", type);
        pag.addEdge(a, b);
        pag.addEdge(a, c);
        pag.addEdge(b, c);

        SCCCollapser sccCollapser = new SCCCollapser(pag, false);
        sccCollapser.collapse();
        pag.cleanUpMerges();

        assertEquals(a, a.getReplacement());
        assertEquals(b, b.getReplacement());
        assertEquals(c, c.getReplacement());
    }
}
