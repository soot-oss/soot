package soot.asm;

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

import java.lang.reflect.Field;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationTag;
import soot.tagkit.GenericAttribute;
import soot.tagkit.Host;
import soot.tagkit.VisibilityAnnotationTag;

/**
 * Tag builder.
 * 
 * @author Aaloan Miftah
 */
final class TagBuilder {

  private VisibilityAnnotationTag invisibleTag, visibleTag;
  private final Host host;
  private final SootClassBuilder scb;

  TagBuilder(Host host, SootClassBuilder scb) {
    this.host = host;
    this.scb = scb;
  }

  /**
   * @see FieldVisitor#visitAnnotation(String, boolean)
   * @see MethodVisitor#visitAnnotation(String, boolean)
   * @see ClassVisitor#visitAnnotation(String, boolean)
   */
  public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
    VisibilityAnnotationTag tag;
    if (visible) {
      tag = visibleTag;
      if (tag == null) {
        visibleTag = tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
        host.addTag(tag);
      }
    } else {
      tag = invisibleTag;
      if (tag == null) {
        invisibleTag = tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
        host.addTag(tag);
      }
    }
    scb.addDep(AsmUtil.toQualifiedName(desc.substring(1, desc.length() - 1)));
    final VisibilityAnnotationTag _tag = tag;
    return new AnnotationElemBuilder() {
      @Override
      public void visitEnd() {
        AnnotationTag annotTag = new AnnotationTag(desc, elems);
        _tag.addAnnotation(annotTag);
      }
    };
  }

  /**
   * @see FieldVisitor#visitAttribute(Attribute)
   * @see MethodVisitor#visitAttribute(Attribute)
   * @see ClassVisitor#visitAttribute(Attribute)
   */
  public void visitAttribute(Attribute attr) {
    // SA, 2017-07-21: As of ASM 5.1, there is no better way to obtain
    // attribute values.
    byte[] value = null;
    try {
      Field fld = Attribute.class.getDeclaredField("value");
      fld.setAccessible(true);
      value = (byte[]) fld.get(attr);
    } catch (Exception ex) {
      // Just carry on
    }
    host.addTag(new GenericAttribute(attr.type, value));
  }
}