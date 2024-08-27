package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
 * Copyright (C) 2004 Ondrej Lhotak, Ganesh Sittampalam
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.JastAddJ.BytecodeParser;
import soot.JastAddJ.CompilationUnit;
import soot.JastAddJ.JastAddJavaParser;
import soot.JastAddJ.JavaParser;
import soot.JastAddJ.Program;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

/** Loads symbols for SootClasses from either class files or jimple files. */
public class SootResolver {
  private static final Logger logger = LoggerFactory.getLogger(SootResolver.class);

  /** Maps each resolved class to a list of all references in it. */
  protected MultiMap<SootClass, Type> classToTypesSignature = new ConcurrentHashMultiMap<SootClass, Type>();

  /** Maps each resolved class to a list of all references in it. */
  protected MultiMap<SootClass, Type> classToTypesHierarchy = new ConcurrentHashMultiMap<SootClass, Type>();

  /** SootClasses waiting to be resolved. */
  @SuppressWarnings("unchecked")
  private final Deque<SootClass>[] worklist = new Deque[4];

  private Program program = null;

  public SootResolver(Singletons.Global g) {
    worklist[SootClass.HIERARCHY] = new ArrayDeque<SootClass>();
    worklist[SootClass.SIGNATURES] = new ArrayDeque<SootClass>();
    worklist[SootClass.BODIES] = new ArrayDeque<SootClass>();
  }

  protected void initializeProgram() {
    if (Options.v().src_prec() != Options.src_prec_apk_c_j) {
      program = new Program();
      program.state().reset();

      program.initBytecodeReader(new BytecodeParser());
      program.initJavaParser(new JavaParser() {
        @Override
        public CompilationUnit parse(InputStream is, String fileName) throws IOException, beaver.Parser.Exception {
          return new JastAddJavaParser().parse(is, fileName);
        }
      });

      final soot.JastAddJ.Options options = program.options();
      options.initOptions();
      options.addKeyValueOption("-classpath");
      options.setValueForOption(Scene.v().getSootClassPath(), "-classpath");

      switch (Options.v().src_prec()) {
        case Options.src_prec_java:
          program.setSrcPrec(Program.SRC_PREC_JAVA);
          break;
        case Options.src_prec_class:
          program.setSrcPrec(Program.SRC_PREC_CLASS);
          break;
        case Options.src_prec_only_class:
          program.setSrcPrec(Program.SRC_PREC_CLASS);
          break;
        default:
          break;
      }
      program.initPaths();
    }
  }

  public static SootResolver v() {
    G g = G.v();
    if (g.soot_ModuleUtil().isInModuleMode()) {
      return g.soot_SootModuleResolver();
    } else {
      return g.soot_SootResolver();
    }
  }

  /** Returns true if we are resolving all class refs recursively. */
  protected boolean resolveEverything() {
    final Options opts = Options.v();
    if (opts.on_the_fly()) {
      return false;
    } else {
      return (opts.whole_program() || opts.whole_shimple() || opts.full_resolver()
          || opts.output_format() == Options.output_format_dava);
    }
  }

  /**
   * Returns a (possibly not yet resolved) SootClass to be used in references to a class. If/when the class is resolved, it
   * will be resolved into this SootClass.
   */
  public SootClass makeClassRef(String className) {
    if (className.length() == 0) {
      throw new RuntimeException("Classname must not be empty!");
    }
    final Scene scene = Scene.v();
    if (scene.containsClass(className)) {
      return scene.getSootClass(className);
    } else {
      SootClass newClass;
      if (className.endsWith(SootModuleInfo.MODULE_INFO)) {
        newClass = new SootModuleInfo(className, null);
      } else {
        newClass = new SootClass(className);
      }
      newClass.setResolvingLevel(SootClass.DANGLING);
      scene.addClass(newClass);
      return newClass;
    }
  }

  /**
   * Resolves the given class. Depending on the resolver settings, may decide to resolve other classes as well. If the class
   * has already been resolved, just returns the class that was already resolved.
   */
  public SootClass resolveClass(String className, int desiredLevel) {
    SootClass resolvedClass = null;
    try {
      resolvedClass = makeClassRef(className);
      addToResolveWorklist(resolvedClass, desiredLevel);
      processResolveWorklist();
      return resolvedClass;
    } catch (SootClassNotFoundException e) {
      // remove unresolved class and rethrow
      if (resolvedClass != null) {
        assert (resolvedClass.resolvingLevel() == SootClass.DANGLING);
        Scene.v().removeClass(resolvedClass);
      }
      throw e;
    }
  }

