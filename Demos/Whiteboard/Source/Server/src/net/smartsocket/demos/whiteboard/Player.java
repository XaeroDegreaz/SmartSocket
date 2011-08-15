/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.demos.whiteboard;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.smartsocket.serverclients.TCPClient;

/**
 *
 * @author XaeroDegreaz
 */
public class Player {
    
    public Number mouseX;
    public Number mouseY;
    public String username;
    public transient TCPClient client;
    public Boolean isDrawing = false;
    public Collection canvas = new ArrayList();
    
    public Player(TCPClient client, JsonObject json) {
        this.client = client;
        this.username = json.get("username").getAsString();
    }
    
}
