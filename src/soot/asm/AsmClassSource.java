package soot.asm;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

import soot.ClassSource;
import soot.SootClass;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * ASM class source implementation.
 */
class AsmClassSource extends ClassSource {

	private InputStream data;
	
	/**
	 * Constructs a new ASM class source.
	 * @param cls fully qualified name of the class.
	 * @param data stream containing data for class.
	 */
	AsmClassSource(String cls, InputStream data) {
		super(cls);
		this.data = data;
	}
	
	private ClassReader read() throws IOException {
		InputStream d = data;
		if (d == null)
			throw new IllegalStateException();
		data = null;
		try {
			return new ClassReader(d);
		} finally {
			d.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Dependencies resolve(SootClass sc) {
		ClassReader clsr;
		try {
			clsr = read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		SootClassBuilder scb = new SootClassBuilder(sc);
		clsr.accept(scb, ClassReader.SKIP_FRAMES);
		Dependencies deps = new Dependencies();
		deps.typesToSignature.addAll(scb.deps);
		return deps;
	}
}