package soot.toDex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jf.dexlib2.Opcode;

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
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

/**
 * Utility class for the conversion from soot to dex.
 */
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
	
	public static String getDexTypeDescriptor(Type sootType) {
		final String typeDesc;
		if (sootType instanceof RefType) {
			typeDesc = getDexClassName(((RefType) sootType).getClassName());
		} else if (sootType instanceof ArrayType) {
			typeDesc = getDexArrayTypeDescriptor((ArrayType) sootType);
		} else {
			typeDesc = sootToDexTypeDescriptor.get(sootType.getClass());
		}
		
		if (typeDesc == null || typeDesc.isEmpty())
			throw new RuntimeException("Could not create type descriptor for class "
					+ sootType);
		return typeDesc;
	}
	
	public static String getDexClassName(String dottedClassName) {
		if (dottedClassName == null || dottedClassName.isEmpty())
			throw new RuntimeException("Empty class name detected");
		
		String slashedName = dottedClassName.replace('.', '/');
		if (slashedName.startsWith("L") && slashedName.endsWith(";"))
			return slashedName;
		return "L" + slashedName + ";";
	}

	public static int getDexAccessFlags(SootMethod m) {
		int dexAccessFlags = m.getModifiers();
		// dex constructor flag is not included in the Soot modifiers, so add it if necessary
		if (m.isConstructor() || m.getName().equals(SootMethod.staticInitializerName)) {
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
	
	public static String getArrayTypeDescriptor(ArrayType type) {
		Type baseType;
		if (type.numDimensions > 1) {
			baseType = ArrayType.v(type.baseType, 1);
		} else {
			baseType = type.baseType;
		}
		return getDexTypeDescriptor(baseType);
	}
	
	private static String getDexArrayTypeDescriptor(ArrayType sootArray) {
		if (sootArray.numDimensions > 255) {
			throw new RuntimeException("dex does not support more than 255 dimensions! " + sootArray + " has " + sootArray.numDimensions);
		}
		String baseTypeDescriptor = getDexTypeDescriptor(sootArray.baseType);
		StringBuilder sb = new StringBuilder(sootArray.numDimensions
				+ baseTypeDescriptor.length());
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
			Type regType = r.getType();
			regCount += getDexWords(regType);
		}
		return regCount;
	}
	
	public static int getDexWords(Type sootType) {
		return isWide(sootType) ? 2 : 1;
	}
	
	public static int getDexWords(List<Type> sootTypes) {
		int dexWords = 0;
		for (Type t : sootTypes) {
			dexWords += getDexWords(t);
		}
		return dexWords;
	}
	
	public static int getOutWordCount(Collection<Unit> units) {
		int outWords = 0;
		for (Unit u : units) {
			Stmt stmt = (Stmt) u;
			if (stmt.containsInvokeExpr()) {
				int wordsForParameters = 0;
				InvokeExpr invocation = stmt.getInvokeExpr();
				List<Value> args = invocation.getArgs();
				for (Value arg : args) {
					wordsForParameters += getDexWords(arg.getType());
				}
				if (!invocation.getMethod().isStatic()) {
					wordsForParameters++; // extra word for "this"
				}
				if (wordsForParameters > outWords) {
					outWords = wordsForParameters;
				}
			}
		}
		return outWords;
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
	
    /**
     * Split the signature string using the same algorithm as
     * in method 'Annotation makeSignature(CstString signature)'
     * in dx (dx/src/com/android/dx/dex/file/AnnotationUtils.java)
     *
     * Rules are:
     * ""
     * - scan to ';' or '<'. Consume ';' but not '<'.
     * - scan to 'L' without consuming it.
     * ""
     *
     * @param sig
     * @return
     */
    public static List<String> splitSignature(String sig) {
        List<String> split = new ArrayList<String>();
        int len = sig.length();
        int i = 0;
        int j = 0;
        while (i < len) {
            char c = sig.charAt(i);
            if (c == 'L') {
                j = i + 1;
                while (j < len) {
                    c = sig.charAt(j);
                    if (c == ';') {
                        j++;
                        break;
                    } else if (c == '<') {
                        break;
                    }
                    j++;
                }
            } else {
                for (j = i + 1; j < len && sig.charAt(j) != 'L'; j++) {
                }
            }
            split.add(sig.substring(i, j));
            i = j;
        }
        return split;
    }

}
