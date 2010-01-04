package Example;
import java.net.Socket;
import org.json.simple.JSONObject;

public class Example {

    public Example() {
	System.out.println("Example called.");
    }

    public void onConnect(Socket socket) {
	System.out.println("onConnect called.");
    }

    public void onDisconnect(Socket socket) {
	System.out.println("onDisconnect called.");
    }

    public void onReceive(Socket socket, Object data) {
	//# No longer used at this time.
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
     * public void login(Socket socket, Object dataObject) { //# Login logic here }
     *
     */


    public void login(Socket socket, Object jsonObject) {
	JSONObject json = (JSONObject) jsonObject;
    }
}
