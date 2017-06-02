package soot.asm.backend;

import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import soot.G;
import soot.Main;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;

/**
 * Test for fields that contain constant values
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ConstantPoolTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/ConstantPool", null,
				"java/lang/Object", null);
		cw.visitSource("ConstantPool.java", null);

		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "s1",
					"Ljava/lang/String;", null, "H:mm:ss.SSS");
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "s2",
					"Ljava/lang/String;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "o1",
					"Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "o2",
					"Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "o3",
					"Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "o4",
					"Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "o5",
					"Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "i1", "I",
					null, new Integer(123));
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "i2", "I",
					null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "l1", "J",
					null, new Long(12233L));
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "l2", "J",
					null, new Long(123L));
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "l3", "J",
					null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "d1", "D",
					null, new Double("123.142"));
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "d2", "D",
					null, new Double("1234.123046875"));
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "d3", "D",
					null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
			mv.visitCode();
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "s2",
					"Ljava/lang/String;");
			mv.visitLdcInsn("O");
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "o1",
					"Ljava/lang/Object;");
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "o2",
					"Ljava/lang/Object;");
			mv.visitIntInsn(BIPUSH, 123);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
					"(I)Ljava/lang/Integer;", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "o3",
					"Ljava/lang/Object;");
			mv.visitLdcInsn(new Long(1234L));
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf",
					"(J)Ljava/lang/Long;", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "o4",
					"Ljava/lang/Object;");
			mv.visitLdcInsn(new Double("123.3"));
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf",
					"(D)Ljava/lang/Double;", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "o5",
					"Ljava/lang/Object;");
			mv.visitTypeInsn(NEW, "java/lang/Integer");
			mv.visitInsn(DUP);
			mv.visitIntInsn(BIPUSH, 123);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>",
					"(I)V", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
					"()I", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "i2", "I");
			mv.visitTypeInsn(NEW, "java/lang/Long");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(new Long(12341L));
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Long", "<init>",
					"(J)V", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue",
					"()J", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "l3", "J");
			mv.visitTypeInsn(NEW, "java/lang/Double");
			mv.visitInsn(DUP);
			mv.visitLdcInsn(new Double("1234.123"));
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Double", "<init>",
					"(D)V", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double",
					"doubleValue", "()D", false);
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/ConstantPool", "d3", "D");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();
	}

	@Test
	public void loadClass() {
		G.reset();
		// Location of the rt.jar
		String rtJar = System.getProperty("java.home") + File.separator + "lib"
				+ File.separator + "rt.jar";

		// Run Soot and print output to .asm-files.
		Main.main(new String[] { "-cp",
				getClassPathFolder() + File.pathSeparator + rtJar,
				"-process-dir", getTargetFolder(), "-src-prec", "only-class",
				"-output-format", "class", "-asm-backend",
				"-allow-phantom-refs", "-java-version",
				getRequiredJavaVersion(), getTargetClass() });

		File file = new File("./sootOutput/ConstantPool.class");
		URL[] urls = null;
		try {
			URL url = file.toURI().toURL();
			urls = new URL[] { url };
			URLClassLoader cl = new URLClassLoader(urls);

			cl.loadClass(getTargetClass());
			
			// cl.close();
			// Java 6 backwards compatibility hack
			try {
				for (Method m : URLClassLoader.class.getDeclaredMethods()) {
					if (m.getName().equals("close")) {
						m.invoke(cl);
						break;
					}
				}
			}
			catch (Exception e) {
			}
			return;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassFormatException e) {
			e.printStackTrace();
		}

		fail();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.ConstantPool";
	}

}
