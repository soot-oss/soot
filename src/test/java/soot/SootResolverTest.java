package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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
import soot.options.Options;

/**
 * Created by canliture on 2021/6/28 <br/>
 *
 * A minimal Test for evaluating the bug in the Method {@link soot.Scene#defaultJavaClassPath}. <br/>
 *
 * When {@link soot.SootResolver} and {@link soot.SourceLocator#getClassSource} resolve classes,
 * they will get default java classPath by call `Scene.v().getSootClassPath()`. <br/>
 *
 * <b>Test under Java 8 environment: </b> <br/>
 *
 * Before fixed the bug, we will get default java class path when <br/>
 * - we set <pre>Options.v().set_whole_program(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 * - we set <pre>Options.v().set_whole_shimple(true);</pre>: `path/to/rt.jar` <br/>
 *
 * After fixed the bug, we will get default java class path when <br/>
 * - we set <pre>Options.v().set_whole_program(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 * - we set <pre>Options.v().set_whole_shimple(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 *
 * @author canliture
 */
public class SootResolverTest {

    @Test
    public void test1() {
        G.reset();

        // setting
        Options.v().set_whole_program(true);

        // No throw. ^_^
        Scene.v().loadNecessaryClasses();
    }

    @Test
    public void test2() {
        G.reset();

        // setting
        Options.v().set_whole_shimple(true);

        // throw java.lang.AssertionError !!!
        Scene.v().loadNecessaryClasses();
    }
}
