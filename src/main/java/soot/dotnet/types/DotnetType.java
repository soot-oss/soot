package soot.dotnet.types;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import com.google.common.base.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.dotnet.AssemblyFile;
import soot.dotnet.members.DotnetEvent;
import soot.dotnet.members.DotnetField;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.DotnetProperty;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.specifications.DotnetAttributeArgument;
import soot.dotnet.specifications.DotnetModifier;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.DeprecatedTag;

/**
 * Represents a .NET Type SourceLocator -> ClassProvider -> ClassSource (DotnetType) -> MethodSource
 */
public class DotnetType {
  private static final Logger logger = LoggerFactory.getLogger(DotnetType.class);
  private final ProtoAssemblyAllTypes.TypeDefinition typeDefinition;

  public DotnetType(ProtoAssemblyAllTypes.TypeDefinition typeDefinition, File assemblyFile) {
    if (typeDefinition == null) {
      throw new NullPointerException("Passed Type Definition is null!");
    }
    this.typeDefinition = typeDefinition;
    if (!(assemblyFile instanceof AssemblyFile)) {
      throw new RuntimeException("Given File object is no assembly file!");
    }
    this.assemblyFile = (AssemblyFile) assemblyFile;
  }

  /**
   * This DotnetType is part of this assembly
   */
  private final AssemblyFile assemblyFile;

  /**
   * Resolve this .NET Type to a SootClass
   *
   * @param sootClass
   *          SootClass to fill with information
   * @return dependencies which this type depend on (base class, implemented interfaces, method calls, etc.)
   */
  public Dependencies resolveSootClass(SootClass sootClass) {
    Dependencies dependencies = new Dependencies();

    resolveModifier(sootClass);
    resolveSuperclassInterfaces(sootClass, dependencies);

    // if this class is nested class
    resolveOuterClass(sootClass, dependencies);

    // members
    resolveFields(sootClass);
    resolveMethods(sootClass);
    resolveProperties(sootClass);
    resolveEvents(sootClass);

    // attributes
    resolveAttributes(sootClass);

    return dependencies;
  }

  private void resolveModifier(SootClass sootClass) {
    sootClass.setModifiers(DotnetModifier.toSootModifier(typeDefinition));
  }

