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

    //# Team specifit arrays
    public JSONObject _teamList = new JSONObject();

    public RoomObject(UserObject user, JSONObject json) {
	_id = SmartLobby.nextRoomId;
	this.put("ID", _id);
	this.put("Name", json.get("_name"));
	this.put("Max", Integer.parseInt(json.get("_maxUsers").toString()));
	this.put("Current", 0);
	this.put("Private", Boolean.parseBoolean(json.get("_private").toString()));
	this.put("Status", _status);
	this.put("Creator", user._username);

	//# Team Stuff
	_teamList.put("unassigned", new JSONArray());
	_teamList.put("red", new JSONArray());
	_teamList.put("blue", new JSONArray());
	
	SmartLobby.nextRoomId++;
	System.out.println("New RoomObject: " + this);

	if (user._id != 0) {
	    user._threadHandler.send("[\"onCreateRoom\",{\"_id\":" + _id + "}]");

	    JSONArray o = new JSONArray();
	    o.add("onRoomAdd");
	    o.add(this);

	    RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	    for (int i = 0; i < lobby._threads.size(); i++) {
		synchronized (lobby._threads) {

		    ThreadHandler handler =
			    (ThreadHandler) lobby._threads.elementAt(i);
		    handler.send(o);
		}
	    }
	}else {
	    //_currentUsers = 0;
	}
    }

    public synchronized void getUserList(ThreadHandler thread) {
	JSONArray a = new JSONArray();
	a.add("onUserList");

	JSONArray userList = new JSONArray();

	for (int i = 0; i < _userList.size(); i++) {
	    userList.add(_userList.get(i));
	}
	//System.out.println(a);
	a.add(userList);
	thread.send(a);
    }

    public synchronized void newUser(UserObject uo, ThreadHandler thread) {
	uo._team = "unassigned";
	
	JSONArray newUser = new JSONArray();
	newUser.add("onUserJoin");

	JSONObject obj = new JSONObject();
	obj.put("Username", uo._username);
	obj.put("uid", uo._threadHandler.unique_identifier);

	newUser.add(obj);
	
	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.send(newUser);
	    }
	}
	
	
	

	_userList.add(uo);
	_threads.add(thread);

	this.put("Current",_threads.size());

	onRoomCountUpdate();

	System.out.println(_userList);

	JSONArray a = new JSONArray();
	a.add("onRoomJoin");

	JSONObject o = new JSONObject();
	o.put("ID", _id);
	o.put("uid", uo._threadHandler.unique_identifier);
	

	a.add(o);
	thread.send(a);
    }

    public void onUserLeave(UserObject user, ThreadHandler thread) {
	_userList.remove(user);
	_threads.removeElement(thread);
	
	this.put("Current", _threads.size());

	JSONArray leaveUser = new JSONArray();
	leaveUser.add("onUserLeave");

	JSONObject obj = new JSONObject();
	obj.put("Username", user._username);
	obj.put("Team", user._team);

	leaveUser.add(obj);
	
	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.send(leaveUser);
	    }
	}

	onRoomCountUpdate();

    }

    public void sendRoom(UserObject user, JSONObject json) {
	JSONArray a = new JSONArray();
	a.add("onMessageRoom");

	JSONObject o = new JSONObject();
	o.put("Username", user._username);
	o.put("Message", json.get("_message"));
	
	a.add(o);

	for (int i = 0; i < _threads.size(); i++) {
	    synchronized (_threads) {

		ThreadHandler handler =
			(ThreadHandler) _threads.elementAt(i);
		handler.send(a);
	    }
	}
    }

    public synchronized void onRoomCountUpdate() {
	//# Check to see if the room now has 0 users. If so, delete the roo.
	if(_threads.size() < 1 && _id != 0) {
	    finalize();
	    return;
	}
	//# Send the event to users in the main lobby ( 0 )
	JSONArray o = new JSONArray();
	o.add("onRoomCountUpdate");

	JSONObject ro = new JSONObject();
	ro.put("Current", Integer.parseInt(this.get("Current").toString()));
	ro.put("ID", _id);
	o.add(ro);

	RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	for (int i = 0; i < lobby._threads.size(); i++) {
	    synchronized (lobby._threads) {

		ThreadHandler handler =
			(ThreadHandler) lobby._threads.elementAt(i);
		handler.send(o);
	    }
	}
	System.out.println("Current users in "+get("Name")+" "+_threads.size());
    }

    

    @Override
    public void finalize() {
	JSONArray o = new JSONArray();
	o.add("onRoomDelete");

	JSONObject ro = new JSONObject();
	ro.put("ID", _id);
	o.add(ro);

	RoomObject lobby = (RoomObject) SmartLobby.roomObjects.get(0);
	for (int i = 0; i < lobby._threads.size(); i++) {
	    synchronized (lobby._threads) {

		ThreadHandler handler =
			(ThreadHandler) lobby._threads.elementAt(i);
		handler.send(o);
	    }
	}

	SmartLobby.roomObjects.remove(this);
    }
}
