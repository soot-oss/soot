package soot.jimple.toolkits.annotation;

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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.ConcurrentModificationException;

import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.Scene;
import soot.options.Options;

public class LineNumberAdderTest {

  @BeforeClass
  public static void setUp() throws IOException {
    G.reset();
    Options.v().set_process_dir(Collections.singletonList("./src/test/resources/LineNumberAdderTest/C.jar"));
    Options.v().set_keep_line_number(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_ignore_resolution_errors(true);
    Scene.v().loadNecessaryClasses();
  }

  @Test
  public void test() {
    try {
      LineNumberAdder.v().internalTransform("", null);

    } catch (final ConcurrentModificationException concurrentModificationException) {
      fail("Should not have thrown ConcurrentModificationException");
    }
  }

}
