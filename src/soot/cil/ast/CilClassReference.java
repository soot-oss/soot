package soot.cil.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.cil.Cil_Utils;

/**
 * A reference to a class in a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilClassReference {
	
	private final String className;
	private final List<CilClassReference> genericInstances;
	
	public CilClassReference(String className) {
		this(className, null);
	}
	
	public CilClassReference(String className, List<CilClassReference> genericInstances) {
		this.className = Cil_Utils.removeGenericsDeclaration(className);
		this.genericInstances = genericInstances;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	public List<CilClassReference> getGenericInstances() {
		return this.genericInstances;
	}
		
	/**
	 * Gets the class reference string, i.e., the class name with the generics
	 * replaced according to the generic type instance map.
	 * @return The class reference string
	 */
	public String getClassReference() {
		if (genericInstances == null)
			return className;
		else {
			String str = className + "`" + getGenericInstances().size();
			str += "<";
			
			boolean isFirst = true;
			for (CilClassReference ref : genericInstances) {
				if (!isFirst)
					str += ",";
				isFirst = false;
				str += ref.getClassReference();
			}
			
			str += ">";
			return str;
		}		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime
				* result
				+ ((genericInstances == null) ? 0 : genericInstances.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CilClassReference other = (CilClassReference) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (genericInstances == null) {
			if (other.genericInstances != null)
				return false;
		} else if (!genericInstances.equals(other.genericInstances))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String res = className;
		if (genericInstances != null) {
			res += "<";
			boolean isFirst = true;
			for (CilClassReference ref : genericInstances) {
				if (!isFirst)
					res += ", ";
				isFirst = false;
				res += ref.toString();
			}
			res += ">";
		}
		return res;
	}
	
	/**
	 * Creates a mangled class name for the referenced target class
	 * @return The mangled class name representing this reference
	 */
	public String getMangledName() {
		String mangledName = className;
		if (this.genericInstances != null) {
			 mangledName += "__" + this.genericInstances.size() + "_";
			 for (CilClassReference ref : this.genericInstances)
				 mangledName += "_" + ref.getMangledName();
		}
		return mangledName;
	}
	
	@Override
	public CilClassReference clone() {
		List<CilClassReference> generics = new ArrayList<CilClassReference>(genericInstances.size());
		for (CilClassReference ref : genericInstances)
			generics.add(ref == null ? null : ref.clone());
		return new CilClassReference(className, generics);
	}
	
	/**
	 * Replaces the generic placeholders in this class reference with actual
	 * types from the current context
	 * @param clazz The class in whose context the reference is evaluated
	 * @return The new class reference with precise generic types
	 */
	public CilClassReference resolveGenerics(CilClass clazz) {
		return resolveGenerics(clazz, new HashSet<String>());
	}
	
	/**
	 * Replaces the generic placeholders in this class reference with actual
	 * types from the current context
	 * @param clazz The class in whose context the reference is evaluated
	 * @param doneSet The set of already-processed generic types
	 * @return The new class reference with precise generic types
	 */
	private CilClassReference resolveGenerics(CilClass clazz, Set<String> doneSet) {
		String newClassName = className;
		List<CilClassReference> newGenerics = null;
		
		// Check for the superclass
		if (className.startsWith("!")) {
			if (!doneSet.add(className))
				return new CilClassReference(className);
			
			CilClassReference superClassRef = clazz.getGenericParams().getElementByName(
					className.substring(1)).getSuperType();
			newClassName = superClassRef.getClassName();
			if (superClassRef.genericInstances != null) {
				if (this.genericInstances != null)
					throw new RuntimeException("Multiple generic nesting not supported");
				
				newGenerics = new ArrayList<CilClassReference>(superClassRef.genericInstances.size());
				for (CilClassReference ref : superClassRef.genericInstances)
					newGenerics.add(ref == null ? null : ref.resolveGenerics(clazz, doneSet));
			}
		}
		
		// Resolve the nested generics
		if (genericInstances != null) {
			newGenerics = new ArrayList<CilClassReference>(genericInstances.size());
			for (CilClassReference ref : genericInstances)
				newGenerics.add(ref == null ? null : ref.resolveGenerics(clazz, doneSet));
		}
		
		return new CilClassReference(newClassName, newGenerics);
	}
	
	/**
	 * Gets whether this reference points to a generic class
	 * @return True if this reference points to a generic class, otherwise false
	 */
	public boolean isGenericClass() {
		return this.genericInstances != null;
	}

}
