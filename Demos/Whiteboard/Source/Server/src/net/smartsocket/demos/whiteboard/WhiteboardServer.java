/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.demos.whiteboard;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.ClientCall;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.serverextensions.TCPExtension;

/**
 *
 * @author XaeroDegreaz
 */
public class WhiteboardServer extends TCPExtension {

    public static Map<String, Player> userList = new HashMap<String, Player>();
    private Map<String, Number> coords = new HashMap<String, Number>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Logger.setLogLevel(Logger.CRITICAL);
        new WhiteboardServer().start();
    }

    public WhiteboardServer() {
        super(8888);
    }

    @Override
    public void onExtensionReady() {
    }

    @Override
    public void onConnect(TCPClient client) {
        Logger.log("Connected");
    }

    @Override
    public void onDisconnect(TCPClient client) {
        Player player = getPlayerByTCPClient(client);
        userList.remove(player.username);

        ClientCall call = new ClientCall("onUserLeave");
        call.put("username", player.username);

        broadcastMessage(call);

    }

    @Override
    public boolean onDataSpecial(TCPClient client, String methodName, JsonObject params) {
        return true;
    }

    public void login(TCPClient client, JsonObject json) {
        String name = json.get("username").getAsString();

        if (userList.containsKey(name)) {
            ClientCall call = new ClientCall("onLoginError");
            call.put("message", "Username " + name + " taken.");
            client.send(call);
        } else {

            //# An important part about assigning a username to a client!
            try {
                client.setUniqueId(name);
            } catch (Exception e) {
                return;
            }
            
            Player player = new Player(client, json);
            player.username = name;
            
            userList.put(name, player);

            ClientCall call = new ClientCall("onLogin");
            call.put("username", name);
            client.send(call);

            //# Notify all players that a new drawer is coming online
            call = new ClientCall("onUserJoin");
            call.put("username", name);

            broadcastMessage(call);

            //# Loop through each player, and send a message containing each player's details to the joining user
            //# so that the new player is able to see current drawings, get the intial position of thier cursor, etc.
            call = new ClientCall("onPlayerCanvases");

            //# Loop throug each player and send the class object as json (easier than picking out which to send)d
            for (Map.Entry<String, Player> entry : userList.entrySet()) {
                Player p = entry.getValue();
                call.put(p.username, ClientCall.serialize(p));
            }

            client.send(call);
        }
    }

    public void mouseMove(TCPClient client, JsonObject json) {
        ClientCall call;
        Player player = getPlayerByTCPClient(client);        

        player.mouseX = json.get("x").getAsNumber();
        player.mouseY = json.get("y").getAsNumber();
        
        //# Add a new stroke to the canvas.
        if (player.isDrawing) {
            coords = new HashMap<String, Number>();            
            coords.put("x", json.get("x").getAsNumber());
            coords.put("y", json.get("y").getAsNumber());
            
            player.canvas.add(coords);
            
            call = new ClientCall("onDraw");
            call.put("x", player.mouseX);
            call.put("y", player.mouseY);
            call.put("username", player.username);

            broadcastMessage(call);
        }

        //# Simple movement of the mouse on movieclip
        call = new ClientCall("onMove");
        call.put("x", player.mouseX);
        call.put("y", player.mouseY);
        call.put("username", player.username);

        broadcastMessage(call);
    }

    public void mouseDown(TCPClient client, JsonObject json) {
        Player player = getPlayerByTCPClient(client);
        player.isDrawing = true;

        ClientCall call = new ClientCall("onMoveTo");
        call.put("coords", ClientCall.serialize(json));
        call.put("username", player.username);
        client.send(call);

        //# start drawing.
        mouseMove(client, json);
    }

    public void mouseUp(TCPClient client, JsonObject json) {
        Player player = getPlayerByTCPClient(client);
        player.isDrawing = false;

        //# we need to add a marker in their canvas saying they lifted their pen at this time.
        //# this allows us to make sure that users coming in after the player has drawn, and lifted pen
        //# multiple times, that we are not drawing continuous lines from coord to coord in the canvas where there shouldn't be
        coords = new HashMap<String, Number>();
        coords.put("l", -1);
        
        player.canvas.add(coords);
        
        ClientCall call = new ClientCall("onRelease");
        call.put("username", player.username);
        
        broadcastMessage(call);
    }

    public void clear(TCPClient client, JsonObject json) {
        //# clear all and send message to all clients
        Player player = getPlayerByTCPClient(client);
        player.canvas = new ArrayList();

        ClientCall call = new ClientCall("onClear");
        call.put("username", player.username);

        broadcastMessage(call);
    }

    public Player getPlayerByTCPClient(TCPClient client) {
        return userList.get(client.getUniqueId().toString());
    }

    public Player getPlayerByUsername(String username) {
        return userList.get(username);
    }
}
