/**
 * @file IdFifo.java
 * @brief Classes for handling a FIFO queue of Thread IDs.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class IdNode
 * @brief An element of a FIFO queue of thread ids.
 */
class IdNode {
	/**
	 * Thread id.
	 */
	long value;

	/**
	 * Reference to next element.
	 */
	IdNode next;
}

/**
 * @class IdFifo
 * @brief FIFO queue of thread ids. Nodes are inserted as the last element of
 * the list and extracted as the first element.
 */
public class IdFifo {
	/**
	 * First element in list.
	 */
	private IdNode first;

	/**
	 * Last element in list
	 */
	private IdNode last;

	/**
	 * Number of elements in list.
	 */
	private int size;

	/**
	 * IdFifo constructor. Initializes data structures.
	 * @return	No value is returned.
	 */
	public IdFifo () {
		first = null;
		last = null;
		size = 0;
	}

	/**
	 * Inserts an element as last element of the list.
	 * @param[in]	l Value of element to insert.
	 * @return	No value is returned.
	 */
	public void insert (long l) {
		IdNode ln = new IdNode ();
		ln.value = l;
		ln.next = null;

		if (size == 0) {
			first = ln;
			last = ln;
		} else {
			last.next = ln;
			last = last.next;
		}
		size++;
	}

	/**
	 * Extracts and returns first element of the list.
	 * @return	Value of extracted element.
	 */
	public long extract () {
		if (size == 0)
			return 0;
		long l = first.value;
		first = first.next;
		if (size == 1)
			last = null;
		size--;
		return l;
	}				
}
