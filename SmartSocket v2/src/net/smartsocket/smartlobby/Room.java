/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.smartlobby;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.smartsocket.Logger;
import org.json.JSONObject;

/**
 *
 * @author XaeroDegreaz
 */
public final class Room {

    private List<User> userList = Collections.synchronizedList( new LinkedList<User>() );
    private SmartLobby slInstance;
    private User owner;
    private boolean serverGenerated = true;
    private String name;


    public Room(JSONObject room, SmartLobby slInstance) {
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
        if(userList.size() <= 0) {
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
        serverGenerated = false;
        return this;
    }

    public User getOwner() {
        return this.owner;
    }

    public void getUserList() {
        //# Maybe here is where we should start thinking about using serialization
    }

}
