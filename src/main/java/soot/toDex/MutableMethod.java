/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import com.android.tools.smali.dexlib2.HiddenApiRestriction;
import com.android.tools.smali.dexlib2.base.reference.BaseMethodReference;
import com.android.tools.smali.dexlib2.iface.Annotation;
import com.android.tools.smali.dexlib2.iface.Method;
import com.android.tools.smali.dexlib2.iface.MethodImplementation;
import com.android.tools.smali.dexlib2.iface.MethodParameter;
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation;
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MutableMethod extends BaseMethodReference implements Method {
  private final MethodImplementation methodImplementation;
  private final String definingClass;
  private String name;
  private List<? extends ImmutableMethodParameter> parameters;
  private String returnType;
  private int accessFlags;
  private Set<? extends ImmutableAnnotation> annotations;
  private Set<HiddenApiRestriction> hiddenApiRestrictions;

  public MutableMethod(String definingClass, String name, Iterable<? extends MethodParameter> parameters, String returnType,
      int accessFlags, Set<? extends Annotation> annotations, Set<HiddenApiRestriction> hiddenApiRestrictions,
      MethodImplementation methodImplementation) {
    this.definingClass = definingClass;
    this.name = name;
    this.parameters = ImmutableMethodParameter.immutableListOf(parameters);
    this.returnType = returnType;
    this.accessFlags = accessFlags;
    this.annotations = ImmutableAnnotation.immutableSetOf(annotations);
    this.hiddenApiRestrictions = hiddenApiRestrictions == null ? Collections.emptySet() : hiddenApiRestrictions;
    this.methodImplementation = methodImplementation;
  }

  @Override
  public String getDefiningClass() {
    return definingClass;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<? extends ImmutableMethodParameter> getParameters() {
    return parameters;
  }

  @Override
  public String getReturnType() {
    return returnType;
  }

  @Override
  public List<? extends CharSequence> getParameterTypes() {
    return parameters;
  }

  @Override
  public int getAccessFlags() {
    return accessFlags;
  }

  @Override
  public Set<? extends ImmutableAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public Set<HiddenApiRestriction> getHiddenApiRestrictions() {
    return hiddenApiRestrictions;
  }

  @Override
  public MethodImplementation getImplementation() {
    return methodImplementation;
  }
}
