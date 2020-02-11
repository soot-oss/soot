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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
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

    assertTrue(unit.isPresent());

    List<ValueBox> useBoxes = unit.get().getUseBoxes();

    assertEquals(2, useBoxes.size());
    ValueBox valueBox = useBoxes.get(0);
    assertTrue(valueBox instanceof ImmediateBox);
    assertEquals(1, valueBox.getTags().size());
    assertTrue(valueBox.getTags().get(0) instanceof LineNumberTag);
    assertEquals(33, valueBox.getJavaSourceStartLineNumber());
  }

  @Test
  public void transitiveNullAssignment() {
    SootMethod target =
        prepareTarget(
            methodSigFromComponents(TEST_TARGET_CLASS, "void", "transitiveNullAssignment"),
            TEST_TARGET_CLASS);

    Body body = target.retrieveActiveBody();

    // first call to foo
    Optional<Unit> unit =
        body.getUnits().stream()
            .filter(
                u ->
                    u.toString()
                        .equals(
                            "staticinvoke <soot.jimple.PropagateLineNumberTag: soot.jimple.PropagateLineNumberTag$A foo(soot.jimple.PropagateLineNumberTag$A)>(null)"))
            .findFirst();

    assertTrue(unit.isPresent());

    List<ValueBox> useBoxes = unit.get().getUseBoxes();

    assertEquals(2, useBoxes.size());
    ValueBox valueBox = useBoxes.get(0);
    assertTrue(valueBox instanceof ImmediateBox);
    assertEquals(1, valueBox.getTags().size());
    assertTrue(valueBox.getTags().get(0) instanceof LineNumberTag);
    assertEquals(39, valueBox.getJavaSourceStartLineNumber());

    // second call to foo
    unit =
        body.getUnits().stream()
            .filter(
                u ->
                    u.toString()
                        .equals(
                            "staticinvoke <soot.jimple.PropagateLineNumberTag: soot.jimple.PropagateLineNumberTag$A foo(soot.jimple.PropagateLineNumberTag$A)>(null)"))
            .skip(1)
            .findFirst();

    assertTrue(unit.isPresent());
    useBoxes = unit.get().getUseBoxes();
    assertEquals(2, useBoxes.size());
    valueBox = useBoxes.get(0);
    assertTrue(valueBox instanceof ImmediateBox);
    assertEquals(1, valueBox.getTags().size());
    assertTrue(valueBox.getTags().get(0) instanceof LineNumberTag);
    assertEquals(39, valueBox.getJavaSourceStartLineNumber());
  }
}
