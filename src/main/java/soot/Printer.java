package soot;

import com.google.common.base.Optional;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;

import soot.asm.AsmUtil;
import soot.options.Options;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationDefaultTag;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.DeprecatedTag;
import soot.tagkit.Host;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.DeterministicHashMap;
import soot.util.StringTools;

/**
 * Prints out a class and all its methods.
 */
public class Printer {

  public static final int USE_ABBREVIATIONS = 0x0001, ADD_JIMPLE_LN = 0x0010;

  private int options = 0;
  private int jimpleLnNum = 0; // actual line number
  private Function<Body, LabeledUnitPrinter> customUnitPrinter;
  private Function<SootClass, String> customClassSignaturePrinter;
  private Function<SootMethod, String> customMethodSignaturePrinter;
  private static final String DUMMY_NAME = "generatedName=";

  public Printer(Singletons.Global g) {
  }

  public static Printer v() {
    return G.v().soot_Printer();
  }

  public boolean useAbbreviations() {
    return (options & USE_ABBREVIATIONS) != 0;
  }

  public boolean addJimpleLn() {
    return (options & ADD_JIMPLE_LN) != 0;
  }

  public void setOption(int opt) {
    options |= opt;
  }

  public void clearOption(int opt) {
    options &= ~opt;
  }

  public int getJimpleLnNum() {
    return jimpleLnNum;
  }

  public void setJimpleLnNum(int newVal) {
    jimpleLnNum = newVal;
  }

  public void incJimpleLnNum() {
    jimpleLnNum++;
    // logger.debug("jimple Ln Num: " + jimpleLnNum);
  }

  public void printTo(SootClass cl, PrintWriter out) {
    // add jimple line number tags
    setJimpleLnNum(1);

    // Print class name + modifiers
    {
      printAnnotations(out, cl.getTags());
      StringBuilder sb = new StringBuilder();
      for (StringTokenizer st = new StringTokenizer(Modifier.toString(cl.getModifiers())); st.hasMoreTokens();) {
        String tok = st.nextToken();
        if (!cl.isInterface() || !"abstract".equals(tok)) {
          sb.append(tok).append(' ');
        }
      }
      sb.append(cl.isInterface() ? " " : "class ").append(printSignature(cl));
      out.print(sb.toString());
    }

    // Print extension
    if (cl.hasSuperclass()) {
      out.print(" extends " + printSignature(cl.getSuperclass()));
    }

    // Print interfaces
    {
      Iterator<SootClass> interfaceIt = cl.getInterfaces().iterator();
      if (interfaceIt.hasNext()) {
        out.print(" implements " + printSignature(interfaceIt.next()));
        while (interfaceIt.hasNext()) {
          out.print(", " + printSignature(interfaceIt.next()));
        }
      }
    }

    out.println();
    incJimpleLnNum();
    // if (!addJimpleLn()) {
    // for (Tag t : cl.getTags()) {
    // out.println(t);
    // }
    // }
    out.println('{');
    incJimpleLnNum();
    final boolean printTagsInOutput = Options.v().print_tags_in_output();
    if (printTagsInOutput) {
      for (Tag t : cl.getTags()) {
        out.println("/*" + t.toString() + "*/");
      }
    }

    // Print fields
    for (SootField f : cl.getFields()) {
      printAnnotations(out, f.getTags());
      if (!f.isPhantom()) {
        if (printTagsInOutput) {
          for (Tag t : f.getTags()) {
            out.println("/*" + t.toString() + "*/");
          }
        }
        out.println("    " + f.getQuotedDeclaration() + ";");
        if (addJimpleLn()) {
          setJimpleLnNum(addJimpleLnTags(getJimpleLnNum(), f));
        }
        // incJimpleLnNum();
      }
    }

    // Print methods
    {
      Iterator<SootMethod> methodIt = cl.methodIterator();
      if (methodIt.hasNext()) {
        if (cl.getMethodCount() != 0) {
          out.println();
          incJimpleLnNum();
        }

        do { // condition already checked
          SootMethod method = methodIt.next();
          printAnnotations(out, method.getTags());

          if (method.isPhantom()) {
            continue;
          }

          if (!Modifier.isAbstract(method.getModifiers()) && !Modifier.isNative(method.getModifiers())) {
            Body body = method.retrieveActiveBody(); // force loading the body
            if (body == null) { // in case we don't have it
              throw new RuntimeException("method " + method.getName() + " has no active body!");
            }
            if (printTagsInOutput) {
              for (Tag t : method.getTags()) {
                out.println("/*" + t.toString() + "*/");
              }
            }
            printTo(body, out);
          } else {
            if (printTagsInOutput) {
              for (Tag t : method.getTags()) {
                out.println("/*" + t.toString() + "*/");
              }
            }
            out.println("    " + method.getQuotedDeclaration() + ";");
            incJimpleLnNum();
          }
          if (methodIt.hasNext()) {
            out.println();
            incJimpleLnNum();
          }
        } while (methodIt.hasNext());
      }
    }
    out.println("}");
    incJimpleLnNum();
  }

