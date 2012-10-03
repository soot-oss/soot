package soot.toDex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jf.dexlib.Code.Opcode;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.Modifier;
import soot.RefLikeType;
import soot.RefType;
import soot.ShortType;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.toDex.instructions.AddressInsn;
import soot.toDex.instructions.Insn;

public class SootToDexUtils {
	
	private static final Map<Class<? extends Type>, String> sootToDexTypeDescriptor;
	
	static {
		sootToDexTypeDescriptor = new HashMap<Class<? extends Type>, String>();
		sootToDexTypeDescriptor.put(BooleanType.class, "Z");
		sootToDexTypeDescriptor.put(ByteType.class, "B");
		sootToDexTypeDescriptor.put(CharType.class, "C");
		sootToDexTypeDescriptor.put(DoubleType.class, "D");
		sootToDexTypeDescriptor.put(FloatType.class, "F");
		sootToDexTypeDescriptor.put(IntType.class, "I");
		sootToDexTypeDescriptor.put(LongType.class, "J");
		sootToDexTypeDescriptor.put(ShortType.class, "S");
		sootToDexTypeDescriptor.put(VoidType.class, "V");
	}
	
	public static String toDexTypeDescriptor(Type sootType) {
		if (sootType instanceof RefType) {
			return toDexClassName(((RefType) sootType).getClassName());
		} else if (sootType instanceof ArrayType) {
			return toDexArrayTypeDescriptor((ArrayType) sootType);
		} else {
			return sootToDexTypeDescriptor.get(sootType.getClass());
		}
	}
	
	public static String toDexClassName(String dottedClassName) {
		String slashedName = dottedClassName.replace('.', '/');
		return "L" + slashedName + ";";
	}

	public static int toDexAccessFlags(SootMethod m) {
		int dexAccessFlags = m.getModifiers();
		// dex constructor flag is not included in the Soot modifiers, so add it if necessary
		if (m.isConstructor()) {
			dexAccessFlags |= Modifier.CONSTRUCTOR;
		}
		// add declared_synchronized for dex if synchronized
		if (m.isSynchronized()) {
			dexAccessFlags |= Modifier.DECLARED_SYNCHRONIZED;
			// even remove synchronized if not native, since only allowed there
			if (!m.isNative()) {
				dexAccessFlags &= ~Modifier.SYNCHRONIZED;
			}
		}
		return dexAccessFlags;
	}
	
	public static String toDexArrayTypeDescriptor(ArrayType sootArray) {
		if (sootArray.numDimensions > 255) {
			throw new RuntimeException("dex does not support more than 255 dimensions! " + sootArray + " has " + sootArray.numDimensions);
		}
		String baseTypeDescriptor = toDexTypeDescriptor(sootArray.baseType);
		StringBuilder sb = new StringBuilder(sootArray.numDimensions);
		for (int i = 0; i < sootArray.numDimensions; i++) {
			sb.append('[');
		}
		sb.append(baseTypeDescriptor);
		return sb.toString();
	}
	
	public static boolean isObject(String typeDescriptor) {
		return typeDescriptor.startsWith("L") || typeDescriptor.startsWith("[");
	}
	
	public static boolean isObject(Type sootType) {
		return sootType instanceof RefLikeType;
	}
	
	public static boolean isWide(String typeDescriptor) {
		return typeDescriptor.equals("J") || typeDescriptor.equals("D");
	}
	
	public static boolean isWide(Type sootType) {
		return sootType instanceof LongType || sootType instanceof DoubleType;
	}
	
	public static int getRealRegCount(List<Register> regs) {
		int regCount = 0;
		for (Register r : regs) {
			regCount += r.isWide() ? 2 : 1;
		}
		return regCount;
	}
	
	public static int toDexWords(Type sootType) {
		if (isWide(sootType)) {
			return 2;
		}
		return 1;
	}
	
	public static int toDexWords(List<Type> sootTypes) {
		int dexWords = 0;
		for (Type t : sootTypes) {
			dexWords += toDexWords(t);
		}
		return dexWords;
	}
	
	// we could use some fancy shift operations...
	
	public static boolean fitsSigned4(long literal) {
		return literal >= -8 && literal <= 7;
	}
	
	public static boolean fitsSigned8(long literal) {
		return literal >= -128 && literal <= 127;
	}
	
	public static boolean fitsSigned16(long literal) {
		return literal >= -32768 && literal <= 32767;
	}
	
	public static boolean fitsSigned32(long literal) {
		return literal >= -2147483648 && literal <= 2147483647;
	}
	
	public static boolean isNormalMove(Opcode opc) {
		return opc.name.startsWith("move") && !opc.name.startsWith("move-result");
	}
	
	public static int getOffset(Object originalStmt, List<Insn> insns) {
		for (Insn curInsn : insns) {
			if (curInsn instanceof AddressInsn) {
				AddressInsn curAddress = (AddressInsn) curInsn;
				if (curAddress.getOriginalSource().equals(originalStmt)) {
					return curAddress.getInsnOffset();
				}
			}
		}
		throw new RuntimeException("original statement not found: " + originalStmt);
	}
}