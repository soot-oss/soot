package soot;

import static soot.util.backend.ASMBackendUtils.createASMAttribute;
import static soot.util.backend.ASMBackendUtils.getDefaultValue;
import static soot.util.backend.ASMBackendUtils.slashify;
import static soot.util.backend.ASMBackendUtils.toTypeDesc;
import static soot.util.backend.ASMBackendUtils.translateJavaVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import soot.baf.BafBody;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
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
import soot.tagkit.Attribute;
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.Host;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.OuterClassTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import soot.util.backend.SootASMClassWriter;

/**
 * Abstract super-class for ASM-based back-ends. Generates byte-code for
 * everything except the method bodies, as they are dependent on the IR.
 * 
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 * 
 */
public abstract class AbstractASMBackend {

	// An ASM ClassVisitor that is used to emit the bytecode to
	protected ClassVisitor cv;
	// The SootClass that is to be converted into bytecode
	protected SootClass sc;
	// The Java version to be used for generating this class
	protected int javaVersion;
	
	private final Map<SootMethod, BafBody> bafBodyCache = new HashMap<SootMethod, BafBody>();

	/**
	 * Creates a new ASM backend
	 * @param sc The SootClass that is to be converted into bytecode
	 * @param javaVersion A particular Java version enforced by the user, may be 0 for automatic detection, must not be lower than necessary for all features used
	 */
	public AbstractASMBackend(SootClass sc, int javaVersion) {
		this.sc = sc;
		
		int minVersion = getMinJavaVersion(sc);		

		if (javaVersion == 0)
			javaVersion = Options.java_version_default;
		
		if(javaVersion != Options.java_version_default && javaVersion < minVersion){
			throw new IllegalArgumentException("Enforced Java version " + translateJavaVersion(javaVersion) + " too low to support required features (" + translateJavaVersion(minVersion) + " required)");
		}
		
		javaVersion = Math.max(javaVersion, minVersion);

		switch (javaVersion) {
		case Options.java_version_1_1:
			this.javaVersion = Opcodes.V1_1;
			break;
		case Options.java_version_1_2:
			this.javaVersion = Opcodes.V1_2;
			break;
		case Options.java_version_1_3:
			this.javaVersion = Opcodes.V1_3;
			break;
		case Options.java_version_1_4:
			this.javaVersion = Opcodes.V1_4;
			break;
		case Options.java_version_1_5:
			this.javaVersion = Opcodes.V1_5;
			break;
		case Options.java_version_1_6:
			this.javaVersion = Opcodes.V1_6;
			break;
		case Options.java_version_1_7:
			this.javaVersion = Opcodes.V1_7;
			break;
		case Options.java_version_1_8:
			this.javaVersion = Opcodes.V1_8;
			break;
		}
	}

	/**
	 * Gets the baf body for the given SootMethod. This method will first check
	 * whether the method already has a baf body. If not, it will query the local
	 * cache. If this fails as well, it will construct a new baf body.
	 * @param method The method for which to obtain a baf body
	 * @return The baf body for the given method
	 */
	protected BafBody getBafBody(SootMethod method) {
		final Body activeBody = method.getActiveBody();
		if (activeBody instanceof BafBody)
			return (BafBody) activeBody;

		BafBody body = bafBodyCache.get(method);
		if (body != null)
			return body;
		
		if (activeBody instanceof JimpleBody) {
			body = PackManager.v().convertJimpleBodyToBaf(method);
		} else {
			throw new RuntimeException(
					"ASM-backend can only translate Baf- and JimpleBodies!");
		}
		
		bafBodyCache.put(method, body);
		return body;
	}
	
