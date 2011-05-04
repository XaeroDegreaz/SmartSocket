package net.smartsocket.examples;

import net.smartsocket.Logger;
import net.smartsocket.clients.TCPClient;
import net.smartsocket.extensions.TCPExtension;
import net.smartsocket.protocols.json.ClientCall;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A very simple shell of a TCPExtension extension which includes all abstract methods and a simple onHelloWorld method.
 * @author XaeroDegreaz
 */
public class TCPTest1 extends TCPExtension {

    public static void main(String[] args) {
        new TCPTest1().start();
    }

    public TCPTest1() {
        super(8889);
    }

    @Override
    public void onConnect(TCPClient client) {
        Logger.log("A client has connected.");
    }

    @Override
    public void onDisconnect(TCPClient client) {
        Logger.log("A client has disconnected.");
    }

    @Override
    public void onExtensionReady() {
        Logger.log("Ready.");
    }

    /**
     * A simple method that can be called by sending a JSON string that looks like
     * ["onHelloWorld",{"test","some test data"}]
     * @param client
     * @param json
     * @throws JSONException
     */
    public void onHelloWorld(TCPClient client, JSONObject json) throws JSONException {
        Logger.log("The value of \'test\' in your onHelloWorld call was: "+json.get("test"));
        //# ClientCalls can be strung together like so:
        /**
         * This one is useful if you have some predefined data that you want to sent to the client,
         * only it doesn't require any looping to aggregate the data, or anything.
         */
        client.send(
                new ClientCall("someMethod")
                .put("someProperty", "someValue")
                .put("someOtherProperty", "someOtherValue")
                );
        //# Or, like this:
        /**
         * This style is good when you like straight-forward coding, or you need to loop through something
         * in order to put together data to send to the client.
         */
        ClientCall call = new ClientCall("someOtherMethod");
        call.put("otherProperty", "otherValue");
        call.put("anotherProperty", "anotherValue");
        client.send(call);
    }

    /**
     * A very simple login mechanism.
     * @param client
     * @param json
     * @throws JSONException
     */
    public void onLogin(TCPClient client, JSONObject json) throws JSONException {
        try {
            client.setUniqueId(json.getString("username"));
            
            ClientCall call = new ClientCall("onLogin");
            call.put("username", json.get("username"));
            call.put("message", "Thank you for logging in.");
            client.send(call);
        }catch(Exception e) {
            Logger.log(e.getMessage());

            ClientCall call = new ClientCall("onLoginFail");
            call.put("error", "That username is taken.");
            client.send(call);
        }

        //# Do some stuff with the client.

    }

    @Override
    public boolean onDataSpecial(TCPClient client, String methodName, JSONObject params) {
        return true;
    }
}
