package soot.asm_backend;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for an interface class and accessibility modifiers
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class InterfaceTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		visitor.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
		"pkg/Comparable", null, "java/lang/Object",
		new String[] { "pkg/Measurable" });
		visitor.visitSource("Comparable.java", null);
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I",
		null, new Integer(-1)).visitEnd();
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I",
		null, new Integer(0)).visitEnd();
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I",
		null, new Integer(1)).visitEnd();
		visitor.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
				"(Ljava/lang/Object;)I", null, null).visitEnd();
		visitor.visitEnd();
		
	}

	@Override
	protected String getTargetClass() {
		return "pkg.Comparable";
	}

	@Override
	protected String getTargetFolder() {
		return "./testcode_asm_backend";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testcode_asm_backend";
	}

}
