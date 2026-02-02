package soot.dotnet.members;

import java.util.ArrayList;
import java.util.List;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.MethodSource;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.SourceLocator;
import soot.Type;
import soot.dotnet.AssemblyFile;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.members.method.DotnetMethodParameter;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.specifications.DotnetAttributeArgument;
import soot.dotnet.specifications.DotnetModifier;
import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.DeprecatedTag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;

/**
 * Represents a .NET Method SourceLocator -> ClassProvider -> ClassSource -> MethodSource (DotnetMethod)
 */
public class DotnetMethod extends AbstractDotnetMember {
  private static final Logger logger = LoggerFactory.getLogger(DotnetMethod.class);

  private final ProtoAssemblyAllTypes.MethodDefinition protoMethod;
  private final SootClass declaringClass;
  private SootMethod sootMethod = null;
  private final DotnetMethodType dotnetMethodType;

  /**
   * Events and Properties also contain Methods
   */
  enum DotnetMethodType {
    METHOD, PROPERTY, EVENT
  }

  public DotnetMethod(ProtoAssemblyAllTypes.MethodDefinition protoMethod, SootClass declaringClass,
      DotnetMethodType declaringMemberType) {
    if (protoMethod == null || declaringClass == null) {
      throw new NullPointerException();
    }
    this.declaringClass = declaringClass;

    if (protoMethod.getIsConstructor()) {
      // rename constructor from .ctor to <init>
      ProtoAssemblyAllTypes.MethodDefinition.Builder builder
          = ProtoAssemblyAllTypes.MethodDefinition.newBuilder(protoMethod);
      builder.setName(convertCtorName(protoMethod.getName()));
      builder.setFullName(convertCtorName(protoMethod.getFullName()));
      this.protoMethod = builder.build();
    } else {
      this.protoMethod = protoMethod;
    }

    this.dotnetMethodType = declaringMemberType;
  }

  public DotnetMethod(ProtoAssemblyAllTypes.MethodDefinition protoMethod, SootClass declaringClass) {
    this(protoMethod, declaringClass, DotnetMethodType.METHOD);
  }

  public DotnetMethod(ProtoAssemblyAllTypes.MethodDefinition protoMethod) {
    if (protoMethod.getIsConstructor()) {
      // rename constructor from .ctor to <init>
      ProtoAssemblyAllTypes.MethodDefinition.Builder builder
          = ProtoAssemblyAllTypes.MethodDefinition.newBuilder(protoMethod);
      builder.setName(convertCtorName(protoMethod.getName()));
      builder.setFullName(convertCtorName(protoMethod.getFullName()));
      this.protoMethod = builder.build();
    } else {
      this.protoMethod = protoMethod;
    }

    if (protoMethod.hasDeclaringType()) {
      this.declaringClass = SootResolver.v().makeClassRef(protoMethod.getDeclaringType().getFullname());
    } else {
      this.declaringClass = null;
    }

    this.dotnetMethodType = DotnetMethodType.METHOD;
  }

  public boolean isConstructor() {
    return protoMethod.getIsConstructor();
  }

  public boolean isStatic() {
    return protoMethod.getIsStatic();
  }

  public ProtoAssemblyAllTypes.MethodDefinition getProtoMessage() {
    return protoMethod;
  }

  public String getName() {
    return protoMethod.getName();
  }

  public ProtoAssemblyAllTypes.TypeDefinition getReturnType() {
    return protoMethod.getReturnType();
  }

  /**
   * Create a SootMethod of this .NET Method
   *
   * @return
   */
  public SootMethod toSootMethod() {
    return toSootMethod(createMethodSource());
  }

