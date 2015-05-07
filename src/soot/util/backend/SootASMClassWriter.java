package soot.util.backend;

import static soot.util.backend.ASMBackendUtils.slashify;

import org.objectweb.asm.ClassWriter;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;

/**
 * ASM class writer with soot-specific resolution of common superclasses
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class SootASMClassWriter extends ClassWriter{

	/**
     * Constructs a new {@link ClassWriter} object.
     * 
     * @param flags
     *            option flags that can be used to modify the default behavior
     *            of this class. See {@link #COMPUTE_MAXS},
     *            {@link #COMPUTE_FRAMES}.
     */
	public SootASMClassWriter(int flags) {
		super(flags);
	}
	
	/*
	 * We need to overwrite this method here, as we are generating
	 * multiple classes that might reference each other. See asm4-guide,
	 * top of page 45 for more information.
	 */	
	/* (non-Javadoc)
	 * @see org.objectweb.asm.ClassWriter#getCommonSuperClass(java.lang.String, java.lang.String)
	 */
	@Override
	protected String getCommonSuperClass(String type1, String type2) {
		String typeName1 = type1.replace('/', '.');
		String typeName2 = type2.replace('/', '.');

		SootClass s1 = Scene.v().getSootClass(typeName1);
		SootClass s2 = Scene.v().getSootClass(typeName2);

		Type t1 = s1.getType();
		Type t2 = s2.getType();
		
		Type mergedType = t1.merge(t2, Scene.v());

		if (mergedType instanceof RefType) {
			return slashify(((RefType) mergedType).getClassName());
		} else {
			throw new RuntimeException(
					"Could not find common super class");
		}
	}

}
