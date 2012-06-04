package examples.stereographic;

import chart.components.util.MercatorUtil;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.UIManager;
import user.util.GeomUtil;

public class Main4Tests
{

  public Main4Tests()
  {
    Frame frame = new SampleFrame();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if(frameSize.height > screenSize.height)
      frameSize.height = screenSize.height;
    if(frameSize.width > screenSize.width)
      frameSize.width = screenSize.width;
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.addWindowListener(new WindowAdapter() {

      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }

    });
    frame.setVisible(true);
  }

  public static void main(String args[])
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    new Main4Tests();
  }
}
