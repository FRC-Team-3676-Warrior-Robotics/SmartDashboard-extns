/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.aisd.martin.frc.DashboardExtensions;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author Neil
 */
public class CameraSwitch extends StaticWidget {

	public static final String name = "Camera Switch";
	JButton button;
	StringProperty label = new StringProperty(this, "Label", "Camera");

	@Override
	public void init() {
		this.button = new JButton((String) this.label.getValue());

		setLayout(new GridLayout());
		add(this.button, 0);
		this.button.setFocusable(false);
		this.button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				SwitchableCameraExtension.SwitchCamera();
				System.out.println("________________");
				System.out.println("SWITCHING CAMERA");
				System.out.println("________________");
			}
		});
	}

	@Override
	public void propertyChanged(Property property) {
		if (property == this.label) {
			this.button.setText((String) this.label.getValue());
		}
	}
}
