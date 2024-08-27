package soot.portedtest;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.util.Chain;

import static org.junit.Assert.assertEquals;
import static soot.portedtest.LoadResource.loadClasses;

public class Issue1577Test {
    // port from https://github.com/soot-oss/SootUp/pull/405
    @Test
    public void test() {
        loadClasses("src", "test", "resources", "ported", "Issue1577");
        Chain<SootClass> classes = Scene.v() .getApplicationClasses();
        assertEquals(1, classes.size());
        SootClass clazz = classes.getFirst();
        clazz.getMethods().forEach(SootMethod::retrieveActiveBody);
        clazz.getMethods().forEach(SootMethod::getActiveBody) ;
    }
}
