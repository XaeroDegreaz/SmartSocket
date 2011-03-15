package net.smartsocket.forms;

import net.smartsocket.clients.TCPClient;

/**
 * Statistics tracker is a class that controls displaying how many clients are connected, etc., in the bottom of the gui
 * @author XaeroDegreaz
 */
public class StatisticsTracker {

    public static void updateClientsConnectedLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }

    public static void updateUpstreamLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }

    public static void updateDownstreamAvailableLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }

    public static void updateUptimeLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }

    public static void updateMemoryUsageLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }

}
