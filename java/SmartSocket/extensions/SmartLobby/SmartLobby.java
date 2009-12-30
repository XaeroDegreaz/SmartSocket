
import java.net.Socket;
import net.smartsocket.Logger;
import org.json.simple.JSONObject;

public class SmartLobby {

    public SmartLobby() {
	System.out.println("Constructor called.");
    }

    public void onConnect(Socket socket) {
	System.out.println("onConnect called.");
    }

    public void onDisconnect(Socket socket) {

    }

    public void onReceive(Socket socket, Object data) {

    }

    /*
     * The rest of the extension's application logic will go down here.
     *
     * When the onReceive method is called, it will parse the XML or JSON
     * and will call the corresponding method named after the node/object name.
     *
     * XML Example:
     * <login username='XaeroDegreaz' password='SmartSocket' />
     *
     * would call
     *
     * public void login(String username, String password) { //# Login logic here }
     *
     */

    public void login(Socket socket, Object jsonObject) {
	JSONObject json = (JSONObject) jsonObject;
	System.out.println("Login called =>."+json.get("c").toString());
    }
}
