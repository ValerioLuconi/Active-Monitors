/**
 * @file Server.java
 * @brief Server Thread, provides resources to Client Threads.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class Server
 * @brief Resource allocator. Independent from resources type.
 */
public class Server extends Thread {
	/**
	 * Debug object for debug messages.
	 */
	protected Debug d;

	/**
	 * Entries array. Provides synchronization between client threads.
	 */
	protected Entries e = new Entries (4);

	/**
	 * Array passed to Entries.accept().
	 */
	protected int[] vet = new int[4];

	/**
	 * Specifies which resources are allocated and which are not.
	 */
	protected boolean[] free;

	/**
	 * Contains number of free resources.
	 */
	protected int available;

	/**
	 * Total resources number.
	 */
	protected int total;

	/**
	 * Entries ids.
	 */
	protected final int entrySReq = 0, entryDReq = 1, entrySRel = 2, entryDRel = 3;

	/**
	 * Server constructor. Initializes data structures.
	 * @param[in]	res Number of resources.
	 * @param[in]	debug Debug file name.
	 * @return	No value is returned.
	 */
	public Server (int res, String debug) {
		d = new Debug (debug);
		free = new boolean[res];
		available = res;
		total = res;
		for (int i = 0; i < res; i++)
			free[i] = true;
	}

	/**
	 * Allocates a single resource when available.
	 * @return	Allocated resource's id.
	 */
	public int singleRequest () {
		e.call (entrySReq);

		int i = 0;
		while (!free[i])
			i++;
		free[i] = false;
		available--;

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": risorsa allocata: " + i);

		e.fineRendez_vous ();
		return i;
	}

	/**
	 * Allocates two resources when available.
	 * @return	An array containing allocated resources' ids.
	 */
	public int[] doubleRequest () {
		e.call (entryDReq);

		int i = 0;
		while (!free[i])
			i++;
		free[i] = false;
		available--;
		int j = 0;
		while (!free[j])
			j++;
		free[j] = false;
		available--;

		int[] arr = new int[2];
		arr[0] = i;
		arr[1] = j;

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": risorse allocate: " + i + " " + j);

		e.fineRendez_vous ();
		return arr;
	}

	/**
	 * Releases a resource (if previously allocated).
	 * @param[in]	i Resource id.
	 * @return	No value is returned.
	 */
	public void singleRelease (int i) {
		e.call (entrySRel);

		if (!free[i]) {
			free[i] = true;
			available++;
		}

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": rilascio una risorsa: " + i);

		e.fineRendez_vous ();
	}

	/**
	 * Releases two resources (if previously allocated).
	 * @param[in]	arr An array containing resources' ids.
	 * @return	No value is returned.
	 */
	public void doubleRelease (int[] arr) {
		e.call (entrySRel);

		if (!free[arr[0]]) {
			free[arr[0]] = true;
			available++;
		}
		if (!free[arr[1]]) {
			free[arr[1]] = true;
			available++;
		}

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": rilascio due risorse: " + arr[0] + " " + arr[1]);

		e.fineRendez_vous ();
	}

	/**
	 * Server Thread body.
	 */
	public void run () {
		int i = 0;
		// stops after 16 calls because of example test.
		// for infinite loop substitute with while (true)
		while (i < 16) {
			if (available == 0) {
				vet[0] = entrySRel;
				vet[1] = entryDRel;
				e.accept (vet, 2);
			} else if (available == 1) {
				vet[0] = entrySReq;
				vet[1] = entrySRel;
				vet[2] = entryDRel;
				e.accept (vet, 3);
			} else {
				vet[0] = entrySReq;
				vet[1] = entryDReq;
				vet[2] = entrySRel;
				vet[3] = entryDRel;
				e.accept (vet, 4);
			}
			i++;
		}
		// debug
		d.dbg ("\n*** Fine debug ***\n\n");
	}
}
