/*
 * Main.java
 */
package net.smartsocket;

import java.lang.reflect.Array;
import java.util.Set;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

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
    public static void main(String[] args) {
	launch(Main.class, args);
	while(MainView.consoleLog == null) {
	    //# Here we are waiting for the console to initialize so we can use it.
	}
	Logger.log("Main", "Initializing configuration...");

	//# Load the configuration file
	_loader = new Loader();

	Set extensions = _loader._extensions.keySet();

	for(Object s : _loader._extensions.keySet()) {
	    Logger.log("Main", "Starting "+s+" extension on port "+_loader._extensions.get(s)+"...");
	    Server server = new Server( (String)s, Integer.parseInt( (String)_loader._extensions.get(s)) );
	    new Thread(server).start();
	}

	
    }
}
