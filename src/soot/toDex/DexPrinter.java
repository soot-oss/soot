package soot.toDex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.jf.dexlib2.AnnotationVisibility;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderOffsetInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.MethodImplementationBuilder;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.MethodParameter;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.immutable.ImmutableAnnotation;
import org.jf.dexlib2.immutable.ImmutableAnnotationElement;
import org.jf.dexlib2.immutable.ImmutableExceptionHandler;
import org.jf.dexlib2.immutable.ImmutableMethodParameter;
import org.jf.dexlib2.immutable.reference.ImmutableFieldReference;
import org.jf.dexlib2.immutable.reference.ImmutableMethodReference;
import org.jf.dexlib2.immutable.value.ImmutableAnnotationEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableArrayEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableBooleanEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableByteEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableCharEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableDoubleEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableEnumEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableFieldEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableFloatEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableIntEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableLongEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableMethodEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableNullEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableShortEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableStringEncodedValue;
import org.jf.dexlib2.immutable.value.ImmutableTypeEncodedValue;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderFieldReference;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodReference;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;

import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.CompilationDeathException;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SourceLocator;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.dexpler.DexInnerClassParser;
import soot.dexpler.DexType;
import soot.dexpler.Util;
import soot.jimple.ClassConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.scalar.EmptySwitchEliminator;
import soot.options.Options;
import soot.tagkit.AbstractHost;
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
import soot.tagkit.ConstantValueTag;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LineNumberTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.ParamNamesTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn10t;
import soot.toDex.instructions.Insn30t;
import soot.toDex.instructions.InsnWithOffset;

/**
 * Main entry point for the "dex" output format.<br>
 * <br>
 * Use {@link #add(SootClass)} to add classes that should be printed as dex output and {@link #print()} to finally print the classes.<br>
 * If the printer has found the original APK of an added class (via {@link SourceLocator#dexClassIndex()}),
 * the files in the APK are copied to a new one, replacing it's classes.dex and excluding the signature files.
 * Note that you have to sign and align the APK yourself, with jarsigner and zipalign, respectively.<br>
 * If there is no original APK, the printer just emits a classes.dex.
 * 
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/tools/windows/jarsigner.html">jarsigner documentation</a>
 * @see <a href="http://developer.android.com/tools/help/zipalign.html">zipalign documentation</a>
 */
public class DexPrinter {
	
	private static final String CLASSES_DEX = "classes.dex";
	
	private DexBuilder dexFile;
	
	private File originalApk;
	
	public DexPrinter() {
		int api = Scene.v().getAndroidAPIVersion();
		dexFile = new DexBuilder(Opcodes.forApi(api));
	}
	
	private void printApk(String outputDir, File originalApk) throws IOException {
		ZipOutputStream outputApk = null;
		if(Options.v().output_jar()) {
			outputApk = PackManager.v().getJarFile();
			G.v().out.println("Writing APK to: " + Options.v().output_dir());
		} else {
			String outputFileName = outputDir + File.separatorChar + originalApk.getName();
		
			File outputFile = new File(outputFileName);
			if(outputFile.exists() && !Options.v().force_overwrite()) {
				throw new CompilationDeathException("Output file "+outputFile+" exists. Not overwriting.");
			} 
			outputApk = new ZipOutputStream(new FileOutputStream(outputFile));
			G.v().out.println("Writing APK to: " + outputFileName);
		}
		G.v().out.println("do not forget to sign the .apk file with jarsigner and to align it with zipalign");
		
		// Copy over additional resources from original APK
		ZipFile original = null;
		try {
			original = new ZipFile(originalApk);
			copyAllButClassesDexAndSigFiles(original, outputApk);
		}
		finally {
			if (original != null)
				original.close();
		}
		
		// put our classes.dex into the zip archive
		File tmpFile = File.createTempFile("toDex", null);
		FileInputStream fis = new FileInputStream(tmpFile);
		try {
			outputApk.putNextEntry(new ZipEntry(CLASSES_DEX));
			writeTo(tmpFile.getAbsolutePath());
			while (fis.available() > 0) {
				byte[] data = new byte[fis.available()];
				fis.read(data);
				outputApk.write(data);
			}
			outputApk.closeEntry();
		}
		finally {
			fis.close();
			tmpFile.delete();
			if (outputApk != null)
				outputApk.close();
		}
	}

	private void copyAllButClassesDexAndSigFiles(ZipFile source, ZipOutputStream destination) throws IOException {
		Enumeration<? extends ZipEntry> sourceEntries = source.entries();
		while (sourceEntries.hasMoreElements()) {
			ZipEntry sourceEntry = sourceEntries.nextElement();
			String sourceEntryName = sourceEntry.getName();
			if (sourceEntryName.equals(CLASSES_DEX) || isSignatureFile(sourceEntryName)) {
				continue;
			}
			// separate ZipEntry avoids compression problems due to encodings
			ZipEntry destinationEntry = new ZipEntry(sourceEntryName);
			// use the same compression method as the original (certain files are stored, not compressed)
			destinationEntry.setMethod(sourceEntry.getMethod());
			// copy other necessary fields for STORE method
			destinationEntry.setSize(sourceEntry.getSize());
			destinationEntry.setCrc(sourceEntry.getCrc());
			// finally craft new entry
			destination.putNextEntry(destinationEntry);
			InputStream zipEntryInput = source.getInputStream(sourceEntry);
			byte[] buffer = new byte[2048];
			int bytesRead = zipEntryInput.read(buffer);
			while (bytesRead > 0) {
				destination.write(buffer, 0, bytesRead);
				bytesRead = zipEntryInput.read(buffer);
			}
			zipEntryInput.close();
		}
	}

	private static boolean isSignatureFile(String fileName) {
		StringBuilder sigFileRegex = new StringBuilder();
		// file name must start with META-INF...
		sigFileRegex.append("META\\-INF");
		// ...followed by a zip file separator...
		sigFileRegex.append('/');
		// ...followed by anything but a zip file separator...
		sigFileRegex.append("[^/]+");
		// ...ending with .SF, .DSA, .RSA or .EC
		sigFileRegex.append("(\\.SF|\\.DSA|\\.RSA|\\.EC)$");
		return fileName.matches(sigFileRegex.toString());
	}

	private void writeTo(String fileName) throws IOException {
		FileDataStore fds = new FileDataStore(new File(fileName));
		dexFile.writeTo(fds);
		fds.close();
	}
	