  private void printAnnotations(PrintWriter out, List<Tag> tags) {
    for (Tag tag : tags) {

      if (tag instanceof InnerClassAttribute) {
        InnerClassAttribute attr = (InnerClassAttribute) tag;
        for (InnerClassTag innerClass : attr.getSpecs()) {
          printInnerClassTag(out, innerClass);
        }
      } else if (tag instanceof InnerClassTag) {
        InnerClassTag innerClass = (InnerClassTag) tag;
        printInnerClassTag(out, innerClass);
      } else if (tag instanceof VisibilityAnnotationTag) {
        final VisibilityAnnotationTag visibilityTag = (VisibilityAnnotationTag) tag;
        printAnnotationTag(out, visibilityTag);
      } else if (tag instanceof AnnotationDefaultTag) {
        final AnnotationDefaultTag annotationTag = (AnnotationDefaultTag) tag;
        out.write("@Default( ");
        printAnnotationElement(out, annotationTag.getDefaultVal());
        out.write(" )");
      } else if (tag instanceof DeprecatedTag) {
        out.write("@Deprecated");
      } else if (tag instanceof SignatureTag) {
        final SignatureTag signatureTag = (SignatureTag) tag;
        out.write("@Signature(Value=");
        out.write(soot.util.StringTools.getQuotedStringOf(signatureTag.getSignature()));
        out.write(")");
      } else if (tag instanceof AnnotationTag) {
        final AnnotationTag annotationTag = (AnnotationTag) tag;
        printAnnotationTag(out, annotationTag);
      } else {
        continue;
      }
      out.println("");
      incJimpleLnNum();
    }
  }

  private void printInnerClassTag(PrintWriter out, InnerClassTag innerClass) {
    out.write("@InnerClass(InnerClass=");
    out.write(soot.util.StringTools.getQuotedStringOf(innerClass.getInnerClass()));
    final String outerClass = innerClass.getOuterClass();

    if (outerClass != null) {
      out.write(", OuterClass=");
      out.write(soot.util.StringTools.getQuotedStringOf(outerClass));
    }
    String ns = innerClass.getShortName();
    if (ns != null) {
      out.write(", Name=");
      out.write(soot.util.StringTools.getQuotedStringOf(ns));
    }
    out.write(", AccessFlags=");
    out.write(String.valueOf(innerClass.getAccessFlags()));
    out.write(")");
  }

  private void printAnnotationTag(PrintWriter out, VisibilityAnnotationTag visTag) {
    if (!visTag.hasAnnotations())
      return;
    final String prolog = "@Visibility(retention=\"";
    final String epilog = "\", annotations= { ";

    switch (visTag.getVisibility()) {
      case AnnotationConstants.RUNTIME_INVISIBLE:
        out.append(prolog).append("CLASS").append(epilog);
        break;
      case AnnotationConstants.SOURCE_VISIBLE:
        out.append(prolog).append("SOURCE").append(epilog);
        break;
      case AnnotationConstants.RUNTIME_VISIBLE:
        out.append(prolog).append("RUNTIME").append(epilog);
        break;
    }
    boolean isFirstElem = true;
    for (AnnotationTag annotationTag : visTag.getAnnotations()) {
      if (isFirstElem)
        isFirstElem = false;
      else
        out.write(", ");

      out.write("element = ");
      printAnnotationTag(out, annotationTag);
    }
    out.write(" })");
  }

