package net.smartsocket.forms;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import net.smartsocket.clients.TCPClient;

/**
 * Statistics tracker is a class that controls displaying how many clients are connected, etc., in the bottom of the GUI
 * @author XaeroDegreaz
 */
public class StatisticsTracker {

    private static NumberFormat format = NumberFormat.getInstance();
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private static long hours = 0;
    private static int minutes = 0;
    private static int seconds = 0;

    /**
     * Start the timer which controls displaying memory, uptime, upstream, and downstream
     */
    public static void start() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new StatisticsTimer(), 0, 1000);
    }
    /**
     * This is called when a user connects or disconnects from the server, showing the correct number in the GUI
     */
    public static void updateClientsConnectedLabel() {
        ConsoleForm.lblConnectedClients.setText("Connected Clients: "+TCPClient.getClients().size());
    }
    /**
     * Display the server's current upstream
     */
    public static void updateUpstreamLabel() {
        double upstream = Double.valueOf( decimalFormat.format( (double)TCPClient.getOutboundBytes() / 1024 ) );
        ConsoleForm.lblUpstream.setText("Upstream: "+upstream+" kb/s");
        TCPClient.setOutboundBytes(0);
    }
    /**
     * Display the server's current downstream
     */
    public static void updateDownstreamLabel() {
        double downstream = Double.valueOf( decimalFormat.format( (double)TCPClient.getInboundBytes() / 1024 ) );
        ConsoleForm.lblDownstream.setText("Downstream: "+downstream+" kb/s");
        TCPClient.setInboundBytes(0);
    }
    /**
     * Show the uptime of the server in the GUI
     */
    public static void updateUptimeLabel() {
        String secondsString;
        String minutesString;

        //# We increment a second each tick..
        seconds++;
        //# Make sure we increment a minute when we need to.
        if (seconds == 60) {
            seconds = 00;
            minutes++;
        }
        //# Add leading zero when needed
        if(seconds < 10) {
            secondsString = String.format("0%d", seconds);
        }else {
            secondsString = String.valueOf(seconds);
        }
        //# Increment our hours when necessary
        if(minutes == 60) {
            minutes = 00;
            hours++;
        }
        //# Add leading zero to minute when necessary.
        if(minutes < 10) {
            minutesString = String.format("0%d", minutes);
        }else {
            minutesString = String.valueOf(minutes);
        }
        ConsoleForm.lblUptime.setText("Uptime: "+hours+":"+minutesString+":"+secondsString);
    }
    /**
     * Display roughly the server's current memory usage.
     */
    public static void updateMemoryUsageLabel() {
        //# Strange, I have to multiply the output by 10 in order to get an accurate number....
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        ConsoleForm.lblMemoryUsage.setText("Memory Usage: "+format.format( ( ( usedMemory * 10 ) / 1024) )+"K" );
    }

}

/**
 * This class is just the timer that updates our interface with all of the information.
 * @author XaeroDegreaz
 */
class StatisticsTimer extends TimerTask {

    public StatisticsTimer() {

    }

    public void run() {
        StatisticsTracker.updateUpstreamLabel();
        StatisticsTracker.updateDownstreamLabel();
        StatisticsTracker.updateUptimeLabel();
        StatisticsTracker.updateMemoryUsageLabel();
    }
}
