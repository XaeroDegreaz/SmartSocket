/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.extensions.smartlobby.tools;

/**
 *
 * @author XaeroDegreaz
 */
public class ParseNumber {

    public static float asFloat(Object obj) {

	float n = Float.parseFloat(obj.toString());
	
	return n;
    }
}