  private void printAnnotationTag(PrintWriter out, AnnotationTag tag) {
    String quoted = tag.getType();
    if (!quoted.startsWith("("))
      quoted = getJimpleTypeString(tag.getType());

    out.append("@").append(quoted);
    final Collection<AnnotationElem> elements = tag.getElems();
    if (elements.size() != 0) {
      out.write("(");
      boolean isFirstElement = true;
      for (AnnotationElem elem : elements) {
        if (isFirstElement)
          isFirstElement = false;
        else
          out.write(", ");
        printAnnotationElement(out, elem);
      }
      out.write(")");
    }
  }

  private void printAnnotationElement(PrintWriter out, AnnotationElem annotation) {
    if (annotation.getName() == null)
      out.write(DUMMY_NAME);
    else
      out.append(Scene.v().quotedNameOf(annotation.getName())).append("=");
    if (annotation instanceof AnnotationArrayElem) {
      final AnnotationArrayElem annotationArray = (AnnotationArrayElem) annotation;
      out.write('{');
      boolean isFirstElement = true;
      for (final AnnotationElem childAnnotation : annotationArray.getValues()) {
        if (isFirstElement)
          isFirstElement = false;
        else
          out.write(", ");
        printAnnotationElement(out, childAnnotation);
      }
      out.write('}');
    } else if (annotation instanceof AnnotationBooleanElem) {
      final AnnotationBooleanElem celem = (AnnotationBooleanElem) annotation;
      if (celem.getValue())
        out.write("true");
      else
        out.write("false");
    } else if (annotation instanceof AnnotationClassElem) {
      final AnnotationClassElem classElement = (AnnotationClassElem) annotation;
      printClassConstant(out, classElement.getDesc());
    } else if (annotation instanceof AnnotationDoubleElem) {
      final AnnotationDoubleElem doubleElement = (AnnotationDoubleElem) annotation;
      out.write(getDoubleConstant(doubleElement.getValue()));
    } else if (annotation instanceof AnnotationFloatElem) {
      final AnnotationFloatElem floatElement = (AnnotationFloatElem) annotation;
      out.write(getFloatConstant(floatElement.getValue()));
    } else if (annotation instanceof AnnotationIntElem) {
      final AnnotationIntElem intElement = (AnnotationIntElem) annotation;
      // I: int; B: byte; Z: boolean; C: char; S: short;
      switch (intElement.getKind()) {
        case 'C':
          String esc = StringTools.getQuotedStringOf(String.valueOf((char) intElement.getValue()));
          out.append("\'").append(esc).append("\'");
          break;
        case 'J':
          out.print(intElement.getValue());
          out.print("L");
          break;
        case 'Z':
          if (intElement.getValue() == 1)
            out.write("true");
          else
            out.write("false");
          break;
        default:
        case 'B':
          // For bytes we have no other alternative at the moment
        case 'S':
          // For shorts we have no other alternative at the moment
        case 'I':
          out.print(intElement.getValue());
      }
    } else if (annotation instanceof AnnotationLongElem) {
      final AnnotationLongElem longElement = (AnnotationLongElem) annotation;
      out.append(String.valueOf(longElement.getValue())).append("L");
    } else if (annotation instanceof AnnotationStringElem) {
      final AnnotationStringElem stringElement = (AnnotationStringElem) annotation;
      if (stringElement.getValue() == null)
        out.write("null");
      else
        out.write(soot.util.StringTools.getQuotedStringOf(stringElement.getValue()));
    } else if (annotation instanceof AnnotationEnumElem) {
      final AnnotationEnumElem enumElem = (AnnotationEnumElem) annotation;
      out.write("<");
      out.write(getJimpleTypeString(enumElem.getTypeName()));
      out.write(": ");
      out.write(getJimpleTypeString(enumElem.getTypeName()));
      out.append(" ").append(Scene.v().quotedNameOf(enumElem.getConstantName())).append(">");
    } else if (annotation instanceof AnnotationAnnotationElem) {
      final AnnotationAnnotationElem annotationElem = (AnnotationAnnotationElem) annotation;
      printAnnotationTag(out, annotationElem.getValue());
    } else
      throw new RuntimeException(
          String.format("%s - unsupported annotation type: ", getClass().getName(), annotation.toString()));
  }

