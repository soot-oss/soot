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
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dava.DavaBody;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.dotnet.members.DotnetMethod;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.util.IterableSet;
import soot.util.NumberedString;

/**
 * Soot representation of a Java method. Can be declared to belong to a {@link SootClass}. Does not contain the actual code,
 * which belongs to a {@link Body}. The {@link #getActiveBody()} method points to the currently-active body.
 */
public class SootMethod extends AbstractHost implements ClassMember, MethodOrMethodContext, SootMethodInterface {

  private static final Logger logger = LoggerFactory.getLogger(SootMethod.class);

  public static final String constructorName = "<init>";
  public static final String staticInitializerName = "<clinit>";

  /**
   * Name of the current method.
   */
  protected String name;

  /**
   * An array of parameter types taken by this {@link SootMethod} object, in declaration order.
   */
  protected Type[] parameterTypes;

  /**
   * The return type of this object.
   */
  protected Type returnType;

  /**
   * True when some {@link SootClass} object declares this {@link SootMethod} object.
   */
  protected boolean isDeclared;

  /**
   * Holds the class which declares this <code>SootClass</code> method.
   */
  protected SootClass declaringClass;

  /**
   * Modifiers associated with this {@link SootMethod} (e.g. private, protected, etc.)
   */
  protected int modifiers;

  /**
   * Is this method a phantom method?
   */
  protected boolean isPhantom = false;

  /**
   * Declared exceptions thrown by this method. Created upon demand.
   */
  protected List<SootClass> exceptions;

  /**
   * Active body associated with this method.
   */
  protected volatile Body activeBody;

  /**
   * Tells this method how to find out where its body lives.
   */
  protected volatile MethodSource ms;

  protected volatile String sig;
  protected volatile String subSig;
  protected NumberedString subsignature;

  /**
   * Constructs a {@link SootMethod} with the given name, parameter types and return type.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType) {
    this(name, parameterTypes, returnType, 0, Collections.<SootClass>emptyList());
  }

  /**
   * Constructs a {@link SootMethod} with the given name, parameter types, return type and modifiers.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType, int modifiers) {
    this(name, parameterTypes, returnType, modifiers, Collections.<SootClass>emptyList());
  }

  /**
   * Constructs a {@link SootMethod} with the given name, parameter types, return type, and list of thrown exceptions.
   */
  public SootMethod(String name, List<Type> parameterTypes, Type returnType, int modifiers,
      List<SootClass> thrownExceptions) {
    this.name = name;

    if (parameterTypes != null && !parameterTypes.isEmpty()) {
      this.parameterTypes = parameterTypes.toArray(new Type[parameterTypes.size()]);
    }

    this.returnType = returnType;
    this.modifiers = modifiers;

    if (thrownExceptions != null && !thrownExceptions.isEmpty()) {
      this.exceptions = new ArrayList<SootClass>(thrownExceptions);
    }
    this.subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
  }

  /**
   * Returns a hash code for this method consistent with structural equality.
   */
  public int equivHashCode() {
    return returnType.hashCode() * 101 + modifiers * 17 + name.hashCode();
  }

  /**
   * Returns the name of this method.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this method.
   */
  public synchronized void setName(String name) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    this.name = name;
    this.sig = null;
    this.subSig = null;
    this.subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /**
   * Sets the declaring class
   */
  public synchronized void setDeclaringClass(SootClass declClass) {
    // We could call setDeclared here, however, when SootClass adds a method, it checks isDeclared
    // and throws an exception if set. So we currently cannot call setDeclared here.
    this.declaringClass = declClass;
    this.sig = null;
  }

  /**
   * Returns the class which declares the current {@link SootMethod}.
   */
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
   * Returns true when some {@link SootClass} object declares this {@link SootMethod} object.
   */
  @Override
  public boolean isDeclared() {
    return isDeclared;
  }

  /**
   * Returns true when this {@link SootMethod} object is phantom.
   */
  @Override
  public boolean isPhantom() {
    return isPhantom;
  }

