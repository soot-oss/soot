package soot.jimple.toolkits.reflection;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2010 Eric Bodden
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.tagkit.Host;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLnPosTag;

public class ReflectionTraceInfo {
  private static final Logger logger = LoggerFactory.getLogger(ReflectionTraceInfo.class);

  public enum Kind {
    ClassForName, ClassNewInstance, ConstructorNewInstance, MethodInvoke, FieldSet, FieldGet
  }

  protected final Map<SootMethod, Set<String>> classForNameReceivers;
  protected final Map<SootMethod, Set<String>> classNewInstanceReceivers;
  protected final Map<SootMethod, Set<String>> constructorNewInstanceReceivers;
  protected final Map<SootMethod, Set<String>> methodInvokeReceivers;
  protected final Map<SootMethod, Set<String>> fieldSetReceivers;
  protected final Map<SootMethod, Set<String>> fieldGetReceivers;

  public ReflectionTraceInfo(String logFile) {
    this.classForNameReceivers = new LinkedHashMap<SootMethod, Set<String>>();
    this.classNewInstanceReceivers = new LinkedHashMap<SootMethod, Set<String>>();
    this.constructorNewInstanceReceivers = new LinkedHashMap<SootMethod, Set<String>>();
    this.methodInvokeReceivers = new LinkedHashMap<SootMethod, Set<String>>();
    this.fieldSetReceivers = new LinkedHashMap<SootMethod, Set<String>>();
    this.fieldGetReceivers = new LinkedHashMap<SootMethod, Set<String>>();

    if (logFile == null) {
      throw new InternalError("Trace based refection model enabled but no trace file given!?");
    } else {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
        final Scene sc = Scene.v();
        final Set<String> ignoredKinds = new HashSet<String>();
        for (String line; (line = reader.readLine()) != null;) {
          if (line.isEmpty()) {
            continue;
          }
          final String[] portions = line.split(";", -1);
          final String kind = portions[0];
          final String target = portions[1];
          final String source = portions[2];
          final int lineNumber = portions[3].length() == 0 ? -1 : Integer.parseInt(portions[3]);

          for (SootMethod sourceMethod : inferSource(source, lineNumber)) {
            switch (kind) {
              case "Class.forName": {
                Set<String> receiverNames = classForNameReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  classForNameReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              case "Class.newInstance": {
                Set<String> receiverNames = classNewInstanceReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  classNewInstanceReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              case "Method.invoke": {
                if (!sc.containsMethod(target)) {
                  throw new RuntimeException("Unknown method for signature: " + target);
                }
                Set<String> receiverNames = methodInvokeReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  methodInvokeReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              case "Constructor.newInstance": {
                if (!sc.containsMethod(target)) {
                  throw new RuntimeException("Unknown method for signature: " + target);
                }
                Set<String> receiverNames = constructorNewInstanceReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  constructorNewInstanceReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              case "Field.set*": {
                if (!sc.containsField(target)) {
                  throw new RuntimeException("Unknown method for signature: " + target);
                }
                Set<String> receiverNames = fieldSetReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  fieldSetReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              case "Field.get*": {
                if (!sc.containsField(target)) {
                  throw new RuntimeException("Unknown method for signature: " + target);
                }
                Set<String> receiverNames = fieldGetReceivers.get(sourceMethod);
                if (receiverNames == null) {
                  fieldGetReceivers.put(sourceMethod, receiverNames = new LinkedHashSet<String>());
                }
                receiverNames.add(target);
                break;
              }
              default:
                ignoredKinds.add(kind);
                break;
            }
          }
        }
        if (!ignoredKinds.isEmpty()) {
          logger.debug("Encountered reflective calls entries of the following kinds that\ncannot currently be handled:");
          for (String kind : ignoredKinds) {
            logger.debug(kind);
          }
        }
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Trace file not found.", e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private Set<SootMethod> inferSource(String source, int lineNumber) {
    int dotIndex = source.lastIndexOf('.');
    String className = source.substring(0, dotIndex);
    String methodName = source.substring(dotIndex + 1);
    final Scene scene = Scene.v();
    if (!scene.containsClass(className)) {
      scene.addBasicClass(className, SootClass.BODIES);
      scene.loadBasicClasses();
      if (!scene.containsClass(className)) {
        throw new RuntimeException("Trace file refers to unknown class: " + className);
      }
    }

    Set<SootMethod> methodsWithRightName = new LinkedHashSet<SootMethod>();
    for (SootMethod m : scene.getSootClass(className).getMethods()) {
      if (m.isConcrete() && m.getName().equals(methodName)) {
        methodsWithRightName.add(m);
      }
    }

    if (methodsWithRightName.isEmpty()) {
      throw new RuntimeException("Trace file refers to unknown method with name " + methodName + " in Class " + className);
    } else if (methodsWithRightName.size() == 1) {
      return Collections.singleton(methodsWithRightName.iterator().next());
    } else {
      // more than one method with that name
      for (SootMethod sootMethod : methodsWithRightName) {
        if (coversLineNumber(lineNumber, sootMethod)) {
          return Collections.singleton(sootMethod);
        }
        if (sootMethod.isConcrete()) {
          if (!sootMethod.hasActiveBody()) {
            sootMethod.retrieveActiveBody();
          }
          Body body = sootMethod.getActiveBody();
          if (coversLineNumber(lineNumber, body)) {
            return Collections.singleton(sootMethod);
          }
          for (Unit u : body.getUnits()) {
            if (coversLineNumber(lineNumber, u)) {
              return Collections.singleton(sootMethod);
            }
          }
        }
      }

      // if we get here then we found no method with the right line number information;
      // be conservative and return all method that we found
      return methodsWithRightName;
    }
  }

  private boolean coversLineNumber(int lineNumber, Host host) {
    {
      SourceLnPosTag tag = (SourceLnPosTag) host.getTag(SourceLnPosTag.NAME);
      if (tag != null) {
        if (tag.startLn() <= lineNumber && tag.endLn() >= lineNumber) {
          return true;
        }
      }
    }
    {
      LineNumberTag tag = (LineNumberTag) host.getTag(LineNumberTag.NAME);
      if (tag != null) {
        if (tag.getLineNumber() == lineNumber) {
          return true;
        }
      }
    }
    return false;
  }

  public Set<String> classForNameClassNames(SootMethod container) {
    Set<String> ret = classForNameReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }

  public Set<SootClass> classForNameClasses(SootMethod container) {
    Set<SootClass> result = new LinkedHashSet<SootClass>();
    for (String className : classForNameClassNames(container)) {
      result.add(Scene.v().getSootClass(className));
    }
    return result;
  }

  public Set<String> classNewInstanceClassNames(SootMethod container) {
    Set<String> ret = classNewInstanceReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }

  public Set<SootClass> classNewInstanceClasses(SootMethod container) {
    Set<SootClass> result = new LinkedHashSet<SootClass>();
    for (String className : classNewInstanceClassNames(container)) {
      result.add(Scene.v().getSootClass(className));
    }
    return result;
  }

  public Set<String> constructorNewInstanceSignatures(SootMethod container) {
    Set<String> ret = constructorNewInstanceReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }

  public Set<SootMethod> constructorNewInstanceConstructors(SootMethod container) {
    Set<SootMethod> result = new LinkedHashSet<SootMethod>();
    for (String signature : constructorNewInstanceSignatures(container)) {
      result.add(Scene.v().getMethod(signature));
    }
    return result;
  }

  public Set<String> methodInvokeSignatures(SootMethod container) {
    Set<String> ret = methodInvokeReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }

  public Set<SootMethod> methodInvokeMethods(SootMethod container) {
    Set<SootMethod> result = new LinkedHashSet<SootMethod>();
    for (String signature : methodInvokeSignatures(container)) {
      result.add(Scene.v().getMethod(signature));
    }
    return result;
  }

  public Set<SootMethod> methodsContainingReflectiveCalls() {
    Set<SootMethod> res = new LinkedHashSet<SootMethod>();
    res.addAll(classForNameReceivers.keySet());
    res.addAll(classNewInstanceReceivers.keySet());
    res.addAll(constructorNewInstanceReceivers.keySet());
    res.addAll(methodInvokeReceivers.keySet());
    return res;
  }

  public Set<String> fieldSetSignatures(SootMethod container) {
    Set<String> ret = fieldSetReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }

  public Set<String> fieldGetSignatures(SootMethod container) {
    Set<String> ret = fieldGetReceivers.get(container);
    return (ret != null) ? ret : Collections.emptySet();
  }
}
