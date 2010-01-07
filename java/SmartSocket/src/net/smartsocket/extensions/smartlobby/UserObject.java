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
    public String test = "testing";
    public ThreadHandler _threadHandler = null;
    public RoomObject _roomObject = null;

    public UserObject(int userId, ThreadHandler thread)  {
	this._id = userId;

	if(userId != 0) {
	    this._username = thread.threadId;
	    //
	    this._threadHandler = thread;
	    //this.put("_id", _id);
	    this.put("_username", thread.threadId);
	    this.put("_id", _id);

	    JSONArray a = new JSONArray();
	    a.add("onJoinLobby");
	    a.add(this);

	    _threadHandler.out.println(a);
	    _threadHandler.out.flush();
	}else {
	    this._username = "Server";
	    this.put("_username", "Server");
	    this.put("_id", _id);
	}

	System.out.println("New UserObject: "+this);
	SmartLobby.nextUserId++;
    }

    public void test() {
	
    }

    @Override
    public void finalize() {
	this._roomObject.onUserLeave(this, _threadHandler);
	SmartLobby.userObjects.remove(this);
    }

}
