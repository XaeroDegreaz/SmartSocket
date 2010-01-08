package net.smartsocket.extensions.smartlobby;

import net.smartsocket.ThreadHandler;
import org.json.simple.*;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobby {

    public static JSONObject userObjects = new JSONObject();
    public static int nextUserId = 0;
    public static JSONArray roomObjects = new JSONArray();
    public static int nextRoomId = 0;

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
    public static void onDisconnect(ThreadHandler thread) {
	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	uo.finalize();
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

	for(int i = 0; i < roomObjects.size(); i++) {
	    roomList.add(roomObjects.get(i));
	}

	a.add(roomList);
	
	thread.out.print(a.toString());
	thread.out.flush();
	System.out.println("Room List: "+a);

    }

    /**
     * Send a JSON user list of users in a room to a client.
     * @param thread
     * @param json
     */
    public void getUserList(ThreadHandler thread, JSONObject json) {

	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	uo._roomObject.getUserList(thread);
	
    }

    /**
     * Send a message or other JSON data ro a room.
     * @param thread
     * @param json
     */
    public void sendRoom(ThreadHandler thread, JSONObject json) {
	UserObject sender = (UserObject)userObjects.get(thread.threadId);
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
	UserObject sender = (UserObject)userObjects.get(thread.threadId);
	UserObject target = (UserObject)userObjects.get(json.get("_target"));

	JSONArray a = new JSONArray();
	a.add("onPrivateMessage");

	JSONObject o = new JSONObject();
	o.put("_sender", json.get("_message"));
	a.add(o);

	target._threadHandler.out.print(a);
	target._threadHandler.out.flush();

    }

    /**
     * Create a room.
     * @param thread
     * @param json
     */
    public void createRoom(ThreadHandler thread, JSONObject json) {
	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	roomObjects.add(new RoomObject(uo, json));
	
	

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
	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	//# Leave the old room.
	if(uo._roomObject != null) {
	    uo._roomObject.onUserLeave(uo, uo._threadHandler);
	}

	RoomObject ro = (RoomObject)roomObjects.get(Integer.parseInt(json.get("_id").toString()));
	uo._roomObject = ro;
	ro.newUser(uo, thread);
    }

    /**
     * Leave a room, should automatically get a new room list and new user list for the base lobby.
     * @param thread
     * @param json
     */
    public void leaveRoom(ThreadHandler thread, JSONObject json) {

	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	JSONObject o = new JSONObject();
	o.put("_id", 0);

	joinRoom(thread, o);
    }

    public void leaveLobby(ThreadHandler thread, JSONObject json) {
	UserObject uo = (UserObject)userObjects.get(thread.threadId);
	uo.finalize();
    }

    public void joinLobby(ThreadHandler thread) {
	userObjects.put(thread.threadId, new UserObject(nextUserId, thread));
    }
    
}