  /** Resolve all classes on toResolveWorklist. */
  protected void processResolveWorklist() {
    final Scene scene = Scene.v();
    final boolean resolveEverything = resolveEverything();
    final boolean no_bodies_for_excluded = Options.v().no_bodies_for_excluded();
    for (int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i--) {
      Deque<SootClass> currWorklist = worklist[i];
      while (!currWorklist.isEmpty()) {
        SootClass sc = currWorklist.pop();
        if (resolveEverything) {
          // Whole program mode
          boolean onlySignatures
              = sc.isPhantom() || (no_bodies_for_excluded && scene.isExcluded(sc) && !scene.isBasicClass(sc.getName()));
          if (onlySignatures) {
            bringToSignatures(sc);
            sc.setPhantomClass();
            for (SootMethod m : sc.getMethods()) {
              m.setPhantom(true);
            }
            for (SootField f : sc.getFields()) {
              f.setPhantom(true);
            }
          } else {
            bringToBodies(sc);
          }
        } else { // No transitive
          switch (i) {
            case SootClass.BODIES:
              bringToBodies(sc);
              break;
            case SootClass.SIGNATURES:
              bringToSignatures(sc);
              break;
            case SootClass.HIERARCHY:
              bringToHierarchy(sc);
              break;
          }
        }
      }
      // The ArrayDeque can grow particularly large but the implementation will
      // never shrink the backing array, leaving a possibly large memory leak.
      worklist[i] = new ArrayDeque<SootClass>(0);
    }
  }

  protected void addToResolveWorklist(Type type, int level) {
    // We go from Type -> SootClass directly, since RefType.getSootClass
    // calls makeClassRef anyway
    if (type instanceof RefType) {
      addToResolveWorklist(((RefType) type).getSootClass(), level);
    } else if (type instanceof ArrayType) {
      addToResolveWorklist(((ArrayType) type).baseType, level);
    }
    // Other types ignored
  }

  protected void addToResolveWorklist(SootClass sc, int desiredLevel) {
    if (sc.resolvingLevel() >= desiredLevel) {
      return;
    }
    worklist[desiredLevel].add(sc);
  }

  /**
   * Hierarchy - we know the hierarchy of the class and that's it requires at least Hierarchy for all supertypes and
   * enclosing types.
   */
  protected void bringToHierarchy(SootClass sc) {
    if (sc.resolvingLevel() >= SootClass.HIERARCHY) {
      return;
    }
    if (Options.v().debug_resolver()) {
      logger.debug("bringing to HIERARCHY: " + sc);
    }
    sc.setResolvingLevel(SootClass.HIERARCHY);

    bringToHierarchyUnchecked(sc);
  }

  protected void bringToHierarchyUnchecked(SootClass sc) {
    String className = sc.getName();
    ClassSource is;
    if (ModuleUtil.module_mode()) {
      is = ModulePathSourceLocator.v().getClassSource(className,
          com.google.common.base.Optional.fromNullable(sc.moduleName));
    } else {
      is = SourceLocator.v().getClassSource(className);
    }
    try {
      boolean modelAsPhantomRef = (is == null);
      if (modelAsPhantomRef) {
        if (!Scene.v().allowsPhantomRefs()) {
          String suffix = "";
          if ("java.lang.Object".equals(className)) {
            suffix = " Try adding rt.jar to Soot's classpath, e.g.:\n" + "java -cp sootclasses.jar soot.Main -cp "
                + ".:/path/to/jdk/jre/lib/rt.jar <other options>";
          } else if ("javax.crypto.Cipher".equals(className)) {
            suffix = " Try adding jce.jar to Soot's classpath, e.g.:\n" + "java -cp sootclasses.jar soot.Main -cp "
                + ".:/path/to/jdk/jre/lib/rt.jar:/path/to/jdk/jre/lib/jce.jar <other options>";
          }
          throw new SootClassNotFoundException(
              "couldn't find class: " + className + " (is your soot-class-path set properly?)" + suffix);
        } else {
          // logger.warn(className + " is a phantom class!");
          sc.setPhantomClass();
        }
      } else {
        Dependencies dependencies = is.resolve(sc);
        if (!dependencies.typesToSignature.isEmpty()) {
          classToTypesSignature.putAll(sc, dependencies.typesToSignature);
        }
        if (!dependencies.typesToHierarchy.isEmpty()) {
          classToTypesHierarchy.putAll(sc, dependencies.typesToHierarchy);
        }
      }
    } finally {
      if (is != null) {
        is.close();
      }
    }
    reResolveHierarchy(sc, SootClass.HIERARCHY);
  }

