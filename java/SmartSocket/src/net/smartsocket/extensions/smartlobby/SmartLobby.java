package net.smartsocket.extensions.smartlobby;

import net.smartsocket.ThreadHandler;
import org.json.simple.JSONObject;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobby {

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
    public void onDisconnect(ThreadHandler thread) {
    }

    /**
     * Send a JSON room list to a client.
     * @param thread
     * @param json
     */
    public void getRoomList(ThreadHandler thread, JSONObject json) {

    }

    /**
     * Send a JSON user list of users in a room to a client.
     * @param thread
     * @param json
     */
    public void getUserList(ThreadHandler thread, JSONObject json) {

    }

    /**
     * Send a message or other JSON data ro a room.
     * @param thread
     * @param json
     */
    public void sendRoom(ThreadHandler thread, JSONObject json) {

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

    }

    /**
     * Create a room.
     * @param thread
     * @param json
     */
    public void createRoom(ThreadHandler thread, JSONObject json) {

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

    }

    /**
     * Leave a room, should automatically get a new room list and new user list for the base lobby.
     * @param thread
     * @param json
     */
    public void leaveRoom(ThreadHandler thread, JSONObject json) {

    }
    
}
