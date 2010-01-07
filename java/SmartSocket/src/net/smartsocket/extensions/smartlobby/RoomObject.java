/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.extensions.smartlobby;

import java.util.Vector;
import net.smartsocket.ThreadHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author XaeroDegreaz
 */
public class RoomObject extends JSONObject {

    public JSONArray _userList = new JSONArray();
    public Vector _threads = new Vector(16);
    public int _currentUsers = 0;
    public int _id = 0;
    public String _status = "Waiting";

    public RoomObject(UserObject user, JSONObject json) {
	_id = SmartLobby.nextRoomId;
	this.put("_id", _id);
	this.put("_name", json.get("_name"));
	this.put("_maxUsers", Integer.parseInt(json.get("_maxUsers").toString()));
	this.put("_currentUsers", 0);
	this.put("_private", Boolean.parseBoolean(json.get("_private").toString()));
	this.put("_status", _status);
	this.put("_creator", user);
	SmartLobby.nextRoomId++;
	System.out.println("New RoomObject: " + this);

	if (user._id != 0) {
	    user._threadHandler.out.println("[\"onCreateRoom\",{\"id\":" + _id + "}]");
	    user._threadHandler.out.flush();

	    JSONArray o = new JSONArray();
	    o.add("onNewRoom");
	    o.add(this);

	    RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	    for (int i = 0; i < lobby._threads.size(); i++) {
		synchronized (lobby._threads) {

		    ThreadHandler handler =
			    (ThreadHandler) lobby._threads.elementAt(i);
		    handler.out.println(o);
		    handler.out.flush();
		}
	    }
	}
    }

    public void getUserList(ThreadHandler thread) {
	JSONArray userList = new JSONArray();
	userList.add("onUserList");

	for (int i = 0; i < _userList.size(); i++) {
	    userList.add(_userList.get(i));
	}
	System.out.println(userList);
	thread.sendSelf(userList.toString());
    }

    public void newUser(UserObject uo, ThreadHandler thread) {
	JSONArray newUser = new JSONArray();
	newUser.add("onUserJoin");
	newUser.add(uo);
	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.out.println(newUser);
		handler.out.flush();
	    }
	}

	this.put("_currentUsers", _currentUsers++);
	onUserCountChange(this);

	_userList.add(uo);
	_threads.add(thread);

	System.out.println(_userList);
	thread.sendSelf("[\"onJoinRoom\"]");

    }

    public void onUserLeave(UserObject user, ThreadHandler thread) {
	_userList.remove(user);
	_threads.removeElement(thread);
	this.put("_currentUsers", _currentUsers--);

	JSONArray leaveUser = new JSONArray();
	leaveUser.add("onUserLeave");
	leaveUser.add(user);
	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.out.println(leaveUser);
		handler.out.flush();
	    }
	}

	onUserCountChange(this);

    }

    public void sendRoom(UserObject user, JSONObject json) {
	JSONArray o = new JSONArray();
	o.add("onRoomMessage");

	json.put("_username", json.get("_username"));
	o.add(json);
	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.out.println(o);
		handler.out.flush();
	    }
	}
    }

    public void onUserCountChange(RoomObject room) {
	//# Check to see if the room now has 0 users. If so, delete the roo.
	if(room._currentUsers < 1 && room._id != 0) {
	    finalize();
	    return;
	}
	//# Send the event to users in the main lobby ( 0 )
	JSONArray o = new JSONArray();
	o.add("onUserCountChange");

	JSONObject ro = new JSONObject();
	ro.put("_currentUsers", Integer.parseInt(this.get("_currentUsers").toString()));
	ro.put("_id", _id);
	o.add(ro);

	RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	for (int i = 0; i < lobby._threads.size(); i++) {
	    synchronized (lobby._threads) {

		ThreadHandler handler =
			(ThreadHandler) lobby._threads.elementAt(i);
		handler.out.println(o);
		handler.out.flush();
	    }
	}
    }

    @Override
    public void finalize() {
	JSONArray o = new JSONArray();
	o.add("onRoomDelete");

	JSONObject ro = new JSONObject();
	ro.put("_id", _id);
	o.add(ro);

	RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	for (int i = 0; i < lobby._threads.size(); i++) {
	    synchronized (lobby._threads) {

		ThreadHandler handler =
			(ThreadHandler) lobby._threads.elementAt(i);
		handler.out.println(o);
		handler.out.flush();
	    }
	}

	SmartLobby.roomObjects.remove(this);
    }
}
