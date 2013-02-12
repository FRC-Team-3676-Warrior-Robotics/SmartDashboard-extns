package switchablecameraextension;

import edu.wpi.first.smartdashboard.gui.DashboardFrame;
import edu.wpi.first.smartdashboard.gui.DashboardPrefs;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IPAddressProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.wpijavacv.WPICamera;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIGrayscaleImage;
import edu.wpi.first.wpijavacv.WPIImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
/**
 *
 * @author Neil
 */
public class SwitchableCameraExtension extends StaticWidget{
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
	
	public class GCThread extends Thread {
		boolean destroyed = false;
		
		public void Team100Camera(){
			
		}
		
		@Override
		public void run(){
			while(!destroyed){
				try{
					Thread.sleep(1000);
				} catch(InterruptedException ex){
				}
				System.gc();
			}
		}
		
		public void destroy(){
			destroyed = true;
			interrupt();
		}
	}
	
	public class BGThread extends Thread {
		boolean destroyed = false;
		Runnable draw = new Runnable(){
			public void run(){
				//Dashboard.getInstance.getPanel().repaint(getBounds());
				DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
				frame.repaint();
			}
		};
		
		public BGThread(){
			super("Camera Backgournd");
		}
		
		@Override
		public void run(){
			WPIImage image;
			while(!destroyed){
				if(cam == null){
					cam = new WPICamera("10.36.76.11");
					System.out.println("CAMERA 1 now set");
				}
				if(cam2 == null){
					cam2 = new WPICamera("10.36.76.12");
					System.out.println("Camera 2 now set");
				}
				
				try{
					if(firstCamera){
						image = cam.getImage();
					} else {
						image = cam2.getImage();
					}
					if(image instanceof WPIColorImage){
						
					}
				} catch(final Exception e){
					e.printStackTrace();
					cam.dispose();
					cam2.dispose();
					cam = null;
					cam2 = null;
					
					System.out.println("CAMERA CONNECTIONS FAILED - ATEMPTING RESET");
					drawnImage = null;
					SwingUtilities.invokeLater(draw);
				}
			}
		}
		
		@Override
		public void destroy(){
			destroyed = true;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g){
		if(drawnImage != null){
			if(!resized){
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
		if(property == ipProperty){
			if(cam != null){
				cam.dispose();
			}
			
			try{
				cam = new WPICamera(ipProperty.getSaveValue());
			} catch (Exception e){
				e.printStackTrace();
                drawnImage = null;
                setPreferredSize(new Dimension(100, 100));
                revalidate();
                //DashboardFrame.getInstance().getPanel().repaint(getBounds());
 
                DashboardFrame frame = (DashboardFrame) DashboardFrame.getInstance();
                
                frame.repaint();
			}
		}
	}
	
	@Override
	public void disconnect(){
		bgThread.destroy();
		gcThread.destroy();
		if(cam != null){
			cam.dispose();
		}
		if(cam2 != null){
			cam2.dispose();
		}
		super.disconnect();
	}
		
	public WPIImage processImage(WPIColorImage rawImage){
		return rawImage;
	}
	public WPIImage processImage(WPIGrayscaleImage rawImage) {
        return rawImage;
    }

	public static void SwitchCamera(){
		firstCamera = !firstCamera;
	}
}
