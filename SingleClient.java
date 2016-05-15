/**
 * @file SingleClient.java
 * @brief Simple client example. Performs a single request and a single release.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class SingleClient
 * @brief Performs a single request and a single release.
 */
public class SingleClient extends Thread {
	private Server srv;
	private int[] res;

	public SingleClient (Server s, int[] r) {
		srv = s;
		res = r;
	}

	public void run () {
		int i = srv.singleRequest ();
		res[i]++;
		srv.singleRelease (i);

		i = srv.singleRequest ();
		res[i]++;
		srv.singleRelease (i);
	}
}
