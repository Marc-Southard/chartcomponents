package examples.casestudy;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultFocusManager;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SampleFrame extends JFrame
{
  private BorderLayout borderLayout;
  private CommandPanel commandPanel;
//private JPanel commandPanel;

  public SampleFrame()
  {
    borderLayout = new BorderLayout();
    commandPanel = new CommandPanel();
//  commandPanel = new JPanel();
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
    setSize(new Dimension(816, 547));
    setTitle("Case Study");
    getContentPane().add(commandPanel, BorderLayout.CENTER);
  }
}
