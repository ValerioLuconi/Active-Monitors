/**
 * @file PriorityServer.java
 * @brief Server Thread, provides resources to Client Threads. Assigns higher
 * priority to double requests.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class PriorityServer
 * @brief Resource allocator. Independent from resources type. Assigns higher
 * priority to double requests. Extends Server class.
 */
public class PriorityServer extends Server {
	/**
	 * Entries array. Provides synchronization between client threads.
	 */
	protected Entries e = new Entries (5);

	/**
	 * Array passed to Entries.accept().
	 */
	protected int[] vet = new int[5];

	/**
	 * Specifies how many double requests have been made. For definig
	 * priority strategy.
	 */
	protected int doubleReserve;

	/**
	 * Does not correspond to a real entry, but is needed for knowing which
	 * entry has been called by client: singleRequest or doubleRequest.
	 */
	protected final int entryReserve = 4;

	/**
	 * PriorityServer constructor. Initializes data structures, calls Server
	 * constructor.
	 * @param[in]	res Number of resources.
	 * @param[in]	debug Debug file name.
	 * @return	No value is returned.
	 */
	public PriorityServer (int res, String debug) {
		super (res, debug);
		doubleReserve = 0;
	}

	/**
	 * Allocates a single resource when available.
	 * @return	Allocated resource's id.
	 */
	public int singleRequest () {
		// do reservation
		e.call (entryReserve);

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": prenoto una risorsa");

		e.fineRendez_vous ();

		// allocate resource
		e.call (entrySReq);

		int i = 0;
		while (!free[i])
			i++;
		free[i] = false;
		available--;

		// debug
		d.dbg ("Thread " + myId + ": risorsa allocata: " + i);

		e.fineRendez_vous ();
		return i;
	}

	/**
	 * Allocates two resources when available.
	 * @return	An array containing allocated resources' ids.
	 */
	public int[] doubleRequest () {
		// do reservation
		e.call (entryReserve);

		doubleReserve++;

		// debug
		long myId = Thread.currentThread().getId();
		d.dbg ("Thread " + myId + ": prenoto due risorse");

		e.fineRendez_vous ();

		// allocate resources
		e.call (entryDReq);

		doubleReserve--;

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
	 * PriorityServer Thread body.
	 */
	public void run () {
		int i = 0;
		// stops after 12 calls because of example test.
		// for infinite loop substitute with while (true)
		while (i < 24) {
			if (available == 0) {
				vet[0] = entryReserve;
				vet[1] = entrySRel;
				vet[2] = entryDRel;
				e.accept (vet, 3);
			} else if (available == 1) {
				vet[0] = entryReserve;
				vet[1] = entrySReq;
				vet[2] = entrySRel;
				vet[3] = entryDRel;
				e.accept (vet, 4);
			} else if (doubleReserve > 0) {
				vet[0] = entryReserve;
				vet[1] = entryDReq;
				vet[2] = entrySRel;
				vet[3] = entryDRel;
				e.accept (vet, 4);
			} else {
				vet[0] = entryReserve;
				vet[1] = entryDReq;
				vet[2] = entrySReq;
				vet[3] = entrySRel;
				vet[4] = entryDRel;
				e.accept (vet, 5);
			}
			i++;
		}
		// debug
		d.dbg ("\n*** Fine debug ***\n\n");
	}
}
