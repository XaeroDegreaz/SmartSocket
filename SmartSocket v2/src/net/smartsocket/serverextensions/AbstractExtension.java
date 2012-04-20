package net.smartsocket.serverextensions;

import javax.swing.ImageIcon;

/**
 * This abstract extension is just a skeleton class that holds the runnable and thread
 * lines that allow TCP and later UDP extensions to be runnable. Disregard this class
 * for the time being, it's only used in a few calls in some of the code.
 * @author XaeroDegreaz
 */
public abstract class AbstractExtension extends Thread {

	/**
	 * Internally used to know when the console form is up and going. This makes it so
	 * multiple extensions that may be launched simultaneously aren't all trying to launch their own
	 * GUI.
	 */
	public static boolean isConsoleFormRegistered = false;
	/**
	 * This ImageIcon will be the system tray / title bar icon for this extension when minimized.
	 * If no ImageIcon is specified, the console will never minimize to the system tray.
	 */
	public ImageIcon imageIcon;

	@Override
	public void run() {
	}
}
