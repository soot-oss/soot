/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

/**
 * The worklist interface that abstracts the selection strategy.
 * 
 * @author xiao
 *
 */
public interface IWorklist 
{
	/**
	 * Some worklist may need the initial capacity.
	 * @param size
	 * @return
	 */
	public void initialize(int size);
	
	public boolean has_job();

	public IVarAbstraction next();

	public void push(IVarAbstraction p);

	public int size();
	
	public void clear();
};
