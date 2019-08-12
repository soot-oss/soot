package soot.lambdaMetaFactory;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import soot.SootMethod;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 2018-12-18
 */
public class LambdaMetaFactoryAdaptTest extends AbstractTestingFramework {


  public static class Retry implements TestRule {

    private final int retryCount;

    public Retry(int retryCount) {
      this.retryCount = retryCount;
    }

    @Override
    public Statement apply(final Statement base,
                           final Description description) {
      return new Statement() {

        @Override
        @SuppressWarnings("synthetic-access")
        public void evaluate() throws Throwable {
          Throwable caughtThrowable = null;
          int failuresCount = 0;
          for (int i = 0; i < retryCount; i++) {
            try {
              System.out.println("run "+i);
              base.evaluate();
            } catch (Throwable t) {
              caughtThrowable = t;
              System.err.println(description.getDisplayName()
                      + ": run " + (i + 1) + " failed:");
              t.printStackTrace();
              ++failuresCount;
            }
          }
          if (caughtThrowable == null) return;
          throw new AssertionError(description.getDisplayName()
                  + ": failures " + failuresCount + " out of "
                  + retryCount + " tries. See last throwable as the cause.", caughtThrowable);
        }
      };
    }
  }

  @Rule
  public Retry retry1 = new Retry(100);




  @Test
  public void parameterBoxing() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "parameterBoxingTarget"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void parameterWidening() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "parameterWidening"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void returnBoxing() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "returnBoxing"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void returnWidening() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "returnWidening"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }
}
