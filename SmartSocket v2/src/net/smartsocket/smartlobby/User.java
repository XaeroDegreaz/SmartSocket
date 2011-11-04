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

	/**
	 * This object contains the specifics of a TCPClient -- essentially, we are adding more features
	 * to the TCPClient to give it a personality. Users can join rooms, play games, have user names
	 * and have a friends list.
	 * @param client The TCPClient bound to this User object
	 * @param slInstance a valid reference to the main SmartLobby instance.
	 */
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
	 * @return The Room object of this User
	 */
	public Room getRoom() { //SOmething like this 
		return this.room;
	}
	
	/**
	 * This is not yet used. This will be used for tracking user in multiple rooms.
	 * @param key
	 * @return 
	 */
	public Room getRoom( int key ) {
		return this.room;
	}

	/**
	 * @param room Sets the room of the user to this room.
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
	 * @param userID Assign this User a unique integer identifier.
	 */
	private synchronized void setUserID() {
		this.userID = currentUserID;
		currentUserID++;
	}

	/**
	 * @return The TCPClient of this User object.
	 */
	public TCPClient getTcpClient() {
		return tcpClient;
	}

	/**
	 * @return The string username of this User
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to assign to this user.
	 */
	public void setUsername( String username ) {
		this.username = username;
	}
}
