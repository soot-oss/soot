package soot.dexpler;

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

import java.util.Iterator;

import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;

import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

/**
 * Class for loading methods from dex files
 */
public class DexClassLoader {

  /**
   * Loads a single method from a dex file
   *
   * @param method
   *          The method to load
   * @param declaringClass
   *          The class that declares the method to load
   * @param annotations
   *          The worker object for handling annotations
   * @param dexMethodFactory
   *          The factory method for creating dex methods
   */
  protected void loadMethod(Method method, SootClass declaringClass, DexAnnotation annotations, DexMethod dexMethodFactory) {
    SootMethod sm = dexMethodFactory.makeSootMethod(method);
    if (declaringClass.declaresMethod(sm.getName(), sm.getParameterTypes(), sm.getReturnType())) {
      return;
    }
    declaringClass.addMethod(sm);
    annotations.handleMethodAnnotation(sm, method);
  }

  public Dependencies makeSootClass(SootClass sc, ClassDef defItem, DexFile dexFile) {
    String superClass = defItem.getSuperclass();
    Dependencies deps = new Dependencies();

    // source file
    String sourceFile = defItem.getSourceFile();
    if (sourceFile != null) {
      sc.addTag(new SourceFileTag(sourceFile));
    }

    // super class for hierarchy level
    if (superClass != null) {
      String superClassName = Util.dottedClassName(superClass);
      SootClass sootSuperClass = SootResolver.v().makeClassRef(superClassName);
      sc.setSuperclass(sootSuperClass);
      deps.typesToHierarchy.add(sootSuperClass.getType());
    }

    // access flags
    int accessFlags = defItem.getAccessFlags();
    sc.setModifiers(accessFlags);

    // Retrieve interface names
    if (defItem.getInterfaces() != null) {
      for (String interfaceName : defItem.getInterfaces()) {
        String interfaceClassName = Util.dottedClassName(interfaceName);
        if (sc.implementsInterface(interfaceClassName)) {
          continue;
        }

        SootClass interfaceClass = SootResolver.v().makeClassRef(interfaceClassName);
        interfaceClass.setModifiers(interfaceClass.getModifiers() | Modifier.INTERFACE);
        sc.addInterface(interfaceClass);
        deps.typesToHierarchy.add(interfaceClass.getType());
      }
    }

    if (Options.v().oaat() && sc.resolvingLevel() <= SootClass.HIERARCHY) {
      return deps;
    }
    DexAnnotation da = createDexAnnotation(sc, deps);

    // get the fields of the class
    for (Field sf : defItem.getStaticFields()) {
      loadField(sc, da, sf);
    }
    for (Field f : defItem.getInstanceFields()) {
      loadField(sc, da, f);
    }

    // get the methods of the class
    DexMethod dexMethod = createDexMethodFactory(dexFile, sc);
    for (Method method : defItem.getDirectMethods()) {
      loadMethod(method, sc, da, dexMethod);
    }
    for (Method method : defItem.getVirtualMethods()) {
      loadMethod(method, sc, da, dexMethod);
    }

    da.handleClassAnnotation(defItem);

    // In contrast to Java, Dalvik associates the InnerClassAttribute
    // with the inner class, not the outer one. We need to copy the
    // tags over to correspond to the Soot semantics.
    InnerClassAttribute ica = (InnerClassAttribute) sc.getTag("InnerClassAttribute");
    if (ica != null) {
      Iterator<InnerClassTag> innerTagIt = ica.getSpecs().iterator();
      while (innerTagIt.hasNext()) {
        Tag t = innerTagIt.next();
        if (t instanceof InnerClassTag) {
          InnerClassTag ict = (InnerClassTag) t;

          // Get the outer class name
          String outer = DexInnerClassParser.getOuterClassNameFromTag(ict);
          if (outer == null) {
            // If we don't have any clue what the outer class is, we
            // just remove
            // the reference entirely
            innerTagIt.remove();
            continue;
          }

          // If the tag is already associated with the outer class,
          // we leave it as it is
          if (outer.equals(sc.getName())) {
            continue;
          }

          // Check the inner class to make sure that this tag actually
          // refers to the current class as the inner class
          String inner = ict.getInnerClass().replaceAll("/", ".");
          if (!inner.equals(sc.getName())) {
            innerTagIt.remove();
            continue;
          }

          SootClass osc = SootResolver.v().makeClassRef(outer);
          if (osc == sc) {
            if (!sc.hasOuterClass()) {
              continue;
            }
            osc = sc.getOuterClass();
          } else {
            deps.typesToHierarchy.add(osc.getType());
          }

          // Get the InnerClassAttribute of the outer class
          InnerClassAttribute icat = (InnerClassAttribute) osc.getTag("InnerClassAttribute");
          if (icat == null) {
            icat = new InnerClassAttribute();
            osc.addTag(icat);
          }

          // Transfer the tag from the inner class to the outer class
          InnerClassTag newt
              = new InnerClassTag(ict.getInnerClass(), ict.getOuterClass(), ict.getShortName(), ict.getAccessFlags());
          icat.add(newt);

          // Remove the tag from the inner class as inner classes do
          // not have these tags in the Java / Soot semantics. The
          // DexPrinter will copy it back if we do dex->dex.
          innerTagIt.remove();

          // Add the InnerClassTag to the inner class. This tag will
          // be put in an InnerClassAttribute
          // within the PackManager in method handleInnerClasses().
          if (!sc.hasTag("InnerClassTag")) {
            if (((InnerClassTag) t).getInnerClass().replaceAll("/", ".").equals(sc.toString())) {
              sc.addTag(t);
            }
          }
        }
      }
      // remove tag if empty
      if (ica.getSpecs().isEmpty()) {
        sc.getTags().remove(ica);
      }
    }

    return deps;
  }

  /**
   * Allow custom implementations to use different dex annotation implementations
   *
   * @param clazz
   * @param deps
   * @return
   */
  protected DexAnnotation createDexAnnotation(SootClass clazz, Dependencies deps) {
    return new DexAnnotation(clazz, deps);
  }

  /**
   * Allow custom implementations to use different dex method factories
   *
   * @param dexFile
   * @param sc
   * @return
   */
  protected DexMethod createDexMethodFactory(DexFile dexFile, SootClass sc) {
    return new DexMethod(dexFile, sc);
  }

  /**
   * Loads a single field from a dex file
   *
   * @param declaringClass
   *          The class that declares the method to load
   * @param annotations
   *          The worker object for handling annotations
   * @param field
   *          The field to load
   */
  protected void loadField(SootClass declaringClass, DexAnnotation annotations, Field sf) {
    if (declaringClass.declaresField(sf.getName(), DexType.toSoot(sf.getType()))) {
      return;
    }

    SootField sootField = DexField.makeSootField(sf);
    sootField = declaringClass.getOrAddField(sootField);
    annotations.handleFieldAnnotation(sootField, sf);
  }

}
