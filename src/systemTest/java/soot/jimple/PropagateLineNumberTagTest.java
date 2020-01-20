package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Manuel Benz
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

import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.internal.ImmediateBox;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.testing.framework.AbstractTestingFramework;

/** @author Manuel Benz at 20.01.20 */
public class PropagateLineNumberTagTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.jimple.PropagateLineNumberTag";

  @Override
  protected void setupSoot() {
    Options.v().setPhaseOption("jb", "use-original-names:true");
    Options.v().setPhaseOption("jb.tr", "ignore-nullpointer-dereferences:true");
    Options.v().set_keep_line_number(true);
    Options.v().setPhaseOption("cg.cha", "on");
  }

  @Test
  public void nullAssignment() {
    SootMethod target =
        prepareTarget(
            methodSigFromComponents(TEST_TARGET_CLASS, "void", "nullAssignment"),
            TEST_TARGET_CLASS);

    Body body = target.retrieveActiveBody();

    Optional<Unit> unit =
        body.getUnits().stream()
            .filter(
                u ->
                    u.toString()
                        .equals(
                            "staticinvoke <soot.jimple.PropagateLineNumberTag: soot.jimple.PropagateLineNumberTag$A foo(soot.jimple.PropagateLineNumberTag$A)>(null)"))
            .findFirst();

    Assert.assertTrue(unit.isPresent());

    List<ValueBox> useBoxes = unit.get().getUseBoxes();

    Assert.assertEquals(2, useBoxes.size());
    ValueBox valueBox = useBoxes.get(0);
    Assert.assertTrue(valueBox instanceof ImmediateBox);
    Assert.assertEquals(1, valueBox.getTags().size());
    Assert.assertTrue(valueBox.getTags().get(0) instanceof LineNumberTag);
    Assert.assertEquals(33, valueBox.getJavaSourceStartLineNumber());
  }
}
