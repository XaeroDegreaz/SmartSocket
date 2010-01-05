package Example;
import net.smartsocket.ThreadHandler;
import org.json.simple.JSONObject;

public class Example {

    public Example() {
	System.out.println("Example called.");
    }

    public void onConnect(ThreadHandler thread) {
	System.out.println("onConnect called.");
    }

    public void onDisconnect(ThreadHandler thread) {
	System.out.println("onDisconnect called.");
    }

    public void onReceive(ThreadHandler thread, JSONObject json) {
	//# No longer used at this time.
    }

    /*
     * The rest of the extension's application logic will go down here.
     *
     * When the onReceive method is called, it will parse the JSON
     * and will call the corresponding method named after the array's first (0) key name
     * and pass the second (1) object as parameters.
     *
     *
     * Simple JSON Example:
     *	["helloWorld",{
     *	    "paramName" : "hello",
     *	    "anotherParam" : "world"
     *	    }
     *	]
     *
     * would call
     *
     * public void helloWorld(ThreadHandler thread, JSONObject json) { //# Method logic here }
     *
     */


    public void helloWorld(ThreadHandler thread, JSONObject json) {
	System.out.println(json.toJSONString());
	
	String paramName = (String)json.get("paramName");
	String anotherParam = (String)json.get("anotherParam");

	System.out.println("paramName=>"+paramName+" / anotherParam=>"+anotherParam);
	//# If using the above example JSON data prints
	//# paramName=>hello / anotherParam=>world
    }
}
