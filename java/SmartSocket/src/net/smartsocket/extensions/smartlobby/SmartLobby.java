package net.smartsocket.extensions.smartlobby;

import java.lang.reflect.Method;
import net.smartsocket.Server;
import net.smartsocket.ThreadHandler;
import org.json.simple.*;

/**
 *
 * @author XaeroDegreaz
 */
public abstract class SmartLobby {

    public static JSONObject userObjects = new JSONObject();
    public static int nextUserId = 0;
    public static JSONArray roomObjects = new JSONArray();
    public static int nextRoomId = 0;
    public static SmartLobby instance;

    /**
     * Not used unless using SmartLobby only
     */
    
    public void SmartLobby() {
	System.out.println("I have been called..");
    }

    /**
     * Actions to perform whena client connects.
     * @param thread
     */
    public void onConnect(ThreadHandler thread) {
	System.out.println("onConnect called.");

    }

    /**
     * Actions to perform when a client disconnects.
     * @param thread
     */
    public static void onDisconnect(String unique_identifier) {
	if(!unique_identifier.equals("<policy-file-request/>") && !unique_identifier.equals(null)) {

	    try {
		UserObject uo = (UserObject)userObjects.get(unique_identifier);
		uo.finalize();
	    }catch(Exception e){
		System.out.println("No corresponding user object to finalize.");
	    }
	}
    }

    /**
     * Send a JSON room list to a client.
     * @param thread
     * @param json
     */
    public void getRoomList(ThreadHandler thread, JSONObject json) {

	JSONArray a = new JSONArray();
	a.add("onRoomList");

	JSONArray roomList = new JSONArray();

//	for(int i = 0; i < roomObjects.size(); i++) {
//	    roomList.add(roomObjects.get(i));
//	}

	a.add(roomObjects);

	thread.send(a);
	System.out.println("Room List: "+a);

    }

    /**
     * Send a JSON user list of users in a room to a client.
     * @param thread
     * @param json
     */
    public void getUserList(ThreadHandler thread, JSONObject json) {

	UserObject uo = getUserObject(thread.unique_identifier);
	uo._roomObject.getUserList(thread);
	
    }

    /**
     * Send a message or other JSON data ro a room.
     * @param thread
     * @param json
     */
    public void sendRoom(ThreadHandler thread, JSONObject json) {
	UserObject sender = getUserObject(thread.unique_identifier);
	sender._roomObject.sendRoom(sender, json);
    }

    /**
     * Send JSON data to the sender's socket. Used for stuff like getUserList
     * @param thread
     * @param json
     */
    public void sendSelf(ThreadHandler thread, JSONObject json) {

    }

    /**
     * Send a private message or JSON data to a specific client.
     * @param thread
     * @param json
     */
    public void sendPrivate(ThreadHandler thread, JSONObject json) {
	UserObject sender = getUserObject(thread.unique_identifier);
	UserObject target = getUserObject(json.get("_target").toString());

	JSONArray a = new JSONArray();
	a.add("onMessagePrivate");

	JSONObject o = new JSONObject();
	o.put("Sender", sender._username);
	o.put("Message", json.get("_message"));
	o.put("uid", sender._threadHandler.unique_identifier);
	a.add(o);

	target._threadHandler.send(a);

    }

    /**
     * Create a room.
     * @param thread
     * @param json
     */
    public void createRoom(ThreadHandler thread, JSONObject json) {
	UserObject uo = getUserObject(thread.unique_identifier);
	roomObjects.add(new RoomObject(uo, json));
    }

    //# Userd for internal purposes only.
    public static void server_callback_createRoom(RoomObject room, JSONObject json){
	Class[] args = new Class[2];
	args[0] = RoomObject.class;
	args[1] = JSONObject.class;

	try {
	    Method m = Server.extension.getMethod("extension_callback_createRoom", args);

	    Object[] parameters = {room, json};
	    
	    m.invoke(Server.extensionInstance, parameters);

	}catch(Exception e) {
            System.out.println("ERROR HERE?!?!");
	    e.printStackTrace();
	}

    }

    /**
     * Delete a room.
     * @param thread
     * @param json
     */
    public void deleteRoom(ThreadHandler thread, JSONObject json) {

    }

    /**
     * Join a room. Should automatically send a user list.
     * @param thread
     * @param json
     */
    public void joinRoom(ThreadHandler thread, JSONObject json) {
	UserObject uo = getUserObject(thread.unique_identifier);
	//# Leave the old room.
	if(uo._roomObject != null) {
	    System.out.println("User was found in a room. We are attempting to remove.");
	    uo._roomObject.onUserLeave(uo, uo._threadHandler);
	    System.out.println("Leave successfull?");
	}

	for(int i = 0; i < roomObjects.size(); i++) {
	    RoomObject ro = (RoomObject)roomObjects.get(i);

	    if(ro._id == Integer.parseInt(json.get("_id").toString())) {
		uo._roomObject = ro;
		ro.newUser(uo, thread);
		break;
	    }
	    
	}
    }

    /**
     * Leave a room, should automatically get a new room list and new user list for the base lobby.
     * @param thread
     * @param json
     */
    public void leaveRoom(ThreadHandler thread, JSONObject json) {

	UserObject uo = getUserObject(thread.unique_identifier);
	JSONObject o = new JSONObject();
	o.put("_id", 0);

	joinRoom(thread, o);
    }

    public void leaveLobby(ThreadHandler thread, JSONObject json) {
	UserObject uo = getUserObject(thread.unique_identifier);
	RoomObject ro = getRoomObject(uo);
	ro.onUserLeave(uo, thread);
	//uo.finalize();
    }

    public void login(ThreadHandler thread, JSONObject json) {

    }
    
    public synchronized UserObject initUserObject(ThreadHandler thread, JSONObject json) {
	JSONArray u = new JSONArray();
	u.add(json.get("_username"));
	u.add(nextUserId);

	//# Create a new user object with the threads unique identifier as the key
	userObjects.put(thread.unique_identifier, new UserObject(u, thread));

	//# Return the new user object to the caller so that they can setup any
	//# custom UserObject JSON properties
	UserObject newUserObject = getUserObject(thread.unique_identifier);
	return newUserObject;
    }


    public UserObject getUserObject(String unique_identifier) {
	UserObject uo = (UserObject)userObjects.get(unique_identifier);
	return uo;
    }

    public RoomObject getRoomObject(UserObject uo) {
	RoomObject ro = uo._roomObject;
	return ro;
    }
    
}
