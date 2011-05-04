/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.smartlobby;

import net.smartsocket.clients.TCPClient;

/**
 *
 * @author XaeroDegreaz
 */
public final class User {
    private static long currentUserID = 0;
    private Room room = null;
    private TCPClient tcpClient = null;
    private String username = null;
    private FriendsList friendsList = null;
    private long userID;
    private SmartLobby slInstance = null;

    public User(TCPClient client, SmartLobby slInstance) {
        tcpClient = client;
        username = client.getUniqueId().toString();
        this.slInstance = slInstance;
        setUserID();
    }
    /**
     * @return the room
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * @return the userID
     */
    public long getUserID() {
        return this.userID;
    }

    /**
     * @param userID the userID to set
     */
    private synchronized void setUserID() {
        this.userID = currentUserID;
        currentUserID++;
    }
}
