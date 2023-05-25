package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 - 2005 Nomair A. Naeem
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

import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.LongType;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.dava.internal.AST.ASTNode;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.options.Options;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;
import soot.util.Chain;
import soot.util.IterableSet;

public class DavaPrinter {

  public DavaPrinter(Singletons.Global g) {
  }

  public static DavaPrinter v() {
    return G.v().soot_dava_DavaPrinter();
  }

  private void printStatementsInBody(Body body, java.io.PrintWriter out) {
    if (Options.v().verbose()) {
      System.out.println("Printing " + body.getMethod().getName());
    }

    Chain<Unit> units = body.getUnits();
    if (units.size() != 1) {
      throw new RuntimeException("DavaBody AST doesn't have single root.");
    }

    UnitPrinter up = new DavaUnitPrinter((DavaBody) body);
    ((ASTNode) units.getFirst()).toString(up);
    out.print(up.toString());
  }

  public void printTo(SootClass cl, PrintWriter out) {
    // IterableSet<String> packagesUsed = new IterableSet<String>();
    IterableSet<String> importList = new IterableSet<String>();
    {
      final String curPackage = cl.getJavaPackageName();
      if (!curPackage.isEmpty()) {
        out.println("package " + curPackage + ';');
        out.println();
      }

      if (cl.hasSuperclass()) {
        SootClass superClass = cl.getSuperclass();
        importList.add(superClass.toString());
        // packagesUsed.add(superClass.getJavaPackageName());
      }

      for (SootClass sc : cl.getInterfaces()) {
        String interfacePackage = sc.toString();
        if (!importList.contains(interfacePackage)) {
          importList.add(interfacePackage);
        }
        // if (!packagesUsed.contains(interfacePackage))
        // packagesUsed.add(interfacePackage);
      }

      for (Iterator<SootMethod> methodIt = cl.methodIterator(); methodIt.hasNext();) {
        SootMethod dm = methodIt.next();

        if (dm.hasActiveBody()) {
          // packagesUsed = packagesUsed.union(((DavaBody) dm.getActiveBody()).get_PackagesUsed());
          importList = importList.union(((DavaBody) dm.getActiveBody()).getImportList());
        }

        for (SootClass sc : dm.getExceptions()) {
          String thrownPackage = sc.toString();
          if (!importList.contains(thrownPackage)) {
            importList.add(thrownPackage);
          }

          // if (!packagesUsed.contains(thrownPackage))
          // packagesUsed.add(thrownPackage);
        }
        for (Type t : dm.getParameterTypes()) {
          if (t instanceof RefType) {
            String paramPackage = ((RefType) t).getSootClass().toString();

            if (!importList.contains(paramPackage)) {
              importList.add(paramPackage);
            }

            // if (packagesUsed.contains(paramPackage) == false)
            // packagesUsed.add(paramPackage);
          }
        }

        Type t = dm.getReturnType();
        if (t instanceof RefType) {
          String returnPackage = ((RefType) t).getSootClass().toString();

          if (!importList.contains(returnPackage)) {
            importList.add(returnPackage);
          }

          // if (packagesUsed.contains(returnPackage) == false)
          // packagesUsed.add(returnPackage);
        }
      }

      for (SootField f : cl.getFields()) {
        if (!f.isPhantom()) {
          Type t = f.getType();
          if (t instanceof RefType) {
            String fieldPackage = ((RefType) t).getSootClass().toString();
            if (!importList.contains(fieldPackage)) {
              importList.add(fieldPackage);
            }
          }
        }
      }

      List<String> toImport = new ArrayList<String>();
      for (String temp : importList) {
        /*
         * dont import any file which has currentPackage.className dont import any file which starts with java.lang
         */
        // System.out.println("temp is "+temp);
        if (temp.contains("java.lang")) {
          // problem is that we need to import sub packages java.lang.ref
          // for instance if the type is java.lang.ref.WeakReference
          String tempClassName = RemoveFullyQualifiedName.getClassName(temp);
          if (temp.equals("java.lang." + tempClassName)) {
            // System.out.println("temp was not printed as it belongs to java.lang");
            continue;
          }
        }

        if ((!curPackage.isEmpty() && temp.contains(curPackage)) || cl.toString().equals(temp)) {
          continue;
        }

        // System.out.println("printing"+);
        toImport.add(temp);
      }
      /*
       * Check that we are not importing two classes with the same last name If yes then remove explicit import and import
       * the whole package else output explicit import statement
       */
      for (String temp : toImport) {
        if (RemoveFullyQualifiedName.containsMultiple(toImport.iterator(), temp, null)) {
          // there are atleast two imports with this className
          // import package add *
          if (temp.lastIndexOf('.') > -1) {
            temp = temp.substring(0, temp.lastIndexOf('.'));
            out.println("import " + temp + ".*;");
          } else {
            throw new DecompilationException("Cant find the DOT . for fullyqualified name");
          }
        } else {
          if (temp.lastIndexOf('.') == -1) {
            // dot not found this is a class belonging to this package so dont add
          } else {
            out.println("import " + temp + ';');
          }
        }
      }

      // out.println("import " + temp + ';');
      out.println();

      /*
       * if (!packagesUsed.isEmpty()) out.println();
       *
       * packagesUsed.add("java.lang"); packagesUsed.add(curPackage);
       */
      Dava.v().set_CurrentPackageContext(importList);
      // Dava.v().set_CurrentPackageContext(packagesUsed);
      Dava.v().set_CurrentPackage(curPackage);
    }

    // Print class name + modifiers
    {
      String classPrefix = Modifier.toString(cl.getModifiers()).trim();
      if (!cl.isInterface()) {
        classPrefix = (classPrefix + " class").trim();
      }
      out.print(classPrefix + " " + cl.getShortJavaStyleName());
    }

    // Print extension
    if (cl.hasSuperclass()) {
      String superClassName = cl.getSuperclass().getName();
      if (!"java.lang.Object".equals(superClassName)) {
        // Nomair Naeem 8th Feb 2006
        // Also check if the super class name is not a fully qualified name,
        // in which case if the package is imported no need for the long name.
        out.print(" extends " + RemoveFullyQualifiedName.getReducedName(importList, superClassName, cl.getType()));
      }
    }

    // Print interfaces
    {
      Iterator<SootClass> interfaceIt = cl.getInterfaces().iterator();
      if (interfaceIt.hasNext()) {
        if (cl.isInterface()) {
          out.print(" extends ");
        } else {
          out.print(" implements ");
        }

        out.print(interfaceIt.next().getName());
        while (interfaceIt.hasNext()) {
          out.print(", " + interfaceIt.next().getName());
        }
      }
    }

    out.println();
    out.println("{");

    // Print fields
    for (SootField f : cl.getFields()) {
      if (f.isPhantom()) {
        continue;
      }

      Type fieldType = f.getType();
      String qualifiers = (Modifier.toString(f.getModifiers()) + " "
          + RemoveFullyQualifiedName.getReducedName(importList, fieldType.toString(), fieldType)).trim();

      String declaration;
      if (qualifiers.isEmpty()) {
        declaration = Scene.v().quotedNameOf(f.getName());
      } else {
        declaration = qualifiers + " " + Scene.v().quotedNameOf(f.getName());
      }

      if (f.isFinal() && f.isStatic()) {
        printTags(f, declaration, out);
      } else {
        out.println("    " + declaration + ';');
      }
    }

    // Print methods
    {
      Iterator<SootMethod> methodIt = cl.methodIterator();
      if (methodIt.hasNext()) {
        if (cl.getMethodCount() != 0) {
          out.println();
        }

        do { // condition is already checked
          SootMethod method = methodIt.next();
          if (method.isPhantom()) {
            continue;
          }

          if (!Modifier.isAbstract(method.getModifiers()) && !Modifier.isNative(method.getModifiers())) {
            if (!method.hasActiveBody()) {
              throw new RuntimeException("method " + method.getName() + " has no active body!");
            } else {
              printTo(method.getActiveBody(), out);
            }

            if (methodIt.hasNext()) {
              out.println();
            }
          } else {
            // if method is abstract then print the declaration
            out.print("    ");
            out.print(method.getDavaDeclaration());
            out.println(";");

            if (methodIt.hasNext()) {
              out.println();
            }
          }
        } while (methodIt.hasNext());
      }
    }

    /*
     * January 23rd, 2006 In trying to handle the suepr class problem we need to introduce an inner class Instead of creating
     * a data structure for it we are right now just going to print it in the form of a string
     *
     * It would be interesting to later have an internal inner class structure so that we could decompile inner classes into
     * inner classes
     */

    if (G.v().SootClassNeedsDavaSuperHandlerClass.contains(cl)) {
      out.println("\n    private static class DavaSuperHandler{");
      out.println("         java.util.Vector myVector = new java.util.Vector();");
      out.println("\n         public Object get(int pos){");
      out.println("            return myVector.elementAt(pos);");
      out.println("         }");
      out.println("\n         public void store(Object obj){");
      out.println("            myVector.add(obj);");
      out.println("         }");
      out.println("    }");
    }

    out.println("}");
  }