    /**
     * Encodes Annotations Elements from Jimple to Dexlib
     * @param elem Jimple Element
     * @return Dexlib encoded element
     */
    private EncodedValue buildEncodedValueForAnnotation(AnnotationElem elem){
        switch (elem.getKind()) {
        case 'Z': {
        	if (elem instanceof AnnotationIntElem) {
	            AnnotationIntElem e = (AnnotationIntElem)elem;
	            if (e.getValue() == 0) {
	                return ImmutableBooleanEncodedValue.FALSE_VALUE;
	            } else if (e.getValue() == 1) {
	            	return ImmutableBooleanEncodedValue.TRUE_VALUE;
	            } else {
	                throw new RuntimeException("error: boolean value from int with value != 0 or 1.");
	            }
        	}
        	else if (elem instanceof AnnotationBooleanElem) {
        		AnnotationBooleanElem e = (AnnotationBooleanElem) elem;
        		if (e.getValue())
        			return ImmutableBooleanEncodedValue.TRUE_VALUE;
        		else
        			return ImmutableBooleanEncodedValue.FALSE_VALUE;
        	}
        	else
        		throw new RuntimeException("Annotation type incompatible with target type boolean");
        }
        case 'S': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            return new ImmutableShortEncodedValue((short)e.getValue());
        }
        case 'B': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            return new ImmutableByteEncodedValue((byte)e.getValue());
        }
        case 'C': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            return new ImmutableCharEncodedValue((char)e.getValue());
        }
        case 'I': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            return new ImmutableIntEncodedValue(e.getValue());
        }
        case 'J': {
            AnnotationLongElem e = (AnnotationLongElem)elem;
            return new ImmutableLongEncodedValue(e.getValue());
        }
        case 'F': {
            AnnotationFloatElem e = (AnnotationFloatElem)elem;
            return new ImmutableFloatEncodedValue(e.getValue());
        }
        case 'D': {
            AnnotationDoubleElem e = (AnnotationDoubleElem)elem;
            return new ImmutableDoubleEncodedValue(e.getValue());
        }
        case 's': {
            AnnotationStringElem e = (AnnotationStringElem)elem;
            return new ImmutableStringEncodedValue(e.getValue());
        }
        case 'e': {
            AnnotationEnumElem e = (AnnotationEnumElem)elem;
            
            String classT = SootToDexUtils.getDexClassName(e.getTypeName());
            String fieldT = classT;
            
            FieldReference fref = dexFile.internFieldReference(new ImmutableFieldReference
            		(classT, e.getConstantName(), fieldT));
            
            return new ImmutableEnumEncodedValue(fref);
        }
        case 'c': {
            AnnotationClassElem e = (AnnotationClassElem)elem;
            return new ImmutableTypeEncodedValue(e.getDesc());
        }
        case '[': {
            AnnotationArrayElem e = (AnnotationArrayElem)elem;
            List<EncodedValue> values = new ArrayList<EncodedValue>();
            for (int i = 0; i < e.getNumValues(); i++){
                EncodedValue val = buildEncodedValueForAnnotation(e.getValueAt(i));
                values.add(val);
            }
            return new ImmutableArrayEncodedValue(values);
        }
        case '@': {
        	AnnotationAnnotationElem e = (AnnotationAnnotationElem)elem;
        	
        	Set<String> alreadyWritten = new HashSet<String>();
            List<AnnotationElement> elements = null;
            if (!e.getValue().getElems().isEmpty()) {
            	elements = new ArrayList<AnnotationElement>();
	            for (AnnotationElem ae : e.getValue().getElems()) {
	            	if (!alreadyWritten.add(ae.getName()))
	            		throw new RuntimeException("Duplicate annotation attribute: " + ae.getName());
	            	
	            	AnnotationElement element = new ImmutableAnnotationElement(ae.getName(),
	            			buildEncodedValueForAnnotation(ae));
	            	elements.add(element);
	            }
            }
			
            return new ImmutableAnnotationEncodedValue
            		(SootToDexUtils.getDexClassName(e.getValue().getType()),
            		elements);
        }
        case 'f': { // field (Dalvik specific?)
            AnnotationStringElem e = (AnnotationStringElem)elem;
            
            String fSig = e.getValue();
            String[] sp = fSig.split(" ");
            String classString = SootToDexUtils.getDexClassName(sp[0].split(":")[0]);
            if (classString.isEmpty())
            	throw new RuntimeException("Empty class name in annotation");

            String typeString = sp[1];
            if (typeString.isEmpty())
            	throw new RuntimeException("Empty type string in annotation");
            
            String fieldName = sp[2];
            
            FieldReference fref = dexFile.internFieldReference(new ImmutableFieldReference
            		(classString, fieldName, typeString));
            return new ImmutableFieldEncodedValue(fref);
        }
        case 'M': { // method (Dalvik specific?)
            AnnotationStringElem e = (AnnotationStringElem)elem;
            
            String[] sp = e.getValue().split(" ");
            String classString = SootToDexUtils.getDexClassName(sp[0].split(":")[0]);
            if (classString.isEmpty())
            	throw new RuntimeException("Empty class name in annotation");

            String returnType = sp[1];
            String[] sp2 = sp[2].split("\\(");
            String methodNameString = sp2[0];
            
            String parameters = sp2[1].replaceAll("\\)", "");
            List<String> paramTypeList = null;
            if (!parameters.isEmpty()) {
            	paramTypeList = new ArrayList<String>();
	            if (parameters.length() > 0)
	                for (String p: parameters.split(",")) {
	                    paramTypeList.add(p);
	                }
            }
            
            MethodReference mref = dexFile.internMethodReference(new ImmutableMethodReference
            		(classString, methodNameString, paramTypeList, returnType));
            return new ImmutableMethodEncodedValue(mref);
        }
        case 'N': { // null (Dalvik specific?)
        	return ImmutableNullEncodedValue.INSTANCE;
        }
        default :
            throw new RuntimeException("Unknown Elem Attr Kind: "+elem.getKind());
        }
    }

    private EncodedValue makeConstantItem(SootField sf, Tag t) {
        if (!(t instanceof ConstantValueTag))
            throw new RuntimeException("error: t not ConstantValueTag.");

        if (t instanceof IntegerConstantValueTag) {
            Type sft = sf.getType();
            IntegerConstantValueTag i = (IntegerConstantValueTag) t;
            if (sft instanceof BooleanType) {
                int v = i.getIntValue();
                if (v == 0) {
                    return ImmutableBooleanEncodedValue.FALSE_VALUE;
                } else if (v == 1) {
                	return ImmutableBooleanEncodedValue.TRUE_VALUE;
                } else {
                    throw new RuntimeException(
                            "error: boolean value from int with value != 0 or 1.");
                }
            } else if (sft instanceof CharType) {
            	return new ImmutableCharEncodedValue((char) i.getIntValue());
            } else if (sft instanceof ByteType) {
            	return new ImmutableByteEncodedValue((byte) i.getIntValue());
            } else if (sft instanceof IntType) {
            	return new ImmutableIntEncodedValue(i.getIntValue());
            } else if (sft instanceof ShortType) {
            	return new ImmutableShortEncodedValue((short) i.getIntValue());
            } else {
                throw new RuntimeException("error: unexpected constant tag type: " + t
                        + " for field " + sf);
            }
        } else if (t instanceof LongConstantValueTag) {
            LongConstantValueTag l = (LongConstantValueTag) t;
            return new ImmutableLongEncodedValue(l.getLongValue());
        } else if (t instanceof DoubleConstantValueTag) {
            DoubleConstantValueTag d = (DoubleConstantValueTag) t;
            return new ImmutableDoubleEncodedValue(d.getDoubleValue());
        } else if (t instanceof FloatConstantValueTag) {
            FloatConstantValueTag f = (FloatConstantValueTag) t;
            return new ImmutableFloatEncodedValue(f.getFloatValue());
        } else if (t instanceof StringConstantValueTag) {
            StringConstantValueTag s = (StringConstantValueTag) t;
            return new ImmutableStringEncodedValue(s.getStringValue());
        } else
        	throw new RuntimeException("Unexpected constant type");
    }
    
    private void addAsClassDefItem(SootClass c) {
        // add source file tag if any
        String sourceFile = null;
        if (c.hasTag("SourceFileTag")) {
            SourceFileTag sft = (SourceFileTag) c.getTag("SourceFileTag");
            sourceFile = sft.getSourceFile();
        }
        
        String classType = SootToDexUtils.getDexTypeDescriptor(c.getType());
        int accessFlags = c.getModifiers();
        String superClass = c.hasSuperclass() ?
        		SootToDexUtils.getDexTypeDescriptor(c.getSuperclass().getType()) : null;
        
        List<String> interfaces = null;
        if (!c.getInterfaces().isEmpty()) {
        	interfaces = new ArrayList<String>();
            for (SootClass ifc : c.getInterfaces())
            	interfaces.add(SootToDexUtils.getDexTypeDescriptor(ifc.getType()));
        }
        
        List<BuilderField> fields = null;
        if (!c.getFields().isEmpty()) {
        	fields = new ArrayList<BuilderField>();
	        for (SootField f : c.getFields()) {       	
	        	// Look for a static initializer
	            EncodedValue staticInit = null;
	            for (Tag t : f.getTags()) {
	                if (t instanceof ConstantValueTag) {
	                    if (staticInit != null) {
	                        G.v().out.println("warning: more than one constant tag for field: " + f + ": "
	                                + t);
	                    } else {
	                        staticInit = makeConstantItem(f, t);
	                    }
	                }
	            }
	            if (staticInit == null)
	            	staticInit = BuilderEncodedValues.defaultValueForType
	            			(SootToDexUtils.getDexTypeDescriptor(f.getType()));
	            
	            // Build field annotations
	            Set<Annotation> fieldAnnotations = buildFieldAnnotations(f);
	            
	        	BuilderField field = dexFile.internField(classType,
	        			f.getName(),
	        			SootToDexUtils.getDexTypeDescriptor(f.getType()),
	        			f.getModifiers(),
	        			staticInit,
	        			fieldAnnotations);
	        	fields.add(field);
	        }
        }
        	
        dexFile.internClassDef(classType,
        		accessFlags,
        		superClass,
        		interfaces,
        		sourceFile,
        		buildClassAnnotations(c),
        		fields,
        		toMethods(c));
	}
    
    private Set<Annotation> buildClassAnnotations(SootClass c) {
    	Set<String> skipList = new HashSet<String>();
    	Set<Annotation> annotations = buildCommonAnnotations(c, skipList);
    	
       	// Classes can have either EnclosingMethod or EnclosingClass tags. Soot
    	// sets the outer class for both "normal" and anonymous inner classes,
    	// so we test for enclosing methods first. 
        if (c.hasTag("EnclosingMethodTag")) {
        	EnclosingMethodTag eMethTag = (EnclosingMethodTag)c.getTag("EnclosingMethodTag");
        	Annotation enclosingMethodItem = buildEnclosingMethodTag(eMethTag, skipList);
        	if (enclosingMethodItem != null)
        	  annotations.add(enclosingMethodItem);
        }
        else if (c.hasOuterClass()) {
   			if (skipList.add("Ldalvik/annotation/EnclosingClass;")) {
		    	// EnclosingClass annotation
		    	ImmutableAnnotationElement enclosingElement = new ImmutableAnnotationElement
		    			("value", new ImmutableTypeEncodedValue
		    					(SootToDexUtils.getDexClassName(c.getOuterClass().getName())));
		    	annotations.add(new ImmutableAnnotation(AnnotationVisibility.SYSTEM,
		    			"Ldalvik/annotation/EnclosingClass;",
		    			Collections.singleton(enclosingElement)));
       		}
       	}
        
        // If we have an outer class, we also pick up the InnerClass annotations
        // from there. Note that Java and Soot associate InnerClass annotations
        // with the respective outer classes, while Dalvik puts them on the
        // respective inner classes.
        if (c.hasOuterClass()) {
        	InnerClassAttribute icTag = (InnerClassAttribute) c.getOuterClass().getTag("InnerClassAttribute");
        	if (icTag != null) {
	        	List<Annotation> innerClassItem = buildInnerClassAttribute(c, icTag, skipList);
	        	if (innerClassItem != null)
	        	  annotations.addAll(innerClassItem);
        	}
        }
        
    	// Write the MemberClasses tag
    	InnerClassAttribute icTag = (InnerClassAttribute) c.getTag("InnerClassAttribute");
    	if (icTag != null) {
        	List<Annotation> memberClassesItem = buildMemberClassesAttribute(c, icTag, skipList);
        	if (memberClassesItem != null)
        	  annotations.addAll(memberClassesItem);
        }
    	
        
        for (Tag t : c.getTags()) {
            if (t.getName().equals("VisibilityAnnotationTag")){
                List<ImmutableAnnotation> visibilityItems = buildVisibilityAnnotationTag
                		((VisibilityAnnotationTag) t, skipList);
            	annotations.addAll(visibilityItems);
            }
    	}
        

        //Write default-annotation tags
		List<AnnotationElem> defaults = new ArrayList<AnnotationElem>();
        for (SootMethod method : c.getMethods()) {
        	AnnotationDefaultTag tag = (AnnotationDefaultTag) method.getTag("AnnotationDefaultTag");
        	if (tag != null) {
        		tag.getDefaultVal().setName(method.getName());
        		defaults.add(tag.getDefaultVal());
        	}
        }
        if (defaults.size() > 0) {
        	VisibilityAnnotationTag defaultAnnotationTag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
        	AnnotationTag a = new AnnotationTag("Ldalvik/annotation/AnnotationDefault;");
        	defaultAnnotationTag.addAnnotation(a);

        	AnnotationTag at = new AnnotationTag(SootToDexUtils.getDexClassName(c.getName()));
        	AnnotationAnnotationElem ae = new AnnotationAnnotationElem(at, '@', "value");
        	a.addElem(ae);

        	
        	for (AnnotationElem aelem : defaults)
        		at.addElem(aelem);
        	
            List<ImmutableAnnotation> visibilityItems = buildVisibilityAnnotationTag
            		(defaultAnnotationTag, skipList);
        	annotations.addAll(visibilityItems);
        }
        
    	return annotations;
    }

    private Set<Annotation> buildFieldAnnotations(SootField f) {
    	Set<String> skipList = new HashSet<String>();
    	Set<Annotation> annotations = buildCommonAnnotations(f, skipList);
    	
    	for (Tag t : f.getTags()) {
            if (t.getName().equals("VisibilityAnnotationTag")){
                List<ImmutableAnnotation> visibilityItems = buildVisibilityAnnotationTag
                		((VisibilityAnnotationTag) t, skipList);
            	annotations.addAll(visibilityItems);
            }
    	}
		
    	return annotations;
    }

    private Set<Annotation> buildMethodAnnotations(SootMethod m) {
    	Set<String> skipList = new HashSet<String>();
    	Set<Annotation> annotations = buildCommonAnnotations(m, skipList);
    	
    	for (Tag t : m.getTags()) {
            if (t.getName().equals("VisibilityAnnotationTag")){
                List<ImmutableAnnotation> visibilityItems = buildVisibilityAnnotationTag
                		((VisibilityAnnotationTag) t, skipList);
            	annotations.addAll(visibilityItems);
            }
    	}
    	List<SootClass> exceptionList = m.getExceptions();
    	if (exceptionList != null && !exceptionList.isEmpty()) {
            Set<ImmutableAnnotationElement> elements = new HashSet<ImmutableAnnotationElement>();
            List<ImmutableEncodedValue> valueList = new ArrayList<ImmutableEncodedValue>();
    		for (SootClass exceptionClass : exceptionList) {
	            valueList.add(new ImmutableTypeEncodedValue(DexType.toDalvikICAT(exceptionClass.getName()).replace(".", "/")));
    		}
            ImmutableArrayEncodedValue valueValue = new ImmutableArrayEncodedValue(valueList);
            ImmutableAnnotationElement valueElement = new ImmutableAnnotationElement
            		("value", valueValue);
            elements.add(valueElement);
            ImmutableAnnotation ann = new ImmutableAnnotation
            		(AnnotationVisibility.SYSTEM,
        			"Ldalvik/annotation/Throws;",
        			elements);
        	annotations.add(ann);
    	}
    	
    	return annotations;
    }

    private Set<Annotation> buildMethodParameterAnnotations(SootMethod m,
    		final int paramIdx) {
    	Set<String> skipList = new HashSet<String>();
    	Set<Annotation> annotations = new HashSet<Annotation>();
    	
    	for (Tag t : m.getTags()) {
            if (t.getName().equals("VisibilityParameterAnnotationTag")) {
                VisibilityParameterAnnotationTag vat = (VisibilityParameterAnnotationTag)t;
                List<ImmutableAnnotation> visibilityItems = buildVisibilityParameterAnnotationTag
                		(vat, skipList, paramIdx);
            	annotations.addAll(visibilityItems);
            }
    	}
    	
    	return annotations;
    }
    
    private Set<Annotation> buildCommonAnnotations(AbstractHost host, Set<String> skipList) {
		Set<Annotation> annotations = new HashSet<Annotation>();
		
        // handle deprecated tag
        if (host.hasTag("DeprecatedTag") && !skipList.contains("Ljava/lang/Deprecated;")) {
        	ImmutableAnnotation ann = new ImmutableAnnotation
        			(AnnotationVisibility.RUNTIME,
        			"Ljava/lang/Deprecated;",
        			Collections.<AnnotationElement>emptySet());
        	annotations.add(ann);
            skipList.add("Ljava/lang/Deprecated;");
        }
        
        // handle signature tag
        if (host.hasTag("SignatureTag") && !skipList.contains("Ldalvik/annotation/Signature;")) {
            SignatureTag tag = (SignatureTag) host.getTag("SignatureTag");
            List<String> splitSignature = SootToDexUtils.splitSignature(tag.getSignature());

            Set<ImmutableAnnotationElement> elements = null;
            if (splitSignature != null && splitSignature.size() > 0) {
            	elements = new HashSet<ImmutableAnnotationElement>();
            
	            List<ImmutableEncodedValue> valueList = new ArrayList<ImmutableEncodedValue>();
	            for (String s : splitSignature) {
	            	ImmutableStringEncodedValue val = new ImmutableStringEncodedValue(s);
	                valueList.add(val);
	            }
	            ImmutableArrayEncodedValue valueValue = new ImmutableArrayEncodedValue(valueList);
	            ImmutableAnnotationElement valueElement = new ImmutableAnnotationElement
	            		("value", valueValue);
	            elements.add(valueElement);
            }
            else
            	G.v().out.println("Signature annotation without value detected");
            
            ImmutableAnnotation ann = new ImmutableAnnotation
            		(AnnotationVisibility.SYSTEM,
        			"Ldalvik/annotation/Signature;",
        			elements);
        	annotations.add(ann);
            skipList.add("Ldalvik/annotation/Signature;");
        }        
        
        return annotations;
	}
    
    private List<ImmutableAnnotation> buildVisibilityAnnotationTag
			(VisibilityAnnotationTag t, Set<String> skipList) {
    	if (t.getAnnotations() == null)
    		return Collections.emptyList();
    	
    	List<ImmutableAnnotation> annotations = new ArrayList<ImmutableAnnotation>();
        for (AnnotationTag at: t.getAnnotations()) {
            String type = at.getType();
            if (!skipList.add(type))
            	continue;
            
            Set<String> alreadyWritten = new HashSet<String>();
            List<AnnotationElement> elements = null;
            if (!at.getElems().isEmpty()) {
            	elements = new ArrayList<AnnotationElement>();
	            for (AnnotationElem ae : at.getElems()) {
	            	if (ae.getName() == null || ae.getName().isEmpty())
	            		throw new RuntimeException("Null or empty annotation name encountered");
	            	if (!alreadyWritten.add(ae.getName()))
	            		throw new RuntimeException("Duplicate annotation attribute: " + ae.getName());
	            	
	                EncodedValue value = buildEncodedValueForAnnotation(ae);
	                ImmutableAnnotationElement element = new ImmutableAnnotationElement
	                		(ae.getName(), value);
	                elements.add(element);
	            }
            }
            
            String typeName = SootToDexUtils.getDexClassName(at.getType());
            ImmutableAnnotation ann = new ImmutableAnnotation(getVisibility(t.getVisibility()),
            		typeName, elements);
            annotations.add(ann);
        }
        return annotations;
	}

    private List<ImmutableAnnotation> buildVisibilityParameterAnnotationTag
    		(VisibilityParameterAnnotationTag t, Set<String> skipList,
    				int paramIdx) {
		if (t.getVisibilityAnnotations() == null)
    		return Collections.emptyList();
		
        int paramTagIdx = 0;
    	List<ImmutableAnnotation> annotations = new ArrayList<ImmutableAnnotation>();
        for (VisibilityAnnotationTag vat : t.getVisibilityAnnotations()) {
        	if (paramTagIdx == paramIdx
        			&& vat != null
        			&& vat.getAnnotations() != null)
	        	for (AnnotationTag at : vat.getAnnotations()) {
		            String type = at.getType();
		            if (!skipList.add(type))
		            	continue;
		            
		            Set<String> alreadyWritten = new HashSet<String>();
		            List<AnnotationElement> elements = null;
		            if (!at.getElems().isEmpty()) {
		            	elements = new ArrayList<AnnotationElement>();
			            for (AnnotationElem ae : at.getElems()) {
			            	if (ae.getName() == null || ae.getName().isEmpty())
			            		throw new RuntimeException("Null or empty annotation name encountered");
			            	if (!alreadyWritten.add(ae.getName()))
			            		throw new RuntimeException("Duplicate annotation attribute: " + ae.getName());
	
			            	EncodedValue value = buildEncodedValueForAnnotation(ae);
			                ImmutableAnnotationElement element = new ImmutableAnnotationElement(ae.getName(), value);
			                elements.add(element);
			            }
		            }
		            
		            ImmutableAnnotation ann = new ImmutableAnnotation(getVisibility(vat.getVisibility()),
		            		SootToDexUtils.getDexClassName(at.getType()),
		            		elements);
		            annotations.add(ann);
	        	}
        	paramTagIdx++;
        }
        return annotations;
    }
    
    private Annotation buildEnclosingMethodTag(EnclosingMethodTag t, Set<String> skipList) {
    	if (!skipList.add("Ldalvik/annotation/EnclosingMethod;"))
    		return null;
    	
    	if (t.getEnclosingMethod() == null)
    		return null;

    	String[] split1 = t.getEnclosingMethodSig().split("\\)");
	    String parametersS = split1[0].replaceAll("\\(", "");
	    String returnTypeS = split1[1];
	    
	    List<String> typeList = new ArrayList<String>();
        if (!parametersS.equals("")) {
            for (String p : Util.splitParameters(parametersS)) {
                if (!p.isEmpty())
                	typeList.add(p);
            }
        }
        
	    ImmutableMethodReference mRef = new ImmutableMethodReference
	    		(SootToDexUtils.getDexClassName(t.getEnclosingClass()),
	    		t.getEnclosingMethod(), typeList, returnTypeS);
    	ImmutableMethodEncodedValue methodRef = new ImmutableMethodEncodedValue
    			(dexFile.internMethodReference(mRef));
    	AnnotationElement methodElement = new ImmutableAnnotationElement("value", methodRef);
    	
    	return new ImmutableAnnotation(AnnotationVisibility.SYSTEM,
    			"Ldalvik/annotation/EnclosingMethod;",
    			Collections.singleton(methodElement));
    }

    private List<Annotation> buildInnerClassAttribute(SootClass parentClass,
    		InnerClassAttribute t, Set<String> skipList) {
    	if (t.getSpecs() == null)
    		return null;
    	
    	List<Annotation> anns = null;
    	for (Tag t2 : t.getSpecs()) {
    		InnerClassTag icTag = (InnerClassTag) t2;
    		
        	// In Dalvik, both the EnclosingMethod/EnclosingClass tag and the
        	// InnerClass tag are written to the inner class which is different
        	// to Java. We thus check whether this tag actually points to our
    		// outer class.
    		String outerClass = DexInnerClassParser.getOuterClassNameFromTag(icTag);
			String innerClass = icTag.getInnerClass().replaceAll("/", ".");
						
			// Only write the InnerClass tag to the inner class itself, not
			// the other one. If the outer class points to our parent, but
			// this is simply the wrong inner class, we also continue with the
			// next tag.
    		if (!parentClass.hasOuterClass()
    				|| !innerClass.equals(parentClass.getName()))
    			continue;
    		
    		// If the outer class points to the very same class, we null it
    		if (parentClass.getName().equals(outerClass)
    				&& icTag.getOuterClass() == null)
    			outerClass = null;
    		
    		// Do not write garbage. Never.
    		if (parentClass.getName().equals(outerClass))
    			continue;
    		
    		// This is an actual inner class. Write the annotation
        	if (skipList.add("Ldalvik/annotation/InnerClass;")) {
	    		// InnerClass annotation
	        	List<AnnotationElement> elements = new ArrayList<AnnotationElement>();
		    	
		    	ImmutableAnnotationElement flagsElement = new ImmutableAnnotationElement
		    			("accessFlags", new ImmutableIntEncodedValue(icTag.getAccessFlags()));
		    	elements.add(flagsElement);
		    	
		    	ImmutableEncodedValue nameValue;
		    	if (icTag.getShortName() != null && !icTag.getShortName().isEmpty())
		    		nameValue = new ImmutableStringEncodedValue(icTag.getShortName());
		    	else
		    		nameValue = ImmutableNullEncodedValue.INSTANCE;
		    	
		    	ImmutableAnnotationElement nameElement = new ImmutableAnnotationElement
			    		("name", nameValue);
			    elements.add(nameElement);
		    	
		    	if (anns == null) anns = new ArrayList<Annotation>();
		    	anns.add(new ImmutableAnnotation(AnnotationVisibility.SYSTEM,
		    			"Ldalvik/annotation/InnerClass;",
		    			elements));
        	}
    	}
    	    	
    	return anns;
    }
    
    private List<Annotation> buildMemberClassesAttribute(SootClass parentClass,
    		InnerClassAttribute t, Set<String> skipList) {
    	List<Annotation> anns = null;
    	Set<String> memberClasses = null;
    	
    	// Collect the inner classes
    	for (Tag t2 : t.getSpecs()) {
    		InnerClassTag icTag = (InnerClassTag) t2;
    		String outerClass = DexInnerClassParser.getOuterClassNameFromTag(icTag);
			
			// Only classes with names are member classes
			if (icTag.getOuterClass() != null
					&& parentClass.getName().equals(outerClass)) {
				if (memberClasses == null)
					memberClasses = new HashSet<String>();
				memberClasses.add(SootToDexUtils.getDexClassName(icTag.getInnerClass()));
			}
    	}
    	
    	// Write the member classes
    	if (memberClasses != null
    			&& !memberClasses.isEmpty()
    			&& skipList.add("Ldalvik/annotation/MemberClasses;")) {
        	List<EncodedValue> classes = new ArrayList<EncodedValue>();
	    	for (String memberClass : memberClasses) {
	    		ImmutableTypeEncodedValue classValue = new ImmutableTypeEncodedValue(memberClass);
	    		classes.add(classValue);
	    	}
	    	
    		ImmutableArrayEncodedValue classesValue =
    				new ImmutableArrayEncodedValue(classes);
    		ImmutableAnnotationElement element =
    				new ImmutableAnnotationElement("value", classesValue);
	    	ImmutableAnnotation memberAnnotation =
    				new ImmutableAnnotation(AnnotationVisibility.SYSTEM,
    						"Ldalvik/annotation/MemberClasses;",
    						Collections.singletonList(element));
	    	if (anns == null) anns = new ArrayList<Annotation>();
	    	anns.add(memberAnnotation);
    	}
    	return anns;
    }
    
    /**
     * Converts Jimple visibility to Dexlib visibility
     * 
     * @param visibility
     *            Jimple visibility
     * @return Dexlib visibility
     */
    private static int getVisibility(int visibility) {
        if (visibility == AnnotationConstants.RUNTIME_VISIBLE)
            return AnnotationVisibility.RUNTIME;
        if (visibility == AnnotationConstants.RUNTIME_INVISIBLE)
            return AnnotationVisibility.SYSTEM;
        if (visibility == AnnotationConstants.SOURCE_VISIBLE)
            return AnnotationVisibility.BUILD;
        throw new RuntimeException("Unknown annotation visibility: '" + visibility + "'");
    }
    
	private Collection<BuilderMethod> toMethods(SootClass clazz) {
		if (clazz.getMethods().isEmpty())
			return null;
		
        String classType = SootToDexUtils.getDexTypeDescriptor(clazz.getType());
        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (SootMethod sm : clazz.getMethods()) {
            if (sm.isPhantom()) {
                // Do not print method bodies for inherited methods
                continue;
            }
            
        	MethodImplementation impl = toMethodImplementation(sm);
        	
        	List<String> parameterNames = null;
        	if (sm.hasTag("ParamNamesTag"))
        		parameterNames = ((ParamNamesTag) sm.getTag("ParamNamesTag")).getNames();
        	
        	int paramIdx = 0;
        	List<MethodParameter> parameters = null;
        	if (sm.getParameterCount() > 0) {
        		parameters = new ArrayList<MethodParameter>();
	        	for (Type tp : sm.getParameterTypes()) {
	        		String paramType = SootToDexUtils.getDexTypeDescriptor(tp);
	        		parameters.add(new ImmutableMethodParameter(paramType,
	        				buildMethodParameterAnnotations(sm, paramIdx),
	        				sm.isConcrete() && parameterNames != null ?
	        						parameterNames.get(paramIdx) : null));
	        		paramIdx++;
	        	}
        	}
        	
            String returnType = SootToDexUtils.getDexTypeDescriptor(sm.getReturnType());
            
			int accessFlags = SootToDexUtils.getDexAccessFlags(sm);
            BuilderMethod meth = dexFile.internMethod(classType,
					sm.getName(),
					parameters,
					returnType,
					accessFlags,
					buildMethodAnnotations(sm),
					impl);
            methods.add(meth);
        }
		return methods;
	}
	
    protected static BuilderFieldReference toFieldReference
    		(SootField f, DexBuilder belongingDexFile) {
    	FieldReference fieldRef = new ImmutableFieldReference
    			(SootToDexUtils.getDexClassName(f.getDeclaringClass().getName()),
    			f.getName(),
    			SootToDexUtils.getDexTypeDescriptor(f.getType()));
    	return belongingDexFile.internFieldReference(fieldRef);
	}
	
    protected static BuilderMethodReference toMethodReference
			(SootMethodRef m, DexBuilder belongingDexFile) {
    	List<String> parameters = new ArrayList<String>();
    	for (Type t : m.parameterTypes())
    		parameters.add(SootToDexUtils.getDexTypeDescriptor(t));
    	MethodReference methodRef = new ImmutableMethodReference
    			(SootToDexUtils.getDexClassName(m.declaringClass().getName()),
    			m.name(),
    			parameters,
    			SootToDexUtils.getDexTypeDescriptor(m.returnType()));
    	return belongingDexFile.internMethodReference(methodRef);
    }

    protected static BuilderTypeReference toTypeReference
			(Type t, DexBuilder belongingDexFile) {
    	return belongingDexFile.internTypeReference
    			(SootToDexUtils.getDexTypeDescriptor(t));
    }
    
	private MethodImplementation toMethodImplementation(SootMethod m) {
		if (m.isAbstract() || m.isNative()) {
			return null;
		}
		Body activeBody = m.retrieveActiveBody();
		
		// check the method name to make sure that dexopt won't get into trouble
		// when installing the app
		if (m.getName().contains("<") || m.getName().equals(">"))
			if (!m.getName().equals("<init>") && !m.getName().equals("<clinit>"))
				throw new RuntimeException("Invalid method name: " + m.getName());
		
		// Switch statements may not be empty in dex, so we have to fix this first
		EmptySwitchEliminator.v().transform(activeBody);
		
		// Dalvik requires synchronized methods to have explicit monitor calls,
		// so we insert them here. See http://milk.com/kodebase/dalvik-docs-mirror/docs/debugger.html
		// We cannot place this upon the developer since it is only required
		// for Dalvik, but not for other targets.
		SynchronizedMethodTransformer.v().transform(activeBody);
		
		// Tries may not start or end at units which have no corresponding Dalvik
		// instructions such as IdentityStmts. We reduce the traps to start at the
		// first "real" instruction. We could also use a TrapTigthener, but that
		// would be too expensive for what we need here.
		FastDexTrapTightener.v().transform(activeBody);
		
		// Look for sequences of array element assignments that we can collapse
		// into bulk initializations
		DexArrayInitDetector initDetector = new DexArrayInitDetector();
		initDetector.constructArrayInitializations(activeBody);
		initDetector.fixTraps(activeBody);
		
		// Split the tries since Dalvik does not supported nested try/catch blocks
		TrapSplitter.v().transform(activeBody);

		// word count of incoming parameters
		int inWords = SootToDexUtils.getDexWords(m.getParameterTypes());
		if (!m.isStatic()) {
			inWords++; // extra word for "this"
		}
		// word count of max outgoing parameters
		Collection<Unit> units = activeBody.getUnits();
		// register count = parameters + additional registers, depending on the dex instructions generated (e.g. locals used and constants loaded)
		StmtVisitor stmtV = new StmtVisitor(m, dexFile, initDetector);
		
		toInstructions(units, stmtV);
		
		int registerCount = stmtV.getRegisterCount();
		if (inWords > registerCount) {
			/*
			 * as the Dalvik VM moves the parameters into the last registers, the "in" word count must be at least equal to the register count.
			 * a smaller register count could occur if soot generated the method body, see e.g. the handling of phantom refs in SootMethodRefImpl.resolve(StringBuffer):
			 * the body has no locals for the ParameterRefs, it just throws an error.
			 * 
			 * we satisfy the verifier by just increasing the register count, since calling phantom refs will lead to an error anyway.
			 */
			registerCount = inWords;
		}
		
		MethodImplementationBuilder builder = new MethodImplementationBuilder(registerCount);
		LabelAssigner labelAssinger = new LabelAssigner(builder);
		List<BuilderInstruction> instructions = stmtV.getRealInsns(labelAssinger);
		
		fixLongJumps(instructions, labelAssinger, stmtV);
		
		Map<Local, Integer> seenRegisters = new HashMap<Local, Integer>();
		Map<Instruction, LocalRegisterAssignmentInformation> instructionRegisterMap = stmtV.getInstructionRegisterMap();
		
		if (Options.v().write_local_annotations()) {
			for (LocalRegisterAssignmentInformation assignment : stmtV.getParameterInstructionsList()) {
				//The "this" local gets added automatically, so we do not need to add it explicitly
				//(at least not if it exists with exactly this name)
				if (assignment.getLocal().getName().equals("this"))
					continue;
				addRegisterAssignmentDebugInfo(assignment, seenRegisters, builder);
			}
		}
    	
        for (BuilderInstruction ins : instructions) {
            Stmt origStmt = stmtV.getStmtForInstruction(ins);
            
            // If this is a switch payload, we need to place the label
            if (stmtV.getInstructionPayloadMap().containsKey(ins))
            	builder.addLabel(labelAssinger.getLabelName(stmtV.getInstructionPayloadMap().get(ins)));
            
            if (origStmt != null) {
	            // Do we need a label here because this a trap handler?
	            for (Trap t : m.getActiveBody().getTraps()) {
	            	if (t.getBeginUnit() == origStmt
	            			|| t.getEndUnit() == origStmt
	            			|| t.getHandlerUnit() == origStmt) {
	            		labelAssinger.getOrCreateLabel(origStmt);
	            		break;
	            	}
	            }
	            
        		// Add the label if the statement has one
				String labelName = labelAssinger.getLabelName(origStmt);
				if (labelName != null && !builder.getLabel(labelName).isPlaced())
					builder.addLabel(labelName);
				
				// Add the tags
				if (stmtV.getStmtForInstruction(ins) != null) {
	                List<Tag> tags = origStmt.getTags();
	                for (Tag t : tags) {
	                    if (t instanceof LineNumberTag) {
	                        LineNumberTag lnt = (LineNumberTag) t;
	            			builder.addLineNumber(lnt.getLineNumber());
	                    }
	                    else if (t instanceof SourceFileTag) {
	                    	SourceFileTag sft = (SourceFileTag) t;
	                    	builder.addSetSourceFile(dexFile.internStringReference
	                    			(sft.getSourceFile()));
	                    }
	                }
	            }
            }
            
            builder.addInstruction(ins);
            LocalRegisterAssignmentInformation registerAssignmentTag = instructionRegisterMap.get(ins);
            if (registerAssignmentTag != null)
            {
				//Add start local debugging information: Register -> Local assignment
        		addRegisterAssignmentDebugInfo(registerAssignmentTag, seenRegisters, builder);
            }
		}
		
		for (int registersLeft : seenRegisters.values())
			builder.addEndLocal(registersLeft);
		
		toTries(activeBody.getTraps(), stmtV, builder, labelAssinger);
        
        // Make sure that all labels have been placed by now
        for (Label lbl : labelAssinger.getAllLabels())
        	if (!lbl.isPlaced())
        		throw new RuntimeException("Label not placed: " + lbl);
        
        return builder.getMethodImplementation();
	}
	
	/**
	 * Fixes long jumps that exceed the maximum distance for the respective jump
	 * type
	 * @param instructions The list of generated dalvik instructions
	 * @param labelAssigner The label assigner that maps statements to labels
	 * @param stmtV The statement visitor used to produce the dalvik instructions
	 */
	private void fixLongJumps(List<BuilderInstruction> instructions,
			LabelAssigner labelAssigner, StmtVisitor stmtV) {
		// Only construct the maps once and update them afterwards
		Map<Instruction, Integer> instructionsToIndex = new HashMap<Instruction, Integer>();
		List<Integer> instructionsToOffsets = new ArrayList<Integer>();
		Map<Label, Integer> labelsToOffsets = new HashMap<Label, Integer>();
		Map<Label, Integer> labelsToIndex = new HashMap<Label, Integer>();
		
		boolean hasChanged;
		l0 : do {
			// Look for changes anew every time
			hasChanged = false;
			instructionsToOffsets.clear();
			
			// Build a mapping between instructions and offsets
			{
			int offset = 0;
			int idx = 0;
			for (BuilderInstruction bi : instructions) {
				instructionsToIndex.put(bi, idx);
				instructionsToOffsets.add(offset);
	            Stmt origStmt = stmtV.getStmtForInstruction(bi);
	            if (origStmt != null) {
	            	Label lbl = labelAssigner.getLabelUnsafe(origStmt);
	            	if (lbl != null) {
	            		labelsToOffsets.put(lbl, offset);
	            		labelsToIndex.put(lbl, idx);
	            	}
	            }
	            offset += (bi.getFormat().size / 2);
	            idx++;
			}
			}
			
	   		// Look for references to labels
	   		for (int j = 0; j < instructions.size(); j++) {
	   			BuilderInstruction bj = instructions.get(j);
	   			if (bj instanceof BuilderOffsetInstruction) {
	   				BuilderOffsetInstruction boj = (BuilderOffsetInstruction) bj;
	   				
	   				// Compute the distance between the instructions
	   				Insn jumpInsn = stmtV.getInsnForInstruction(boj);
	   				if (jumpInsn instanceof InsnWithOffset) {
	   					InsnWithOffset offsetInsn = (InsnWithOffset) jumpInsn;
	   					Integer targetOffset = labelsToOffsets.get(boj.getTarget());
	   					if (targetOffset == null)
	   						continue;
	   					
	   					int distance = instructionsToOffsets.get(j) - targetOffset;
	   					if (Math.abs(distance) > offsetInsn.getMaxJumpOffset()) {
	   						// We need intermediate jumps
	   						insertIntermediateJump(labelsToIndex.get(boj.getTarget()),
	   								j, stmtV, instructions, labelAssigner);
	   						hasChanged = true;
	   						continue l0;
	   					}
	   				}
	   			}
	   		}
		} while (hasChanged);
	}
	
	/**
	 * Creates an intermediate jump instruction between the original jump
	 * instruction and its target
	 * @param targetInsPos The jump target index
	 * @param jumpInsPos The position of the jump instruction
	 * @param stmtV The statement visitor used for constructing the instructions
	 * @param instructions The list of Dalvik instructions
	 * @param labelAssigner The label assigner to be used for creating new labels
	 */
	private void insertIntermediateJump(int targetInsPos, int jumpInsPos,
			StmtVisitor stmtV, List<BuilderInstruction> instructions,
			LabelAssigner labelAssigner) {
		// Get the original jump instruction
		BuilderInstruction originalJumpInstruction = instructions.get(jumpInsPos);
		Insn originalJumpInsn = stmtV.getInsnForInstruction(originalJumpInstruction);
		if (originalJumpInsn == null)
			return;
		if (!(originalJumpInsn instanceof InsnWithOffset))
			throw new RuntimeException("Unexpected jump instruction target");
		InsnWithOffset offsetInsn = (InsnWithOffset) originalJumpInsn;
		
		// If this is goto instruction, we can just replace it
		if (originalJumpInsn instanceof Insn10t) {
			if (originalJumpInsn.getOpcode() == Opcode.GOTO) {
				Insn30t newJump = new Insn30t(Opcode.GOTO_32);
				newJump.setTarget(((Insn10t) originalJumpInsn).getTarget());
				BuilderInstruction newJumpInstruction = newJump.getRealInsn(labelAssigner);
				instructions.remove(jumpInsPos);
				instructions.add(jumpInsPos, newJumpInstruction);
				stmtV. fakeNewInsn(stmtV.getStmtForInstruction(originalJumpInstruction),
						newJump, newJumpInstruction);
				return;
			}
		}
		
		// Find a position where we can jump to
		int distance = Math.max(targetInsPos, jumpInsPos) - Math.min(targetInsPos, jumpInsPos);
		if (distance == 0)
			return;
		int newJumpIdx = Math.min(targetInsPos, jumpInsPos) + (distance / 2);
		int sign = (int) Math.signum(targetInsPos - jumpInsPos);
		
		// There must be a statement at the instruction after the jump target.
		// This statement must not appear at an earlier statement as the jump
		// label may otherwise be attached to the wrong statement
		do {
			Stmt newStmt = stmtV.getStmtForInstruction(instructions.get(newJumpIdx));
			Stmt prevStmt = newJumpIdx > 0 ? stmtV.getStmtForInstruction(instructions.get(newJumpIdx - 1)) : null;
			
			if (newStmt == null || newStmt == prevStmt) {
				newJumpIdx -= sign;
				if (newJumpIdx < 0 || newJumpIdx >= instructions.size())
					throw new RuntimeException("No position for inserting intermediate "
							+ "jump instruction found");
			}
			else
				break;
		} while (true);
		
		// Create a jump instruction from the middle to the end
		NopStmt nop = Jimple.v().newNopStmt();
		Insn30t newJump = new Insn30t(Opcode.GOTO_32);
		newJump.setTarget(stmtV.getStmtForInstruction(instructions.get(targetInsPos)));
		BuilderInstruction newJumpInstruction = newJump.getRealInsn(labelAssigner);
		instructions.add(newJumpIdx, newJumpInstruction);
		stmtV.fakeNewInsn(nop, newJump, newJumpInstruction);
		
		// We have added something, so we need to fix indices
		if (newJumpIdx <= jumpInsPos)
			jumpInsPos++;
		if (newJumpIdx <= targetInsPos)
			targetInsPos++;
		
		// Jump from the original instruction to the new one in the middle
		offsetInsn.setTarget(nop);
		BuilderInstruction replacementJumpInstruction = offsetInsn.getRealInsn(labelAssigner);
		assert instructions.get(jumpInsPos) == originalJumpInstruction;
		instructions.remove(jumpInsPos);
		instructions.add(jumpInsPos, replacementJumpInstruction);
		stmtV.fakeNewInsn(stmtV.getStmtForInstruction(originalJumpInstruction),
				originalJumpInsn, replacementJumpInstruction);
		
		// Our indices are still fine, because we just replaced something
		Stmt afterNewJump = stmtV.getStmtForInstruction(instructions.get(newJumpIdx + 1));
		
		// Make the original control flow jump around the new artificial jump instruction
		Insn10t jumpAround = new Insn10t(Opcode.GOTO);
		jumpAround.setTarget(afterNewJump);
		BuilderInstruction jumpAroundInstruction = jumpAround.getRealInsn(labelAssigner);
		instructions.add(newJumpIdx, jumpAroundInstruction);
		stmtV.fakeNewInsn(Jimple.v().newNopStmt(), jumpAround, jumpAroundInstruction);
	}
	
	private void addRegisterAssignmentDebugInfo(
			LocalRegisterAssignmentInformation registerAssignment,
			Map<Local, Integer> seenRegisters,
			MethodImplementationBuilder builder) {
		Local local = registerAssignment.getLocal();
		String dexLocalType = SootToDexUtils.getDexTypeDescriptor(local.getType());
		StringReference localName = dexFile.internStringReference(local.getName());
		Register reg = registerAssignment.getRegister();
		int register = reg.getNumber();
		
		Integer beforeRegister = seenRegisters.get(local);
		if (beforeRegister != null)
		{
			if (beforeRegister == register)
				//No change
				return;
			builder.addEndLocal(beforeRegister);
		}
		builder.addStartLocal(register, localName, dexFile.internTypeReference(dexLocalType), dexFile.internStringReference(""));
		seenRegisters.put(local, register);
	}

	private void toInstructions(Collection<Unit> units, StmtVisitor stmtV) {
		// Collect all constant arguments to monitor instructions and
		// pre-alloocate their registers
		Set<ClassConstant> monitorConsts = new HashSet<ClassConstant>();
		for (Unit u : units) {
			if (u instanceof MonitorStmt) {
				MonitorStmt monitorStmt = (MonitorStmt) u;
				if (monitorStmt.getOp() instanceof ClassConstant){
					monitorConsts.add((ClassConstant) monitorStmt.getOp());
				}
			}
		}
		
		boolean monitorAllocsMade = false;
		for (Unit u : units) {
			if (!monitorAllocsMade
					&& !monitorConsts.isEmpty()
					&& !(u instanceof IdentityStmt)) {
				stmtV.preAllocateMonitorConsts(monitorConsts);
				monitorAllocsMade = true;
			}
			
			stmtV.beginNewStmt((Stmt) u);
			u.apply(stmtV);
		}
		stmtV.finalizeInstructions();
	}
	
	private static class CodeRange {
		int startAddress;
		int endAddress;
		
		public CodeRange(int startAddress, int endAddress) {
			this.startAddress = startAddress;
			this.endAddress = endAddress;
		}
		
		/**
		 * Checks whether the given code range r is fully enclosed by this code
		 * range.
		 * @param r The other code range
		 * @return True if the given code range r is fully enclosed by this code
		 * range, otherwise false.
		 */
		public boolean containsRange(CodeRange r) {
			return (r.startAddress >= this.startAddress && r.endAddress <= this.endAddress);
		}
		
		/**
		 * Checks whether this range overlaps with the given one
		 * @param r The region to check for overlaps
		 * @return True if this region has a non-empty overlap with the given one
		 */
		public boolean overlaps(CodeRange r) {
			return (r.startAddress >= this.startAddress && r.startAddress < this.endAddress)
					|| (r.startAddress <= this.startAddress && r.endAddress > this.startAddress);
		}
		
		@Override
		public String toString() {
			return this.startAddress + "-" + this.endAddress;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == this)
				return true;
			if (other == null && !(other instanceof CodeRange))
				return false;
			CodeRange cr = (CodeRange) other;
			return (this.startAddress == cr.startAddress && this.endAddress == cr.endAddress);
		}
		
		@Override
		public int hashCode() {
			return 17 * startAddress + 13 * endAddress;
		}
	}
	
	private void toTries(Collection<Trap> traps, StmtVisitor stmtV,
			MethodImplementationBuilder builder, LabelAssigner labelAssigner) {
		// Original code: assume that the mapping startCodeAddress -> TryItem is enough for
		// 		a "code range", ignore different end Units / try lengths
		// That's definitely not enough since we can have two handlers H1, H2 with
		//		H1:240-322, H2:242-322. There is no valid ordering for such overlapping traps
		//		in dex. Current solution: If there is already a trap T' for a subrange of the
		//		current trap T, merge T and T' on the fully range of T. This is not a 100%
		//		correct since we extend traps over the requested range, but it's better than
		//		the previous code that produced APKs which failed Dalvik's bytecode verification.
		//		(Steven Arzt, 09.08.2013)
		// There are cases in which we need to split traps, e.g. in cases like
		//		( (t1) ... (t2) )<big catch all around it> where the all three handlers do
		//		something different. That's why we run the TrapSplitter before we get here.
		//		(Steven Arzt, 25.09.2013)
		Map<CodeRange, List<ExceptionHandler>> codeRangesToTryItem =
				new LinkedHashMap<CodeRange, List<ExceptionHandler>>();
		for (Trap t : traps) {
			// see if there is old handler info at this code range
			Stmt beginStmt = (Stmt) t.getBeginUnit();
			Stmt endStmt = (Stmt) t.getEndUnit();
			
			int startCodeAddress = labelAssigner.getLabel(beginStmt).getCodeAddress();
			int endCodeAddress = labelAssigner.getLabel(endStmt).getCodeAddress();
			CodeRange range = new CodeRange(startCodeAddress, endCodeAddress);
			
	        String exceptionType = SootToDexUtils.getDexTypeDescriptor(t.getException().getType());
	        
	        int codeAddress = labelAssigner.getLabel((Stmt) t.getHandlerUnit()).getCodeAddress();
			ImmutableExceptionHandler exceptionHandler = new ImmutableExceptionHandler
					(exceptionType, codeAddress);
			
			List<ExceptionHandler> newHandlers = new ArrayList<ExceptionHandler>();
			for (CodeRange r : codeRangesToTryItem.keySet()) {
				// Check whether this range is contained in some other range. We then extend our
				// trap over the bigger range containing this range
				if (r.containsRange(range)) {
					range.startAddress = r.startAddress;
					range.endAddress = r.endAddress;
					
					// copy the old handlers to a bigger array (the old one cannot be modified...)
					List<ExceptionHandler> oldHandlers = codeRangesToTryItem.get(r);
					if (oldHandlers != null)
						newHandlers.addAll(oldHandlers);
					break;
				}
				// Check whether the other range is contained in this range. In this case,
				// a smaller range is already in the list. We merge the two over the larger
				// range.
				else if (range.containsRange(r)) {
					range.startAddress = r.startAddress;
					range.endAddress = r.endAddress;

					// just use the newly found handler info
					List<ExceptionHandler> oldHandlers = codeRangesToTryItem.get(range);
					if (oldHandlers != null)
						newHandlers.addAll(oldHandlers);
					
					// remove the old range, the new one will be added anyway and contain
					// the merged handlers
					codeRangesToTryItem.remove(r);
					break;
				}
			}
			
			if (!newHandlers.contains(exceptionHandler))
				newHandlers.add(exceptionHandler);
			codeRangesToTryItem.put(range, newHandlers);
		}
		
		// Check for overlaps
		for (CodeRange r1 : codeRangesToTryItem.keySet()) {
			for (CodeRange r2 : codeRangesToTryItem.keySet()) {
				if (r1 != r2 && r1.overlaps(r2))
					System.out.println("WARNING: Trap region overlap detected");
			}
		}
		
		for (CodeRange range : codeRangesToTryItem.keySet()) {
			boolean allCaughtForRange = false;
			for (ExceptionHandler handler : codeRangesToTryItem.get(range)) {
				// If we have a catchall directive for a range and then some follow-up
				// exception handler, we can discard the latter as it will never be used
				// anyway.
				if (allCaughtForRange)
					continue;
				
				// Normally, we would model catchall as real catchall directives. For
				// some reason, this however fails with an invalid handler index. We
				// therefore hack it using java.lang.Throwable.
				if (handler.getExceptionType().equals("Ljava/lang/Throwable;")) {
					/*
					builder.addCatch(labelAssigner.getLabelAtAddress(range.startAddress),
							labelAssigner.getLabelAtAddress(range.endAddress),
							labelAssigner.getLabelAtAddress(handler.getHandlerCodeAddress()));
							*/
					allCaughtForRange = true;
				}
//				else
					builder.addCatch(dexFile.internTypeReference(handler.getExceptionType()),
							labelAssigner.getLabelAtAddress(range.startAddress),
							labelAssigner.getLabelAtAddress(range.endAddress),
							labelAssigner.getLabelAtAddress(handler.getHandlerCodeAddress()));
			}
		}
	}
	
	public void add(SootClass c) {
		if (c.isPhantom())
			return;
				
		addAsClassDefItem(c);
		// save original APK for this class, needed to copy all the other files inside
		Map<String, File> dexClassIndex = SourceLocator.v().dexClassIndex();
    	if (dexClassIndex == null) {
    		return; // no dex classes were loaded
    	}
		File sourceForClass = dexClassIndex.get(c.getName());
    	if (sourceForClass == null || sourceForClass.getName().endsWith(".dex")) {
    		return; // a class was written that was not a dex class or the class originates from a .dex file, not an APK
    	}
    	if (originalApk != null && !originalApk.equals(sourceForClass)) {
    		throw new CompilationDeathException("multiple APKs as source of an application are not supported");
    	}
    	originalApk = sourceForClass;
	}

	public void print() {
		String outputDir = SourceLocator.v().getOutputDir();
		try {
			if (originalApk != null
					&& Options.v().output_format() != Options.output_format_force_dex) {
				printApk(outputDir, originalApk);
			} else {
				String fileName = outputDir + File.separatorChar + CLASSES_DEX;
				G.v().out.println("Writing dex to: " + fileName);
				writeTo(fileName);
			}
		} catch (IOException e) {
			throw new CompilationDeathException("I/O exception while printing dex", e);
		}
	}

}