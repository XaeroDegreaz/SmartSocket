/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.extensions.smartlobby.tools;

import java.awt.Point;

/**
 *
 * @author XaeroDegreaz
 */
public class PathProjection {

    //static Point point;

    public static float getTime(float x1, float y1, float x2, float y2, int speed) {
	float time = 0;
	//# Distance: The total distance to be traveled along the grid.
	float distance = (float)Point.distance(x1, y1, x2, y2);

	time =  distance / (speed*5);

	System.err.println("Distance is "+distance);
	return time;
    }

//    private static void getPath(float x1, float y1, float x2, float y2, float speed, float elapsed) {
//
//	//# Distance: The total distance to be traveled along the grid.
//	float d = (float)point.distance(x1, y1, x2, y2);
//
//	//# Time: How long it would take to reach the destined distance
//	float t = d / speed;
//
//	float slope = PathProjection.getSlope(x1, y1, x2, y2, d);
//	//# Location at elapsed time: Current location along the grid
//	float l = (elapsed*speed) / slope;
//
//	System.out.println("Total distance: " + d);
//	System.out.println("Total time: " + t);
//	System.out.println("Current location at " + elapsed + " seconds of movement: " + l);
//
//	PathProjection.printCoordinates(x1, y1, x2, y2, Math.abs(slope));
//
//    }
//
//    private static float getSlope(float x1, float y1, float x2, float y2, float distance) {
//	float yValue = y2 - y1;
//	float xValue = x2 - x1;
//	float slope = yValue / xValue;
//	System.out.println("The slope is: "+Math.abs(slope));
//	return distance / slope;
//    }
//
//    private static void printCoordinates(float x1, float y1, float x2, float y2, double slope) {
//	int totalPoints = 0;
//	while(x1 < x2 && y1 < y2) {
//	    x1 += slope;
//	    y1 += slope;
//	    totalPoints++;
//	    System.out.println("("+x1+" , "+y1+") : "+totalPoints);
//	}
//
//    }
}
