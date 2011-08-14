package net.smartsocket.smartlobby;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
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

/**
 * The SmartLobby class is a class that controls and handles multi-user
 * connectivity via a lobby, chat, game matchmaking, user-list, friends-list, etc.
 * This can be as simple as a chat room, or a full fledged multi-player game.
 * @author XaeroDegreaz
 */
public abstract class SmartLobby extends TCPExtension {

    protected Map<String, User> userList = Collections.synchronizedMap(new HashMap<String, User>());
    protected Map<String, Room> roomList = Collections.synchronizedMap(new HashMap<String, Room>());
    protected JsonObject config;
    protected Gson gson = new Gson();
    //private SmartLobby extension;

    public SmartLobby(int port) {
        super(port);
        //this.extension = extension;
    }

    @Override
    public void onExtensionReady() {
        onSmartLobbyReady();
    }

    /**
     * Must be implemented by extending classes.
     */
    protected abstract void onSmartLobbyReady();

    protected void setConfig(String file, String defaultRoomsKey) {
        try {
            config = (JsonObject) new JsonParser().parse(Config.readFile(file).toString());
            System.out.println(config);
            setDefaultRooms((JsonArray) config.get(defaultRoomsKey));
        } catch (FileNotFoundException e) {
            Logger.log("Cannot find the SmartLobby configuration file \'" + file + "\'.");
        } catch (JsonParseException e) {
            Logger.log("Malformed JSONObject in the SmartLobby config file: " + e.getMessage());
        }
    }

    protected void setConfig() {
        setConfig("SmartLobbyConfig.json", "default-rooms");
    }

    protected void setConfig(JsonObject configJSON) {
        config = configJSON;
        Logger.log("Custom JSON Object being used for the SmartLobby configuration!");
    }

    @Override
    public void onConnect(TCPClient client) {
        //# No need to really do anything here.
    }

    @Override
    public void onDisconnect(TCPClient client) {
        //# Remove them from their room, if they are in one
        User user = getUserByTCPClient(client);
        //# This is mostly a check for the crossdomain request;
        //# we don't want to try to remove a non-existant user
        if (user != null) {
            user.getRoom().onUserLeave(user);
        }
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
    public boolean onDataSpecial(TCPClient client, String methodName, JsonObject params) {
        User user = getUserByTCPClient(client);
        Method m = null;
        Class[] c = new Class[2];
        c[0] = User.class;
        c[1] = JsonObject.class;
        Object[] o = {user, params};
        try {
            if (params.get("for").toString().equals("room")) {
                m = Room.class.getMethod(methodName, c);
                m.invoke(user.getRoom(), o);
                return false;
            } else if (params.get("for").toString().equals("user")) {
                m = User.class.getMethod(methodName, c);
                m.invoke(user, o);
                return false;
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return true;
    }

    private void setDefaultRooms(JsonArray array) {
        //# Loop through the array and create some rooms
        //# no username need be set; owner will be null wich will suffice
        for (int i = 0; i < array.size(); i++) {
            JsonObject r = (JsonObject) array.get(i);

            Room rm = gson.fromJson(r.toString(), Room.class);

            roomList.put(r.get("name").getAsString(), rm);
        }
    }

    /**
     * This is a very simple login mechanism that accepts a string name
     * and creates a User object, then places it in the Collection.
     * If you want more complex login mechanisms, please override this
     * method in your SmartLobby extension.
     * @param client
     * @param json 
     */
    public void onLogin(TCPClient client, JsonObject json) {
        //# Perform some login logic here to see if user already logged in
        //# Perhaps evaluate to pass back to secondary callback for custom
        //# Logging in
        try {
            client.setUniqueId(json.get("username").getAsString());
            userList.put(client.getUniqueId().toString(), new User(client, this));
        } catch (Exception e) {
            Logger.log("SmartLobby username taken: " + e);
        }
    }

    /**
     * Send a full list of users in all rooms to the specified client
     * @param client
     * @param json 
     */
    public void getFullUserList(TCPClient client, JsonObject json) {
    }

    /**
     * Send a full room list to the client
     * @param client
     * @param json 
     */
    public void getRoomList(TCPClient client, JsonObject json) {
        ClientCall call = new ClientCall("onRoomList");
        call.put("roomList", ClientCall.serialize(roomList));
        client.send(call);
    }

    public void createRoom(TCPClient client, JsonObject json) {
        //# Make sure that only one room with same name registered
        if (roomList.containsKey(json.get("name").getAsString())) {
            ClientCall call = new ClientCall("onCreateRoomError");
            call.put("message", "A room already exists with that name.");
            client.send(call);
            return;
        }

        //# Make sure this user is not the owner of another room
        User user = getUserByTCPClient(client);
        if (user.getRoom().getOwner() == user) {
            ClientCall call = new ClientCall("onCreateRoomError");
            call.put("message", "You cannot create more than one room.");
            client.send(call);
            return;
        }

        Room room = gson.fromJson(json.toString(), Room.class);
        room.setOwner(user);

        roomList.put(json.get("name").toString(), room);

        room.onUserJoin(getUserByTCPClient(client));
    }

    //# Would like to have joinRoom inside the Room object, but think it's easier to maintain out here
    public void joinRoom(TCPClient client, JsonObject json) {
        Room room = getRoomByName(json.get("name").getAsString());

        if (room != null) {
            room.onUserJoin(getUserByTCPClient(client));
        } else {
            ClientCall call = new ClientCall("onJoinRoomError");
            call.put("message", "The selected room is not available to join.");
        }

    }

    public void leaveRoom(TCPClient client, JsonObject json) {
        User user = getUserByTCPClient(client);
        user.getRoom().onUserLeave(user);
    }

    //# Helper methods..
    public Room getRoomByName(String name) {
        return roomList.get(name);
    }

    public User getUserByTCPClient(TCPClient client) {
        return userList.get(client.getUniqueId().toString());
    }

    public User getUserByUsername(String username) {
        return userList.get(username);
    }
}
