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

package soot.toDex;

import com.android.tools.smali.dexlib2.base.reference.BaseTypeReference;
import com.android.tools.smali.dexlib2.iface.Annotation;
import com.android.tools.smali.dexlib2.iface.ClassDef;
import com.android.tools.smali.dexlib2.iface.Field;
import com.android.tools.smali.dexlib2.iface.Method;
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation;
import com.android.tools.smali.dexlib2.util.FieldUtil;
import com.android.tools.smali.dexlib2.util.MethodUtil;
import com.android.tools.smali.util.AbstractIterator;
import com.android.tools.smali.util.ChainedIterable;
import com.android.tools.smali.util.IteratorUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MutableClassDef extends BaseTypeReference implements ClassDef {

  private String type, superclass;
  private int accessFlags;
  private List<String> interfaces;
  private String sourceFile;

  private Set<? extends ImmutableAnnotation> annotations;
  private SortedSet<? extends Field> staticFields;
  private SortedSet<? extends Field> instanceFields;
  private SortedSet<? extends Method> directMethods;
  private SortedSet<? extends Method> virtualMethods;

  public MutableClassDef(String type, int accessFlags, String superclass, List<String> interfaces, String sourceFile,
      Collection<? extends Annotation> annotations, Iterable<? extends Field> fields, Iterable<? extends Method> methods) {
    if (fields == null) {
      fields = Collections.emptyList();
    }
    if (methods == null) {
      methods = Collections.emptyList();
    }
    if (interfaces == null) {
      interfaces = Collections.emptyList();
    }
    this.interfaces = interfaces;

    this.type = type;
    this.accessFlags = accessFlags;
    this.superclass = superclass;

    this.sourceFile = sourceFile;
    this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
    this.staticFields = getSortedSet(IteratorUtils.filter(fields, FieldUtil.FIELD_IS_STATIC));
    this.instanceFields = getSortedSet(IteratorUtils.filter(fields, FieldUtil.FIELD_IS_INSTANCE));
    this.directMethods = getSortedSet(IteratorUtils.filter(methods, MethodUtil.METHOD_IS_DIRECT));
    this.virtualMethods = getSortedSet(IteratorUtils.filter(methods, MethodUtil.METHOD_IS_VIRTUAL));
  }

  private static <T> SortedSet<? extends T> getSortedSet(AbstractIterator<? extends T> filter) {
    TreeSet<T> treeset = new TreeSet<>();
    while (filter.hasNext()) {
      treeset.add(filter.next());
    }
    return treeset;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public int getAccessFlags() {
    return accessFlags;
  }

  @Override
  public String getSuperclass() {
    return superclass;
  }

  @Override
  public List<String> getInterfaces() {
    return interfaces;
  }

  @Override
  public String getSourceFile() {
    return sourceFile;
  }

  @Override
  public Set<? extends ImmutableAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public Set<? extends Field> getStaticFields() {
    return staticFields;
  }

  @Override
  public Set<? extends Field> getInstanceFields() {
    return instanceFields;
  }

  @Override
  public Set<? extends Method> getDirectMethods() {
    return directMethods;
  }

  @Override
  public Set<? extends Method> getVirtualMethods() {
    return virtualMethods;
  }

  @Override
  public Iterable<? extends Field> getFields() {
    return new ChainedIterable(staticFields, instanceFields);
  }

  @Override
  public Iterable<? extends Method> getMethods() {
    return new ChainedIterable(directMethods, virtualMethods);
  }
}
