package soot.toDex.instructions;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.Label;
import org.jf.dexlib2.builder.instruction.BuilderPackedSwitchPayload;

import soot.Unit;
import soot.jimple.Stmt;
import soot.toDex.LabelAssigner;

/**
 * The payload for a packed-switch instruction.
 * 
 * @see SwitchPayload
 */
public class PackedSwitchPayload extends SwitchPayload {
	
	private int firstKey;
	
	public PackedSwitchPayload(int firstKey, List<Unit> targets) {
		super(targets);
		this.firstKey = firstKey;
	}
	
	@Override
	public int getSize() {
		// size = (identFieldSize+sizeFieldSize+firstKeyFieldSize) + (numTargets * targetFieldSize)
		return 4 + targets.size() * 2;
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		List<Label> elements = new ArrayList<Label>();
		for (int i = 0; i < targets.size(); i++)
			elements.add(assigner.getOrCreateLabel((Stmt) targets.get(i)));
		return new BuilderPackedSwitchPayload(firstKey, elements);
	}
}