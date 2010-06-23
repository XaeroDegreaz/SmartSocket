/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.extensions.smartlobby;

import net.smartsocket.ThreadHandler;
import org.json.simple.*;

/**
 *
 * @author XaeroDegreaz
 */
public class UserObject extends JSONObject  {

    /**
     * The unique identifier that SmartLobby assigns. This is probably not referenced
     * anywhere. It was added in the early stages of development.
     */
    public int _id;
    /**
     * The username of the client
     */
    public String _username;
    /**
     * This is a Facebook identifier that is used in one of the projects that use SmartLobby.
     * I have just left it here because the project stil uses this variable. It's not
     * used in any of the core SmartLobby stuff.
     */
    public String fb_id = null;
    /**
     * the ThreadHandler object for this client.
     */
    public ThreadHandler _threadHandler = null;
    /**
     * The RoomObject for this client.
     */
    public RoomObject _roomObject = null;

    //# team stuff
    /**
     * This is some team stuff that I programmed in when developing early on. It's not
     * used in any of the core SmartLobby stuff.
     */
    public String _team = "unassigned";
    /**
     * This is the ready status change variable. It will go from Ready to Not ready.
     */
    public String _status = "";

    /**
     * Creates a new UserObject
     * @param userInfo A JSONArray of information. I'm not sure why I did it this way.
     * @param thread The ThreadHandler object of the newly created user.
     */
    public UserObject(JSONArray userInfo, ThreadHandler thread)  {
	_username = userInfo.get(0).toString();
	_id = Integer.parseInt(userInfo.get(1).toString());

	this.put("_id", _id);
	this.put("Username", _username);

	if(_id != 0) {
	    //
	    _threadHandler = thread;	    

	    JSONArray a = new JSONArray();
	    a.add("onInitUserObject");
	    a.add(this);

	    _threadHandler.send(a);
	}else {
	    _username = "Server";
	    this.put("Username", "Server");
	}

	System.out.println("New UserObject: "+this);
	SmartLobby.nextUserId++;
    }

    @Override
    public void finalize() {
	_roomObject.onUserLeave(this, _threadHandler);
	SmartLobby.userObjects.remove(this);
    }

}