	/**
	 * Determines the minimum Java version required for the bytecode of the given SootClass
	 * @param sc The SootClass the minimum Java version is to be determined for
	 * @return The minimum Java version required for the given SootClass
	 */
	private int getMinJavaVersion(SootClass sc) {
		int minVersion = Options.java_version_1_1;
		
		if(sc.hasTag("SignatureTag")){
			if(((SignatureTag)sc.getTag("SignatureTag")).getSignature().contains("<")){
				minVersion=Options.java_version_1_5;
			}
		}
		if(sc.hasTag("VisibilityAnnotationTag")){
			minVersion = Options.java_version_1_5;
		}
		if(Modifier.isAnnotation(sc.getModifiers())){
			// Class is defining an annotation
			minVersion = Options.java_version_1_5;
		}
		
		for(SootField sf : sc.getFields()){
			if(minVersion >= Options.java_version_1_5){
				break;
			}
			if(sf.hasTag("SignatureTag")){
				if(((SignatureTag)sf.getTag("SignatureTag")).getSignature().contains("<")){
					minVersion=Options.java_version_1_5;
				}
			}
			if(sf.hasTag("VisibilityAnnotationTag")){
				minVersion = Options.java_version_1_5;
			}
		}
		
		for(SootMethod sm : sc.getMethods()){
			if(minVersion >= Options.java_version_1_8){
				break;
			}
			if(sm.hasTag("SignatureTag")){
				if(((SignatureTag)sm.getTag("SignatureTag")).getSignature().contains("<")){
					minVersion = Math.max(minVersion, Options.java_version_1_5);
				}
			}
			if(sm.hasTag("VisibilityAnnotationTag") || sm.hasTag("VisibilityParameterAnnotationTag")){
				minVersion = Math.max(minVersion, Options.java_version_1_5);
			}
			if(sm.hasActiveBody()){
				minVersion = Math.max(minVersion, getMinJavaVersion(sm));
			}
		}
		return minVersion;
	}

	/**
	 * Determines the minimum Java version required for the bytecode of the given SootMethod
	 * Subclasses should override this method to suit their needs, otherwise Java 1.7 is assumed for compatibility with invokeDynamic
	 * @param sm The SootMethod the minimum Java version is to be determined for
	 * @return The minimum Java version required for the given SootMethod
	 */
	protected int getMinJavaVersion(SootMethod sm) {
		return Options.java_version_1_7;
	}