  /**
   * Returns true if this method is not phantom, abstract or native, i.e. this method can have a body.
   */
  public boolean isConcrete() {
    return !isPhantom() && !isAbstract() && (!isNative() || Options.v().native_code());
  }

  /**
   * Sets the phantom flag on this method.
   */
  @Override
  public void setPhantom(boolean value) {
    if (value) {
      if (!Scene.v().allowsPhantomRefs()) {
        throw new RuntimeException("Phantom refs not allowed");
      }
      if (!Options.v().allow_phantom_elms() && declaringClass != null && !declaringClass.isPhantom()) {
        throw new RuntimeException("Declaring class would have to be phantom");
      }
    }
    this.isPhantom = value;
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
    this.modifiers = modifiers;
  }

  /**
   * Returns the return type of this method.
   */
  @Override
  public Type getReturnType() {
    return returnType;
  }

  /**
   * Sets the return type of this method.
   */
  public synchronized void setReturnType(Type t) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    this.returnType = t;
    this.sig = null;
    this.subSig = null;
    this.subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /**
   * Returns the number of parameters taken by this method.
   */
  public int getParameterCount() {
    return parameterTypes == null ? 0 : parameterTypes.length;
  }

  /**
   * Gets the type of the <i>n</i>th parameter of this method.
   */
  @Override
  public Type getParameterType(int n) {
    return parameterTypes[n];
  }

  /**
   * Returns a read-only list of the parameter types of this method.
   */
  @Override
  public List<Type> getParameterTypes() {
    return parameterTypes == null ? Collections.<Type>emptyList() : Arrays.asList(parameterTypes);
  }

  /**
   * Changes the set of parameter types of this method.
   */
  public synchronized void setParameterTypes(List<Type> l) {
    boolean wasDeclared = isDeclared;
    SootClass oldDeclaringClass = declaringClass;
    if (wasDeclared) {
      oldDeclaringClass.removeMethod(this);
    }
    this.parameterTypes = l.toArray(new Type[l.size()]);
    this.sig = null;
    this.subSig = null;
    this.subsignature = Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
    if (wasDeclared) {
      oldDeclaringClass.addMethod(this);
    }
  }

  /**
   * Returns the {@link MethodSource} of the current {@link SootMethod}.
   */
  public MethodSource getSource() {
    return ms;
  }

  /**
   * Sets the {@link MethodSource} of the current {@link SootMethod}.
   */
  public synchronized void setSource(MethodSource ms) {
    this.ms = ms;
  }

  /**
   * Retrieves the active body for this method.
   */
  @SuppressWarnings("deprecation")
  public Body getActiveBody() {
    // Retrieve the active body so thread changes do not affect the
    // synchronization between if the body exists and the returned body.
    // This is a quick check just in case the activeBody exists.
    Body activeBody = this.activeBody;
    if (activeBody != null) {
      return activeBody;
    }

    // Synchronize because we are operating on two fields that may be updated
    // separately otherwise.
    synchronized (this) {
      // Re-check the activeBody because things might have changed
      activeBody = this.activeBody;
      if (activeBody != null) {
        return activeBody;
      }

      if (declaringClass != null) {
        declaringClass.checkLevel(SootClass.BODIES);
      }
      if ((declaringClass != null && declaringClass.isPhantomClass()) || isPhantom()) {
        throw new RuntimeException("cannot get active body for phantom method: " + getSignature());
      }

      // ignore empty body exceptions if we are just computing coffi metrics
      if (!soot.jbco.Main.metrics) {
        throw new RuntimeException("no active body present for method " + getSignature());
      }
      return null;
    }
  }

  /**
   * Sets the active body for this method.
   */
  public synchronized void setActiveBody(Body body) {
    if ((declaringClass != null) && declaringClass.isPhantomClass()) {
      throw new RuntimeException("cannot set active body for phantom class! " + this);
    }

    // If someone sets a body for a phantom method, this method then is no
    // longer phantom
    setPhantom(false);

    if (!isConcrete()) {
      throw new RuntimeException("cannot set body for non-concrete method! " + this);
    }
    if (body != null) {
      body.setMethod(this);
    }

    this.activeBody = body;
  }

