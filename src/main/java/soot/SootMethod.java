package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import soot.dava.DavaBody;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.util.IterableSet;
import soot.util.Numberable;
import soot.util.NumberedString;

/**
 * Soot representation of a Java method. Can be declared to belong to a SootClass. Does not contain the actual code, which
 * belongs to a Body. The getActiveBody() method points to the currently-active body.
 */
public class SootMethod extends AbstractHost implements ClassMember, Numberable, MethodOrMethodContext {
  public static final String constructorName = "<init>";
  public static final String staticInitializerName = "<clinit>";
  public static boolean DEBUG = false;
  /** Name of the current method. */
  protected String name;

  /**
   * An array of parameter types taken by this <code>SootMethod</code> object, in declaration order.
   */
  protected Type[] parameterTypes;

  /** The return type of this object. */
  protected Type returnType;

  /**
   * True when some <code>SootClass</code> object declares this <code>SootMethod</code> object.
   */
  protected boolean isDeclared;

  /** Holds the class which declares this <code>SootClass</code> method. */
  protected SootClass declaringClass;

  /**
   * Modifiers associated with this SootMethod (e.g. private, protected, etc.)
   */
  protected int modifiers;

  /** Is this method a phantom method? */
  protected boolean isPhantom = false;

  /** Declared exceptions thrown by this method. Created upon demand. */
  protected List<SootClass> exceptions = null;

  /** Active body associated with this method. */
  protected volatile Body activeBody;

  /** Tells this method how to find out where its body lives. */
  protected volatile MethodSource ms;

  /**
   * Uses methodSource to retrieve the method body in question; does not set it to be the active body.
   *
   * @param phaseName
   *          Phase name for body loading.
   */
  private Body getBodyFromMethodSource(String phaseName) {
    // We get a copy of the field value just in case another thread
    // overwrites the method source in the meantime. We then check
    // again whether we really need to load anything.
    //
    // The loader does something like this:
    // (1) <a lot of stuff>
    // (2) activeBody = ...;
    // (3) ms = null;
    //
    // We need to avoid the situation in which we don't have a body yet,
    // trigger the loader, and then another thread triggers
    // retrieveActiveBody() again. If the first loader is between
    // statements (2) and (3), we would pass the check on the body, but
    // but then find that the method source is already gone when the other
    // thread finally passes statement (3) before we attempt to use the
    // method source here.

    MethodSource ms = this.ms;

    // Method sources are not expected to be thread safe
    synchronized (this) {
      if (this.activeBody == null) {
        if (ms == null) {
          throw new RuntimeException("No method source set for method " + this.getSignature());
        }

        // Method sources are not expected to be thread safe
        return ms.getBody(this, phaseName);
      } else {
        return this.activeBody;
      }
    }
  }

  /** Sets the MethodSource of the current SootMethod. */
  public void setSource(MethodSource ms) {
    this.ms = ms;
  }

  /** Returns the MethodSource of the current SootMethod. */
  public MethodSource getSource() {
    return ms;
  }

  /**
   * Returns a hash code for this method consistent with structural equality.
   */
  public int equivHashCode() {
    return returnType.hashCode() * 101 + modifiers * 17 + name.hashCode();
  }

