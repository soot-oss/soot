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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.InnerClassTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

/**
 * Constructs a Soot class from a visited class.
 * 
 * @author Aaloan Miftah
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class SootClassBuilder extends ClassVisitor {

	private TagBuilder tb;
	private final SootClass klass;
	final Set<soot.Type> deps;

	/**
	 * Constructs a new Soot class builder.
	 *
	 * @param klass
	 *            Soot class to build.
	 */
	SootClassBuilder(SootClass klass) {
        super(Opcodes.ASM6);
		this.klass = klass;
		this.deps = new HashSet();
	}

	private TagBuilder getTagBuilder() {
		TagBuilder t = tb;
		if (t == null)
			t = tb = new TagBuilder(klass, this);
		return t;
	}

	void addDep(String s) {
        addDep(RefType.v(AsmUtil.baseTypeName(s), Optional.fromNullable(this.klass.moduleName)));
	}

	/**
	 * Adds a dependency of the target class.
	 * 
	 * @param s
	 *            name, or type of class.
	 */
	void addDep(soot.Type s) {
		deps.add(s);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {


        //resolve module Info first
        //FIXME: check if class is  a module-info,
        // if not add the module information to it
        if (access != Opcodes.ACC_MODULE) {
            SootModuleInfo moduleInfo = null;
            //if we are in module mode
            if (Options.v().module_mode()) {
                moduleInfo = (SootModuleInfo) SootResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.fromNullable(this.klass.moduleName));
            } else {
                //FIXME: this wont work, we have to use a class reference here,
                //FIXME: the empty module name will throw an error if not created properly
                //TODO: hint create unamed module at startUp of Soot as Dummy Class via load dummy Classes in Soot (load necessaryClasses)
                //moduleInfo = (SootModuleInfo) SootResolver.v().makeClassRef(":"+"module-info");
            }
            klass.setModuleInformation(moduleInfo);
        }


        //FIXME: the name for module-info files is different
		name = AsmUtil.toQualifiedName(name);
        //FIXME: look if this works
        if (!name.equals(klass.getName()) && Options.v().verbose())
            System.err.println("Class names not equal! " + name + " != " + klass.getName());
        //	throw new RuntimeException("Class names not equal! "+name+" != "+klass.getName());
		klass.setModifiers(access & ~Opcodes.ACC_SUPER);
		if (superName != null) {
			superName = AsmUtil.toQualifiedName(superName);
            addDep(RefType.v(superName, Optional.fromNullable(this.klass.moduleName)));
            klass.setSuperclass(SootResolver.v().makeClassRef(superName, Optional.fromNullable(this.klass.moduleName)));
		}
		for (String intrf : interfaces) {
			intrf = AsmUtil.toQualifiedName(intrf);
            addDep(RefType.v(intrf, Optional.fromNullable(this.klass.moduleName)));

            SootClass interfaceClass = SootResolver.v().makeClassRef(intrf, Optional.fromNullable(this.klass.moduleName));
			interfaceClass.setModifiers(interfaceClass.getModifiers() | Modifier.INTERFACE);
			klass.addInterface(interfaceClass);
		}
		if (signature != null)
			klass.addTag(new SignatureTag(signature));
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        soot.Type type = AsmUtil.toJimpleType(desc, Optional.fromNullable(this.klass.moduleName));
		addDep(type);
		SootField field = Scene.v().makeSootField(name, type, access);
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
		field = klass.getOrAddField(field);
		return new FieldBuilder(field, this);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		List<SootClass> thrownExceptions;
		if (exceptions == null || exceptions.length == 0) {
			thrownExceptions = Collections.emptyList();
		} else {
			int len = exceptions.length;
			thrownExceptions = new ArrayList<SootClass>(len);
			for (int i = 0; i != len; i++) {
				String ex = AsmUtil.toQualifiedName(exceptions[i]);
                addDep(RefType.v(ex, Optional.fromNullable(this.klass.moduleName)));
                thrownExceptions.add(SootResolver.v().makeClassRef(ex, Optional.fromNullable(this.klass.moduleName)));
			}
		}
        List<soot.Type> sigTypes = AsmUtil.toJimpleDesc(desc, Optional.fromNullable(this.klass.moduleName));
		for (soot.Type type : sigTypes)
			addDep(type);
		SootMethod method = Scene.v().makeSootMethod(name, sigTypes, sigTypes.remove(sigTypes.size() - 1), access,
				thrownExceptions);
		if (signature != null)
			method.addTag(new SignatureTag(signature));
		method = klass.getOrAddMethod(method);
		return new MethodBuilder(method, this, desc, exceptions);
	}

	@Override
	public void visitSource(String source, String debug) {
		if (source != null)
			klass.addTag(new SourceFileTag(source));
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		klass.addTag(new InnerClassTag(name, outerName, innerName, access));


        //FIXME; hack o resovle inner Classes (e.g. java.util.stream.FindOps$FindSink$... is not resolved
        if (!(this.klass instanceof SootModuleInfo)) {
            String innerClassname = AsmUtil.toQualifiedName(name);
            deps.add(RefType.v(innerClassname, Optional.fromNullable(this.klass.getModuleInformation().getModuleName())));
        }
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {

		if (name != null)
			klass.addTag(new EnclosingMethodTag(owner, name, desc));

		owner = AsmUtil.toQualifiedName(owner);
        deps.add(RefType.v(owner, Optional.fromNullable(this.klass.getModuleInformation().getModuleName())));
        klass.setOuterClass(SootResolver.v().makeClassRef(owner, Optional.fromNullable(this.klass.getModuleInformation().getModuleName())));
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return getTagBuilder().visitAnnotation(desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		getTagBuilder().visitAttribute(attr);
	}


    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return new SootModuleInfoBuilder(name, (SootModuleInfo) this.klass, this);
    }
}
