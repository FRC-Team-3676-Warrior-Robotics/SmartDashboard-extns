package net.aisd.martin.frc.DashboardExtensions;

import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.gui.DashboardPrefs;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IPAddressProperty;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpijavacv.WPICamera;
import edu.wpi.first.wpijavacv.WPIColor;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIGrayscaleImage;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/**
 *
 * @author Neil
 */
public class SwitchableCameraExtension extends StaticWidget {

	public static final String NAME = "Team 3676 Cameras";
	public static boolean firstCamera = true;
	private boolean resized = false;
	private WPICamera cam;
	private WPICamera cam2;
	private BufferedImage drawnImage;
	private BGThread bgThread = new BGThread();
	private GCThread gcThread = new GCThread();
	DashboardPrefs prefs = (DashboardPrefs) DashboardPrefs.getInstance();
	public final IPAddressProperty ipProperty = new IPAddressProperty(this, "Camera IP Address", new int[]{10, (prefs.team.getValue() / 100), (prefs.team.getValue() % 100), 11});
	public final IntegerProperty heightProperty = new IntegerProperty(this, "Target height", 20);
	public final IntegerProperty widthProperty = new IntegerProperty(this, "Target width", 20);
	public final IntegerProperty dist1 = new IntegerProperty(this, "Target1 y", 70);
	public final IntegerProperty dist2 = new IntegerProperty(this, "Target2 y", 120);
	public final IntegerProperty dist3 = new IntegerProperty(this, "Target3 y", 170);
	public final IntegerProperty xAdj1 = new IntegerProperty(this, "Target1 x", 0);
	public final IntegerProperty xAdj2 = new IntegerProperty(this, "Target2 x", 0);
	public final IntegerProperty xAdj3 = new IntegerProperty(this, "Target3 x", 0);

	public class GCThread extends Thread {

		boolean destroyed = false;

		public void Team100Camera() {
		}

		@Override
		public void run() {
			while (!destroyed) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
				System.gc();
			}
		}

