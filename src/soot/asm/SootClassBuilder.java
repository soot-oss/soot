/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2014 Raja Vallee-Rai and others
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.*;
import org.objectweb.asm.Attribute;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.tagkit.*;

/**
 * Constructs a Soot class from a visited class.
 * 
 * @author amiftah
 */
@SuppressWarnings({"unchecked","rawtypes"})
class SootClassBuilder extends ClassVisitor {

	private TagBuilder tb;
	private final SootClass klass;
	final Set deps;
	
	/**
	 * Constructs a new Soot class builder.
	 *
	 * @param klass Soot class to build.
	 */
	SootClassBuilder(SootClass klass) {
		super(Opcodes.ASM4);
		this.klass = klass;
		this.deps = new HashSet();
	}
	
	private TagBuilder getTagBuilder() {
		TagBuilder t = tb;
		if (t == null)
			t = tb = new TagBuilder(klass, this);
		return t;
	}
	
	/**
	 * Adds a dependency of the target class.
	 * @param s name, or type of class.
	 */
	void addDep(Object s) {
		deps.add(s);
	}
	
	@Override
	public void visit(int version, int access,
			String name, String signature,
			String superName, String[] interfaces) {
		name = AsmUtil.toQualifiedName(name);
		if (!name.equals(klass.getName()))
			throw new RuntimeException("Class names not equal!");
		klass.setModifiers(access & ~Opcodes.ACC_SUPER);
		if (superName != null) {
			superName = AsmUtil.toQualifiedName(superName);
			addDep(superName);
			klass.setSuperclass(SootResolver.v().makeClassRef(superName));
		}
		for (String intrf : interfaces) {
			intrf = AsmUtil.toQualifiedName(intrf);
			addDep(intrf);
			klass.addInterface(SootResolver.v().makeClassRef(intrf));
		}
		if (signature != null)
			klass.addTag(new SignatureTag(signature));
	}
	
	@Override
	public FieldVisitor visitField(int access, String name,
			String desc, String signature, Object value) {
		soot.Type type = AsmUtil.toJimpleType(desc);
		addDep(type);
		SootField field = new SootField(name, type, access);
		Tag tag;
		if (value instanceof Integer)
			tag = new IntegerConstantValueTag((Integer) value);
		else if (value instanceof Float)
			tag = new FloatConstantValueTag((Float) value);
		else if (value instanceof Long)
			tag = new LongConstantValueTag((Long) value);
		else if (value instanceof Double)
			tag = new DoubleConstantValueTag((Double) value);
		else if (value instanceof String)
			tag = new StringConstantValueTag(value.toString());
		else
			tag = null;
		if (tag != null)
			field.addTag(tag);
		if (signature != null)
			field.addTag(new SignatureTag(signature));
		klass.addField(field);
		return new FieldBuilder(field, this);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		List<SootClass> thrownExceptions;
		if (exceptions == null || exceptions.length == 0) {
			thrownExceptions = Collections.emptyList();
		} else {
			int len = exceptions.length;
			thrownExceptions = new ArrayList<SootClass>(len);
			for (int i = 0; i != len; i++) {
				String ex = AsmUtil.toQualifiedName(exceptions[i]);
				addDep(ex);
				thrownExceptions.add(SootResolver.v().makeClassRef(ex));
			}
		}
		List<soot.Type> sigTypes = AsmUtil.toJimpleDesc(desc);
		for (soot.Type type : sigTypes)
			addDep(type);
		SootMethod method = new SootMethod(name,
				sigTypes, sigTypes.remove(sigTypes.size() - 1),
				access, thrownExceptions);
		if (signature != null)
			method.addTag(new SignatureTag(signature));
		klass.addMethod(method);
		return new MethodBuilder(method, this, desc, exceptions);
	}
	
	@Override
	public void visitSource(String source, String debug) {
		if (source != null)
			klass.addTag(new SourceFileTag(source));
	}
	
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		klass.addTag(new InnerClassTag(outerName + "$" + innerName, outerName, name, access));
	}
	
	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		if (name == null) {
			owner = AsmUtil.toQualifiedName(owner);
			deps.add(owner);
			klass.setOuterClass(SootResolver.v().makeClassRef(owner));
		} else {
			klass.addTag(new EnclosingMethodTag(owner, name, desc));
		}
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return getTagBuilder().visitAnnotation(desc, visible);
	}
	
	@Override
	public void visitAttribute(Attribute attr) {
		getTagBuilder().visitAttribute(attr);
	}
}