package SmartLobby;

import net.smartsocket.ThreadHandler;

public class SmartLobby {

    public void SmartLobby() {
	System.out.println("I have been called..");
    }

    public void onConnect(ThreadHandler thread) {
	System.out.println("onConnect called.");

    }

    public void onDisconnect(ThreadHandler thread) {
    }

    public void onReceive(ThreadHandler thread, Object data) {
    }

    /*
     * The rest of the extension's application logic will go down here.
     *
     * When the onReceive method is called, it will parse the XML or JSON
     * and will call the corresponding method named after the node/object name.
     *
     * Simple XML Example:
     * <login username='XaeroDegreaz' password='SmartSocket' />
     *
     * Simple JSON Example:
     * {"c":"login","username":"XaeroDegreaz","password":"SmartSocket"}
     *
     * would call
     *
     * public void login(ThreadHandler thread, Object dataObject) { //# Login logic here }
     *
     */
    
}