  private static String getJimpleTypeString(String jni) {
    Type t = AsmUtil.toJimpleType(jni, Optional.absent());
    String array = "";
    if (t instanceof ArrayType) {
      ArrayType at = ((ArrayType) t);
      t = at.getBaseType();
      for (int i = 0; i < at.numDimensions; i++) {
        array += "[]";
      }
    }
    if (t instanceof RefType) {
      RefType rt = (RefType) t;
      return Scene.v().quotedNameOf(rt.getSootClass().getName()) + array;
    } else {
      return t + array;
    }
  }

  private String getFloatConstant(float floatValue) {
    if (Float.isInfinite(floatValue)) {
      if (floatValue > 0)
        return "#InfinityF";
      else
        return "#-InfinityF";
    }
    if (Float.isNaN(floatValue))
      return "#NaNF";
    return floatValue + "F";
  }

  private String getDoubleConstant(double doubleValue) {
    if (Double.isInfinite(doubleValue)) {
      if (doubleValue > 0)
        return "#Infinity";
      else
        return "#-Infinity";
    }
    if (Double.isNaN(doubleValue))
      return "#NaN";
    return String.valueOf(doubleValue);
  }

  private void printClassConstant(PrintWriter out, String desc) {
    out.append("class \"").append(desc).append("\"");
  }

  /**
   * Prints out the method corresponding to the {@link Body}, (declaration and body) in the textual format corresponding to
   * the IR used to encode the {@link Body}.
   *
   * @param b
   *          the Body instance to print.
   * @param out
   *          a PrintWriter instance to print to.
   */
  public void printTo(Body b, PrintWriter out) {
    // b.validate();
    out.println("    " + printSignature(b.getMethod()));
    // incJimpleLnNum();

    if (addJimpleLn()) {
      setJimpleLnNum(addJimpleLnTags(getJimpleLnNum(), b.getMethod()));
      // logger.debug("added jimple ln tag for method: " + b.getMethod().toString() + " " +
      // b.getMethod().getDeclaringClass().getName());
    } else {
      // only print tags if not printing attributes in a file
      // for (Tag t : b.getMethod().getTags()) {
      // out.println(t);
      // incJimpleLnNum();
      // }
    }

    out.println("    {");
    incJimpleLnNum();

    LabeledUnitPrinter up = getUnitPrinter(b);
    if (addJimpleLn()) {
      up.setPositionTagger(new AttributesUnitPrinter(getJimpleLnNum()));
    }
    printLocalsInBody(b, up);
    printStatementsInBody(b, out, up, new soot.toolkits.graph.BriefUnitGraph(b));

    out.println("    }");
    incJimpleLnNum();
  }

  public void setCustomUnitPrinter(Function<Body, LabeledUnitPrinter> customUnitPrinter) {
    this.customUnitPrinter = customUnitPrinter;
  }

  public void setCustomClassSignaturePrinter(Function<SootClass, String> customPrinter) {
    this.customClassSignaturePrinter = customPrinter;
  }

  public void setCustomMethodSignaturePrinter(Function<SootMethod, String> customPrinter) {
    this.customMethodSignaturePrinter = customPrinter;
  }

  private LabeledUnitPrinter getUnitPrinter(Body b) {
    if (customUnitPrinter != null) {
      return customUnitPrinter.apply(b);
    } else if (useAbbreviations()) {
      return new BriefUnitPrinter(b);
    } else {
      return new NormalUnitPrinter(b);
    }
  }

  private String printSignature(SootClass sootClass) {
    if (customClassSignaturePrinter != null) {
      return customClassSignaturePrinter.apply(sootClass);
    } else {
      return Scene.v().quotedNameOf(sootClass.getName());
    }
  }

