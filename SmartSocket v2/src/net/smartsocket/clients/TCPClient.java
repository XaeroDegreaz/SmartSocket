package net.smartsocket.clients;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;
import net.smartsocket.Logger;
import net.smartsocket.extensions.TCPExtension;
import net.smartsocket.forms.StatisticsTracker;
import net.smartsocket.protocols.json.ClientCall;
import org.json.*;

/**
 * The TCPClient class controls the dedicated thread, and input/output for the TCP client.
 * This class does not need to be instatiated by any end users; it's created automatically when the
 * client is accepted by the server socket.
 * @author XaeroDegreaz
 */
public class TCPClient extends AbstractClient {
    /**
     * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
     */
    private static Deque<TCPClient> clients = new LinkedList<TCPClient>();
    private static long inboundBytes = 0;
    private static long outboundBytes = 0;

    //# These are the private vars that never need to be accessed period outside this class.
    private BufferedReader _in = null;
    private PrintWriter _out = null;
    private Socket _client = null;
    private TCPExtension _extension = null;

    public TCPClient(Socket client, TCPExtension extension) {
        this._client = client;
        this._extension = extension;
    }

    @Override
    /**
     * The TCPClient's main thread running method. This method basically sets up the
     * read operations and thins of that nature.
     */
    public void run() {
        Logger.log("There are currently: "+Thread.activeCount()+" thread active in this group.");
        synchronized(clients) {
            //# Add this to the deck
            clients.add(this);
            //# Update the interface to show new connection
            StatisticsTracker.updateClientsConnectedLabel();
            //# Send an onconnect message to the extension
            _extension.onConnect(this);
            //# Method used to setup initial streams, ie string, json, xml, binary streams, etc
            setupSession();
        }
        //# Begin the read loop for this client's streams.
        read();
        destroySession();
    }

    /**
     * Setup the client session
     */
    private void setupSession() {
        //# Initialize all of the input and output streams here...
        try {
            _in = new BufferedReader( new InputStreamReader( _client.getInputStream() ) );
            _out = new PrintWriter( new OutputStreamWriter( _client.getOutputStream() ) );
        }catch (Exception e) {
            //# Really should never get here, but just in case...
            Logger.log(e);
            destroySession();
        }
    }

    /**
     * Destroy the client session and remove the thread.
     */
    private void destroySession() {
        synchronized(clients) {
            //# Remove this client from the thread deque
            clients.remove(this);
            //# Update the interface to show new connection
            StatisticsTracker.updateClientsConnectedLabel();
            //# Send onDisconnect message to our extension
            _extension.onDisconnect(this);

            //# Tidy up our resources
            try {
                _out.close();
                _in.close();
                _client.close();
            }catch(Exception e) {
                //# Should never get here, but just in case
                Logger.log("Having problems closing streams for thread: "+Thread.currentThread().getId()+" - "+e.getMessage());
            }
        }
    }

    /**
     * Infinite read loop until the client closes their connections
     */
    private void read() {
        String input = null;

        try {
            while(!(input = _in.readLine()).equals(null)) {
                process(input);
            }
        }catch(Exception e) {
            Logger.log("Client "+Thread.currentThread().getId()+" disconnected.");
        }

    }

    /**
     * Process the lines of text being sent from the client to the server.
     * @param line
     */
    private void process(String line) {
        Logger.log("Client "+Thread.currentThread().getId()+" says: "+line);

        //# Get ready to create dynamic method call to extension
        Class[] classes = new Class[2];
        classes[0] = TCPClient.class;
        classes[1] = JSONObject.class;
        
        //# Reflection
        Method m = null;

        //# Setup method and params
        String methodName = null;
        JSONObject params = null;        
        JSONArray jsonA = null;
        
        try {
            //# Get the particulars of the JSON call from the client
            jsonA = new JSONArray(line);
            methodName = jsonA.getString(0);
            params = jsonA.getJSONObject(1);

            //# Try to call the method on the desired extension class
            Object[] o = {this, params};
            m = _extension.getExtension().getMethod(methodName, classes);
            m.invoke(_extension.getExtensionInstance(), o);
        }catch(JSONException e) {
            Logger.log("["+_extension.getExtensionName()+"] Client has tried to pass invalid JSON");
        }catch(NoSuchMethodException e) {
            Logger.log("["+_extension.getExtensionName()+"] The method: "+methodName+" does not exist");
        }catch(IllegalAccessException e) {
            Logger.log("["+_extension.getExtensionName()+"] The method: "+methodName+" is not accessible from this scope.");
        }catch(InvocationTargetException e) {
            Logger.log("["+_extension.getExtensionName()+"] The method: \'"+methodName+"\' reports: "+
                    e.getTargetException().getMessage()+" in JSONObject string: "+params.toString());
        }
    }

    /**
     * The ClientCall message to send to this client
     * @param message
     * @see ClientCall
     */
    public void send(ClientCall message) {
        _out.print(message.toString()+_extension.getNewlineCharacter());
        _out.flush();
    }

    /**
     * A list of all TCPClient objects that are running across all extensions
     * @return the clients
     */
    public static Deque<TCPClient> getClients() {
        return clients;
    }

    /**
     * A list of all TCPClient objects that are running across all extensions
     * @param aClients the clients to set
     */
    private static void setClients(Deque<TCPClient> aClients) {
        clients = aClients;
    }

    /**
     * The number of inbound bytes that have been received since the last wipe on the StatisticsTracker poll.
     * @return the inboundBytes
     */
    public static long getInboundBytes() {
        return inboundBytes;
    }

    /**
     * The number of inbound bytes that have been received since the last wipe on the StatisticsTracker poll.
     * @param aInboundBytes the inboundBytes to set
     */
    public static void setInboundBytes(long aInboundBytes) {
        inboundBytes = aInboundBytes;
    }

    /**
     * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
     * @return the outboundBytes
     */
    public static long getOutboundBytes() {
        return outboundBytes;
    }

    /**
     * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
     * @param aOutboundBytes the outboundBytes to set
     */
    public static void setOutboundBytes(long aOutboundBytes) {
        outboundBytes = aOutboundBytes;
    }
}