  public void reResolveHierarchy(SootClass sc, int level) {
    // Bring superclasses to hierarchy
    SootClass superClass = sc.getSuperclassUnsafe();
    if (superClass != null) {
      addToResolveWorklist(superClass, level);
    }
    SootClass outerClass = sc.getOuterClassUnsafe();
    if (outerClass != null) {
      addToResolveWorklist(outerClass, level);
    }
    for (SootClass iface : sc.getInterfaces()) {
      addToResolveWorklist(iface, level);
    }
  }

  /**
   * Signatures - we know the signatures of all methods and fields requires at least Hierarchy for all referred to types in
   * these signatures.
   */
  protected void bringToSignatures(SootClass sc) {
    if (sc.resolvingLevel() >= SootClass.SIGNATURES) {
      return;
    }
    bringToHierarchy(sc);
    if (Options.v().debug_resolver()) {
      logger.debug("bringing to SIGNATURES: " + sc);
    }
    sc.setResolvingLevel(SootClass.SIGNATURES);

    bringToSignaturesUnchecked(sc);
  }

  protected void bringToSignaturesUnchecked(SootClass sc) {
    for (SootField f : sc.getFields()) {
      addToResolveWorklist(f.getType(), SootClass.HIERARCHY);
    }
    for (SootMethod m : sc.getMethods()) {
      addToResolveWorklist(m.getReturnType(), SootClass.HIERARCHY);
      for (Type ptype : m.getParameterTypes()) {
        addToResolveWorklist(ptype, SootClass.HIERARCHY);
      }
      List<SootClass> exceptions = m.getExceptionsUnsafe();
      if (exceptions != null) {
        for (SootClass exception : exceptions) {
          addToResolveWorklist(exception, SootClass.HIERARCHY);
        }
      }
    }

    // Bring superclasses to signatures
    reResolveHierarchy(sc, SootClass.SIGNATURES);
  }

  /**
   * Bodies - we can now start loading the bodies of methods for all referred to methods and fields in the bodies, requires
   * signatures for the method receiver and field container, and hierarchy for all other classes referenced in method
   * references. Current implementation does not distinguish between the receiver and other references. Therefore, it is
   * conservative and brings all of them to signatures. But this could/should be improved.
   */
  protected void bringToBodies(SootClass sc) {
    if (sc.resolvingLevel() >= SootClass.BODIES) {
      return;
    }
    bringToSignatures(sc);
    if (Options.v().debug_resolver()) {
      logger.debug("bringing to BODIES: " + sc);
    }
    sc.setResolvingLevel(SootClass.BODIES);

    bringToBodiesUnchecked(sc);
  }

  protected void bringToBodiesUnchecked(SootClass sc) {
    {
      Collection<Type> references = classToTypesHierarchy.get(sc);
      if (references != null) {
        // This must be an iterator, not a for-all since the underlying
        // collection may change as we go
        for (Type t : references) {
          addToResolveWorklist(t, SootClass.HIERARCHY);
        }
      }
    }
    {
      Collection<Type> references = classToTypesSignature.get(sc);
      if (references != null) {
        // This must be an iterator, not a for-all since the underlying
        // collection may change as we go
        for (Type t : references) {
          addToResolveWorklist(t, SootClass.SIGNATURES);
        }
      }
    }
  }

  public void reResolve(SootClass cl, int newResolvingLevel) {
    int resolvingLevel = cl.resolvingLevel();
    if (resolvingLevel >= newResolvingLevel) {
      return;
    }
    reResolveHierarchy(cl, SootClass.HIERARCHY);
    cl.setResolvingLevel(newResolvingLevel);
    addToResolveWorklist(cl, resolvingLevel);
    processResolveWorklist();
  }

  public void reResolve(SootClass cl) {
    reResolve(cl, SootClass.HIERARCHY);
  }

  public Program getProgram() {
    if (program == null) {
      initializeProgram();
    }
    return program;
  }

  public class SootClassNotFoundException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1563461446590293827L;

    public SootClassNotFoundException(String s) {
      super(s);
    }
  }
}