  private void printTags(SootField f, String declaration, PrintWriter out) {
    Type fieldType = f.getType();
    if (fieldType instanceof DoubleType) {
      DoubleConstantValueTag t = (DoubleConstantValueTag) f.getTag(DoubleConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = " + t.getDoubleValue() + ';');
        return;
      }
    } else if (fieldType instanceof FloatType) {
      FloatConstantValueTag t = (FloatConstantValueTag) f.getTag(FloatConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = " + t.getFloatValue() + "f;");
        return;
      }
    } else if (fieldType instanceof LongType) {
      LongConstantValueTag t = (LongConstantValueTag) f.getTag(LongConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = " + t.getLongValue() + "l;");
        return;
      }
    } else if (fieldType instanceof CharType) {
      IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = '" + ((char) t.getIntValue()) + "';");
        return;
      }
    } else if (fieldType instanceof BooleanType) {
      IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + (t.getIntValue() == 0 ? " = false;" : " = true;"));
        return;
      }
    } else if (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) {
      IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = " + t.getIntValue() + ';');
        return;
      }
    } else {
      StringConstantValueTag t = (StringConstantValueTag) f.getTag(StringConstantValueTag.NAME);
      if (t != null) {
        out.println("    " + declaration + " = \"" + t.getStringValue() + "\";");
        return;
      }
    }
    // System.out.println("Couldn't find type of field: " + f.getDeclaration());
    out.println("    " + declaration + ';');
  }

  /**
   * Prints out the method corresponding to b Body, (declaration and body), in the textual format corresponding to the IR
   * used to encode b body.
   *
   * @param out
   *          a PrintWriter instance to print to.
   */
  private void printTo(Body b, PrintWriter out) {
    b.validate();

    out.println("    " + b.getMethod().getDavaDeclaration());
    if (Options.v().print_tags_in_output()) {
      for (Tag t : b.getMethod().getTags()) {
        out.println(t);
      }
    }
    out.println("    {");

    /*
     * The locals are now printed out from within the toString method of ASTMethodNode Nomair A Naeem 10-MARCH-2005
     */
    // printLocalsInBody(b, out);

    printStatementsInBody(b, out);
    out.println("    }");
  }
}
