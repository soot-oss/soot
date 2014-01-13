package soot.toDex;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.Jimple;

/**
 * Transformer that splits nested traps for Dalvik which does not support
 * hierarchies of traps. If we have a trap (1-3) with handler A and a trap
 * (2) with handler B, we transform them into three new traps: (1) and (3)
 * with A, (2) with A+B.
 *
 * @author Steven Arzt
 */
public class TrapSplitter extends BodyTransformer {

    public TrapSplitter( Singletons.Global g ) {}
    public static TrapSplitter v() { return soot.G.v().soot_toDex_TrapSplitter(); }

    private class TrapOverlap {
		private Trap t1;
		private Trap t2;
		private Unit t2Start;

		public TrapOverlap(Trap t1, Trap t2, Unit t2Start) {
			this.t1 = t1;
			this.t2 = t2;
			this.t2Start = t2Start;
		}
	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		// If we have less then two traps, there's nothing to do here
		if (b.getTraps().size() < 2)
			return;

		// Look for overlapping traps
		TrapOverlap to;
		while ((to = getNextOverlap(b)) != null) {
			// If one of the two traps is empty, we remove it
			if (to.t1.getBeginUnit() == to.t1.getEndUnit()) {
				b.getTraps().remove(to.t1);
				continue;
			}
			if (to.t2.getBeginUnit() == to.t2.getEndUnit()) {
				b.getTraps().remove(to.t2);
				continue;
			}
			
			// t1start..t2start -> t1'start...t1'end,t2start...
			if (to.t1.getBeginUnit() != to.t2Start) {
				// We need to split off t1.start - predOf(t2.splitUnit). If both traps
				// start at the same statement, this range is empty, so we have checked
				// that.
				Trap newTrap = Jimple.v().newTrap(to.t1.getException(), to.t1.getBeginUnit(),
						to.t2Start, to.t1.getHandlerUnit());
				safeAddTrap(b, newTrap);
				to.t1.setBeginUnit(to.t2Start);
			}
			// (t1start, t2start) ... t1end ... t2end
			else if (to.t1.getBeginUnit() == to.t2.getBeginUnit()) {
				Unit firstEndUnit = to.t1.getBeginUnit();
				while (firstEndUnit != to.t1.getEndUnit() && firstEndUnit != to.t2.getEndUnit())
					firstEndUnit = b.getUnits().getSuccOf(firstEndUnit);

				if (firstEndUnit == to.t1.getEndUnit()) {
					Trap newTrap = Jimple.v().newTrap(to.t2.getException(), to.t1.getBeginUnit(), firstEndUnit, to.t2.getHandlerUnit());
					safeAddTrap(b, newTrap);
					to.t2.setBeginUnit(firstEndUnit);
				}
				else if (firstEndUnit == to.t2.getEndUnit()) {
					Trap newTrap2 = Jimple.v().newTrap(to.t1.getException(), to.t1.getBeginUnit(), firstEndUnit, to.t1.getHandlerUnit());
					safeAddTrap(b, newTrap2);
					to.t1.setBeginUnit(firstEndUnit);
				}
			}
		}
	}

	/**
	 * Adds a new trap to the given body only if the given trap is not empty
	 * @param b The body to which to add the trap
	 * @param newTrap The trap to add
	 */
	private void safeAddTrap(Body b, Trap newTrap) {
		if (newTrap.getBeginUnit() != newTrap.getEndUnit())
		b.getTraps().add(newTrap);		
	}
	/**
	 * Gets two arbitrary overlapping traps in the given method body
	 * @param b The body in which to look for overlapping traps
	 * @return Two overlapping traps if they exist, otherwise null
	 */
	private TrapOverlap getNextOverlap(Body b) {
		for (Trap t1 : b.getTraps()) {
			// Look whether one of our trapped statements is the begin
			// statement of another trap
			for (Unit splitUnit = t1.getBeginUnit(); splitUnit != t1.getEndUnit(); splitUnit = b.getUnits().getSuccOf(splitUnit)) {
				for (Trap t2 : b.getTraps())
					if (t1 != t2 && t1.getEndUnit() != t2.getEndUnit() && t2.getBeginUnit() == splitUnit) {
						return new TrapOverlap(t1, t2, t2.getBeginUnit());
					}
				}
		}
		return null;
	}

}
