package soot.jimple.interproc.ifds.pathedges;


public class PathEdge<N> {

	protected final N source, target;
	protected final int dSource, dTarget;

	public PathEdge(N source, int dSource, N target, int dTarget) {
		super();
		this.source = source;
		this.target = target;
		this.dSource = dSource;
		this.dTarget = dTarget;
	}
	
	public N getSource() {
		return source;
	}

	public N getTarget() {
		return target;
	}

	public int factAtSource() {
		return dSource;
	}

	public int factAtTarget() {
		return dTarget;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dSource;
		result = prime * result + dTarget;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		@SuppressWarnings("rawtypes")
		PathEdge other = (PathEdge) obj;
		if (dSource != other.dSource)
			return false;
		if (dTarget != other.dTarget)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("<");
		result.append(source.toString());
		result.append(",");
		result.append(dSource);
		result.append("> -> <");
		result.append(target.toString());
		result.append(",");
		result.append(dTarget);
		result.append(">");
		return result.toString();
	}

}
