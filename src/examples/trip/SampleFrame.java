package examples.trip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

public class SampleFrame extends JFrame
{
  private BorderLayout borderLayout;
  private CommandPanel commandPanel;

  public SampleFrame()
  {
    borderLayout = new BorderLayout();
    commandPanel = new CommandPanel();
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
    getContentPane().setLayout(borderLayout);
    setSize(new Dimension(600, 400));
    setTitle("Pacific Tour - Version 7");
    getContentPane().add(commandPanel, BorderLayout.CENTER);
  }
}
