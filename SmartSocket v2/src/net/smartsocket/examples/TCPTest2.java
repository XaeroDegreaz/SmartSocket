package net.smartsocket.examples;

import com.google.gson.JsonObject;
import net.smartsocket.Logger;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.serverextensions.TCPExtension;
import net.smartsocket.protocols.json.RemoteCall;

/**
 * A very simple shell of a TCPExtension extension which includes all abstract methods and a simple onHelloWorld method.
 * @author XaeroDegreaz
 */
public class TCPTest2 extends TCPExtension {

    public static void main(String[] args) {
        new TCPTest2().start();
    }

    public TCPTest2() {
        super(8890);
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
    public void onHelloWorld(TCPClient client, JsonObject json) {
        Logger.log("The value of \'test\' in your onHelloWorld call was: "+json.get("test"));
        //# ClientCalls can be strung together like so:
        /**
         * This one is useful if you have some predefined data that you want to sent to the client,
         * only it doesn't require any looping to aggregate the data, or anything.
         */
        client.send(
                new RemoteCall("someMethod")
                .put("someProperty", "someValue")
                .put("someOtherProperty", "someOtherValue")
                );
        //# Or, like this:
        /**
         * This style is good when you like straight-forward coding, or you need to loop through something
         * in order to put together data to send to the client.
         */
        RemoteCall call = new RemoteCall("someOtherMethod");
        call.put("otherProperty", "otherValue");
        call.put("anotherProperty", "anotherValue");
        client.send(call);
    }

    @Override
    public boolean onDataSpecial(TCPClient client, String methodName, JsonObject params) {
        return true;
    }
}
