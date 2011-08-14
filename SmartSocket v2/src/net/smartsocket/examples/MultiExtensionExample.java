package net.smartsocket.examples;

import com.google.gson.Gson;
import net.smartsocket.Logger;
import net.smartsocket.examples.TCPTest1;

/**
 * A simple class that models how to instantiate a couple test TCPExtension based extensions.
 * You can make a class like this if you are planning on having several extensions running on your server.
 * You can pretty much run as many extensions as your server can handle.
 * @author XaeroDegreaz
 */
public class MultiExtensionExample {    

    public static void main(String[] args) {
        MultiExtensionExample m = new MultiExtensionExample();
        TCPTest1.main(args);
        TCPTest2.main(args);
    }

}
