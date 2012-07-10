package soot.util;

/**
 * A table data structure. Multiple rows may be associated with one column, but
 * only one value may be associated with one row.
 * 
 * @param <C>
 *            Column type.
 * @param <R>
 *            Row type.
 * @param <V>
 *            Value type.
 */
public interface Table<C, R, V> {

	/**
	 * Clears this table.
	 */
	void clear();

	/**
	 * @return number of entries in this table.
	 */
	int size();

	/**
	 * Sets the value of a row in a column. If the row does not exist in the
	 * column, it is added.
	 * 
	 * @param column
	 *            the column.
	 * @param row
	 *            the row.
	 * @param value
	 *            the value.
	 * @return the previous value for the row, if any.
	 */
	V put(C column, R row, V value);

	/**
	 * Retrieves value of a row in a column.
	 * 
	 * @param column
	 *            the column.
	 * @param row
	 *            the row.
	 * @return value set for the row, or {@code null} if the specified row or
	 *         column were not present in this map.
	 */
	V get(Object column, Object row);

	/**
	 * Removes a row from a column.
	 * 
	 * @param column
	 *            the column.
	 * @param row
	 *            the row.
	 * @return value set for the row, or {@code null} if the specified row or
	 *         column were not present in this map.
	 */
	V remove(Object column, Object row);

	/**
	 * Determines if a column and row exist in this table.
	 * 
	 * @param column
	 *            the column.
	 * @param row
	 *            row in the column.
	 * @return {@code true} if the row specified is contained in the specified
	 *         column.
	 */
	boolean contains(Object column, Object row);
}