  /**
   * Constructs a SootMethod with the given name, parameter types and return type.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType) {
    this(name, parameterTypes, returnType, 0, Collections.<SootClass>emptyList());
  }

  /**
   * Constructs a SootMethod with the given name, parameter types, return type and modifiers.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType, int modifiers) {
    this(name, parameterTypes, returnType, modifiers, Collections.<SootClass>emptyList());
  }

  /**
   * Constructs a SootMethod with the given name, parameter types, return type, and list of thrown exceptions.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType, int modifiers,
      List<SootClass> thrownExceptions) {
    this.name = name;

    if (parameterTypes != null && !parameterTypes.isEmpty()) {
      this.parameterTypes = parameterTypes.toArray(new Type[parameterTypes.size()]);
    }

    this.returnType = returnType;
    this.modifiers = modifiers;

    if (exceptions == null && !thrownExceptions.isEmpty()) {
      exceptions = new ArrayList<SootClass>();
      this.exceptions.addAll(thrownExceptions);
      /*
       * DEBUG=true; if(DEBUG) System.out.println("Added thrown exceptions"+thrownExceptions); DEBUG=false;
       */
    }
    final Scene scene = Scene.v();
    subsignature = scene.getSubSigNumberer().findOrAdd(getSubSignature());

  }

  /** Returns the name of this method. */
  public String getName() {
    return name;
  }

  /**
   * Nomair A. Naeem , January 14th 2006 Need it for the decompiler to create a new SootMethod The SootMethod can be created
   * fine but when one tries to create a SootMethodRef there is an error because there is no declaring class set. Dava cannot
   * add the method to the class until after it has ended decompiling the remaining method (new method added is added in the
   * PackManager) It would make sense to setDeclared to true within this method too. However later when the sootMethod is
   * added it checks that the method is not set to declared (isDeclared).
   */
  public void setDeclaringClass(SootClass declClass) {
    if (declClass != null) {
      declaringClass = declClass;
      // setDeclared(true);
    }
    Scene.v().getMethodNumberer().add(this);
  }

  /** Returns the class which declares the current <code>SootMethod</code>. */
  @Override
  public SootClass getDeclaringClass() {
    if (!isDeclared) {
      throw new RuntimeException("not declared: " + getName());
    }

    return declaringClass;
  }

  public void setDeclared(boolean isDeclared) {
    this.isDeclared = isDeclared;
  }

  /**
   * Returns true when some <code>SootClass</code> object declares this <code>SootMethod</code> object.
   */
  @Override
  public boolean isDeclared() {
    return isDeclared;
  }

  /** Returns true when this <code>SootMethod</code> object is phantom. */
  @Override
  public boolean isPhantom() {
    return isPhantom;
  }

  /**
   * Returns true if this method is not phantom, abstract or native, i.e. this method can have a body.
   */

  public boolean isConcrete() {
    return !isPhantom() && !isAbstract() && !isNative();
  }

  /** Sets the phantom flag on this method. */
  @Override
  public void setPhantom(boolean value) {
    if (value) {
      if (!Scene.v().allowsPhantomRefs()) {
        throw new RuntimeException("Phantom refs not allowed");
      }
      if (declaringClass != null && !declaringClass.isPhantom()) {
        throw new RuntimeException("Declaring class would have to be phantom");
      }
    }
    isPhantom = value;
  }

  /** Sets the name of this method. */
  public void setName(String name) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    this.name = name;
    subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /**
   * Gets the modifiers of this method.
   *
   * @see soot.Modifier
   */
  @Override
  public int getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers of this method.
   *
   * @see soot.Modifier
   */
  @Override
  public void setModifiers(int modifiers) {
    if ((declaringClass != null) && (!declaringClass.isApplicationClass())) {
      throw new RuntimeException("Cannot set modifiers of a method from a non-app class!");
    }
    this.modifiers = modifiers;
  }

  /** Returns the return type of this method. */
  public Type getReturnType() {
    return returnType;
  }

  /** Sets the return type of this method. */
  public void setReturnType(Type t) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    returnType = t;
    subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /** Returns the number of parameters taken by this method. */
  public int getParameterCount() {
    return parameterTypes == null ? 0 : parameterTypes.length;
  }

  /** Gets the type of the <i>n</i>th parameter of this method. */
  public Type getParameterType(int n) {
    return parameterTypes[n];
  }

  /**
   * Returns a read-only list of the parameter types of this method.
   */
  public List<Type> getParameterTypes() {
    return parameterTypes == null ? Collections.<Type>emptyList() : Arrays.asList(parameterTypes);
  }

  /**
   * Changes the set of parameter types of this method.
   */
  public void setParameterTypes(List<Type> l) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    this.parameterTypes = l.toArray(new Type[l.size()]);
    subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /**
   * Retrieves the active body for this method.
   */
  public Body getActiveBody() {
    if (activeBody != null) {
      return activeBody;
    }

    if (declaringClass != null && declaringClass.isPhantomClass()) {
      throw new RuntimeException("cannot get active body for phantom class: " + getSignature());
    }

    // ignore empty body exceptions if we are just computing coffi metrics
    if (!soot.jbco.Main.metrics) {
      throw new RuntimeException("no active body present for method " + getSignature());
    }

    return activeBody;
  }

  /**
   * Returns the active body if present, else constructs an active body and returns that.
   *
   * If you called Scene.v().loadClassAndSupport() for a class yourself, it will not be an application class, so you cannot
   * get retrieve its active body. Please call setApplicationClass() on the relevant class.
   */

  public Body retrieveActiveBody() {
    // If we already have a body for some reason, we just take it. In this
    // case,
    // we don't care about resolving levels or whatever.
    if (hasActiveBody()) {
      return getActiveBody();
    }

    declaringClass.checkLevel(SootClass.BODIES);
    if (declaringClass.isPhantomClass()) {
      throw new RuntimeException("cannot get resident body for phantom class : " + getSignature()
          + "; maybe you want to call c.setApplicationClass() on this class!");
    }

    Body b = this.getBodyFromMethodSource("jb");
    setActiveBody(b);

    // If configured, we drop the method source to save memory
    if (Options.v().drop_bodies_after_load()) {
      ms = null;
    }

    return b;
  }

  /**
   * Sets the active body for this method.
   */
  public void setActiveBody(Body body) {
    if ((declaringClass != null) && declaringClass.isPhantomClass()) {
      throw new RuntimeException("cannot set active body for phantom class! " + this);
    }

    // If someone sets a body for a phantom method, this method then is no
    // longer phantom
    setPhantom(false);

    if (!isConcrete()) {
      throw new RuntimeException("cannot set body for non-concrete method! " + this);
    }

    if (body != null && body.getMethod() != this) {
      body.setMethod(this);
    }

    activeBody = body;
  }

  /** Returns true if this method has an active body. */
  public boolean hasActiveBody() {
    return activeBody != null;
  }

  /** Releases the active body associated with this method. */
  public void releaseActiveBody() {
    activeBody = null;
  }

  /**
   * Adds the given exception to the list of exceptions thrown by this method unless the exception is already in the list.
   */
  public void addExceptionIfAbsent(SootClass e) {
    if (!throwsException(e)) {
      addException(e);
    }
  }

  /**
   * Adds the given exception to the list of exceptions thrown by this method.
   */
  public void addException(SootClass e) {
    if (DEBUG) {
      System.out.println("Adding exception " + e);
    }

    if (exceptions == null) {
      exceptions = new ArrayList<SootClass>();
    } else if (exceptions.contains(e)) {
      throw new RuntimeException("already throws exception " + e.getName());
    }

    exceptions.add(e);
  }

  /**
   * Removes the given exception from the list of exceptions thrown by this method.
   */
  public void removeException(SootClass e) {
    if (DEBUG) {
      System.out.println("Removing exception " + e);
    }

    if (exceptions == null) {
      throw new RuntimeException("does not throw exception " + e.getName());
    }

    if (!exceptions.contains(e)) {
      throw new RuntimeException("does not throw exception " + e.getName());
    }

    exceptions.remove(e);
  }

  /** Returns true if this method throws exception <code>e</code>. */
  public boolean throwsException(SootClass e) {
    return exceptions != null && exceptions.contains(e);
  }

  public void setExceptions(List<SootClass> exceptions) {
    if (exceptions != null && !exceptions.isEmpty()) {
      this.exceptions = new ArrayList<SootClass>(exceptions);
    } else {
      this.exceptions = null;
    }
  }

  /**
   * Returns a backed list of the exceptions thrown by this method.
   */

  public List<SootClass> getExceptions() {
    if (exceptions == null) {
      exceptions = new ArrayList<SootClass>();
    }

    return exceptions;
  }

  public List<SootClass> getExceptionsUnsafe() {
    return exceptions;
  }

  /**
   * Convenience method returning true if this method is static.
   */
  @Override
  public boolean isStatic() {
    return Modifier.isStatic(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is private.
   */
  @Override
  public boolean isPrivate() {
    return Modifier.isPrivate(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is public.
   */
  @Override
  public boolean isPublic() {
    return Modifier.isPublic(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is protected.
   */
  @Override
  public boolean isProtected() {
    return Modifier.isProtected(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is abstract.
   */
  public boolean isAbstract() {
    return Modifier.isAbstract(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is final.
   */
  public boolean isFinal() {
    return Modifier.isFinal(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is native.
   */
  public boolean isNative() {
    return Modifier.isNative(this.getModifiers());
  }

  /**
   * Convenience method returning true if this method is synchronized.
   */
  public boolean isSynchronized() {
    return Modifier.isSynchronized(this.getModifiers());
  }

  /**
   *
   * @return yes if this is the main method
   */
  public boolean isMain() {
    if (isPublic() && isStatic()) {
      NumberedString main_sig = Scene.v().getSubSigNumberer().findOrAdd("void main(java.lang.String[])");
      if (main_sig.equals(subsignature)) {
        return true;
      }
    }

    return false;
  }

  /**
   *
   * @return yes, if this function is a constructor. Please not that <clinit> methods are not treated as constructors in this
   *         method.
   */
  public boolean isConstructor() {
    return name.equals(constructorName);
  }

  /**
   *
   * @return yes, if this function is a static initializer.
   */
  public boolean isStaticInitializer() {
    return name.equals(staticInitializerName);
  }

  /**
   * @return yes, if this is a class initializer or main function.
   */
  public boolean isEntryMethod() {
    if (isStatic() && subsignature.equals(VirtualCalls.v().sigClinit)) {
      return true;
    }

    return isMain();
  }

  /**
   * We rely on the JDK class recognition to decide if a method is JDK method.
   */
  public boolean isJavaLibraryMethod() {
    SootClass cl = getDeclaringClass();
    return cl.isJavaLibraryClass();
  }

  /**
   * Returns the parameters part of the signature in the format in which it appears in bytecode.
   */
  public String getBytecodeParms() {
    StringBuffer buffer = new StringBuffer();
    for (Iterator<Type> typeIt = getParameterTypes().iterator(); typeIt.hasNext();) {
      final Type type = typeIt.next();
      buffer.append(AbstractJasminClass.jasminDescriptorOf(type));
    }
    return buffer.toString().intern();
  }

  /**
   * Returns the signature of this method in the format in which it appears in bytecode (eg. [Ljava/lang/Object instead of
   * java.lang.Object[]).
   */
  public String getBytecodeSignature() {
    String name = getName();

    StringBuffer buffer = new StringBuffer();
    buffer.append("<" + Scene.v().quotedNameOf(getDeclaringClass().getName()) + ": ");
    buffer.append(name);
    buffer.append(AbstractJasminClass.jasminDescriptorOf(makeRef()));
    buffer.append(">");

    return buffer.toString().intern();
  }

  /**
   * Returns the Soot signature of this method. Used to refer to methods unambiguously.
   */
  public String getSignature() {
    return getSignature(getDeclaringClass(), getName(), getParameterTypes(), getReturnType());
  }

  public static String getSignature(SootClass cl, String name, List<Type> params, Type returnType) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("<");
    buffer.append(Scene.v().quotedNameOf(cl.getName()));
    buffer.append(": ");
    buffer.append(getSubSignatureImpl(name, params, returnType));
    buffer.append(">");

    // Again, memory-usage tweak depending on JDK implementation due
    // to Michael Pan.
    return buffer.toString().intern();
  }

  /**
   * Returns the Soot subsignature of this method. Used to refer to methods unambiguously.
   */
  public String getSubSignature() {
    String name = getName();
    List<Type> params = getParameterTypes();
    Type returnType = getReturnType();

    return getSubSignatureImpl(name, params, returnType);
  }

  public static String getSubSignature(String name, List<Type> params, Type returnType) {
    return getSubSignatureImpl(name, params, returnType);
  }

  private static String getSubSignatureImpl(String name, List<Type> params, Type returnType) {
    StringBuilder buffer = new StringBuilder();

    buffer.append(returnType.toQuotedString());

    buffer.append(" ");
    buffer.append(Scene.v().quotedNameOf(name));
    buffer.append("(");

    if (params != null) {
      for (int i = 0; i < params.size(); i++) {
        buffer.append(params.get(i).toQuotedString());
        if (i < params.size() - 1) {
          buffer.append(",");
        }
      }
    }
    buffer.append(")");

    return buffer.toString();
  }

  protected NumberedString subsignature;

  public NumberedString getNumberedSubSignature() {
    return subsignature;
  }

  /** Returns the signature of this method. */
  @Override
  public String toString() {
    return getSignature();
  }

  /*
   * TODO: Nomair A. Naeem .... 8th Feb 2006 This is really messy coding So much for modularization!! Should some day look
   * into creating the DavaDeclaration from within DavaBody
   */
  public String getDavaDeclaration() {
    if (getName().equals(staticInitializerName)) {
      return "static";
    }

    StringBuffer buffer = new StringBuffer();

    // modifiers
    StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
    if (st.hasMoreTokens()) {
      buffer.append(st.nextToken());
    }

    while (st.hasMoreTokens()) {
      buffer.append(" " + st.nextToken());
    }

    if (buffer.length() != 0) {
      buffer.append(" ");
    }

    // return type + name

    if (getName().equals(constructorName)) {
      buffer.append(getDeclaringClass().getShortJavaStyleName());
    } else {
      Type t = this.getReturnType();

      String tempString = t.toString();

      /*
       * Added code to handle RuntimeExcepotion thrown by getActiveBody
       */
      if (hasActiveBody()) {
        DavaBody body = (DavaBody) getActiveBody();
        IterableSet<String> importSet = body.getImportList();

        if (!importSet.contains(tempString)) {
          body.addToImportList(tempString);
        }
        tempString = RemoveFullyQualifiedName.getReducedName(importSet, tempString, t);
      }

      buffer.append(tempString + " ");

      buffer.append(Scene.v().quotedNameOf(this.getName()));
    }

    buffer.append("(");

    // parameters
    Iterator<Type> typeIt = this.getParameterTypes().iterator();
    int count = 0;
    while (typeIt.hasNext()) {
      Type t = typeIt.next();
      String tempString = t.toString();

      /*
       * Nomair A. Naeem 7th Feb 2006 It is nice to remove the fully qualified type names of parameters if the package they
       * belong to have been imported javax.swing.ImageIcon should be just ImageIcon if javax.swing is imported If not
       * imported WHY NOT..import it!!
       */
      if (hasActiveBody()) {
        DavaBody body = (DavaBody) getActiveBody();
        IterableSet<String> importSet = body.getImportList();

        if (!importSet.contains(tempString)) {
          body.addToImportList(tempString);
        }
        tempString = RemoveFullyQualifiedName.getReducedName(importSet, tempString, t);
      }

      buffer.append(tempString + " ");

      buffer.append(" ");
      if (hasActiveBody()) {
        buffer.append(((DavaBody) getActiveBody()).get_ParamMap().get(new Integer(count++)));
      } else {
        if (t == BooleanType.v()) {
          buffer.append("z" + count++);
        } else if (t == ByteType.v()) {
          buffer.append("b" + count++);
        } else if (t == ShortType.v()) {
          buffer.append("s" + count++);
        } else if (t == CharType.v()) {
          buffer.append("c" + count++);
        } else if (t == IntType.v()) {
          buffer.append("i" + count++);
        } else if (t == LongType.v()) {
          buffer.append("l" + count++);
        } else if (t == DoubleType.v()) {
          buffer.append("d" + count++);
        } else if (t == FloatType.v()) {
          buffer.append("f" + count++);
        } else if (t == StmtAddressType.v()) {
          buffer.append("a" + count++);
        } else if (t == ErroneousType.v()) {
          buffer.append("e" + count++);
        } else if (t == NullType.v()) {
          buffer.append("n" + count++);
        } else {
          buffer.append("r" + count++);
        }
      }

      if (typeIt.hasNext()) {
        buffer.append(", ");
      }

    }

    buffer.append(")");

    // Print exceptions
    if (exceptions != null) {
      Iterator<SootClass> exceptionIt = this.getExceptions().iterator();

      if (exceptionIt.hasNext()) {
        buffer.append(" throws " + exceptionIt.next().getName());

        while (exceptionIt.hasNext()) {
          buffer.append(", " + exceptionIt.next().getName());
        }
      }
    }

    return buffer.toString().intern();
  }

  /**
   * Returns the declaration of this method, as used at the top of textual body representations (before the {}'s containing
   * the code for representation.)
   */
  public String getDeclaration() {
    StringBuffer buffer = new StringBuffer();

    // modifiers
    StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
    if (st.hasMoreTokens()) {
      buffer.append(st.nextToken());
    }

    while (st.hasMoreTokens()) {
      buffer.append(" " + st.nextToken());
    }

    if (buffer.length() != 0) {
      buffer.append(" ");
    }

    // return type + name

    buffer.append(this.getReturnType().toQuotedString() + " ");
    buffer.append(Scene.v().quotedNameOf(this.getName()));

    buffer.append("(");

    // parameters
    Iterator<Type> typeIt = this.getParameterTypes().iterator();
    // int count = 0;
    while (typeIt.hasNext()) {
      Type t = typeIt.next();

      buffer.append(t.toQuotedString());

      if (typeIt.hasNext()) {
        buffer.append(", ");
      }

    }

    buffer.append(")");

    // Print exceptions
    if (exceptions != null) {
      Iterator<SootClass> exceptionIt = this.getExceptions().iterator();

      if (exceptionIt.hasNext()) {
        buffer.append(" throws " + Scene.v().quotedNameOf(exceptionIt.next().getName()));

        while (exceptionIt.hasNext()) {
          buffer.append(", " + Scene.v().quotedNameOf(exceptionIt.next().getName()));
        }
      }
    }

    return buffer.toString().intern();
  }

  @Override
  public final int getNumber() {
    return number;
  }

  @Override
  public final void setNumber(int number) {
    this.number = number;
  }

  protected int number = 0;

  @Override
  public SootMethod method() {
    return this;
  }

  @Override
  public Context context() {
    return null;
  }

  public SootMethodRef makeRef() {
    return Scene.v().makeMethodRef(declaringClass, name, parameterTypes == null ? null : Arrays.asList(parameterTypes),
        returnType, isStatic());
  }

  @Override
  public int getJavaSourceStartLineNumber() {
    super.getJavaSourceStartLineNumber();
    // search statements for first line number
    if (line == -1 && hasActiveBody()) {
      PatchingChain<Unit> unit = getActiveBody().getUnits();
      for (Unit u : unit) {
        int l = u.getJavaSourceStartLineNumber();
        if (l > -1) {
          // store l-1, as method header is usually one line before
          // 1st statement
          line = l - 1;
          break;
        }
      }
    }
    return line;
  }

}