		@Override
		public void destroy() {
			destroyed = true;
			interrupt();
		}
	}

	public class BGThread extends Thread {

		boolean destroyed = false;
		Runnable draw = new Runnable() {
			@Override
			public void run() {
				DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
				frame.repaint();
			}
		};

		public BGThread() {
			super("Camera Background");
		}

		@Override
		public void run() {
			WPIImage image;
			while (!destroyed) {

				if (cam == null) {
					cam = new WPICamera("10.36.76.11");
					System.out.println("CAMERA 1 now set");
				}
				if (cam2 == null) {
					cam2 = new WPICamera("10.36.76.12");
					System.out.println("Camera 2 now set");
				}

				try {
					if (firstCamera && cam != null) {
						image = cam.getNewImage(5.0);
					} else if (cam2 != null) {
						image = cam2.getNewImage(5.0);
					} else {
						image = null;
					}
					if (image instanceof WPIColorImage) {
						System.out.println("Process image width:" + image.getWidth() + " Height:" + image.getHeight());
						drawnImage = processImage((WPIColorImage) image).getBufferedImage();
						SwingUtilities.invokeLater(draw);
					} else if (image instanceof WPIGrayscaleImage) {
						System.out.println("Process image width:" + image.getWidth() + " Height:" + image.getHeight());
						drawnImage = processImage((WPIGrayscaleImage) image).getBufferedImage();
						SwingUtilities.invokeLater(draw);
					}
				} catch (final Exception e) {
					e.printStackTrace();
					if (firstCamera) {
						cam.dispose();
						cam = null;
					} else {
						cam2.dispose();
						cam2 = null;
					}

					System.out.println("CAMERA CONNECTIONS FAILED - ATEMPTING RESET");
					drawnImage = null;
					SwingUtilities.invokeLater(draw);
				}
			}
		}

		@Override
		public void destroy() {
			destroyed = true;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (drawnImage != null) {
			if (!resized) {
				setPreferredSize(new Dimension(drawnImage.getWidth(), drawnImage.getHeight()));
				revalidate();
			}
			int width = getBounds().width;
			int height = getBounds().height;
			double scale = Math.min((double) width / (double) drawnImage.getWidth(), (double) height / (double) drawnImage.getHeight());
			g.drawImage(drawnImage, (int) (width - (scale * drawnImage.getWidth())) / 2, (int) (height - (scale * drawnImage.getHeight())) / 2,
					(int) ((width + scale * drawnImage.getWidth()) / 2), (int) (height + scale * drawnImage.getHeight()) / 2,
					0, 0, drawnImage.getWidth(), drawnImage.getHeight(), null);
		} else {
			g.setColor(Color.RED);
			g.fillRect(0, 0, getBounds().width, getBounds().height);
			g.setColor(Color.darkGray);
			g.drawString("NO CONNECTION", 10, 10);
		}
	}

	@Override
	public void init() {
		setPreferredSize(new Dimension(100, 100));
		bgThread.start();
		gcThread.start();
		revalidate();

		DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
		frame.repaint();
	}

	@Override
	public void propertyChanged(Property property) {
		if (property == ipProperty) {
			if (cam != null) {
				cam.dispose();
			}

			try {
				cam = new WPICamera(ipProperty.getSaveValue());
			} catch (Exception e) {
				e.printStackTrace();
				drawnImage = null;
				setPreferredSize(new Dimension(100, 100));
				revalidate();
				//DashboardFrame.getInstance().getPanel().repaint(getBounds());

				DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();

				frame.repaint();
			}
		} else {
			TOP_GOAL_WIDTH = widthProperty.getValue();
			TOP_GOAL_HEIGHT = heightProperty.getValue();
			y1 = dist1.getValue();
			y2 = dist2.getValue();
			y3 = dist3.getValue();
			x1 = xAdj1.getValue();
			x2 = xAdj2.getValue();
			x3 = xAdj3.getValue();
		}
	}

	@Override
	public void disconnect() {
		bgThread.destroy();
		gcThread.destroy();
		if (cam != null) {
			cam.dispose();
		}
		if (cam2 != null) {
			cam2.dispose();
		}
		super.disconnect();
	}
	//Put her down the center of the goal
	private int TOP_GOAL_WIDTH = 20;
	private int TOP_GOAL_HEIGHT = 20;
	private int y1 = 70;
	private int y2 = 120;
	private int y3 = 170;
	private int x1 = 0;
	private int x2 = 0;
	private int x3 = 0;

	public WPIImage processImage(WPIColorImage rawImage) {
		if (firstCamera) {
			return rawImage;
		}
		WPIPoint topPoint = new WPIPoint(rawImage.getWidth() / 2, 0);
		WPIPoint bottomPoint = new WPIPoint(rawImage.getWidth() / 2, rawImage.getHeight());
		rawImage.drawLine(topPoint, bottomPoint, WPIColor.BLACK, 2);
		rawImage.drawRect(rawImage.getWidth() / 2 - (TOP_GOAL_WIDTH / 2) + x1, y1, TOP_GOAL_WIDTH, TOP_GOAL_HEIGHT, WPIColor.YELLOW, 2);
		rawImage.drawRect(rawImage.getWidth() / 2 - (TOP_GOAL_WIDTH / 2) + x2, y2, TOP_GOAL_WIDTH, TOP_GOAL_HEIGHT, WPIColor.YELLOW, 2);
		rawImage.drawRect(rawImage.getWidth() / 2 - (TOP_GOAL_WIDTH / 2) + x3, y3, TOP_GOAL_WIDTH, TOP_GOAL_HEIGHT, WPIColor.YELLOW, 2);

		return rawImage;
	}

	public WPIImage processImage(WPIGrayscaleImage rawImage) {
		return rawImage;
	}

	public static void SwitchCamera() {
		firstCamera = !firstCamera;
	}
}
