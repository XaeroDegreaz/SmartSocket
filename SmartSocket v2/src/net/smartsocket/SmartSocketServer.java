package net.smartsocket;

import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.serverclients.UDPClient;
import net.smartsocket.forms.ConsoleForm;

/**
 * This class is basically a class that is launched if someone were to launch SmartSocket.jar by itself, instead of
 * using it in their library path. It will basically have some information on how the class is meant to be used,
 * with some useful tutorial-like information.
 * @author XaeroDegreaz
 */
public class SmartSocketServer {

    public static SmartSocketServer extensionInstance;
    public static int extensionPort;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConsoleForm.start(null);
    }

    /**
     * This method is called directly after the GUI is launched when launched as a standalone jar.
     * Here, we'll simply display some information on how SmartSocket is supposed to be used.
     */
    public static void open() {
        Logger.log("SmartSocket is not meant to be used in this fashion. "
                + "<a href='http://www.smartsocket.net' target='_new'>Click here</a> for detailed documentation.");
    }
}