  private void resolveSuperclassInterfaces(SootClass sootClass, Dependencies deps) {
    // interfaces / superclass
    for (ProtoAssemblyAllTypes.TypeDefinition baseType : typeDefinition.getDirectBaseTypesList()) {
      if (baseType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.CLASS)) {
        SootClass superClass = SootResolver.v().makeClassRef(baseType.getFullname());
        sootClass.setSuperclass(superClass);
        deps.typesToHierarchy.add(superClass.getType());
      }
      if (baseType.getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.INTERFACE)) {
        SootClass superClass = SootResolver.v().makeClassRef(baseType.getFullname());
        // Due to Generics, duplicates can occur - no duplicates
        if (sootClass.getInterfaces().stream().noneMatch(x -> x.getName().equals(baseType.getFullname()))) {
          sootClass.addInterface(superClass);
          deps.typesToHierarchy.add(superClass.getType());
        }
      }
    }
  }

  private void resolveOuterClass(SootClass declaringClass, Dependencies deps) {
    // outer class, if exists
    if (!Strings.isNullOrEmpty(typeDefinition.getDeclaringOuterClass())) {
      SootClass outerClass = SootResolver.v().makeClassRef(typeDefinition.getDeclaringOuterClass());
      declaringClass.setOuterClass(outerClass);
      deps.typesToHierarchy.add(outerClass.getType());
    }
  }

  private void resolveFields(SootClass declaringClass) {
    for (ProtoAssemblyAllTypes.FieldDefinition field : typeDefinition.getFieldsList()) {
      DotnetField dotnetField = new DotnetField(field);
      SootField sootField = dotnetField.makeSootField();
      if (declaringClass.declaresField(sootField.getSubSignature())) {
        continue;
      }
      declaringClass.addField(sootField);
    }
  }

  /**
   * Visit Method Header of a dotnet class and generate sootMethod
   *
   * @param declaringClass
   */
  private void resolveMethods(SootClass declaringClass) {
    for (ProtoAssemblyAllTypes.MethodDefinition method : typeDefinition.getMethodsList()) {
      DotnetMethod dotnetMethod = new DotnetMethod(method, declaringClass);

      // ignore unsafe or call-by-ref params methods if parameter is set
      if (!Options.v().resolve_all_dotnet_methods() && (method.getIsUnsafe()
          // getIsUnsafe is not working right, due to the "to do" in the Soot.Dotnet.Decompiler project
          || method.getName().equals("InternalCopy") && declaringClass.getName().equals("System.String"))) {
        continue;
      }

      SootMethod sootMethod = dotnetMethod.toSootMethod();
      // prevent duplicates due to compatibility with same signatures such as uint and int
      if (declaringClass.declaresMethod(sootMethod.getName(), sootMethod.getParameterTypes(), sootMethod.getReturnType())) {
        return;
      }
      declaringClass.addMethod(sootMethod);
    }
  }

  private void resolveProperties(SootClass declaringClass) {
    // properties of this class
    for (ProtoAssemblyAllTypes.PropertyDefinition property : typeDefinition.getPropertiesList()) {
      DotnetProperty dotnetProperty = new DotnetProperty(property, declaringClass);
      if (dotnetProperty.getCanGet()) {
        SootMethod getter = dotnetProperty.makeSootMethodGetter();
        if (getter == null
            || declaringClass.declaresMethod(getter.getName(), getter.getParameterTypes(), getter.getReturnType())) {
          continue;
        }
        declaringClass.addMethod(getter);
      }
      if (dotnetProperty.getCanSet()) {
        SootMethod setter = dotnetProperty.makeSootMethodSetter();
        if (setter == null
            || declaringClass.declaresMethod(setter.getName(), setter.getParameterTypes(), setter.getReturnType())) {
          continue;
        }
        declaringClass.addMethod(setter);
      }
    }
  }

  private void resolveEvents(SootClass declaringClass) {
    // events of this class
    for (ProtoAssemblyAllTypes.EventDefinition eventDefinition : typeDefinition.getEventsList()) {
      loadEvent(declaringClass, eventDefinition);
    }
  }

  private void loadEvent(SootClass declaringClass, ProtoAssemblyAllTypes.EventDefinition protoEvent) {
    // helper method
    DotnetEvent dotnetEvent = new DotnetEvent(protoEvent, declaringClass);
    if (dotnetEvent.getCanAdd()) {
      SootMethod getter = dotnetEvent.makeSootMethodAdd();
      if (declaringClass.declaresMethod(getter.getName(), getter.getParameterTypes(), getter.getReturnType())) {
        return;
      }
      declaringClass.addMethod(getter);
    }
    if (dotnetEvent.getCanInvoke()) {
      SootMethod setter = dotnetEvent.makeSootMethodInvoke();
      if (declaringClass.declaresMethod(setter.getName(), setter.getParameterTypes(), setter.getReturnType())) {
        return;
      }
      declaringClass.addMethod(setter);
    }
    if (dotnetEvent.getCanRemove()) {
      SootMethod setter = dotnetEvent.makeSootMethodRemove();
      if (declaringClass.declaresMethod(setter.getName(), setter.getParameterTypes(), setter.getReturnType())) {
        return;
      }
      declaringClass.addMethod(setter);
    }
  }

  /**
   * .NET Types can have attributes, resolve them as Jimple annotations
   * https://docs.microsoft.com/de-de/dotnet/csharp/programming-guide/concepts/attributes/
   *
   * @param declaringClass
   */
  @SuppressWarnings("DuplicatedCode")
  private void resolveAttributes(SootClass declaringClass) {
    if (typeDefinition.getAttributesCount() == 0) {
      return;
    }

    for (ProtoAssemblyAllTypes.AttributeDefinition attrMsg : typeDefinition.getAttributesList()) {
      try {
        String annotationType = attrMsg.getAttributeType().getFullname();

        // Elements
        List<AnnotationElem> elements = new ArrayList<>();
        for (ProtoAssemblyAllTypes.AttributeArgumentDefinition fixedArg : attrMsg.getFixedArgumentsList()) {
          elements.add(DotnetAttributeArgument.toAnnotationElem(fixedArg));
        }
        for (ProtoAssemblyAllTypes.AttributeArgumentDefinition namedArg : attrMsg.getNamedArgumentsList()) {
          elements.add(DotnetAttributeArgument.toAnnotationElem(namedArg));
        }

        declaringClass.addTag(new AnnotationTag(annotationType, elements));

        if (annotationType.equals(DotnetBasicTypes.SYSTEM_OBSOLETEATTRIBUTE)) {
          declaringClass.addTag(new DeprecatedTag());
        }
      } catch (Exception ignore) {
        logger.info("Ignored", ignore);
      }
    }

  }

}
