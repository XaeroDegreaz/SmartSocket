/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author XaeroDegreaz
 */
public class ThreadHandler implements Runnable {

    static Vector handlers = new Vector(10);
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //static SmartSocketJAVAApp _server;

    public ThreadHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        String line;
        synchronized (handlers) {
            handlers.addElement(this);
        }
        try {
            while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
                for (int i = 0; i < handlers.size(); i++) {
                    synchronized (handlers) {
                        ThreadHandler handler =
                                (ThreadHandler) handlers.elementAt(i);
                        handler.out.println(line + "\r");
                        handler.out.flush();
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ioe) {
            } finally {
                synchronized (handlers) {
                    handlers.removeElement(this);
                }

            }
        }
    }
}

