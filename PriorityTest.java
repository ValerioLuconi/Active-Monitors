/**
 * @file PriorityTest.java
 * @brief Example test for PriorityServer with priority policy.
 * @author Valerio Luconi
 * @date July 2010
 */

public class PriorityTest {
	public static void main (String args[]) {
		// resources is an array of int but can be any type.
		// server is independent from resource type.
		int[] resources = new int[5];
		for (int i = 0; i < 5; i++)
			resources[i] = 0;

		PriorityServer s = new PriorityServer (5, "priority_debug.txt");
		SingleClient sc1 = new SingleClient (s, resources);
		SingleClient sc2 = new SingleClient (s, resources);
		DoubleClient dc1 = new DoubleClient (s, resources);
		DoubleClient dc2 = new DoubleClient (s, resources);

		s.start ();
		sc1.start ();
		sc2.start ();
		dc1.start ();
		dc2.start ();
	}
}
