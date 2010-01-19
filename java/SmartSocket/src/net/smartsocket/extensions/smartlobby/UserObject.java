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

    public int _id;
    public String _username;
    public String fb_id = null;
    public ThreadHandler _threadHandler = null;
    public RoomObject _roomObject = null;

    //# team stuff
    public String _team = "unassigned";
    public String _status = "";

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

    public void test() {
	
    }

    @Override
    public void finalize() {
	_roomObject.onUserLeave(this, _threadHandler);
	SmartLobby.userObjects.remove(this);
    }

}
