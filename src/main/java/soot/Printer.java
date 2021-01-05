package soot;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;

import soot.options.Options;
import soot.tagkit.Host;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import soot.util.DeterministicHashMap;

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
      if (!f.isPhantom()) {
        if (printTagsInOutput) {
          for (Tag t : f.getTags()) {
            out.println("/*" + t.toString() + "*/");
          }
        }
        out.println("    " + f.getDeclaration() + ";");
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
            out.println("    " + method.getDeclaration() + ";");
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
      return sootMethod.getDeclaration();
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
