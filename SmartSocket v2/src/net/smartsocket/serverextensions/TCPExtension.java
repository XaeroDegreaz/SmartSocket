package net.smartsocket.serverextensions;

import com.google.gson.JsonObject;
import java.net.*;
import java.util.*;
import net.smartsocket.Config;
import net.smartsocket.Logger;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.forms.ConsoleForm;
import net.smartsocket.forms.ExtensionConsole;
import net.smartsocket.protocols.json.ClientCall;

/**
 * The TCPExtension class is an abstract class that provides the shell around which all extensions using the TCP protocol
 * will use. This class handles setting up the actual server, creating the initial console gui, extension tabs on the gui,
 * as well as accepting connections and creating separate thread for each client (TCPClient).
 * @author XaeroDegreaz
 */
public abstract class TCPExtension extends AbstractExtension {

    /**
     * The port number that this extension is running on
     */
    private int port;
    /**
     * Determines if the server is running or not. When set to false, this server will cease to accept incoming connections.
     */
    private boolean running;
    /**
     * The current thread id that this extension instance is running on
     */
    private Thread thread;
    /**
     * The server's socket object
     */
    private ServerSocket socket;
    /**
     * The class of the extension object
     */
    private Class extension;
    /**
     * The instance object of the extension class.
     */
    private TCPExtension extensionInstance;
    /**
     * The string name of the extension class.
     */
    private String extensionName;
    /**
     * This is the newline character that is sent at the end of each
     * send to the client. It can be changed on an extension-level basis.
     * It's necessary to have this functionality because different client languages
     * (c/++, Java, ActionScript, etc.) will have different line ending characters
     * that are used for socket connection read/write.<br/><br/>
     *
     * Default is set to \r\n
     */
    private String newlineCharacter = "\r\n";

    /**
     * This deque contains all of the clients that are currently connected to this TCPExtension instance.
     */
    private Map<Object, TCPClient> clients = Collections.synchronizedMap( new HashMap<Object, TCPClient>() );

    public TCPExtension(int port) {
        synchronized(this) {
            this.port = port;
            this.extensionInstance = this;
            this.extension = this.getClass();
            this.extensionName = this.extensionInstance.getClass().getSimpleName();
        }
    }

    public synchronized void run() {
        //# Assign a thread identifier to this extension object
        synchronized(this) {
            this.setThread(Thread.currentThread());
        }

        //# Register this extension with the console
        ConsoleForm.start(this);
        open();
    }

    /**
     * This method is called directly from the ConsoleForm upon starting up
     */
    private synchronized void open() {
        while(!isConsoleFormRegistered){

        }
        Config.load();
        //# Open a ServerSocket with a backlog of 500, which should be plenty...
        try {
            setSocket(new ServerSocket(getPort(), 500));
            setRunning(true);
            Logger.log("["+getExtensionName()+"] Extension running on port "+getPort());

            //# Add custom tab for this extension...
            ConsoleForm.tabbedPane.add(getExtensionName(), new ExtensionConsole(getExtensionName()));
            //# Set this extension tab as the active extension tab
            ConsoleForm.tabbedPane.setSelectedIndex(ConsoleForm.tabbedPane.indexOfTab(getExtensionName()));
            accept();
        }catch(Exception e) {
            Logger.log(e.getMessage());
        }
    }

    /**
     * Loop and keep accepting connections while this server extension is active
     */
    private synchronized void accept() {
        //# Allow the extension to perform some initialization code...
        onExtensionReady();

        //# Create initial client object
        Socket client = null;
        boolean t = true;
        //# Begin server loop
        while(isRunning()) {
            try {
                //# Accept client
                client = getSocket().accept();
                Logger.log("["+getExtensionName()+"] New client accepted: ");                
            }catch(Exception e) {
                Logger.log("["+getExtensionName()+"] Error accepting client: "+e.getMessage());
            }

            //# Spawn their own thread
            new TCPClient(client, this).start();
        }
        //# If we get here, the server is stopped.
        Logger.log("["+getExtensionName()+"] The server has stopped running.");
    }

    private void close() {
        setRunning(false);
    }
    //# TODO - Add some diferent broadcast messages for different protocols as they are created.
    /**
     * Broadcast a message to all connected TCP clients
     * @param call
     */
    public static void broadcastMessage(ClientCall call) {
        synchronized(TCPClient.getClients()) {
            for(Map.Entry<Object, TCPClient> item : TCPClient.getClients().entrySet()) {
                item.getValue().send(call);
            }
        }
    }

