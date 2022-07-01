package soot.dotnet;

import java.io.File;
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

import soot.ClassSource;
import soot.PrimType;
import soot.Scene;
import soot.SootClass;
import soot.SootResolver;
import soot.Type;
import soot.VoidType;
import soot.dotnet.proto.ProtoAssemblyAllTypes;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetFakeLdFtnType;
import soot.dotnet.types.DotnetType;
import soot.dotnet.types.DotnetTypeFactory;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

/**
 * This ClassSource provides support for SootClass resolving SourceLocator -> ClassProvider -> ClassSource -> MethodSource
 *
 * @author Thomas Schmeiduch
 */
public class DotnetClassSource extends ClassSource {
  private static final Logger logger = LoggerFactory.getLogger(DotnetClassSource.class);
  protected AssemblyFile assemblyFile;

  public DotnetClassSource(String className, File path) {
    super(className);
    // if LdFtn fake, is ok
    if (className.equals(DotnetBasicTypes.FAKE_LDFTN)) {
      return;
    }
    if (!(path instanceof AssemblyFile)) {
      throw new RuntimeException("Given File object is no assembly file!");
    }
    this.assemblyFile = (AssemblyFile) path;
  }

  /**
   * Resolve the set class with this class source
   *
   * @param sc
   *          SootClass which we will fill with relevant information
   * @return dependencies which this class depends on (other method calls or outer class or base class/implementation)
   */
  @Override
  public Dependencies resolve(SootClass sc) {
    // If Fake.LdFtn
    if (sc.getName().equals(DotnetBasicTypes.FAKE_LDFTN)) {
      return DotnetFakeLdFtnType.resolve(sc);
    }

    if (Options.v().verbose()) {
      logger.info("resolving " + className + " type definition from file " + assemblyFile.getPath());
    }

    // dependencies that might occur
    resolveSignatureDependencies();

    ProtoAssemblyAllTypes.TypeDefinition typeDefinition = assemblyFile.getTypeDefinition(sc.getName());
    DotnetType dotnetType = new DotnetType(typeDefinition, assemblyFile);

    return dotnetType.resolveSootClass(sc);
  }

  /**
   * Resolve references as basic classes in the scene which may are also dependencies
   */
  private void resolveSignatureDependencies() {
    List<String> allModuleTypesList = assemblyFile.getAllReferencedModuleTypes();
    for (String i : allModuleTypesList) {
      Type st = DotnetTypeFactory.toSootType(i);
      String sootTypeName = st.toString();
      if (!Scene.v().containsClass(sootTypeName)) {
        if (st instanceof PrimType || st instanceof VoidType) {
          // primitive types - we obviously do not want them
          // to be resolved
          continue;
        }
        SootResolver.v().makeClassRef(sootTypeName);
      }
      SootResolver.v().resolveClass(sootTypeName, SootClass.SIGNATURES);
    }
  }
}
