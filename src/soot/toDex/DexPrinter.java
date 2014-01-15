package soot.toDex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.ClassDataItem.EncodedField;
import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.CodeItem;
import org.jf.dexlib.CodeItem.EncodedCatchHandler;
import org.jf.dexlib.CodeItem.EncodedTypeAddrPair;
import org.jf.dexlib.CodeItem.TryItem;
import org.jf.dexlib.DebugInfoItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;
import org.jf.dexlib.Util.DebugInfoBuilder;
import org.jf.dexlib.Util.Pair;

import soot.Body;
import soot.CompilationDeathException;
import soot.G;
import soot.PackManager;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SourceLocator;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.scalar.EmptySwitchEliminator;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

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
	
	private static DexFile dexFile;
	
	private File originalApk;
	
	private static DexAnnotation dexAnnotation;
	
	public DexPrinter() {
		dexFile = new DexFile();
		//dexAnnotation = new DexAnnotation(dexFile);
	}
	
	private void printApk(String outputDir, File originalApk) throws IOException {
		ZipOutputStream outputApk;
		if(Options.v().output_jar()) {
			outputApk = PackManager.v().getJarFile();
			G.v().out.println("Writing APK to: " + Options.v().output_dir());
		} else {
			String outputFileName = outputDir + File.separatorChar + originalApk.getName();
		
			File outputFile = new File(outputFileName);
			if(outputFile.exists()) {
				throw new CompilationDeathException("Output file "+outputFile+" exists. Not overwriting.");
			} 
			outputApk = new ZipOutputStream(new FileOutputStream(outputFile));
			G.v().out.println("Writing APK to: " + outputFileName);
		}
		G.v().out.println("do not forget to sign the .apk file with jarsigner and to align it with zipalign");
		ZipFile original = new ZipFile(originalApk);
		copyAllButClassesDexAndSigFiles(original, outputApk);
		original.close();
		
		// put our classes.dex into the zip archive
		outputApk.putNextEntry(new ZipEntry(CLASSES_DEX));
		writeTo(outputApk);
		outputApk.closeEntry();
		outputApk.close();
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

	private void writeTo(OutputStream outputStream) throws IOException {
	    
		dexFile.place();
		
		byte[] outArray = new byte[dexFile.getFileSize()];
		ByteArrayAnnotatedOutput outBytes = new ByteArrayAnnotatedOutput(outArray);
		dexFile.writeTo(outBytes);
		DexFile.calcSignature(outArray);
		DexFile.calcChecksum(outArray);
		outputStream.write(outArray);
		outputStream.flush();
		
//		TODO: requires baksmali-1.4.2-dev.jar
//		if (Debug.TODEX_DEBUG) {
//    		List<MethodIdItem> methods = dexFile.MethodIdsSection.getItems();
//    		for (MethodIdItem m : methods) {
//    		    Debug.printDbg("method id: "+ m.getIndex() +" "+ m.getMethodString());
//    		}
//    		
//    		List<FieldIdItem> fields = dexFile.FieldIdsSection.getItems();
//    		for (FieldIdItem f : fields) {
//    		    Debug.printDbg("field id: "+ f.getIndex() +" "+ f.getFieldString());
//    		}
//    		
//    		List<AnnotationDirectoryItem> dirs = dexFile.AnnotationDirectoriesSection.getItems();
//    		for (AnnotationDirectoryItem dir : dirs) {	 
//    		    Debug.printDbg("    ---dir "+ dir);
//                List<FieldAnnotation> fas = dir.getFieldAnnotations();
//                for (FieldAnnotation fa : fas) {
//                    Debug.printDbg("field annotation: "+ fa.field.getIndex());
//                }
//                List<MethodAnnotation> mas = dir.getMethodAnnotations();
//                for (MethodAnnotation ma : mas) {
//                    Debug.printDbg("method annotation: "+ ma.method.getIndex() +" "+ ma.method.getMethodString());
//                }
//    		}
//		}
	}

	private void addAsClassDefItem(SootClass c) {
	    dexAnnotation = new DexAnnotation(dexFile, c);
		TypeIdItem classType = toTypeIdItem(c.getType(), dexFile);
		int accessFlags = c.getModifiers();
		TypeIdItem superType = c.hasSuperclass() ? toTypeIdItem(c.getSuperclass().getType(), dexFile) : null;
		List<Type> interfaceTypes = new ArrayList<Type>(c.getInterfaceCount());
		for (SootClass implementedInterface : c.getInterfaces()) {
			interfaceTypes.add(implementedInterface.getType());
		}
		TypeListItem implementedInterfaces = toTypeListItem(interfaceTypes, dexFile);
		ClassDataItem classData = toClassDataItem(c, dexFile);
		AnnotationDirectoryItem di= dexAnnotation.finish();
		// staticFieldInitializers is not used since the <clinit> method should be enough
        // add source file tag if any
        StringIdItem sourceFileItem = null;
        if (c.hasTag("SourceFileTag")) {
            SourceFileTag sft = (SourceFileTag) c.getTag("SourceFileTag");
            String sourceFile = sft.getSourceFile();
            sourceFileItem = StringIdItem.internStringIdItem(dexFile, sourceFile);
        }
        ClassDefItem.internClassDefItem(dexFile, classType, accessFlags, superType,
                implementedInterfaces, sourceFileItem, di, classData, null);
		dexAnnotation = null;
	}
	
	private static ClassDataItem toClassDataItem(SootClass c, DexFile belongingDexFile) {
		Pair<List<EncodedField>, List<EncodedField>> fields = toFields(c.getFields(), belongingDexFile);
		Pair<List<EncodedMethod>, List<EncodedMethod>> methods = toMethods(c.getMethods(), belongingDexFile);
		ClassDataItem citem = ClassDataItem.internClassDataItem(belongingDexFile, fields.first, fields.second, methods.first, methods.second);
		dexAnnotation.handleClass(c, citem);
		return citem;
	}
	
	private static Pair<List<EncodedField>, List<EncodedField>> toFields(Collection<SootField> sootFields, DexFile belongingDexFile) {
		List<EncodedField> staticFields = new ArrayList<EncodedField>();
		List<EncodedField> instanceFields = new ArrayList<EncodedField>();
		Pair<List<EncodedField>, List<EncodedField>> fields = new Pair<List<EncodedField>, List<EncodedField>>(staticFields, instanceFields);
		for (SootField f : sootFields) {
			if (f.isPhantom()) {
				continue;
			}
			FieldIdItem fieldIdItem = toFieldIdItem(f, belongingDexFile);
			int accessFlags = f.getModifiers();
			EncodedField ef = new EncodedField(fieldIdItem, accessFlags);
			if (f.isStatic()) {
				staticFields.add(ef);
			} else {
				instanceFields.add(ef);
			}
		}
		return fields;
	}
	
	private static Pair<List<EncodedMethod>, List<EncodedMethod>> toMethods(Collection<SootMethod> sootMethods, DexFile belongingDexFile) {
		List<EncodedMethod> directMethods = new ArrayList<EncodedMethod>();
		List<EncodedMethod> virtualMethods = new ArrayList<EncodedMethod>();
		Pair<List<EncodedMethod>, List<EncodedMethod>> methods = new Pair<List<EncodedMethod>, List<EncodedMethod>>(directMethods, virtualMethods);
		for (SootMethod m : sootMethods) {
			if (m.isPhantom()) {
				continue;
			}
			MethodIdItem methodIdItem = toMethodIdItem(m.makeRef(), belongingDexFile);
			dexAnnotation.handleMethod(m, methodIdItem);
			int accessFlags = SootToDexUtils.getDexAccessFlags(m);
			CodeItem codeItem = toCodeItem(m, belongingDexFile);
			EncodedMethod eM = new EncodedMethod(methodIdItem, accessFlags, codeItem);
			if (eM.isDirect()) {
				directMethods.add(eM);
			} else {
				virtualMethods.add(eM);
			}
		}
		return methods;
	}
	
	private static TypeListItem toTypeListItem(List<Type> types, DexFile belongingDexFile) {
		List<TypeIdItem> typeItems = new ArrayList<TypeIdItem>(types.size());
		for (Type t : types) {
			typeItems.add(toTypeIdItem(t, belongingDexFile));
		}
		return TypeListItem.internTypeListItem(belongingDexFile, typeItems);
	}
	
	protected static FieldIdItem toFieldIdItem(SootField f, DexFile belongingDexFile) {
		TypeIdItem declaringClassType = toTypeIdItem(f.getDeclaringClass().getType(), belongingDexFile);
		TypeIdItem fieldType = toTypeIdItem(f.getType(), belongingDexFile);
		StringIdItem fieldName = StringIdItem.internStringIdItem(belongingDexFile, f.getName());
		FieldIdItem fid = FieldIdItem.internFieldIdItem(belongingDexFile, declaringClassType, fieldType, fieldName);
		dexAnnotation.handleField(f, fid);
		return fid;
	}
	
	protected static MethodIdItem toMethodIdItem(SootMethodRef m, DexFile belongingDexFile) {
		// we use a method ref and not a method since the former is unresolved and thus not changed during conversion
		TypeIdItem declaringClassType = toTypeIdItem(m.declaringClass().getType(), belongingDexFile);
		ProtoIdItem methodType = toProtoIdItem(m, belongingDexFile);
		StringIdItem methodName = StringIdItem.internStringIdItem(belongingDexFile, m.name());
		MethodIdItem mid = MethodIdItem.internMethodIdItem(belongingDexFile, declaringClassType, methodType, methodName);
		return mid;
	}
	
	private static ProtoIdItem toProtoIdItem(SootMethodRef m, DexFile belongingDexFile) {
		TypeIdItem returnType = toTypeIdItem(m.returnType(), belongingDexFile);
		List<Type> parameterTypes = m.parameterTypes();
		TypeListItem parameters = toTypeListItem(parameterTypes, belongingDexFile);
		return ProtoIdItem.internProtoIdItem(belongingDexFile, returnType, parameters);
	}
	
	protected static TypeIdItem toTypeIdItem(Type sootType, DexFile belongingDexFile) {
		String dexTypeDescriptor = SootToDexUtils.getDexTypeDescriptor(sootType);
		StringIdItem dexDescriptor = StringIdItem.internStringIdItem(belongingDexFile, dexTypeDescriptor);
		return TypeIdItem.internTypeIdItem(belongingDexFile, dexDescriptor);
	}

	private static CodeItem toCodeItem(SootMethod m, DexFile belongingDexFile) {
		if (m.isAbstract() || m.isNative()) {
			return null;
		}
		Body activeBody = m.retrieveActiveBody();
		
		// Switch statements may not be empty in dex, so we have to fix this first
		EmptySwitchEliminator.v().transform(activeBody);
		
		// Dalvik requires synchronized methods to have explicit monitor calls,
		// so we insert them here. See http://milk.com/kodebase/dalvik-docs-mirror/docs/debugger.html
		// We cannot place this upon the developer since it is only required
		// for Dalvik, but not for other targets.
		SynchronizedMethodTransformer.v().transform(activeBody);

		// word count of incoming parameters
		int inWords = SootToDexUtils.getDexWords(m.getParameterTypes());
		if (!m.isStatic()) {
			inWords++; // extra word for "this"
		}
		// word count of max outgoing parameters
		Collection<Unit> units = activeBody.getUnits();
		int outWords = SootToDexUtils.getOutWordCount(units);
		// register count = parameters + additional registers, depending on the dex instructions generated (e.g. locals used and constants loaded)
		StmtVisitor stmtV = new StmtVisitor(m, belongingDexFile);
		List<Instruction> instructions = toInstructions(units, stmtV);
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

		// Split the tries since Dalvik does not supported nested try/catch blocks
		TrapSplitter.v().transform(activeBody);

        DebugInfoBuilder diBuilder = new DebugInfoBuilder();
        Map<Instruction, List<Tag>> instructionTagMap = stmtV.getInstructionTagMap();
        // get line numbers
        int codeAddress = 0;
        for (Instruction i : instructions) {
            if (instructionTagMap.containsKey(i)) {
                List<Tag> tags = instructionTagMap.get(i);
                for (Tag t : tags) {
                    if (t instanceof LineNumberTag) {
                        LineNumberTag lnt = (LineNumberTag) t;
                        int lineNumber = lnt.getLineNumber();
                        diBuilder.addLine(codeAddress, lineNumber);
                        Debug.printDbg(" [toDex] add line " + lineNumber + " at code offset: "
                                + codeAddress + " (" + i + ")");
                    }
                }
            }

            codeAddress += i.getSize(codeAddress);
        }

		List<EncodedCatchHandler> encodedCatchHandlers = new ArrayList<CodeItem.EncodedCatchHandler>();
		List<TryItem> tries = toTries(activeBody.getTraps(), encodedCatchHandlers, stmtV, belongingDexFile);
        DebugInfoItem debugInfo = diBuilder.encodeDebugInfo(belongingDexFile);

        return CodeItem.internCodeItem(belongingDexFile, registerCount, inWords, outWords,
                debugInfo, instructions, tries, encodedCatchHandlers);
	}

	private static List<Instruction> toInstructions(Collection<Unit> units, StmtVisitor stmtV) {
		for (Unit u : units) {
			stmtV.beginNewStmt((Stmt) u);
			u.apply(stmtV);
		}
		return stmtV.getFinalInsns();
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
	
	private static List<TryItem> toTries(Collection<Trap> traps, List<EncodedCatchHandler> encodedCatchHandlers, StmtVisitor stmtV, DexFile belongingDexFile) {
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
		Map<CodeRange, TryItem> codeRangesToTryItem = new HashMap<CodeRange, TryItem>();
		for (Trap t : traps) {
			EncodedTypeAddrPair newHandlerInfo = createNewHandlerInfo(t, stmtV, belongingDexFile);
			EncodedTypeAddrPair[] handlersInfo = null;
			
			// see if there is old handler info at this code range
			Stmt beginStmt = (Stmt) t.getBeginUnit();
			Stmt endStmt = (Stmt) t.getEndUnit();
			int startCodeAddress = stmtV.getOffset(beginStmt);
			int tryLength = stmtV.getOffset(endStmt) - startCodeAddress; // FIXME this is not that simple - a jimple stmt belongs to 1 to N dex insns, so the length could be bigger
			
			CodeRange range = new CodeRange(startCodeAddress, startCodeAddress + tryLength);
			
			boolean found = false;
			for (CodeRange r : codeRangesToTryItem.keySet()) {
				// Check whether this range is contained in some other range. We then extend our
				// trap over the bigger range containing this range
				if (r.containsRange(range)) {
					range.startAddress = r.startAddress;
					range.endAddress = r.endAddress;
					
					// copy the old handlers to a bigger array (the old one cannot be modified...)
					TryItem oldTryItem = codeRangesToTryItem.get(r);
					handlersInfo = addNewHandlerInfo(newHandlerInfo, oldTryItem.encodedCatchHandler.handlers);
					found = true;
					break;
				}
				// Check whether the other range is contained in this range. In this case,
				// a smaller range is already in the list. We merge the two over the larger
				// range.
				else if (range.containsRange(r)) {
					// just use the newly found handler info
					TryItem oldTryItem = codeRangesToTryItem.get(r);
					handlersInfo = addNewHandlerInfo(newHandlerInfo, oldTryItem.encodedCatchHandler.handlers);
					
					// remove the old range, the new one will be added anyway and contain
					// the merged handlers
					codeRangesToTryItem.remove(r);
					
					found = true;
					break;
				}
			}

			if (!found)
				handlersInfo = new EncodedTypeAddrPair[]{newHandlerInfo};

			int catchAllHandlerAddress = -1; // due to Soot, we cannot distinguish a "finally" exception handler from the others
			EncodedCatchHandler handler = new EncodedCatchHandler(handlersInfo , catchAllHandlerAddress);
			encodedCatchHandlers.add(handler);
			TryItem newTryItem = new TryItem(startCodeAddress, tryLength, handler);
			codeRangesToTryItem.put(range, newTryItem);
		}
		return toSortedTries(codeRangesToTryItem.values());
	}

	private static EncodedTypeAddrPair createNewHandlerInfo(Trap t, StmtVisitor stmtV, DexFile belongingDexFile) {
		Stmt handlerStmt = (Stmt) t.getHandlerUnit();
		int handlerAddress = stmtV.getOffset(handlerStmt);
		TypeIdItem exceptionTypeIdItem = toTypeIdItem(t.getException().getType(), belongingDexFile);
		return new EncodedTypeAddrPair(exceptionTypeIdItem, handlerAddress);
	}

	private static EncodedTypeAddrPair[] addNewHandlerInfo(EncodedTypeAddrPair newHandler, EncodedTypeAddrPair[] oldHandlers) {
		// copy old handlers to new array
		int oldHandlersSize = oldHandlers.length;
		EncodedTypeAddrPair[] newHandlers = new EncodedTypeAddrPair[oldHandlersSize + 1];
		System.arraycopy(oldHandlers, 0, newHandlers, 0, oldHandlersSize);
		// add the new one
		newHandlers[newHandlers.length - 1] = newHandler;
		return newHandlers;
	}
	
	private static List<TryItem> toSortedTries(Collection<TryItem> unsortedTries) {
		List<TryItem> tries = new ArrayList<TryItem>(unsortedTries);
				
		// sort the tries in order from low to high address
		Collections.sort(tries, new Comparator<TryItem>() {
			@Override
			public int compare(TryItem a, TryItem b) {
				int addressA = a.getStartCodeAddress() + a.getTryLength();
				int addressB = b.getStartCodeAddress() + b.getTryLength();
				return addressA - addressB;
			}
		});
		
		// Validate the sorted tries. This is a direct translation of
		// "swapTriesAndCatches" from DexSwapVerify.c
		int lastEnd = 0;
		for (int tryIdx = 0; tryIdx < tries.size(); tryIdx++) {
			TryItem ti = tries.get(tryIdx);
			if (ti.getStartCodeAddress() < lastEnd)
				throw new RuntimeException("Out-of-order try block");
			lastEnd = ti.getStartCodeAddress() + ti.getTryLength();
		}
		
		return tries;
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
		assertClassesAdded();
		String outputDir = SourceLocator.v().getOutputDir();
		try {
			if (originalApk != null) {
				printApk(outputDir, originalApk);
			} else {
				String fileName = outputDir + File.separatorChar + CLASSES_DEX;
				G.v().out.println("Writing dex to: " + fileName);
				OutputStream outputStream = new FileOutputStream(fileName);
				writeTo(outputStream);
				outputStream.close();
			}
		} catch (IOException e) {
			throw new CompilationDeathException("I/O exception while printing dex", e);
		}
	}

	private void assertClassesAdded() {
		List<ClassDefItem> classes = dexFile.ClassDefsSection.getItems();
		if (classes.isEmpty()) {
			// the dexlib would respond with an IndexOutOfBoundsException while printing the dexfile
			throw new IllegalStateException("there were no classes added");
		}
	}
}