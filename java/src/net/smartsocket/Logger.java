/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket;

/**
 *
 * @author XaeroDegreaz
 */
public class Logger {
    
    public static void log(String className, String msg) {
	javax.swing.JTextPane c = MainView.consoleLog;
	c.setText(c.getText()+"["+className+"]:\t"+msg+"\r");
    }
}
