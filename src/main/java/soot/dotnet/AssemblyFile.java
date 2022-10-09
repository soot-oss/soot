package soot.dotnet;

import com.google.common.base.Strings;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.members.DotnetEvent;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.proto.ProtoDotnetNativeHost;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.options.Options;
import soot.toolkits.scalar.Pair;

/**
 * Represents an Assembly File
 *
 * @author Thomas Schmeiduch
 */
public class AssemblyFile extends File {
  private static final Logger logger = LoggerFactory.getLogger(AssemblyFile.class);

  /**
   * Constructs a new AssemblyFile with the path to the file
   *
   * @param fullyQualifiedAssemblyPathFilename
   *          e.g. /home/user/cs/myassembly.dll
   */
  public AssemblyFile(String fullyQualifiedAssemblyPathFilename) {
    super(fullyQualifiedAssemblyPathFilename);
    this.fullyQualifiedAssemblyPathFilename = fullyQualifiedAssemblyPathFilename;
    this.pathNativeHost = Options.v().dotnet_nativehost_path();

    // load JNI library
    System.load(this.pathNativeHost);
  }

  /**
   * e.g. /home/user/cs/myassembly.dll
   */
  private final String fullyQualifiedAssemblyPathFilename;

  /**
   * all types of this assembly file
   */
  private ProtoAssemblyAllTypes.AssemblyAllTypes protoAllTypes;

  /**
   * e.g. "/Users/user/Soot.Dotnet.NativeHost/bin/Debug/libNativeHost.dylib"
   */
  private final String pathNativeHost;

  /**
   * Store state if all references of this assembly were requested. Is needed not to add basic classes to scene twice.
   */
  private boolean gotAllReferencesModuleTypes = false;

  /**
   * Get all Types of this assembly
   *
   * @return proto message with all types of this assembly
   */
  public ProtoAssemblyAllTypes.AssemblyAllTypes getAllTypes() {
    if (protoAllTypes != null) {
      return protoAllTypes;
    }

    try {
      ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder analyzerParamsBuilder
          = createAnalyzerParamsBuilder("", ProtoDotnetNativeHost.AnalyzerMethodCall.GET_ALL_TYPES);
      ProtoDotnetNativeHost.AnalyzerParamsMsg analyzerParamsMsg = analyzerParamsBuilder.build();

      byte[] protobufMessageBytes = nativeGetAllTypesMsg(pathNativeHost, analyzerParamsMsg.toByteArray());
      ProtoAssemblyAllTypes.AssemblyAllTypes a = ProtoAssemblyAllTypes.AssemblyAllTypes.parseFrom(protobufMessageBytes);
      protoAllTypes = a;
      return a;
    } catch (Exception e) {
      if (Options.v().verbose()) {
        logger.warn(getAssemblyFileName() + " has no types. Error of protobuf message: " + e.getMessage());
      }
      return null;
    }
  }

  /**
   * Get Method Body with IL Instructions
   *
   * @param className
   *          given class
   * @param method
   *          given method name
   * @return list/tree of il instructions otherwise null
   */
  public ProtoIlInstructions.IlFunctionMsg getMethodBody(String className, String method, int peToken) {
    ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder analyzerParamsBuilder
        = createAnalyzerParamsBuilder(className, ProtoDotnetNativeHost.AnalyzerMethodCall.GET_METHOD_BODY);
    Pair<String, String> methodNameSuffixPair = helperExtractMethodNameSuffix(method);
    analyzerParamsBuilder.setMethodName(methodNameSuffixPair.getO1());
    analyzerParamsBuilder.setMethodNameSuffix(methodNameSuffixPair.getO2());
    analyzerParamsBuilder.setMethodPeToken(peToken);
    ProtoDotnetNativeHost.AnalyzerParamsMsg analyzerParamsMsg = analyzerParamsBuilder.build();

    try {
      byte[] protoMsgBytes = nativeGetMethodBodyMsg(pathNativeHost, analyzerParamsMsg.toByteArray());
      return ProtoIlInstructions.IlFunctionMsg.parseFrom(protoMsgBytes);
    } catch (Exception e) {
      if (Options.v().verbose()) {
        logger.warn("Exception while getting method body of method " + className + "." + method + ": " + e.getMessage());
      }
      return null;
    }
  }

  private Pair<String, String> helperExtractMethodNameSuffix(String sootMethodName) {
    // if name mangling, extract suffix (due to cil and java bytecode differences)
    if (!(sootMethodName.contains("[[") && sootMethodName.contains("]]"))) {
      return new Pair<>(sootMethodName, "");
    }

    int startSuffix = sootMethodName.indexOf("[[");

    String suffix = sootMethodName.substring(startSuffix);
    String cilMethodName = sootMethodName.substring(0, startSuffix);
    return new Pair<>(cilMethodName, suffix);
  }

