package soot.cil.ast;

import soot.cil.Cil_Utils;


/**
 * Class for moedelling a class inside a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilClass {
	private String className;
	private int startLine;
	private int endLine;
	private CilGenericDeclarationList generics;
	private boolean isInterface;
	
	public CilClass(String className, int startLine) {
		this(className, startLine, -1);
	}
	
	public CilClass(String className, int startLine,
			CilGenericDeclarationList generics) {
		this(className, startLine, -1, generics, false);
	}
	
	public CilClass(String className, int startLine,
			CilGenericDeclarationList generics, boolean isInterface) {
		this(className, startLine, -1, generics, isInterface);
	}
	
	public CilClass(String className, int startLine, int endLine) {
		this(className, startLine, endLine, null, false);
	}
	
	public CilClass(String className, int startLine, int endLine,
			CilGenericDeclarationList generics, boolean isInterface) {
		this.className = Cil_Utils.removeGenericsDeclaration(className);
		this.startLine = startLine;
		this.endLine = endLine;
		this.generics = generics;
		this.isInterface = isInterface;
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
	
	public int getStartLine() {
		return this.startLine;
	}
	
	public int getEndLine() {
		return this.endLine;
	}
	
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	public CilGenericDeclarationList getGenericParams() {
		return this.generics;
	}
	
	public boolean isInterface() {
		return this.isInterface;
	}
	
	@Override
	public String toString() {
		return this.className;
	}
	
}