    /**
     * Broadcast a message to all connected clients on this local TCPExtension instance
     * @param call
     */
    public void broadcastMessageLocal(ClientCall call) {
        synchronized(clients) {
            for(Map.Entry<Object, TCPClient> item : clients.entrySet()) {
                item.getValue().send(call);
            }
        }
    }

    /**
     * This method will be called on the extension the server is ready for business.
     * @param client
     */
    public abstract void onExtensionReady();
    /**
     * This method will be called on the extension when a user joins the server.
     * @param client
     */
    public abstract void onConnect(TCPClient client);
    /**
     * This method will be called on the extension when a user leaves the server.
     * @param client
     */
    public abstract void onDisconnect(TCPClient client);
    /**
     * This method passes the complete data to the extension, without triggering
     * any special methods (unless true is returned). This allows developers to have direct access to the
     * JSONObject that has been passed to the extension, and route it different ways,
     * if desired.
     * @param client The client sending this message
     * @param methodName The method to execute
     * @param params The parameters to send to said method, in JSONObject form
     * @return true if we wish to continue passing data to our other methods on the extension, and false if we will handle this call in this method only.
     */
    public abstract boolean onDataSpecial(TCPClient client, String methodName, JsonObject params);

    /**
     * The port number that this extension is running on
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * The port number that this extension is running on
     * @param port the port to set
     */
    private void setPort(int port) {
        this.port = port;
    }

    /**
     * Determines if the server is running or not. When set to false, this server will cease to accept incoming connections.
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Determines if the server is running or not. When set to false, this server will cease to accept incoming connections.
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * The current thread id that this extension instance is running on
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * The current thread id that this extension instance is running on
     * @param thread the thread to set
     */
    private void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * The server's socket object
     * @return the socket
     */
    public ServerSocket getSocket() {
        return socket;
    }

    /**
     * The server's socket object
     * @param socket the socket to set
     */
    private void setSocket(ServerSocket socket) {
        this.socket = socket;
    }

    /**
     * The class of the extension object
     * @return the extension
     */
    public Class getExtension() {
        return extension;
    }

    /**
     * The class of the extension object
     * @param extension the extension to set
     */
    private void setExtension(Class extension) {
        this.extension = extension;
    }

    /**
     * The instance object of the extension class.
     * @return the extensionInstance
     */
    public TCPExtension getExtensionInstance() {
        return extensionInstance;
    }

    /**
     * The instance object of the extension class.
     * @param extensionInstance the extensionInstance to set
     */
    private void setExtensionInstance(TCPExtension extensionInstance) {
        this.extensionInstance = extensionInstance;
    }

    /**
     * The string name of the extension class.
     * @return the extensionName
     */
    public String getExtensionName() {
        return extensionName;
    }

    /**
     * The string name of the extension class.
     * @param extensionName the extensionName to set
     */
    private void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    /**
     * This is the newline character that is sent at the end of each
     * send to the client. It can be changed on an extension-level basis.
     * It's necessary to have this functionality because different client languages
     * (c/++, Java, ActionScript, etc.) will have different line ending characters
     * that are used for socket connection read/write.<br/><br/>
     *
     * Default is set to \r\n
     * @return the newlineCharacter
     */
    public String getNewlineCharacter() {
        return newlineCharacter;
    }

    /**
     * This is the newline character that is sent at the end of each
     * send to the client. It can be changed on an extension-level basis.
     * It's necessary to have this functionality because different client languages
     * (c/++, Java, ActionScript, etc.) will have different line ending characters
     * that are used for socket connection read/write.<br/><br/>
     *
     * Default is set to \r\n
     * @param newlineCharacter the newlineCharacter to set
     */
    public void setNewlineCharacter(String newlineCharacter) {
        this.newlineCharacter = newlineCharacter;
    }
    /**
     * This deque contains all of the clients that are currently connected to this TCPExtension instance.
     * @return the allClients
     */
    public Map<Object, TCPClient> getClients() {
        return clients;
    }
    /**
     * Remove a TCPClient from the allClients map from this extension instance.
     * @param client The TCPClient to remove
     */
    public void removeClient(TCPClient client) {
        clients.remove(client.getUniqueId());
    }
    /**
     * Add a TCPClient to the allClients map from this extension instance.
     * @param client The TCPClient to add
     */
    public void addClient(TCPClient client) {
        clients.put(client.getUniqueId(), client);
    }
    /**
     * Return a TCPClient by its unique id
     * @param uniqueId The uniqueId of the client for which you wish to retrieve
     * @return The TCPClient instance with the requested uniqueId. Null if none is found.
     */
    public TCPClient getClientByUniqueId(Object uniqueId) {
        return clients.get(uniqueId);
    }
}
