/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.aisd.martin.frc.DashboardExtensions;

import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 *
 * @author Neil
 */
public class AutoReady extends StaticWidget {

	@Override
	public void init() {
		setPreferredSize(new Dimension(200, 100));
		revalidate();
		DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
		frame.repaint();
	}

	@Override
	public void propertyChanged(Property prprt) {
		//Do nothing
	}
	
	@Override
	public void paintComponent(Graphics g){
		if(NetworkTable.getTable("SmartDash").getBoolean("X-axis")){
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			g.setColor(Color.BLACK);
			g.drawString("READY TO FIRE", 0, 0);
		} else {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			g.setColor(Color.BLACK);
			g.drawString("NOT READY TO FIRE", 0, 0);
		}
	}
	
}