  /**
   * Returns the active body if present, else constructs an active body and returns that.
   *
   * If you called Scene.v().loadClassAndSupport() for a class yourself, it will not be an application class, so you cannot
   * get retrieve its active body. Please call {@link SootClass#setApplicationClass()} on the relevant class.
   */
  public Body retrieveActiveBody() {
    return retrieveActiveBody((b) -> {
    });
  }

  /**
   * Returns the active body if present, else constructs an active body, calls the consumer and returns the body afterward.
   *
   * If you called Scene.v().loadClassAndSupport() for a class yourself, it will not be an application class, so you cannot
   * get retrieve its active body. Please call {@link SootClass#setApplicationClass()} on the relevant class.
   *
   * @param consumer
   *          Consumer that takes in the body of the method. The consumer is only invoked if the current invocation
   *          constructs a new body and is guaranteed to terminate before the body is available to other threads.
   * @return active body of the method
   */
  public Body retrieveActiveBody(Consumer<Body> consumer) {
    // Retrieve the active body so thread changes do not affect the
    // synchronization between if the body exists and the returned body.
    // This is a quick check just in case the activeBody exists.
    Body activeBody = this.activeBody;
    if (activeBody != null) {
      return activeBody;
    }

    // Synchronize because we are operating on multiple fields that may be updated
    // separately otherwise.
    synchronized (this) {
      // Re-check the activeBody because things might have changed
      activeBody = this.activeBody;
      if (activeBody != null) {
        return activeBody;
      }

      if (declaringClass != null) {
        declaringClass.checkLevel(SootClass.BODIES);
      }
      if ((declaringClass != null && declaringClass.isPhantomClass()) || isPhantom()) {
        throw new RuntimeException("cannot get resident body for phantom method : " + this);
      }

      if (ms == null) {
        throw new RuntimeException("No method source set for method " + this);
      }

      // Method sources are not expected to be thread safe
      activeBody = ms.getBody(this, "jb");

      // Call the consumer such that clients can update any data structures, caches, etc.
      // atomically before the body is available to other threads.
      consumer.accept(activeBody);

      setActiveBody(activeBody);

      // If configured, we drop the method source to save memory
      if (Options.v().drop_bodies_after_load()) {
        ms = null;
      }
      return activeBody;
    }
  }

  /**
   * Returns true if this method has an active body.
   */
  public boolean hasActiveBody() {
    return activeBody != null;
  }

  /**
   * Releases the active body associated with this method.
   */
  public synchronized void releaseActiveBody() {
    this.activeBody = null;
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
    logger.trace("Adding exception {}", e);

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
    logger.trace("Removing exception {}", e);

    if (exceptions == null || !exceptions.contains(e)) {
      throw new RuntimeException("does not throw exception " + e.getName());
    }

    exceptions.remove(e);
  }

  /**
   * Returns true if this method throws exception <code>e</code>.
   */
  public boolean throwsException(SootClass e) {
    return exceptions != null && exceptions.contains(e);
  }

  public void setExceptions(List<SootClass> exceptions) {
    this.exceptions = (exceptions == null || exceptions.isEmpty()) ? null : new ArrayList<SootClass>(exceptions);
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
    return isPublic() && isStatic()
        && Scene.v().getSubSigNumberer()
            .findOrAdd(Options.v().src_prec() != Options.src_prec_dotnet ? "void main(java.lang.String[])"
                : DotnetMethod.MAIN_METHOD_SIGNATURE)
            .equals(subsignature);
  }

  /**
   *
   * @return yes, if this function is a constructor. Please not that <clinit> methods are not treated as constructors in this
   *         method.
   */
  public boolean isConstructor() {
    return constructorName.equals(name);
  }

  /**
   *
   * @return yes, if this function is a static initializer.
   */
  public boolean isStaticInitializer() {
    return staticInitializerName.equals(name);
  }