  /**
   * Get Method Body of property methods
   *
   * @param className
   *          declaring class
   * @param propertyName
   *          name of property
   * @param isSetter
   *          request setter or getter
   * @return proto message with method body
   */
  public ProtoIlInstructions.IlFunctionMsg getMethodBodyOfProperty(String className, String propertyName,
      boolean isSetter) {
    ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder analyzerParamsBuilder
        = createAnalyzerParamsBuilder(className, ProtoDotnetNativeHost.AnalyzerMethodCall.GET_METHOD_BODY_OF_PROPERTY);
    analyzerParamsBuilder.setPropertyName(propertyName);
    analyzerParamsBuilder.setPropertyIsSetter(isSetter);
    ProtoDotnetNativeHost.AnalyzerParamsMsg analyzerParamsMsg = analyzerParamsBuilder.build();

    try {
      byte[] protoMsgBytes = nativeGetMethodBodyOfPropertyMsg(pathNativeHost, analyzerParamsMsg.toByteArray());
      return ProtoIlInstructions.IlFunctionMsg.parseFrom(protoMsgBytes);
    } catch (Exception e) {
      if (Options.v().verbose()) {
        logger.warn(
            "Exception while getting method body of property " + className + "." + propertyName + ": " + e.getMessage());
        logger.warn("Return null");
      }
      return null;
    }
  }

  /**
   * Get Method Body of event methods
   *
   * @param className
   *          declaring class
   * @param eventName
   *          name of event
   * @param eventDirective
   *          method request
   * @return proto message with method body
   */
  public ProtoIlInstructions.IlFunctionMsg getMethodBodyOfEvent(String className, String eventName,
      DotnetEvent.EventDirective eventDirective) {
    // set parameter for request to Soot.Dotnet.Decompiler
    ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder analyzerParamsBuilder
        = createAnalyzerParamsBuilder(className, ProtoDotnetNativeHost.AnalyzerMethodCall.GET_METHOD_BODY_OF_EVENT);
    analyzerParamsBuilder.setEventName(eventName);
    ProtoDotnetNativeHost.EventAccessorType accessorType;
    switch (eventDirective) {
      case ADD:
        accessorType = ProtoDotnetNativeHost.EventAccessorType.ADD_ACCESSOR;
        break;
      case REMOVE:
        accessorType = ProtoDotnetNativeHost.EventAccessorType.REMOVE_ACCESSOR;
        break;
      case INVOKE:
        accessorType = ProtoDotnetNativeHost.EventAccessorType.INVOKE_ACCESSOR;
        break;
      default:
        throw new RuntimeException("Wrong Event Accessor Type!");
    }
    analyzerParamsBuilder.setEventAccessorType(accessorType);
    ProtoDotnetNativeHost.AnalyzerParamsMsg analyzerParamsMsg = analyzerParamsBuilder.build();

    try {
      byte[] protoMsgBytes = nativeGetMethodBodyOfEventMsg(pathNativeHost, analyzerParamsMsg.toByteArray());
      return ProtoIlInstructions.IlFunctionMsg.parseFrom(protoMsgBytes);
    } catch (Exception e) {
      if (Options.v().verbose()) {
        logger.warn("Exception while getting method body of event " + className + "." + eventName + ": " + e.getMessage());
      }
      return null;
    }
  }

  /**
   * Check if given file is an assembly file
   *
   * @return true if this object referenced to a file is an assembly
   */
  public boolean isAssembly() {
    return nativeIsAssembly(pathNativeHost, fullyQualifiedAssemblyPathFilename);
  }

  /**
   * Get Type definition as Proto Message
   *
   * @param className
   *          requested type
   * @return proto message with the given type definition
   */
  public ProtoAssemblyAllTypes.TypeDefinition getTypeDefinition(String className) {
    if (Strings.isNullOrEmpty(className)) {
      return null;
    }
    ProtoAssemblyAllTypes.AssemblyAllTypes allTypes = getAllTypes();
    if (allTypes == null) {
      return null;
    }
    List<ProtoAssemblyAllTypes.TypeDefinition> allTypesList = allTypes.getListOfTypesList();
    Optional<ProtoAssemblyAllTypes.TypeDefinition> c
        = allTypesList.stream().filter(x -> x.getFullname().equals(className)).findFirst();
    return c.orElse(null);
  }

