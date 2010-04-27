package examples.sfbay;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class SampleFrame extends JFrame
{
  private BorderLayout borderLayout1;
  private CommandPanel commandPanel1;

  public SampleFrame()
  {
    borderLayout1 = new BorderLayout();
    commandPanel1 = new CommandPanel();
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    getContentPane().setLayout(borderLayout1);
    setSize(new Dimension(600, 400));
    setTitle("Example 7 - the Bay");
    getContentPane().add(commandPanel1, "Center");
  }
}
