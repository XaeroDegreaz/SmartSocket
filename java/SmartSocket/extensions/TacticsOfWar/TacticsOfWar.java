/*
 * This script is broken, it seems to not want to import nor extend SmartLobby.SmartLobby.
 * I think it has something to do with the way that this class has been loaded?
 *
 * I am putting this here for reference only.
 */
package TacticsOfWar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.smartsocket.Logger;
import net.smartsocket.ThreadHandler;
import org.json.simple.JSONObject;

/**
 *
 * @author XaeroDegreaz
 */
public class TacticsOfWar extends SmartLobby.SmartLobby{

    Connection conn = null;

    public TacticsOfWar() {

	try {
	    this.conn = DriverManager.getConnection("");
	    System.out.println(conn);

	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log("Server", e.toString());
	}
	System.out.println("TOW Constructor calledd.");
	Logger.log("TOW", "Constructor calleddddd.");
	System.out.print(this);
    }

    public void login(ThreadHandler thread, JSONObject json) {
	String fb_id = null;
	try {
	    System.out.println("Login called =>" + json.get("fb_id").toString());
	    fb_id = json.get("fb_id").toString();
	} catch (Exception e) {
	}

	Statement stmt = null;
	ResultSet rs = null;
	try {
	    stmt = conn.createStatement();
	    rs = stmt.executeQuery("SELECT * FROM `users` WHERE `fb_id` = " + fb_id + " LIMIT 1");

	    if (rs.first()) {
		/*
		 * First we wanna check to see if the user has a commander name. if not, we need to send a
		 * message to the client saying that he/she needs to create one. They will need to create a
		 * commander name and an army before they can login here and have any relevant information
		 * sent back to the client
		 */
		if (rs.getString("commander") == null) {
		    //# They have no commander. We will return after we send them a create commander command.
		    thread.sendSelf("[\"newCommander\"]");
		    return;
		}


	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (rs != null) {
		try {
		    rs.close();
		} catch (SQLException sqlEx) {
		} // ignore

		rs = null;
	    }

	    if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException sqlEx) {
		} // ignore

		stmt = null;
	    }

	}
    }
}
