package soot.lambdaMetaFactory;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 Manuel Benz
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.SootMethod;
import soot.testing.framework.AbstractTestingFramework;

/**
 * Reproduces issue 1367: https://github.com/Sable/soot/issues/1367
 *
 * @author David Seekatz
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class Issue1367Test extends AbstractTestingFramework {

    @Test
    public void constructorReference() {
        String testClass = "soot.lambdaMetaFactory.Issue1367";

        final SootMethod target = prepareTarget(
                methodSigFromComponents(testClass, "java.util.function.Supplier", "constructorReference"),
                testClass,
                "java.util.function.Function");

        validateAllBodies(target.getDeclaringClass());
    }

}