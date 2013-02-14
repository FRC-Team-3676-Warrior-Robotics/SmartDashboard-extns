/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.aisd.martin.frc.DashboardExtensions;

import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpijavacv.WPICamera;
import edu.wpi.first.wpijavacv.WPIImage;

/**
 *
 * @author Neil
 */
public class TrackingWare extends StaticWidget{
	//PIVS for the DashboardPart
	public static final String NAME = "Team 3676 Tracker";
	public static boolean firstCamera = true;
	private boolean resized = false;
	private BGThread bgThread = new BGThread();
	private GCThread gcThread = new GCThread();
	
	//PIVS for the image Processing
	final int XMAXSIZE = 24;
    final int XMINSIZE = 24;
    final int YMAXSIZE = 24;
    final int YMINSIZE = 48;
    final double xMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double xMin[] = {.4, .6, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, 0.6, 0};
    final double yMax[] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
    final double yMin[] = {.4, .6, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
								.05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
								.05, .05, .6, 0};
	final int RECTANGULARITY_LIMIT = 60;
    final int ASPECT_RATIO_LIMIT = 75;
    final int X_EDGE_LIMIT = 40;
    final int Y_EDGE_LIMIT = 60;
    
    final int X_IMAGE_RES = 320;          //X Image resolution in pixels, should be 160, 320 or 640
    //final double VIEW_ANGLE = 43.5;       //Axis 206 camera (We aren't using this one)
    final double VIEW_ANGLE = 48;       //Axis M1011 camera (We are using this one)
	
	//PIVS to get the image
	WPICamera camera;
	
	public class GCThread extends Thread {
		boolean destroyed = false;
		
		public void TrackingWare(){
			
		}
		//Our garbage collecter thread may not need it in this one
		//Basically keeps memory from getting out of hard during
		//complex operations
		@Override
		public void run(){
			while(!destroyed){
				try{
					Thread.sleep(1000);
				} catch(InterruptedException e){
				
				}
				System.gc();
			}
		}
		
		@Override
		public void destroy(){
			destroyed = true;
			interrupt();
		}
	}
	
	public class BGThread extends Thread{
		boolean destroyed = false;
		
		Runnable draw = new Runnable(){
			@Override
			public void run(){
				DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
				frame.repaint();
			}
		};
		
		public BGThread(){
			super("Team 3676 Tracking Thread");
		}
		
		@Override
		public void run(){
			
		}
		
	}
	
	@Override
	public void init() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void propertyChanged(Property prprt) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
