package net.smartsocket;

import javax.swing.JScrollPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import net.smartsocket.forms.*;
import javax.swing.SwingUtilities;

/**
 * The Logger class handles all of the displaying of log text to the console gui in the correct tabs.
 * The class works on a level based system, in which only the levels which meet or exceed the minimum log level are
 * output to the gui console.
 * @author XaeroDegreaz
 */
public class Logger {

	/**
	 * This is the default level used for any log call that doesn't have an explicit log level defined.
	 * If this is set to 0, all incoming messages will be logged to the Critical pane. This is only recommended
	 * when debugging, because it can cause higher memory usage than wanted; especially if you are sending 
	 * hours and hours worth of chat messages, not to mention game calls to the logger.
	 */
	public final static int DEBUG = 0;
	/**
	 * This is a level above the debug level. This should be used sparingly for situations where you really want to have a certain
	 * piece of information stored in the log window. Otherwise, if debugging, just log with 0, and turn Logger.setLogLevel(0) on.
	 */
	public final static int INFO = 1;
	/**
	 * THis level should be used only when wanting to log a really important server event.
	 */
	public final static int CRITICAL = 2;
	/**
	 * The lowest level of log content to be displayed to the GUI console. Default is 1 to prevent clutter, and high memory usage.
	 */
	private static int _logLevel = 1;

	/**
	 * Here we finally display the information in the log after all of the details have been sorted out
	 * @param message
	 * @param logLevel
	 */
	private static void toLog( Object message, int logLevel ) {
		//# Make sure that the message being logged is at least the log level desired, otherwise return
		if ( logLevel < getLogLevel() ) {
			return;
		}

		//# Take apart all of the information from the class that is calling this log.
		String fullyQualifiedName = new Throwable().fillInStackTrace().getStackTrace()[2].getClassName();
		String callingClass = fullyQualifiedName.substring( fullyQualifiedName.lastIndexOf( "." ) + 1, fullyQualifiedName.length() );

		//# This is the actual HTML string that will be input into our log.
		String newLine = "<tr><td valign='top' align='left'>" + callingClass + ":&nbsp;&nbsp;&nbsp;</td><td>" + message + "</td></tr>";

		//# Put together the objects to be used in this log message
		final Object[] obj = getScrollPane( callingClass );
		final HTMLDocument doc = (HTMLDocument) obj[0];
		final JScrollPane scrollPane = (JScrollPane) obj[1];

		//# Here we just make sure that we insert a new table row into the HTML before our tr with the "marker" id
		try {
			Element el = doc.getElement( "marker" );
			doc.insertBeforeStart( el, newLine );

			//# Scroll the scrollpane to the bottom, if possible. Have to do it in another thread due to Swing threading issues:
			//# Without this, the scrollbar really never gets to the bottom, only really close. >.<
			SwingUtilities.invokeLater( new Runnable() {

				public void run() {
					scrollPane.getVerticalScrollBar().setValue(
							scrollPane.getVerticalScrollBar().getMaximum() );
				}
			} );
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the particulars of the calling class and its objects for use in manipulating the logText and also the scrollbar
	 * @param callingClass
	 * @return Object[] where [0] is the HTMLDocument and [1] is the JScrollPane
	 */
	private static Object[] getScrollPane( String callingClass ) {
		HTMLDocument doc = null;
		JScrollPane scrollPane = null;

		//# Try to place the new log message in a tab corresponding to its extension name.
		//# If none is found, simply place the log into the Critical pane, or another pane used for whatever.
		try {
			scrollPane = (ExtensionConsole) ConsoleForm.tabbedPane.getComponentAt( ConsoleForm.tabbedPane.indexOfTab( callingClass ) );
			doc = (HTMLDocument) ((ExtensionConsole) scrollPane).logText.getDocument();
		} catch (Exception e) {
			scrollPane = ConsoleForm.scrollPaneCritical;
			doc = (HTMLDocument) ConsoleForm.logText.getDocument();
		}

		Object[] obj = { doc, scrollPane };
		return obj;
	}

	/**
	 * Send a message to the GUI console with the given message at the Logger.DEBUG log level.
	 * @param message
	 * @see Logger.DEBUG, Logger.INFO, Logger.CRITICAL
	 */
	public static void log( Object message ) {
		toLog( message, Logger.DEBUG );
	}

	/**
	 * Send a message to the GUI console with the given message at the desired log level.
	 * @param message
	 * @param logLevel
	 * @see Logger.DEBUG, Logger.INFO, Logger.CRITICAL
	 */
	public static void log( Object message, int logLevel ) {
		toLog( message, logLevel );
	}

	/**
	 * Get the lowest level of log content to be output to the gui console.
	 * @return the _logLevel
	 * @see Logger.DEBUG, Logger.INFO, Logger.CRITICAL
	 */
	public static int getLogLevel() {
		return _logLevel;
	}

	/**
	 * Sets the minimum log level to display in the console
	 * @param logLevel the _logLevel to set
	 * @see Logger.DEBUG, Logger.INFO, Logger.CRITICAL
	 */
	public static void setLogLevel( int logLevel ) {
		_logLevel = logLevel;
	}
}