  /**
   * @return yes, if this is a class initializer or main function.
   */
  public boolean isEntryMethod() {
    if (isStatic() && SootMethod.staticInitializerName.equals(subsignature.getString())) {
      return true;
    }
    return isMain();
  }

  /**
   * We rely on the JDK class recognition to decide if a method is JDK method.
   */
  public boolean isJavaLibraryMethod() {
    return getDeclaringClass().isJavaLibraryClass();
  }

  /**
   * Returns the parameters part of the signature in the format in which it appears in bytecode.
   */
  public String getBytecodeParms() {
    StringBuilder buffer = new StringBuilder();
    for (Type type : getParameterTypes()) {
      buffer.append(AbstractJasminClass.jasminDescriptorOf(type));
    }
    return buffer.toString().intern();
  }

  /**
   * Returns the signature of this method in the format in which it appears in bytecode (eg. [Ljava/lang/Object instead of
   * java.lang.Object[]).
   */
  public String getBytecodeSignature() {
    StringBuilder buffer = new StringBuilder();
    buffer.append('<');
    buffer.append(Scene.v().quotedNameOf(getDeclaringClass().getName()));
    buffer.append(": ");
    buffer.append(getName());
    buffer.append(AbstractJasminClass.jasminDescriptorOf(makeRef()));
    buffer.append('>');
    return buffer.toString().intern();
  }

  /**
   * Returns the Soot signature of this method. Used to refer to methods unambiguously.
   */
  @Override
  public String getSignature() {
    String sig = this.sig;
    if (sig == null) {
      synchronized (this) {
        sig = this.sig;
        if (sig == null) {
          this.sig = sig = getSignature(getDeclaringClass(), getSubSignature());
        }
      }
    }
    return sig;
  }

  public static String getSignature(SootClass cl, String name, List<Type> params, Type returnType) {
    return getSignature(cl, getSubSignatureImpl(name, params, returnType));
  }

  public static String getSignature(SootClass cl, String subSignature) {
    StringBuilder buffer = new StringBuilder();
    buffer.append('<');
    buffer.append(Scene.v().quotedNameOf(cl.getName()));
    buffer.append(": ");
    buffer.append(subSignature);
    buffer.append('>');
    return buffer.toString();
  }

  /**
   * Returns the Soot subsignature of this method. Used to refer to methods unambiguously.
   */
  public String getSubSignature() {
    String subSig = this.subSig;
    if (subSig == null) {
      synchronized (this) {
        subSig = this.subSig;
        if (subSig == null) {
          this.subSig = subSig = getSubSignatureImpl(getName(), getParameterTypes(), getReturnType());
        }
      }
    }
    return subSig;
  }

  public static String getSubSignature(String name, List<Type> params, Type returnType) {
    return getSubSignatureImpl(name, params, returnType);
  }

  private static String getSubSignatureImpl(String name, List<Type> params, Type returnType) {
    StringBuilder buffer = new StringBuilder();

    buffer.append(returnType.toQuotedString());
    buffer.append(' ');
    buffer.append(Scene.v().quotedNameOf(name));
    buffer.append('(');
    if (params != null) {
      for (int i = 0, e = params.size(); i < e; i++) {
        if (i > 0) {
          buffer.append(',');
        }
        buffer.append(params.get(i).toQuotedString());
      }
    }
    buffer.append(')');

    return buffer.toString();
  }

  public NumberedString getNumberedSubSignature() {
    return subsignature;
  }

  /**
   * Returns the signature of this method.
   */
  @Override
  public String toString() {
    return getSignature();
  }

