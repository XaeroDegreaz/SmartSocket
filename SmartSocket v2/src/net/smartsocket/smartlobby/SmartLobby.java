package net.smartsocket.smartlobby;

import java.io.FileNotFoundException;
import java.lang.Class;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.smartsocket.Config;
import net.smartsocket.Logger;
import net.smartsocket.clients.TCPClient;
import net.smartsocket.extensions.TCPExtension;
import net.smartsocket.protocols.json.ClientCall;
import org.json.*;

/**
 * The SmartLobby class is a class that controls and handles multi-user
 * connectivity via a lobby, chat, game matchmaking, user-list, friends-list, etc.
 * This can be as simple as a chat room, or a full fledged multi-player game.
 * @author XaeroDegreaz
 */
public class SmartLobby extends TCPExtension {

    Map<String, User> userList = Collections.synchronizedMap( new HashMap<String, User>() );
    Map<String, Room> roomList = Collections.synchronizedMap( new HashMap<String, Room>() );
    public JSONObject config;

    public SmartLobby(int port) {
        super(port);
    }

    @Override
    public void onExtensionReady() {
        //# Add some code for initializing our default rooms.
        try {
            config = new JSONObject( Config.readFile("SmartLobbyConfig").toString() );

            setDefaultRooms( config.getJSONArray("default-rooms") );

        }catch(FileNotFoundException e) {
            Logger.log("Cannot find the SmartLobby configuration file.");
        }catch (JSONException e) {
            Logger.log("Malformed JSONObject in the SmartLobby config file: "+e.getMessage());
        }
    }

    @Override
    public void onConnect(TCPClient client) {
        //# No need to really do anything here.
    }

    @Override
    public void onDisconnect(TCPClient client) {
        //# Remove them from their room, if they are in one.
    }

    /**
     * We use the special onDataSpecial in our SmartLobby extension to route Room
     * and User events to their designated methods instead of handling them here in
     * the main extension, and re-passing them to the User or Room object, when necessary.
     * @param client The client sending this message
     * @param methodName The method to execute
     * @param params The parameters to send to said method, in JSONObject form
     */
    @Override
    public boolean onDataSpecial(TCPClient client, String methodName, JSONObject params) {
        User user = getUserByTCPClient(client);
        Method m = null;
        Class[] c = new Class[2];
        c[0] = User.class;
        c[1] = JSONObject.class;
        Object[] o = {user, params};
        try{
            if( params.getString("for").equals("room") ) {
                m = Room.class.getMethod(methodName, c);
                m.invoke(user.getRoom(), o);
                return false;
            }else if( params.getString("for").equals("user") ) {
                m = User.class.getMethod(methodName, c);
                m.invoke(user, o);
                return false;
            }
        }catch(JSONException e) {

        }catch(NoSuchMethodException e) {

        }catch(IllegalAccessException e) {

        }catch(InvocationTargetException e) {

        }
        return true;
    }

    private void setDefaultRooms(JSONArray array) {
        try {
            //# Loop through the array and create some rooms
            //# no username need be set; owner will be null wich will suffice
            for(int i = 0; i < array.length(); i++) {
                JSONObject r = array.getJSONObject(i);
                roomList.put( r.getString("name"), new Room(r, this) );
            }
        }catch(JSONException e) {
            Logger.log("There is a problem parsing some of the default rooms: "+e.getMessage());
        }

    }

    public void onLogin(TCPClient client, JSONObject json) {
        //# Perform some login logic here to see if user already logged in
        //# Perhaps evaluate to pass back to secondary callback for custom
        //# Logging in

        userList.put( client.getUniqueId().toString(), new User( client, this) );
    }

    /*
     * The methods below are directly called on either a User object, or Room object.
     */

    public void getUserList(TCPClient client, JSONObject json) {

    }

    public void getRoomList(TCPClient client, JSONObject json) {

    }

    public void createRoom(TCPClient client, JSONObject json) throws JSONException {
        //# Make sure that only one room with same name registered
        if( roomList.containsKey( json.getString("name") ) ) {
            ClientCall call = new ClientCall("onCreateRoomError");
            call.put("message", "A room already exists with that name.");
            client.send(call);
            return;
        }
        
        //# Make sure this user is not the owner of another room
        User user = getUserByTCPClient(client);
        if(user.getRoom().getOwner() == user) {
            ClientCall call = new ClientCall("onCreateRoomError");
            call.put("message", "You cannot create more than one room.");
            client.send(call);
            return;
        }

        Room room = new Room(json, this).setOwner(user);
        roomList.put( json.getString("name"), room );

        joinRoom(room, getUserByTCPClient(client));
    }

    //# Would like to have joinROom inside the Room object, but think it's easier to maintain out here
    public void joinRoom(TCPClient client, JSONObject json) throws JSONException {
        Room room = getRoomByName( json.getString("name") );

        if (room != null) {
            joinRoom(room, getUserByTCPClient(client));
        }else {
            ClientCall call = new ClientCall("onJoinRoomError");
            call.put("message", "The selected room is not available to join.");
        }

    }

    private void joinRoom(Room room, User user) {
        room.onUserJoin(user);
    }

    public void leaveRoom(TCPClient client, JSONObject json) {

    }

    //# Helper methods..
    public Room getRoomByName(String name) {
       return roomList.get( name );
    }

    public User getUserByTCPClient(TCPClient client) {
        return userList.get( client.getUniqueId().toString() );
    }

    public User getUserByUsername(String username) {
        return userList.get( username );
    }
}