  /**
   * Create SootMethod with specific MethodSource (used by Events and Properties) Events and Properties have other sources
   * for method bodies
   *
   * @param methodSource
   * @return
   */
  public SootMethod toSootMethod(MethodSource methodSource) {
    if (sootMethod != null) {
      return sootMethod;
    }
    String name = getUniqueName();
    List<Type> parameters = DotnetMethodParameter.toSootTypeParamsList(getParameterDefinitions());
    Type return_type = DotnetTypeFactory.toSootType(getReturnType());

    // Only METHOD
    // There are unsafe methods which returns a void* (pointer to a unspecified type)
    // public unsafe void* ToPointer() { return this._value; }
    if (dotnetMethodType == DotnetMethodType.METHOD) {
      if (protoMethod.getReturnType().getTypeKind().equals(ProtoAssemblyAllTypes.TypeKindDef.POINTER)
          && protoMethod.getReturnType().getFullname().equals(DotNetBasicTypes.SYSTEM_VOID)) {
        return_type = declaringClass.getType();
      }
    }

    int modifier = DotnetModifier.toSootModifier(protoMethod);

    SootMethod sm = Scene.v().makeSootMethod(name, parameters, return_type, modifier);

    resolveMethodAttributes(sm);
    resolveMethodParameterRefType(sm);

    // if the method is abstract or native, no code needs to be transformed
    if (Modifier.isAbstract(modifier) || Modifier.isNative(modifier)
        || (Options.v().oaat() && declaringClass.resolvingLevel() <= SootClass.SIGNATURES)) {
      sootMethod = sm;
      return sm;
    }

    sm.setSource(methodSource);

    sootMethod = sm;
    return sm;
  }

  /**
   * MethodSource for .NET Method (this)
   *
   * @return
   */
  private MethodSource createMethodSource() {
    return (m, phaseName) -> {
      // Get body of method
      AssemblyFile assemblyFile = (AssemblyFile) SourceLocator.v().dexClassIndex().get(declaringClass.getName());
      ProtoIlInstructions.IlFunctionMsg ilFunctionMsg
          = assemblyFile.getMethodBody(declaringClass.getName(), m.getName(), protoMethod.getPeToken());

      Body b = this.jimplifyMethodBody(ilFunctionMsg);
      m.setActiveBody(b);

      return m.getActiveBody();
    };
  }

  /**
   * Generate Jimple Body of this Method
   *
   * @param ilFunctionMsg
   *          ProtoMsg Method Body
   * @return
   */
  public Body jimplifyMethodBody(ProtoIlInstructions.IlFunctionMsg ilFunctionMsg) {
    JimpleBody b = Jimple.v().newBody(sootMethod);
    try {
      if (ilFunctionMsg == null) {
        throw new RuntimeException("Could not resolve JimpleBody for " + dotnetMethodType.name() + " " + sootMethod.getName()
            + " declared in class " + declaringClass.getName());
      }

      // add the body of this code item
      DotnetBody methodBody = new DotnetBody(this, ilFunctionMsg);
      methodBody.jimplify(b);
    } catch (Exception e) {
      logger.warn("Error while generating jimple body of " + dotnetMethodType.name() + " " + sootMethod.getName()
          + " declared in class " + declaringClass.getName() + "!");
      logger.warn(e.getMessage());
      if (Options.v().ignore_methodsource_error()) {
        logger.warn("Ignore errors in generation due to the set parameter. Generate empty Jimple Body.");
        b = Jimple.v().newBody(sootMethod);
        DotnetBody.resolveEmptyJimpleBody(b, sootMethod);
      } else {
        throw e;
      }
    }
    return b;
  }

