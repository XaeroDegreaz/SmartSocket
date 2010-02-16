/*
 * Main.java
 */
package net.smartsocket;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.json.simple.JSONObject;


/**
 * The main class of the application.
 */
public class Main extends SingleFrameApplication {

    public static EventHandler _eventHandler;
    public static Loader _loader;
    public static Server _server;
    public static ThreadHandler _threadHandler;
    private static Main me;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
	show(new MainView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Main
     */
    public static Main getApplication() {
	return Application.getInstance(Main.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(Class extension, int port) {
	launch(Main.class, null);
	while (MainView.consoleLog == null) {
	    //# Here we are waiting for the console to initialize so we can use it.
	}
	Logger.log("Main", "Initializing configuration...");

	//# Load the configuration file
	_loader = new Loader();

	try {
		Logger.log("Main", "Starting " + extension.getName() + " extension on port "  +port);
		Server server = new Server(extension, port);
		new Thread(server).start();
	    
	} catch (Exception e) {
	    Logger.log("Main", e.toString());
	}


    }
}
