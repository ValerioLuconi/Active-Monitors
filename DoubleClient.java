/**
 * @file DoubleClient.java
 * @brief Simple client example. Performs a double request and a double release.
 * @author Valerio Luconi
 * @date July 2010
 */

/**
 * @class DoubleClient
 * @brief Performs a double request and a double release.
 */
public class DoubleClient extends Thread {
	private Server srv;
	private int[] res;

	public DoubleClient (Server s, int[] r) {
		srv = s;
		res = r;
	}

	public void run () {
		int[] arr;
		arr = srv.doubleRequest ();
		int tmp = res[arr[0]];
		res[arr[0]] = res[arr[1]];
		res[arr[1]] = tmp;
		srv.doubleRelease (arr);

		arr = srv.doubleRequest ();
		tmp = res[arr[0]];
		res[arr[0]] = res[arr[1]];
		res[arr[1]] = tmp;
		srv.doubleRelease (arr);
	}
}
