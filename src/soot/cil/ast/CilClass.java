package soot.cil.ast;

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
	
	public CilClass(String className, int startLine) {
		this(className, startLine, -1);
	}
	
	public CilClass(String className, int startLine, int endLine) {
		this.className = className;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	public String getClassName() {
		return this.className;
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
	
	@Override
	public String toString() {
		return this.className;
	}
	
}