  /**
   * .NET Methods can have attributes, resolve them as Jimple annotations
   * https://docs.microsoft.com/de-de/dotnet/csharp/programming-guide/concepts/attributes/
   *
   * @param method
   */
  @SuppressWarnings("DuplicatedCode")
  private void resolveMethodAttributes(SootMethod method) {
    if (protoMethod.getAttributesCount() == 0) {
      return;
    }

    for (ProtoAssemblyAllTypes.AttributeDefinition attrMsg : protoMethod.getAttributesList()) {
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

        method.addTag(new AnnotationTag(annotationType, elements));

        if (annotationType.equals(DotNetBasicTypes.SYSTEM_OBSOLETEATTRIBUTE)) {
          method.addTag(new DeprecatedTag());
        }
      } catch (Exception ignore) {
        logger.info("Ignores", ignore);
      }
    }

  }

  /**
   * Visit Method Parameters and check which one is call-by-value, which one call-by-reference
   *
   * @param method
   *          e.g. MyMethod(ref param1, param2)
   */
  private void resolveMethodParameterRefType(SootMethod method) {
    // misuse VisibilityParameterAnnotationTag for marking parameters as ref
    VisibilityParameterAnnotationTag tag
        = new VisibilityParameterAnnotationTag(protoMethod.getParameterCount(), AnnotationConstants.RUNTIME_VISIBLE);
    for (ProtoAssemblyAllTypes.ParameterDefinition parameter : protoMethod.getParameterList()) {
      // if method parameter is a reference, tag this
      if (parameter.getIsRef() || parameter.getIsOut() || parameter.getIsIn()) {
        tag.addVisibilityAnnotation(new VisibilityAnnotationTag(1));
      } else {
        tag.addVisibilityAnnotation(new VisibilityAnnotationTag(0));
      }
    }
    method.addTag(tag);
  }

  /**
   * Get parameters of this Method
   *
   * @return
   */
  public List<ProtoAssemblyAllTypes.ParameterDefinition> getParameterDefinitions() {
    return protoMethod.getParameterList();
  }

  public SootClass getDeclaringClass() {
    return declaringClass;
  }

  public SootMethod getSootMethodSignature() {
    return sootMethod;
  }

  /**
   * Check if this method contains call-by-ref parameters (e.g. MyMethod(param1&))
   *
   * @return bool
   */
  public boolean hasCallByRefParameters() {
    return protoMethod.getParameterList().stream().anyMatch(x -> x.getIsIn() || x.getIsOut() || x.getIsRef());
  }

  /**
   * Check whether this method has generics or call-by-ref parameters
   *
   * @return bool
   */
  public boolean hasGenericParameters() {
    return protoMethod.getParameterList().stream().anyMatch(x -> x.getType().getFullname().contains("`"));
  }

  /**
   * Check whether this method contains CIL primitives, such as uint sbyte
   *
   * @return
   */
  public boolean hasCilPrimitiveParameters() {
    return protoMethod.getParameterList().stream()
        .anyMatch(x -> DotnetTypeFactory.listOfCilPrimitives().contains(x.getType().getFullname()));
  }

  /**
   * Process Name Mangling to achieve a unique name in the declared class Convert method name to a unique method name if it
   * has generics or call-by-ref as parameters
   *
   * @return unique name
   */
  public String getUniqueName() {
    // For now, use the actual name. Having random name portions e.g. in library code isn't a good idea.
    return getName();
    /*
     * if (!(hasGenericParameters() || hasCallByRefParameters() || hasCilPrimitiveParameters()) || isConstructor()) { return
     * getName(); }
     * 
     * return getName() + "[[" + protoMethod.getPeToken() + "]]";
     */
  }

  // --- static methods ---

  /**
   * Convert Dotnet/CLI constructor names to java byte code constructors, if available
   *
   * @param methodName
   *          dotnet constructor name
   * @return java constructor name
   */
  public static String convertCtorName(String methodName) {
    if (methodName.equals(CONSTRUCTOR_NAME)) {
      return JAVA_CONSTRUCTOR_NAME;
    }
    if (methodName.equals(STATIC_CONSTRUCTOR_NAME)) {
      return JAVA_STATIC_CONSTRUCTOR_NAME;
    }
    if (methodName.endsWith(CONSTRUCTOR_NAME)) {
      methodName = methodName.substring(0, methodName.length() - CONSTRUCTOR_NAME.length()) + JAVA_CONSTRUCTOR_NAME;
    }
    if (methodName.endsWith(STATIC_CONSTRUCTOR_NAME)) {
      methodName
          = methodName.substring(0, methodName.length() - STATIC_CONSTRUCTOR_NAME.length()) + JAVA_STATIC_CONSTRUCTOR_NAME;
    }
    methodName = methodName.replace("+", "$");
    return methodName;
  }

  public static final String STATIC_CONSTRUCTOR_NAME = ".cctor";
  public static final String CONSTRUCTOR_NAME = ".ctor";
  public static final String JAVA_STATIC_CONSTRUCTOR_NAME = "<clinit>";
  public static final String JAVA_CONSTRUCTOR_NAME = "<init>";
  public static final String DESTRUCTOR_NAME = "Finalize";

  public static final String MAIN_METHOD_SIGNATURE = "void Main(" + DotNetBasicTypes.SYSTEM_STRING + "[])";
}
