package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;
import soot.validation.CheckInitValidator;
import soot.validation.ValidationException;

/**
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class AsmMethodSourceOrigNamesTest extends AbstractTestingFramework {

  @Override
  protected void setupSoot() {
    final Options opts = Options.v();
    opts.set_validate(false);
    opts.setPhaseOption("jb", "use-original-names:true");
    opts.setPhaseOption("jb.sils", "enabled:false");
  }

  @Test
  public void testWriterToUTF8Buffered1() {
    final String clazz = "org.apache.xml.serializer.WriterToUTF8Buffered";
    final String[] params = { "char[]", "int", "int" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "write", params), clazz));
  }

  @Test
  public void testWriterToUTF8Buffered2() {
    final String clazz = "org.apache.xml.serializer.WriterToUTF8Buffered";
    final String[] params = { "java.lang.String" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "write", params), clazz));
  }

  @Test
  public void testElemApplyTemplates() {
    final String clazz = "org.apache.xalan.templates.ElemApplyTemplates";
    final String[] params = { "org.apache.xalan.transformer.TransformerImpl" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "void", "transformSelectedNodes", params), clazz));
  }

  @Test
  public void testXNodeSet() {
    final String clazz = "org.apache.xpath.objects.XNodeSet";
    final String[] params = { "org.apache.xpath.objects.XObject", "org.apache.xpath.objects.Comparator" };
    runXalanTest(prepareTarget(methodSigFromComponents(clazz, "boolean", "compare", params), clazz));
  }

  private void runXalanTest(SootMethod m) {
    Body body = m.retrieveActiveBody();
    // Run CheckInitValidator to ensure the special case for "use-original-names"
    // in AsmMethodSource did not cause any problems when replacing locals.
    ArrayList<ValidationException> exceptions = new ArrayList<>();
    CheckInitValidator.INSTANCE.validate(body, exceptions);
    Assert.assertTrue(exceptions.isEmpty());
  }
}
