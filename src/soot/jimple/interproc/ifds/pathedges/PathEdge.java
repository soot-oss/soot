package soot.jimple.interproc.ifds.pathedges;


public class PathEdge<N,A> {

	protected final N source, target;
	protected final A dSource, dTarget;

	public PathEdge(N source, A dSource, N target, A dTarget) {
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

	public A factAtSource() {
		return dSource;
	}

	public A factAtTarget() {
		return dTarget;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dSource == null) ? 0 : dSource.hashCode());
		result = prime * result + ((dTarget == null) ? 0 : dTarget.hashCode());
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
		if (dSource == null) {
			if (other.dSource != null)
				return false;
		} else if (!dSource.equals(other.dSource))
			return false;
		if (dTarget == null) {
			if (other.dTarget != null)
				return false;
		} else if (!dTarget.equals(other.dTarget))
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
