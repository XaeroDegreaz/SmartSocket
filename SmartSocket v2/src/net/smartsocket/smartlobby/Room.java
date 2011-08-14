/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.smartlobby;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.ClientCall;

/**
 *
 * @author XaeroDegreaz
 */
public  class Room {
    
    //# We mark this transient because we don't want users serialized when sending out room lists.
    //# If a user wants the list of users in the room, they must explicitly call getUserList from their client.
    private transient List<User> userList = Collections.synchronizedList( new LinkedList<User>() );
    private int maxUsers = 16;
    //# Defining this allows developers to run multiple unique SmartLobby instances.
    //# Without this, we would need to make userList and roomList static fields in SmartLobby's class.
    private transient SmartLobby slInstance;
    private User owner;
    private boolean userCreated = false;
    private String name;
    private transient String password;
    private transient Gson gson = new Gson();


    public Room(JsonObject room, SmartLobby slInstance) {
        this.slInstance = slInstance;
        //# Process room object, etc...
    }
    
    //# Events
    public void onUserJoin(User user) {
        
        try {
            user.getRoom().onUserLeave(user);
        }catch(Exception e) {
            Logger.log("User does not have a room to leave... "+e.getMessage());
        }

        userList.add(user);
    }

    public void onUserLeave(User user) {
        userList.remove(user);
        user.setRoom(null);
        
        //# Check to see if we need to remove this room
        if(userList.size() <= 0 && userCreated) {
            slInstance.roomList.remove(this.name);
            //# TODO Send roomlist update to rooms that need to receive this event
            //
            return;
        }
        
        //# Check to see if we need to assign another room owner
        if (getOwner() == user) {            
            owner = null;
            User newOwner = userList.get(0);
            //# TODO Send message to room showing that ownership has changed            
        }
    }

    public Room setOwner(User user) {
        owner = user;
        userCreated = true;
        return this;
    }

    public User getOwner() {
        return this.owner;
    }

    public void getUserList(User user, JsonObject json) {
        //# We must recreate it in a temp variable since we marked it transient in the class.
        List tmp = userList;
        ClientCall call = new ClientCall("onUserList");
        call.put( "userList", ClientCall.serialize(tmp) );
        user.getTcpClient().send(call);        
    }

}
