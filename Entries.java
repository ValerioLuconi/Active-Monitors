/**
 * @file Entries.java
 * @brief Class for handling entry execution.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class Entries
 * @brief Provides synchronization for entries execution.
 */
public class Entries {
	/**
	 * Array of FIFO queues: one queue for each entry to handle.
	 * Each queue contains ids of queued threads.
	 */
	private IdFifo[] threadFifo;

	/**
	 * Array: for each queue contains the number of threads queued.
	 */
	private int[] queued;

	/**
	 * Number of entries to handle
	 */
	private int numOps;

	/**
	 * Indicates if a thread has completed the excecution of an entry, so
	 * server can complete the accept execution.
	 */
	private boolean completed;

	/**
	 * Contains id of the thread allowed to execute entry body.
	 */
	private long exec;

	/**
	 * Entries constructor. Initializes data structures.
	 * @param[in]	n Number of operations to handle.
	 * @return	No value is returned.
	 */
	public Entries (int n) {
		numOps = n;
		exec = 0;
		threadFifo = new IdFifo[numOps];
		queued = new int[numOps];
		completed = true;
		for (int i = 0; i < n; i++) {
			threadFifo[i] = new IdFifo ();
			queued[i] = 0;
		}
	}

	/**
	 * Must be executed before entry body. Provides synchronization allowing
	 * only one thread to execute an entry at a time.
	 * Inserts thread id in the right FIFO queue and blocks until thread is
	 * scheduled for execution.
	 * @param[in]	x Id of entry to execute.
	 * @return	No value is returned.
	 */
	public synchronized void call (int x) {
		long myId = Thread.currentThread().getId ();
		threadFifo[x].insert (myId);
		queued[x]++;
		notifyAll ();
		while (myId != exec) {
			try {
				wait ();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	/**
	 * Must be executed after entry body. Indicates that thread has finished
	 * entry execution, and server can finish accept execution.
	 * @return	No value is returned.
	 */
	public synchronized void fineRendez_vous () {
		exec = 0;
		completed = true;
		notifyAll ();
	}

	/**
	 * Accepts a queued request for executing an entry. The server thread
	 * must execute this function. If there are no queued requests the
	 * thread blocks until a request arrives. Then it will wait until the
	 * entry execution has finished.
	 * @param[in]	vet An array containing ids of entries that server is
	 * 		willing to accept.
	 * @param[in]	n Array dimension.
	 * @return	No value is returned.
	 */
	public synchronized void accept (int[] vet, int n) {
		// waits for a thread to request for an entry in vet[]
		boolean ready = false;
		do {
			for (int i = 0; i < n; i++) {
				if (queued[vet[i]] > 0) {
					ready = true;
					queued[vet[i]]--;
					exec = threadFifo[vet[i]].extract ();
					completed = false;
					notifyAll ();
					break;
				}
			}
			if (!ready) {
				try {
					wait ();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		} while (!ready);

		// waits for thread to complete entry execution
		while (!completed) {
			try {
				wait ();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
}