  /**
   * Get all types of given assembly as a list of strings
   *
   * @return list of strings with all types
   */
  public List<String> getAllTypeNames() {
    ProtoAssemblyAllTypes.AssemblyAllTypes allTypes = getAllTypes();
    if (allTypes == null) {
      return null;
    }
    List<ProtoAssemblyAllTypes.TypeDefinition> listOfTypesList = allTypes.getListOfTypesList();
    return listOfTypesList.stream().map(ProtoAssemblyAllTypes.TypeDefinition::getFullname).collect(Collectors.toList());
  }

  /**
   * Get all module type names which are references from this assembly
   *
   * @return list of strings with all possible referenced module type names
   */
  public List<String> getAllReferencedModuleTypes() {
    ProtoAssemblyAllTypes.AssemblyAllTypes allTypes = getAllTypes();
    if (allTypes == null || gotAllReferencesModuleTypes) {
      return new ArrayList<>();
    }

    gotAllReferencesModuleTypes = true;
    return allTypes.getAllReferencedModuleTypesList();
  }

  /**
   * Helper method
   *
   * @param className
   * @param methodCall
   * @return
   */
  private ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder createAnalyzerParamsBuilder(String className,
      ProtoDotnetNativeHost.AnalyzerMethodCall methodCall) {
    ProtoDotnetNativeHost.AnalyzerParamsMsg.Builder analyzerParamsBuilder
        = ProtoDotnetNativeHost.AnalyzerParamsMsg.newBuilder();
    analyzerParamsBuilder.setAnalyzerMethodCall(methodCall);
    analyzerParamsBuilder.setAssemblyFileAbsolutePath(fullyQualifiedAssemblyPathFilename);
    analyzerParamsBuilder.setTypeReflectionName(className);
    if (Options.v().verbose() || Options.v().debug()) {
      analyzerParamsBuilder.setDebugMode(true);
    }
    return analyzerParamsBuilder;
  }

  public String getFullPath() {
    return FilenameUtils.getFullPath(fullyQualifiedAssemblyPathFilename);
  }

  public String getAssemblyFileName() {
    return FilenameUtils.getName(fullyQualifiedAssemblyPathFilename);
  }

  // --- native declarations ---

  /**
   * Get all classes of given assembly
   *
   * @param pathToNativeHost
   *          Path where Soot.Dotnet.Nativehost binary is located
   * @param disassemblerParams
   *          disassembler parameter, such as: path to assembly file, type/class name, method name
   * @return list of classes
   */
  private native byte[] nativeGetAllTypesMsg(String pathToNativeHost, byte[] disassemblerParams);

  /**
   * Get method body of given method and type (class)
   *
   * @param pathToNativeHost
   *          Path where Soot.Dotnet.Nativehost binary is located
   * @param disassemblerParams
   *          parameter, such as: path to assembly file, type/class name, method name
   * @return list/trees of il instructions
   */
  private native byte[] nativeGetMethodBodyMsg(String pathToNativeHost, byte[] disassemblerParams);

  /**
   * Get method body of getter/setter of a property
   *
   * @param pathToNativeHost
   *          Path where Soot.Dotnet.Nativehost binary is located
   * @param disassemblerParams
   *          parameter, such as: path to assembly file, type/class name, method name
   * @return byte array with requested proto message as response
   */
  private native byte[] nativeGetMethodBodyOfPropertyMsg(String pathToNativeHost, byte[] disassemblerParams);

  /**
   * Get method body of method of an event
   *
   * @param pathToNativeHost
   *          Path where Soot.Dotnet.Nativehost binary is located
   * @param disassemblerParams
   *          parameter, such as: path to assembly file, type/class name, method name
   * @return byte array with requested proto message as response
   */
  private native byte[] nativeGetMethodBodyOfEventMsg(String pathToNativeHost, byte[] disassemblerParams);

  /**
   * Universal method for getting content of Soot.Dotnet.Decompiler. Purpose of this method is that we do not need to edit
   * the bridge Soot.Dotnet.NativeHost
   *
   * @param pathToNativeHost
   *          Path where the library file of the native host is located, e.g.
   *          /Users/user/soot-dotnet/src/Soot.Dotnet.NativeHost/bin/Debug/libNativeHost.dylib
   * @param disassemblerParams
   *          parameter, such as: path to assembly file, type/class name, method name
   * @return byte array with requested proto message as response
   */
  private native byte[] nativeGetAssemblyContentMsg(String pathToNativeHost, byte[] disassemblerParams);

  /**
   * Check if given assembly file is an assembly
   *
   * @param absolutePathAssembly
   *          e.g. /home/user/cs/myassembly.dll
   * @return true if given file is assembly
   */
  private native boolean nativeIsAssembly(String pathToNativeHost, String absolutePathAssembly);

}
