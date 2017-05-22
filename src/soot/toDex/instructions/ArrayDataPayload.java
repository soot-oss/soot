package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderArrayPayload;

import soot.toDex.LabelAssigner;

/**
 * Payload for the fill-array-data instructions in dex
 * 
 * @author Steven Arzt
 *
 */
public class ArrayDataPayload extends AbstractPayload {
	
	private final int elementWidth;
	private final List<Number> arrayElements;
	
	public ArrayDataPayload(int elementWidth, List<Number> arrayElements) {
		super();
		this.elementWidth = elementWidth;
		this.arrayElements = arrayElements;
	}
	
	@Override
	public int getSize() {
		// size = (identFieldSize+sizeFieldSize) + numValues * (valueSize)
		return 4 + (arrayElements.size() * elementWidth + 1) / 2;
	}
	
	@Override
	public int getMaxJumpOffset() {
		return Short.MAX_VALUE;
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderArrayPayload(elementWidth, arrayElements);
	}

}