  private String printSignature(SootMethod sootMethod) {
    if (customMethodSignaturePrinter != null) {
      return customMethodSignaturePrinter.apply(sootMethod);
    } else {
      return sootMethod.getQuotedDeclaration();
    }
  }

  /**
   * Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>.
   */
  private void printStatementsInBody(Body body, java.io.PrintWriter out, LabeledUnitPrinter up, UnitGraph unitGraph) {
    final Chain<Unit> units = body.getUnits();
    final Unit firstUnit = units.getFirst();
    for (final Unit currentStmt : units) {
      // Print appropriate header.
      {
        // Put an empty line if the previous node was a branch node, the current node is a join node
        // or the previous statement does not have body statement as a successor, or if
        // body statement has a label on it
        if (currentStmt != firstUnit) {
          List<Unit> succs = unitGraph.getSuccsOf(currentStmt);
          if (succs.size() != 1 || succs.get(0) != currentStmt || unitGraph.getPredsOf(currentStmt).size() != 1
              || up.labels().containsKey(currentStmt)) {
            up.newline();
          }
        }

        if (up.labels().containsKey(currentStmt)) {
          up.unitRef(currentStmt, true);
          up.literal(":");
          up.newline();
        }

        if (up.references().containsKey(currentStmt)) {
          up.unitRef(currentStmt, false);
        }
      }

      up.startUnit(currentStmt);
      currentStmt.toString(up);
      up.endUnit(currentStmt);

      up.literal(";");
      up.newline();

      // only print them if not generating attributes files
      // because they mess up line number
      // if (!addJimpleLn()) {
      if (Options.v().print_tags_in_output()) {
        for (Tag t : currentStmt.getTags()) {
          up.noIndent();
          up.literal("/*");
          up.literal(t.toString());
          up.literal("*/");
          up.newline();
        }
        // for (ValueBox temp : currentStmt.getUseAndDefBoxes()) {
        // for (Tag t : temp.getTags()) {
        // up.noIndent();
        // up.literal("VB Tag: " + t.toString());
        // up.newline();
        // }
        // }
      }
    }

    out.print(up.toString());
    if (addJimpleLn()) {
      setJimpleLnNum(up.getPositionTagger().getEndLn());
    }

    // Print out exceptions
    {
      Iterator<Trap> trapIt = body.getTraps().iterator();
      if (trapIt.hasNext()) {
        out.println();
        incJimpleLnNum();
        do { // condition already checked
          Trap trap = trapIt.next();
          Map<Unit, String> lbls = up.labels();
          out.println("        catch " + printSignature(trap.getException()) + " from " + lbls.get(trap.getBeginUnit())
              + " to " + lbls.get(trap.getEndUnit()) + " with " + lbls.get(trap.getHandlerUnit()) + ";");
          incJimpleLnNum();
        } while (trapIt.hasNext());
      }
    }
  }

  private int addJimpleLnTags(int lnNum, Host h) {
    h.addTag(new JimpleLineNumberTag(lnNum));
    return lnNum + 1;
  }

  /**
   * Prints the Locals in the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>.
   */
  private void printLocalsInBody(Body body, UnitPrinter up) {
    Map<Type, List<Local>> typeToLocals = new DeterministicHashMap<Type, List<Local>>(body.getLocalCount() * 2 + 1, 0.7f);

    // Collect locals
    for (Local local : body.getLocals()) {
      Type t = local.getType();
      List<Local> localList = typeToLocals.get(t);
      if (localList == null) {
        typeToLocals.put(t, localList = new ArrayList<Local>());
      }
      localList.add(local);
    }

    // Print locals
    for (Map.Entry<Type, List<Local>> e : typeToLocals.entrySet()) {
      up.type(e.getKey());
      up.literal(" ");
      for (Iterator<Local> it = e.getValue().iterator(); it.hasNext();) {
        Local l = it.next();
        up.local(l);
        if (it.hasNext()) {
          up.literal(", ");
        }
      }
      up.literal(";");
      up.newline();
    }

    if (!typeToLocals.isEmpty()) {
      up.newline();
    }
  }
}
