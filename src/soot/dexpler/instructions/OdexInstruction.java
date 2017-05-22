package soot.dexpler.instructions;

import org.jf.dexlib2.analysis.ClassPath;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;

/**
 * Interface for instructions that are only valid in optimized dex files (ODEX).
 * These instructions require special handling for de-odexing.
 * 
 * @author Steven Arzt
 */
public interface OdexInstruction {
	
	/**
	 * De-odexes the current instruction.
	 * @param parentFile The parent file to which the current ODEX instruction
	 * belongs
	 */
	public void deOdex(DexFile parentFile, Method m, ClassPath cp);

}
