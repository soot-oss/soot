package soot.toDex.instructions;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.SwitchLabelElement;
import org.jf.dexlib2.builder.instruction.BuilderSparseSwitchPayload;

import soot.Unit;
import soot.jimple.Stmt;
import soot.toDex.LabelAssigner;

/**
 * The payload for a sparse-switch instruction.
 * 
 * @see SwitchPayload
 */
public class SparseSwitchPayload extends SwitchPayload {

	private int[] keys;
	
	public SparseSwitchPayload(int[] keys, List<Unit> targets) {
		super(targets);
		this.keys = keys;
	}
	
	@Override
	public int getSize() {
		// size = (identFieldSize+sizeFieldSize) + numTargets * (keyFieldSize+targetFieldSize)
		return 2 + targets.size() * 4;
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		List<SwitchLabelElement> elements = new ArrayList<SwitchLabelElement>();
		for (int i = 0; i < keys.length; i++)
			elements.add(new SwitchLabelElement(keys[i],
					assigner.getOrCreateLabel((Stmt) targets.get(i))));
		return new BuilderSparseSwitchPayload(elements);
	}

}