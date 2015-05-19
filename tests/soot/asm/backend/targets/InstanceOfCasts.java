package soot.asm.backend.targets;

public class InstanceOfCasts {
	public boolean isMeasurable(Object o) {
		return o instanceof Measurable;
	}

	public Measurable[] convertMeasurableArray(Object[] o) {
		if (o instanceof Measurable[]) {
			return (Measurable[]) o;
		}
		return null;
	}

}
