package soot.jimple.spark.ondemand;

import java.util.Set;

import soot.Local;
import soot.PointsToSet;
import soot.Type;
import soot.jimple.ClassConstant;
import soot.jimple.spark.sets.EqualsSupportingPointsToSet;

/**
 * This is a <i>lazy</i> points-to set that is potentially context sensitive. It is created by the {@link DemandCSPointsTo} analysis.
 * The idea is that the points-to set is usually context-insensitive. However, when compared with another points-to set and the intersection
 * of these points-to sets is non-empty, <i>then</i> context information is computed for this points-to set and also for the other one, if applicable.
 * Then the test is repeated. Once context information is computed it is stored in this wrapper object so that it does not have to be computed again.
 * Objects of this type should only be compared to other {@link LazyContextSensitivePointsToSet} objects using the equals method.
 * Checking for non-empty intersection with points-to sets of other types should be possible but it is recommended to consistently use
 * {@link LazyContextSensitivePointsToSet} nevertheless. 
 * 
 * @author Eric Bodden
 */
public class LazyContextSensitivePointsToSet implements EqualsSupportingPointsToSet {

	private EqualsSupportingPointsToSet delegate;
	private final DemandCSPointsTo demandCSPointsTo;
	private final Local local;
	private boolean isContextSensitive;

	public boolean isContextSensitive() {
		return isContextSensitive;
	}

	public LazyContextSensitivePointsToSet(Local l, EqualsSupportingPointsToSet contextInsensitiveSet, DemandCSPointsTo demandCSPointsTo) {
		this.local = l;
		this.delegate = contextInsensitiveSet;
		this.demandCSPointsTo = demandCSPointsTo;
		this.isContextSensitive = false;
	}

	public boolean hasNonEmptyIntersection(PointsToSet other) {
		PointsToSet otherInner; 
		if(other instanceof LazyContextSensitivePointsToSet)
			otherInner = ((LazyContextSensitivePointsToSet)other).delegate;
		else
			otherInner = other;
		
		if(delegate.hasNonEmptyIntersection(otherInner)) {		
			if(other instanceof LazyContextSensitivePointsToSet) {
				((LazyContextSensitivePointsToSet) other).computeContextSensitiveInfo();
				otherInner = ((LazyContextSensitivePointsToSet)other).delegate;
			} 
			computeContextSensitiveInfo();
			
			return delegate.hasNonEmptyIntersection(otherInner);
		} else {
			return false;
		}
	}
	
	public void computeContextSensitiveInfo() {
		if(!isContextSensitive) {
			delegate = (EqualsSupportingPointsToSet) demandCSPointsTo.doReachingObjects(local);
			isContextSensitive = true;
		}
	}

	public boolean isEmpty() {		
		return delegate.isEmpty();
	}

	public Set<ClassConstant> possibleClassConstants() {
		return delegate.possibleClassConstants();
	}

	public Set<String> possibleStringConstants() {
		return delegate.possibleStringConstants();
	}

	public Set<Type> possibleTypes() {
		return delegate.possibleTypes();
	}

	public boolean pointsToSetEquals(Object other) {
		if(!(other instanceof LazyContextSensitivePointsToSet)) return false;
		return ((LazyContextSensitivePointsToSet)other).delegate.equals(delegate);
	}

	public int pointsToSetHashCode() {
		return delegate.pointsToSetHashCode();
	}
	
	public EqualsSupportingPointsToSet getDelegate() {
		return delegate;
	}
	
}
