package net.smartsocket.smartlobby;

import net.smartsocket.serverclients.TCPClient;

/**
 *
 * @author XaeroDegreaz
 */
public final class User {

	private transient static long currentUserID = 0;
	private transient Room room;
	private transient TCPClient tcpClient = null;
	private String username = null;
	private transient FriendsList friendsList = null;
	private long userID = -1;
	private transient SmartLobby slInstance = null;

	public User( TCPClient client, SmartLobby slInstance ) {
		tcpClient = client;
		username = client.getUniqueId().toString();
		this.slInstance = slInstance;
		setUserID();
	}

	/**
	 * This should only be used for setting up *test users* with no real TCPClient
	 * bound to them. For instance, when creating default rooms, this constructor will
	 * be called and assign a 'fake' user as the owner named Server.
	 * @param user 
	 */
	public User( String user ) {
		this.username = user;
	}

	/**
	 * @return the room
	 */
	public Room getRoom() { //SOmething like this 
		return this.room;
	}

	public Room getRoom( int key ) {
		return this.room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom( Room room ) {
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

	/**
	 * @return the tcpClient
	 */
	public TCPClient getTcpClient() {
		return tcpClient;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername( String username ) {
		this.username = username;
	}
}
