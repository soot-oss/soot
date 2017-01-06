package soot.cil.ast;

import java.util.ArrayList;
import java.util.List;

import soot.cil.Cil_Utils;


/**
 * Class for moedelling a class inside a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilClass {
	private String className;
	private CilGenericDeclarationList generics;
	private boolean isInterface;
	private int accessModifiers;
	
	private CilTypeRef superclass;
	private List<CilTypeRef> interfaces = null;
	
	public CilClass(String className,
			CilGenericDeclarationList generics,
			boolean isInterface,
			int accessModifiers) {
		this.className = Cil_Utils.removeGenericsDeclaration(className);
		this.generics = generics;
		this.isInterface = isInterface;
		this.accessModifiers = accessModifiers;
	}
	
	/**
	 * Gets the base class name, ignoring any generics
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Gets the unique name of the class including the number of generics
	 * @return The uniuque, mangled class name
	 */
	public String getUniqueClassName() {
		String mangledName = className;
		if (this.generics != null && !this.generics.isEmpty())
			 mangledName += "__" + this.generics.size();
		return mangledName;
	}
	
	public CilGenericDeclarationList getGenericParams() {
		return this.generics;
	}
	
	public boolean isInterface() {
		return this.isInterface;
	}
	
	public int getAccessModifiers() {
		return this.accessModifiers;
	}
	
	public void setSuperclass(CilTypeRef superclass) {
		this.superclass = superclass;
	}
	
	public CilTypeRef getSuperclass() {
		return this.superclass;
	}
	
	public List<CilTypeRef> getInterfaces() {
		return this.interfaces;
	}
	
	public void addInterface(CilTypeRef ifc) {
		if (this.interfaces == null)
			this.interfaces = new ArrayList<>();
		this.interfaces.add(ifc);
	}
	
	@Override
	public String toString() {
		return this.className;
	}
	
}