	/**
	 * Outputs the bytecode generated as a class file
	 * @param os The OutputStream the class file is written to
	 */
	public void generateClassFile(OutputStream os) {
		ClassWriter cw = new SootASMClassWriter(ClassWriter.COMPUTE_FRAMES);
		cv = cw;
		generateByteCode();
		try {
			os.write(cw.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not write class file in the ASM-backend!", e);
		}
	}

	/**
	 * Outputs the bytecode generated as a textual representation
	 * @param pw The PrintWriter the textual representation is written to
	 */
	public void generateTextualRepresentation(PrintWriter pw) {
		cv = new TraceClassVisitor(pw);
		generateByteCode();
	}

	/**
	 * Emits the bytecode for the complete class 
	 */
	protected void generateByteCode() {
		generateClassHeader();

		// Retrieve information about the source of the class
		if (sc.hasTag("SourceFileTag")
				&& !Options.v().no_output_source_file_attribute()) {
			String srcName = ((SourceFileTag) sc.getTag("SourceFileTag"))
					.getSourceFile();
			cv.visitSource(srcName, null); // TODO Correct value for the debug
											// argument
		}

		// Retrieve information about outer class if present
		if (sc.hasOuterClass() || sc.hasTag("EnclosingMethodTag")
				|| sc.hasTag("OuterClassTag")) {
			generateOuterClassReference();
		}

		// Retrieve information about annotations
		generateAnnotations(cv, sc);

		// Retrieve information about attributes
		generateAttributes();

		// Retrieve information about inner classes
		generateInnerClassReferences();

		// Generate fields
		generateFields();

		// Generate methods
		generateMethods();

		cv.visitEnd();
	}
	
	/**
	 * Comparatator that is used to sort the methods before they are written
	 * out. This is mainly used to enforce a deterministic output between runs
	 * which we need for testing.
	 * 
	 * @author Steven Arzt
	 *
	 */
	private class SootMethodComparator implements Comparator<SootMethod> {

		@Override
		public int compare(SootMethod o1, SootMethod o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}

	/**
	 * Emits the bytecode for all methods of the class
	 */
	protected void generateMethods() {
		List<SootMethod> sortedMethods = new ArrayList<SootMethod>(sc.getMethods());
		Collections.sort(sortedMethods, new SootMethodComparator());
		for (SootMethod sm : sortedMethods) {
			int access = getModifiers(sm.getModifiers(), sm);
			String name = sm.getName();
			StringBuilder descBuilder = new StringBuilder(5);
			descBuilder.append('(');
			for (Type t : sm.getParameterTypes()) {
				descBuilder.append(toTypeDesc(t));
			}
			descBuilder.append(')');
			descBuilder.append(toTypeDesc(sm.getReturnType()));

			String sig = null;
			if (sm.hasTag("SignatureTag")) {
				SignatureTag genericSignature = (SignatureTag) sm
						.getTag("SignatureTag");
				sig = genericSignature.getSignature();
			}

			String[] exceptions = new String[sm.getExceptions().size()];
			int i = 0;
			for (SootClass exc : sm.getExceptions()) {
				exceptions[i] = slashify(exc.getName());
				++i;
			}
			MethodVisitor mv = cv.visitMethod(access, name,
					descBuilder.toString(), sig, exceptions);
			if (mv != null) {
				// Visit parameter annotations
				for (Tag t : sm.getTags()) {
					if (t instanceof VisibilityParameterAnnotationTag) {
						VisibilityParameterAnnotationTag vpt = (VisibilityParameterAnnotationTag) t;
						ArrayList<VisibilityAnnotationTag> tags = vpt
								.getVisibilityAnnotations();
						if (tags != null) {
							for (int j = 0; j < tags.size(); ++j) {
								VisibilityAnnotationTag va = tags.get(j);
								if (va == null) {
									continue;
								}
								for (AnnotationTag at : va.getAnnotations()) {
									AnnotationVisitor av = mv
											.visitParameterAnnotation(
													j,
													at.getType(),
													(va.getVisibility() == AnnotationConstants.RUNTIME_VISIBLE));
									generateAnnotationElems(av, at.getElems(), true);
								}
							}
						}
					}
				}

				generateAnnotations(mv, sm);

				generateAttributes(mv, sm);
				if (sm.hasActiveBody()) {
					mv.visitCode();
					generateMethodBody(mv, sm);
					/*
					 * Correct values are computed automatically by ASM,
					 * but we need the call anyways.
					 */
					mv.visitMaxs(0, 0);
				}
				mv.visitEnd();
			}
		}
	}

	/**
	 * Emits the bytecode for all fields of the class
	 */
	protected void generateFields() {
        for (SootField f : sc.getFields()) {
            String name = f.getName();
            String desc = toTypeDesc(f.getType());
            String sig = null;
            if (f.hasTag("SignatureTag")) {
                SignatureTag genericSignature = (SignatureTag) f
                        .getTag("SignatureTag");
                sig = genericSignature.getSignature();
            }
            Object value = getDefaultValue(f);
            int access = getModifiers(f.getModifiers(), f);
            FieldVisitor fv = cv.visitField(access, name, desc, sig, value);
            if (fv != null) {
                generateAnnotations(fv, f);
                generateAttributes(fv, f);
                fv.visitEnd();
            }
        }
	}

	/**
	 * Comparatator that is used to sort the inner class references before they
	 * are written out. This is mainly used to enforce a deterministic output
	 * between runs which we need for testing.
	 * 
	 * @author Steven Arzt
	 *
	 */
	private class SootInnerClassComparator implements Comparator<InnerClassTag> {

		@Override
		public int compare(InnerClassTag o1, InnerClassTag o2) {
			return o1.getInnerClass() == null ? 0
					: o1.getInnerClass().compareTo(o2.getInnerClass());
		}
		
	}
	
	/**
	 * Emits the bytecode for all references to inner classes if present
	 */
	protected void generateInnerClassReferences() {
		if (sc.hasTag("InnerClassAttribute")
				&& !Options.v().no_output_inner_classes_attribute()) {
			InnerClassAttribute ica = (InnerClassAttribute) sc
					.getTag("InnerClassAttribute");
			List<InnerClassTag> sortedTags = new ArrayList<InnerClassTag>(ica.getSpecs());
			Collections.sort(sortedTags, new SootInnerClassComparator());
			for (InnerClassTag ict : sortedTags) {
				String name = slashify(ict.getInnerClass());
				String outerClassName = slashify(ict.getOuterClass());
				String innerName = slashify(ict.getShortName());
				int access = ict.getAccessFlags();
				cv.visitInnerClass(name, outerClassName, innerName, access);
			}
		}
	}

	/**
	 * Emits the bytecode for all attributes of the class
	 */
    protected void generateAttributes() {
        for (Tag t : sc.getTags()) {
            if (t instanceof Attribute) {
                cv.visitAttribute(createASMAttribute((Attribute) t));
            }
        }
    }

    /**
     * Emits the bytecode for all attributes of a field
     * @param fv The FieldVisitor to emit the bytecode to
     * @param f The SootField the bytecode is to be emitted for
     */
    protected void generateAttributes(FieldVisitor fv, SootField f) {
        for (Tag t : f.getTags()) {
            if (t instanceof Attribute) {
                org.objectweb.asm.Attribute a = createASMAttribute((Attribute) t);
                fv.visitAttribute(a);
            }
        }
    }

    /**
     * Emits the bytecode for all attributes of a method
     * @param fv The MethodVisitor to emit the bytecode to
     * @param f The SootMethod the bytecode is to be emitted for
     */
    protected void generateAttributes(MethodVisitor mv, SootMethod m) {
        for (Tag t : m.getTags()) {
            if (t instanceof Attribute) {
                org.objectweb.asm.Attribute a = createASMAttribute((Attribute) t);
                mv.visitAttribute(a);
            }
        }
    }

    /**
     * Emits the bytecode for all annotations of a class, field or method
     * @param visitor A ClassVisitor, FieldVisitor or MethodVisitor to emit the bytecode to
     * @param host A Host (SootClass, SootField or SootMethod) the bytecode is to be emitted for, has to match the visitor
     */
	protected void generateAnnotations(Object visitor, Host host) {
		for (Tag t : host.getTags()) {
			// Find all VisibilityAnnotationTags
			if (t instanceof VisibilityAnnotationTag) {
				VisibilityAnnotationTag vat = (VisibilityAnnotationTag) t;
				boolean runTimeVisible = (vat.getVisibility() == AnnotationConstants.RUNTIME_VISIBLE);
				for (AnnotationTag at : vat.getAnnotations()) {
					AnnotationVisitor av = null;
					if (visitor instanceof ClassVisitor) {
						av = ((ClassVisitor) visitor).visitAnnotation(
								at.getType(), runTimeVisible);
					} else if (visitor instanceof FieldVisitor) {
						av = ((FieldVisitor) visitor).visitAnnotation(
								at.getType(), runTimeVisible);
					} else if (visitor instanceof MethodVisitor) {
						av = ((MethodVisitor) visitor).visitAnnotation(
								at.getType(), runTimeVisible);
					}

					generateAnnotationElems(av, at.getElems(), true);
				}
			}
			/*
			 * Here TypeAnnotations could be visited potentially. Currently
			 * (2015/02/03) they are not supported by the ASM-front-end and
			 * their information is not accessible.
			 */

			// Visit AnnotationDefault on methods
			else if (host instanceof SootMethod
					&& t instanceof AnnotationDefaultTag) {
				AnnotationDefaultTag adt = (AnnotationDefaultTag) t;
				AnnotationVisitor av = ((MethodVisitor) visitor)
						.visitAnnotationDefault();
				generateAnnotationElems(av,
						Collections.singleton(adt.getDefaultVal()), true);
			}

		}
	}

	/**
	 * Emits the bytecode for the values of an annotation
	 * @param av The AnnotationVisitor to emit the bytecode to
	 * @param elements A collection of AnnatiotionElem that are the values of the annotation
	 * @param addName True, if the name of the annotation has to be added, false otherwise (should be false only in recursive calls!)
	 */
	protected void generateAnnotationElems(AnnotationVisitor av,
			Collection<AnnotationElem> elements, boolean addName) {
		if (av != null) {
			Iterator<AnnotationElem> it = elements.iterator();
			while (it.hasNext()) {
				AnnotationElem elem = it.next();
				if (elem instanceof AnnotationEnumElem) {
					AnnotationEnumElem enumElem = (AnnotationEnumElem) elem;
					av.visitEnum(enumElem.getName(), enumElem.getTypeName(),
							enumElem.getConstantName());
				} else if (elem instanceof AnnotationArrayElem) {
					AnnotationArrayElem arrayElem = (AnnotationArrayElem) elem;
					AnnotationVisitor arrayVisitor = av.visitArray(arrayElem
							.getName());
					generateAnnotationElems(arrayVisitor, arrayElem.getValues(), false);
				} else if (elem instanceof AnnotationAnnotationElem) {
					AnnotationAnnotationElem aElem = (AnnotationAnnotationElem) elem;
					AnnotationVisitor aVisitor = av.visitAnnotation(
							aElem.getName(), aElem.getValue().getType());
					generateAnnotationElems(aVisitor, aElem.getValue()
							.getElems(), true);
				} else {
					Object val = null;
					if (elem instanceof AnnotationIntElem) {
						AnnotationIntElem intElem = (AnnotationIntElem) elem;
						int value = intElem.getValue();
						switch (intElem.getKind()) {
						case 'B':
							val = (byte) value;
							break;
						case 'Z':
							val = (value == 1);
							break;
						case 'I':
							val = value;
							break;
						case 'S':
							val = (short) value;
							break;
						}
					} else if (elem instanceof AnnotationFloatElem) {
						AnnotationFloatElem floatElem = (AnnotationFloatElem) elem;
						val = floatElem.getValue();
					} else if (elem instanceof AnnotationLongElem) {
						AnnotationLongElem longElem = (AnnotationLongElem) elem;
						val = longElem.getValue();
					} else if (elem instanceof AnnotationDoubleElem) {
						AnnotationDoubleElem doubleElem = (AnnotationDoubleElem) elem;
						val = doubleElem.getValue();
					} else if (elem instanceof AnnotationStringElem) {
						AnnotationStringElem stringElem = (AnnotationStringElem) elem;
						val = stringElem.getValue();
					} else if (elem instanceof AnnotationClassElem) {
						AnnotationClassElem classElem = (AnnotationClassElem) elem;
						val = org.objectweb.asm.Type.getType(classElem
								.getDesc());
					}
					if(addName){
						av.visit(elem.getName(), val);
					}
					else{
						av.visit(null, val);
					}
					
				}
			}
			av.visitEnd();
		}
	}

	/**
	 * Emits the bytecode for a reference to an outer class if necessary
	 */
	protected void generateOuterClassReference() {
		SootClass outerClass = sc.getOuterClass();
		String outerClassName = slashify(outerClass.getName());
		String enclosingMethod = null;
		String enclosingMethodSig = null;
		if (sc.hasTag("EnclosingMethodTag")) {
			EnclosingMethodTag emTag = (EnclosingMethodTag) sc
					.getTag("EnclosingMethodTag");
			if (!sc.hasOuterClass()) {
				outerClassName = slashify(emTag.getEnclosingClass());
			}
			enclosingMethod = emTag.getEnclosingMethod();
			enclosingMethodSig = emTag.getEnclosingMethodSig();
		}
		if (!sc.hasOuterClass() && sc.hasTag("OuterClassTag")) {
			outerClassName = slashify(((OuterClassTag) sc
					.getTag("OuterClassTag")).getName());
		}
		cv.visitOuterClass(outerClassName, enclosingMethod, enclosingMethodSig);
	}

	/**
	 * Emits the bytecode for the class itself, including its signature
	 */
	protected void generateClassHeader() {
		/*
		 * Retrieve all modifiers
		 */
		int classModifiers = sc.getModifiers();
		int modifier = getModifiers(classModifiers, sc);

		// Retrieve class-name
		String className = slashify(sc.getName());
		// Retrieve generics
		String signature = null;
		if (sc.hasTag("SignatureTag")) {
			signature = ((SignatureTag) sc.getTag("SignatureTag"))
					.getSignature();
		}
		/*
		 * Retrieve super-class If no super-class is explicitly given, the
		 * default is java.lang.Object.
		 */
		String superClass = "java/lang/Object";
		if (sc.hasSuperclass()) {
			superClass = slashify(sc.getSuperclass().getName());
		}

		// Retrieve directly implemented interfaces
		String[] interfaces = new String[sc.getInterfaceCount()];
		Iterator<SootClass> implementedInterfaces = sc.getInterfaces()
				.iterator();
		int i = 0;
		while (implementedInterfaces.hasNext()) {
			SootClass interf = implementedInterfaces.next();
			interfaces[i] = slashify(interf.getName());
			++i;
		}

		cv.visit(javaVersion, modifier, className, signature, superClass,
				interfaces);
	}

	/**
	 * Utility method to get the access modifiers of a Host
	 * @param modVal The bitset representation of the Host's modifiers
	 * @param host The Host (SootClass, SootField or SootMethod) the modifiers are to be retrieved from
	 * @return A bitset representation of the Host's modifiers in ASM's internal representation
	 */
	protected static int getModifiers(int modVal, Host host) {
		int modifier = 0;
		// Retrieve visibility-modifier
		if (Modifier.isPublic(modVal)) {
			modifier |= Opcodes.ACC_PUBLIC;
		} else if (Modifier.isPrivate(modVal)) {
			modifier |= Opcodes.ACC_PRIVATE;
		} else if (Modifier.isProtected(modVal)) {
			modifier |= Opcodes.ACC_PROTECTED;
		}
		// Retrieve static-modifier
		if (Modifier.isStatic(modVal)
				&& ((host instanceof SootField) || (host instanceof SootMethod))) {
			modifier |= Opcodes.ACC_STATIC;
		}
		// Retrieve final-modifier
		if (Modifier.isFinal(modVal)) {
			modifier |= Opcodes.ACC_FINAL;
		}
		// Retrieve synchronized-modifier
		if (Modifier.isSynchronized(modVal) && host instanceof SootMethod) {
			modifier |= Opcodes.ACC_SYNCHRONIZED;
		}
		// Retrieve volatile/bridge-modifier
		if (Modifier.isVolatile(modVal) && !(host instanceof SootClass)) {
			modifier |= Opcodes.ACC_VOLATILE;
		}
		// Retrieve transient/varargs-modifier
		if (Modifier.isTransient(modVal) && !(host instanceof SootClass)) {
			modifier |= Opcodes.ACC_TRANSIENT;
		}
		// Retrieve native-modifier
		if (Modifier.isNative(modVal) && host instanceof SootMethod) {
			modifier |= Opcodes.ACC_NATIVE;
		}
		// Retrieve interface-modifier
		if (Modifier.isInterface(modVal) && host instanceof SootClass) {
			modifier |= Opcodes.ACC_INTERFACE;
		} else if (host instanceof SootClass) {
			/*
			 * For all classes except for interfaces the super-flag should be
			 * set. See JVM 8-Specification section 4.1, page 72.
			 */
			modifier |= Opcodes.ACC_SUPER;
		}
		// Retrieve abstract-modifier
		if (Modifier.isAbstract(modVal) && !(host instanceof SootField)) {
			modifier |= Opcodes.ACC_ABSTRACT;
		}
		// Retrieve strictFP-modifier
		if (Modifier.isStrictFP(modVal) && host instanceof SootMethod) {
			modifier |= Opcodes.ACC_STRICT;
		}
		/*
		 * Retrieve synthetic-modifier. Class not present in source-code but
		 * generated by e.g. compiler TODO Do we need both checks?
		 */
		if (Modifier.isSynthetic(modVal) || host.hasTag("SyntheticTag")) {
			modifier |= Opcodes.ACC_SYNTHETIC;
		}
		// Retrieve annotation-modifier
		if (Modifier.isAnnotation(modVal) && host instanceof SootClass) {
			modifier |= Opcodes.ACC_ANNOTATION;
		}
		// Retrieve enum-modifier
		if (Modifier.isEnum(modVal) && !(host instanceof SootMethod)) {
			modifier |= Opcodes.ACC_ENUM;
		}
		return modifier;
	}

	/**
	 * Emits the bytecode for the body of a single method
	 * Has to be implemented by subclasses to suit their needs
	 * @param mv The MethodVisitor to emit the bytecode to 
	 * @param method The SootMethod the bytecode is to be emitted for
	 */
	abstract protected void generateMethodBody(MethodVisitor mv,
			SootMethod method);


}
