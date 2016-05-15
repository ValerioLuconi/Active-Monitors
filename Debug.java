/**
 * @file Server.java
 * @brief Server Thread, provides resources to Client Threads.
 * @author Valerio Luconi
 * @date July 2010
 */

import java.io.*;

/**
 * @class Debug
 * @brief Outputs debug messages on a file.
 */
public class Debug {
	/**
	 * Debug file name
	 */
	private String fileName;

	/**
	 * Handles writings on debug file.
	 */
	private PrintWriter out;

	/**
	 * Debug constructor. Creates debug file if doesn't exist.
	 * @param[in]	fn Debug file name.
	 * @return	No value is returned.
	 */
	public Debug (String fn) {
		fileName = fn;
		try {
			out = new PrintWriter (new FileWriter (fileName, true), true);
		} catch (IOException e) { }
		out.println ("*** Inizio debug ***\n");
		out.close ();
	}

	/**
	 * Prints a debug message on debug file.
	 * @param[in]	in Message to be printed.
	 * @return	No value is returned.
	 */
	public void dbg (String in) {
		try {
			out = new PrintWriter (new FileWriter (fileName, true), true);
		} catch (IOException e) { }
		out.println (in);
		out.close ();
	}
}