  /*
   * TODO: Nomair A. Naeem .... 8th Feb 2006 This is really messy coding So much for modularization!! Should some day look
   * into creating the DavaDeclaration from within DavaBody
   */
  public String getDavaDeclaration() {
    if (staticInitializerName.equals(getName())) {
      return "static";
    }

    StringBuilder buffer = new StringBuilder();

    // modifiers
    StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
    if (st.hasMoreTokens()) {
      buffer.append(st.nextToken());
      while (st.hasMoreTokens()) {
        buffer.append(' ').append(st.nextToken());
      }
    }

    if (buffer.length() != 0) {
      buffer.append(' ');
    }

    // return type + name
    if (constructorName.equals(getName())) {
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

      buffer.append(tempString).append(' ');
      buffer.append(Scene.v().quotedNameOf(this.getName()));
    }

    // parameters
    int count = 0;
    buffer.append('(');
    for (Iterator<Type> typeIt = this.getParameterTypes().iterator(); typeIt.hasNext();) {
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

      buffer.append(tempString).append(' ');
      buffer.append(' ');
      if (hasActiveBody()) {
        buffer.append(((DavaBody) getActiveBody()).get_ParamMap().get(count++));
      } else {
        if (t == BooleanType.v()) {
          buffer.append('z').append(count++);
        } else if (t == ByteType.v()) {
          buffer.append('b').append(count++);
        } else if (t == ShortType.v()) {
          buffer.append('s').append(count++);
        } else if (t == CharType.v()) {
          buffer.append('c').append(count++);
        } else if (t == IntType.v()) {
          buffer.append('i').append(count++);
        } else if (t == LongType.v()) {
          buffer.append('l').append(count++);
        } else if (t == DoubleType.v()) {
          buffer.append('d').append(count++);
        } else if (t == FloatType.v()) {
          buffer.append('f').append(count++);
        } else if (t == StmtAddressType.v()) {
          buffer.append('a').append(count++);
        } else if (t == ErroneousType.v()) {
          buffer.append('e').append(count++);
        } else if (t == NullType.v()) {
          buffer.append('n').append(count++);
        } else {
          buffer.append('r').append(count++);
        }
      }

      if (typeIt.hasNext()) {
        buffer.append(", ");
      }
    }
    buffer.append(')');

    // Print exceptions
    if (exceptions != null) {
      Iterator<SootClass> exceptionIt = this.getExceptions().iterator();
      if (exceptionIt.hasNext()) {
        buffer.append(" throws ").append(exceptionIt.next().getName());
        while (exceptionIt.hasNext()) {
          buffer.append(", ").append(exceptionIt.next().getName());
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
    StringBuilder buffer = new StringBuilder();

    // modifiers
    StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
    if (st.hasMoreTokens()) {
      buffer.append(st.nextToken());
      while (st.hasMoreTokens()) {
        buffer.append(' ').append(st.nextToken());
      }
    }

    if (buffer.length() != 0) {
      buffer.append(' ');
    }

    // return type + name
    buffer.append(this.getReturnType().toQuotedString()).append(' ');
    buffer.append(Scene.v().quotedNameOf(this.getName()));

    // parameters
    buffer.append('(');
    for (Iterator<Type> typeIt = this.getParameterTypes().iterator(); typeIt.hasNext();) {
      Type t = typeIt.next();

      buffer.append(t.toQuotedString());
      if (typeIt.hasNext()) {
        buffer.append(", ");
      }
    }
    buffer.append(')');

    // Print exceptions
    if (exceptions != null) {
      Iterator<SootClass> exceptionIt = this.getExceptions().iterator();
      if (exceptionIt.hasNext()) {
        buffer.append(" throws ").append(Scene.v().quotedNameOf(exceptionIt.next().getName()));
        while (exceptionIt.hasNext()) {
          buffer.append(", ").append(Scene.v().quotedNameOf(exceptionIt.next().getName()));
        }
      }
    }

    return buffer.toString().intern();
  }

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

  public boolean isValidResolve(SootMethodRef ref) {
    return (this.isStatic() == ref.isStatic()) && Objects.equals(this.getDeclaringClass(), ref.getDeclaringClass())
        && Objects.equals(this.getName(), ref.getName()) && Objects.equals(this.getReturnType(), ref.getReturnType())
        && Objects.equals(this.getParameterTypes(), ref.getParameterTypes());
  }

  @Override
  public int getJavaSourceStartLineNumber() {
    super.getJavaSourceStartLineNumber();
    // search statements for first line number
    if (line == -1 && hasActiveBody()) {
      for (Unit u : getActiveBody().getUnits()) {
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
