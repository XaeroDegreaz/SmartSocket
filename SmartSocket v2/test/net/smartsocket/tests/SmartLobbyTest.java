/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.tests;

import net.smartsocket.smartlobby.SmartLobby;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobbyTest extends SmartLobby {
    
    public static void main(String[] args) {
        new SmartLobbyTest().start();
    }
    
    public SmartLobbyTest() {
        super(8888);
    }

    @Override
    protected void onSmartLobbyReady() {
        setConfig();
    }
    
}
